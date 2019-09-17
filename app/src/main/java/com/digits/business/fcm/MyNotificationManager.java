package com.digits.business.fcm;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import com.digits.business.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Amro on 13/01/2017.
 */

public class MyNotificationManager {

    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;

    private Context mCtx;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }
    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String Channel_name = "Business App notifications";
            String channel_ID="com.digits.business";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel mChannel = new NotificationChannel(channel_ID, Channel_name, importance);

            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mChannel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000, 1000 });
            AudioAttributes audioAttributes=new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
                mChannel.setSound(alarmSound,audioAttributes);

//            String sp_value_sound= readSharedPreference("com.digits.business.SETTING_KEY_SOUND","saved setting sound");
//            String sp_value_vibrate=readSharedPreference("com.digits.business.SETTING_KEY_VIBRATE","saved setting vibrate");
//
//            if(sp_value_vibrate.equals("on"))
//            {  mChannel.enableVibration(true);
//                mChannel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000, 1000 });}
//            if(sp_value_sound.equals("on"))
//            { AudioAttributes audioAttributes=new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
//                mChannel.setSound(alarmSound,audioAttributes);}
            if(mNotificationManager !=null)
                mNotificationManager.createNotificationChannel(mChannel);

            return channel_ID;
        }
        else {
            return "";
        }

    }
    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification(String title, String message, String url, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_BIG_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

       // saveNotification( message,mCtx);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx,createChannel());
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.voicemail).setTicker(title)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.drawable.small_logo)
                .setColor(ContextCompat.getColor(mCtx,R.color.colorPrimary))
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.logo))
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setLights(Color.GREEN, 3000, 3000)
                .setShowWhen(true)
                .setSound(alarmSound)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .build();

//        String sp_value_sound= readSharedPreference("com.digits.business.SETTING_KEY_SOUND","saved setting sound");
//        String sp_value_vibrate=readSharedPreference("com.digits.business.SETTING_KEY_VIBRATE","saved setting vibrate");
//
//        if(sp_value_vibrate.equals("on"))
//        { notification =mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).build();}
//        if(sp_value_sound.equals("on"))
//        { notification =mBuilder.setSound(alarmSound).build();}

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_BIG_NOTIFICATION, notification);
    }

    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showSmallNotification(String title, String message, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        //saveNotification( message,mCtx);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(message);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx,createChannel());
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.voicemail).setTicker(title)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.small_logo)
                .setColor(ContextCompat.getColor(mCtx,R.color.colorPrimary))
                .setStyle(bigTextStyle)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.logo))
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setLights(Color.GREEN, 3000, 3000)
                .setShowWhen(true)
                .setSound(alarmSound)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .build();

//        String sp_value_sound= readSharedPreference("com.digits.business.SETTING_KEY_SOUND","saved setting sound");
//        String sp_value_vibrate=readSharedPreference("com.digits.business.SETTING_KEY_VIBRATE","saved setting vibrate");
//
//        if(sp_value_vibrate.equals("on"))
//        { notification =mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).build();}
//        if(sp_value_sound.equals("on"))
//        { notification =mBuilder.setSound(alarmSound).build();}

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }

    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  void  saveNotification(String title,Context mCtx)
    {
        Log.i("addNotify", "addNotify: " +title);

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();


        final String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date offsetTime = new Date(cal.getTime().getTime() + tz.getRawOffset());

//        DatabaseHandler dh=new DatabaseHandler(mCtx);
//        Mynotifiaction mynotifiaction=new Mynotifiaction(sdf.format(offsetTime),title);
//        dh.addNotify(mynotifiaction);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(offsetTime);
    }

    public String readSharedPreference(String key,String s )
    {
        SharedPreferences sharedPref =mCtx.getSharedPreferences(key,MODE_PRIVATE);
        //0 is default_value if no vaule
        String savedSetting = sharedPref .getString(s,"");

        return savedSetting;
    }
}
