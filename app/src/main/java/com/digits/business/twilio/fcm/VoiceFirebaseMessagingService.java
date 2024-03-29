package com.digits.business.twilio.fcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
//import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.digits.business.R;
import com.digits.business.activities.VoiceMailActivity;
import com.digits.business.fcm.MyNotificationManager;
import com.digits.business.fcm.SharedPrefManager;
import com.digits.business.phoneauth.PhoneNumberAuthActivity;
import com.digits.business.twilio.SoundPoolManager;
import com.digits.business.twilio.VoiceActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.twilio.voice.CallInvite;
import com.twilio.voice.MessageException;
import com.twilio.voice.MessageListener;
import com.twilio.voice.Voice;
//import com.twilio.voice.quickstart.R;
//import com.twilio.voice.quickstart.SoundPoolManager;
//import com.twilio.voice.quickstart.VoiceActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.digits.business.classes.JsonTAG.TAG_DATA;

public class VoiceFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "VoiceFCMService";
    private static final String NOTIFICATION_ID_KEY = "NOTIFICATION_ID";
    private static final String CALL_SID_KEY = "CALL_SID";
    private static final String VOICE_CHANNEL = "default";

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Received onMessageReceived()");
        Log.d(TAG, "Bundle data: " + remoteMessage.getData());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.

        if (remoteMessage.getData().size() > 0) {
            try {
                Map<String, String> data = remoteMessage.getData();
//            JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                JSONObject data_obj = json.getJSONObject(TAG_DATA);

//            if(data_obj.getString("title").equals("New voice mail"))
//            {
//
//
//                    sendPushNotification(json);
//
//            }
//            else
//            {

                checkcall(data);
                sendPushNotification(data);
//            }
            } catch (Exception e) {
                // Log.e(TAG, "Exception: " + e.getMessage());
//                checkcall(data);
            }
        }
    }

    private void checkcall(Map<String, String> data)
    {
        final int notificationId = (int) System.currentTimeMillis();
        Voice.handleMessage(this, data, new MessageListener() {
            @Override
            public void onCallInvite(CallInvite callInvite) {
                VoiceFirebaseMessagingService.this.notify(callInvite, notificationId);
                VoiceFirebaseMessagingService.this.sendCallInviteToActivity(callInvite, notificationId);
            }

            @Override
            public void onError(MessageException messageException) {
                Log.e(TAG, messageException.getLocalizedMessage());
            }
        });
    }

    private void notify(CallInvite callInvite, int notificationId) {
        String callSid = callInvite.getCallSid();
        Notification notification = null;

        if (callInvite.getState() == CallInvite.State.PENDING) {
            Intent intent = new Intent(this, VoiceActivity.class);
            intent.setAction(VoiceActivity.ACTION_INCOMING_CALL);
            intent.putExtra(VoiceActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
            intent.putExtra(VoiceActivity.INCOMING_CALL_INVITE, callInvite);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
            /*
             * Pass the notification id and call sid to use as an identifier to cancel the
             * notification later
             */
            Bundle extras = new Bundle();
            extras.putInt(NOTIFICATION_ID_KEY, notificationId);
            extras.putString(CALL_SID_KEY, callSid);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel callInviteChannel = new NotificationChannel(VOICE_CHANNEL,
                        "Primary Voice Channel", NotificationManager.IMPORTANCE_DEFAULT);
                callInviteChannel.setLightColor(Color.GREEN);
                callInviteChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                AudioAttributes audioAttributes=new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
                callInviteChannel.setSound(alarmSound,audioAttributes);

                notificationManager.createNotificationChannel(callInviteChannel);

                notification = buildNotification(callInvite.getFrom() + " is calling.", pendingIntent, extras);
                notificationManager.notify(notificationId, notification);
            } else {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this,"")
                                .setSmallIcon(R.drawable.ic_call_end_white_24dp)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(callInvite.getFrom() + " is calling.")
                                .setAutoCancel(true)
                                .setExtras(extras)
                                .setSound(alarmSound)
                                .setContentIntent(pendingIntent)
                                .setGroup("test_app_notification")
                                .setColor(Color.rgb(214, 10, 37));

                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        } else {
            SoundPoolManager.getInstance(this).stopRinging();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                /*
                 * If the incoming call was cancelled then remove the notification by matching
                 * it with the call sid from the list of notifications in the notification drawer.
                 */
                StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
                for (StatusBarNotification statusBarNotification : activeNotifications) {
                    notification = statusBarNotification.getNotification();
                    Bundle extras = notification.extras;
                    String notificationCallSid = extras.getString(CALL_SID_KEY);

                    if (callSid.equals(notificationCallSid)) {
                        notificationManager.cancel(extras.getInt(NOTIFICATION_ID_KEY));
                    } else {
                        sendCallInviteToActivity(callInvite, notificationId);
                    }
                }
            } else {
                /*
                 * Prior to Android M the notification manager did not provide a list of
                 * active notifications so we lazily clear all the notifications when
                 * receiving a cancelled call.
                 *
                 * In order to properly cancel a notification using
                 * NotificationManager.cancel(notificationId) we should store the call sid &
                 * notification id of any incoming calls using shared preferences or some other form
                 * of persistent storage.
                 */
                notificationManager.cancelAll();
            }
        }
    }

    /*
     * Send the CallInvite to the VoiceActivity. Start the activity if it is not running already.
     */
    private void sendCallInviteToActivity(CallInvite callInvite, int notificationId) {
        Intent intent = new Intent(this, VoiceActivity.class);
        intent.setAction(VoiceActivity.ACTION_INCOMING_CALL);
        intent.putExtra(VoiceActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.putExtra(VoiceActivity.INCOMING_CALL_INVITE, callInvite);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    /**
     * Build a notification.
     *
     * @param text          the text of the notification
     * @param pendingIntent the body, pending intent for the notification
     * @param extras        extras passed with the notification
     * @return the builder
     */
    @TargetApi(Build.VERSION_CODES.O)
    public Notification buildNotification(String text, PendingIntent pendingIntent, Bundle extras) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new Notification.Builder(getApplicationContext(), VOICE_CHANNEL)
                .setSmallIcon(R.drawable.ic_call_end_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setExtras(extras)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .build();
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);

        //Displaying token on logcat
        //   Log.d(TAG, "Refreshed token: " + refreshedToken);

        //calling the method store token and passing token

        // Notify Activity of FCM token
        storeToken(token);

        Intent intent = new Intent(VoiceActivity.ACTION_FCM_TOKEN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }

    private void storeToken(String token) {
        //saving the token on shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);

    }


    private void sendPushNotification( Map<String, String> data) {
        //optionally we can display the json into log
        //   Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data

            JSONObject json = new JSONObject(data.toString());
            JSONObject data_obj = json.getJSONObject(TAG_DATA);

            //parsing json data
            String title = data_obj.getString("title");
            String message = data_obj.getString("message");
       //     String imageUrl = data.getString("image");

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), VoiceMailActivity.class);

            //if there is no image
//            if(imageUrl.equals("null")){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
//            }else{
//                //if there is an image
//                //displaying a big notification
//                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
//            }
        } catch (JSONException e) {
              Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
