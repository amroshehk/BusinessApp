package com.digits.business.twilio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digits.business.R;
import com.digits.business.classes.JsonTAG;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.dialpad.view.DialPadKey;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class VoiceActivity extends AppCompatActivity {

    private SessionHandler session;
    private static final String TAG = "VoiceActivity";
    //private String identity;
    private static String identity;
    /*
     * You must provide the URL to the publicly accessible Twilio access token server route
     *
     * For example: https://myurl.io/accessToken
     *
     * If your token server is written in PHP, TWILIO_ACCESS_TOKEN_SERVER_URL needs .php extension at the end.
     *
     * For example : https://myurl.io/accessToken.php
     */
    private static final String TWILIO_ACCESS_TOKEN_SERVER_URL = "http://45.63.23.112/twilio/accessToken.php";

    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    private static final int SNACKBAR_DURATION = 4000;

    private String accessToken;
    private AudioManager audioManager;
    private int savedAudioMode = AudioManager.MODE_INVALID;

    private boolean isReceiverRegistered = false;
    private VoiceBroadcastReceiver voiceBroadcastReceiver;

    // Empty HashMap, never populated for the Quickstart
    HashMap<String, String> twiMLParams = new HashMap<>();

    private CoordinatorLayout coordinatorLayout;
    public static FloatingActionButton callActionFab;
    private FloatingActionButton hangupActionFab;
    private FloatingActionButton muteActionFab;
    private Chronometer chronometer;
    private SoundPoolManager soundPoolManager;

    public static final String INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE";
    public static final String INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID";
    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";
    public static final String ACTION_FCM_TOKEN = "ACTION_FCM_TOKEN";

    private NotificationManager notificationManager;
    private AlertDialog alertDialog;
    private CallInvite activeCallInvite;
    private Call activeCall;
    private int activeCallNotificationId;

    RegistrationListener registrationListener = registrationListener();
    Call.Listener callListener = callListener();

    //-----------------------dialpad component
    private EditText number;
    private View mCallBtn;
    private RelativeLayout dialpad_rel;


    private ImageButton backspce;
    //-----------------------dialpad component end

    private Dialog dialog2;
    private Fragment fragment;
    private FrameLayout content_fragment;


    // Dialpad keys
    private DialPadKey number1;
    private DialPadKey number2;
    private DialPadKey number3;
    private DialPadKey number4;
    private DialPadKey number5;
    private DialPadKey number6;
    private DialPadKey number7;
    private DialPadKey number8;
    private DialPadKey number9;
    private DialPadKey number10;
    private DialPadKey number11;
    private DialPadKey number12;

    private static StringBuilder numberBuilder;

    private ImageButton muteButton;
    private ImageView imageBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        session = new SessionHandler(getApplicationContext());

        User user = session.getUserDetails();

        PreferenceHelper helper = new PreferenceHelper(this);

        identity = helper.getSettingValueGeneratedId();

        //----------------------fragment
//        final FragmentManager fragmentManager = getSupportFragmentManager();
//        content_fragment=findViewById(R.id.content_fragment);
//        fragment = new DialPadAnimationFragment();
//        fragmentManager.beginTransaction().replace(
//                R.id.content_fragment, fragment)
//                .commit();
        //--------------------------------------------end

        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        coordinatorLayout = findViewById(R.id.coordinator_layout);
        callActionFab = findViewById(R.id.call_action_fab);
        hangupActionFab = findViewById(R.id.hangup_action_fab);
        /*muteActionFab = findViewById(R.id.mute_action_fab);*/
        muteButton = findViewById(R.id.muteButton);
        chronometer = findViewById(R.id.chronometer);

        //---------------new Dialpad
        dialpad_rel = findViewById(R.id.dialpad_rel);
        number = findViewById(R.id.number);
        mCallBtn = findViewById(R.id.call_button);
        mCallBtn.setOnClickListener(callClickListenerNew());
        //---------------new Dialpad End

        callActionFab.setOnClickListener(callActionFabClickListener());
        hangupActionFab.setOnClickListener(hangupActionFabClickListener());
        /*muteActionFab.setOnClickListener(muteActionFabClickListener());*/
        muteButton.setOnClickListener(muteActionFabClickListener());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        soundPoolManager = SoundPoolManager.getInstance(this);

        /*
         * Setup the broadcast receiver to be notified of FCM Token updates
         * or incoming call invite in this Activity.
         */
        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        registerReceiver();

        /*
         * Needed for setting/abandoning audio focus during a call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        imageBackground = findViewById(R.id.imageBackground);

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Setup the UI
         */
        resetUI();

        /*
         * Displays a call dialog if the intent contains a call invite
         */
        handleIncomingCallIntent(getIntent());

        /*
         * Ensure the microphone permission is enabled
         */
        if (!checkPermissionForMicrophone()) {
            requestPermissionForMicrophone();
        } else {
            retrieveAccessToken();
        }

        number.setEnabled(false);

        number1 = findViewById(R.id.button1);
        number2 = findViewById(R.id.button2);
        number3 = findViewById(R.id.button3);
        number4 = findViewById(R.id.button4);
        number5 = findViewById(R.id.button5);
        number6 = findViewById(R.id.button6);
        number7 = findViewById(R.id.button7);
        number8 = findViewById(R.id.button8);
        number9 = findViewById(R.id.button9);
        number10 = findViewById(R.id.button10);
        number11 = findViewById(R.id.button11);
        number12 = findViewById(R.id.button12);

        backspce = findViewById(R.id.backspce);

        numberBuilder = new StringBuilder();

        number1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("1");
                number.setText(numberBuilder.toString());
            }
        });

        number2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("2");
                number.setText(numberBuilder.toString());
            }
        });

        number3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("3");
                number.setText(numberBuilder.toString());
            }
        });

        number4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("4");
                number.setText(numberBuilder.toString());
            }
        });

        number5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("5");
                number.setText(numberBuilder.toString());
            }
        });

        number6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("6");
                number.setText(numberBuilder.toString());
            }
        });

        number7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("7");
                number.setText(numberBuilder.toString());
            }
        });

        number8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("8");
                number.setText(numberBuilder.toString());
            }
        });

        number9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("9");
                number.setText(numberBuilder.toString());
            }
        });

        number10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("*");
                number.setText(numberBuilder.toString());
            }
        });

        number11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("0");
                number.setText(numberBuilder.toString());
            }
        });


        number11.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numberBuilder.append("+");
                number.setText(numberBuilder.toString());
                return true;
            }
        } );


        number12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("#");
                number.setText(numberBuilder.toString());
            }
        });


        backspce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberBuilder.length() > 0) {
                    numberBuilder.deleteCharAt(numberBuilder.length()-1);
                    number.setText(numberBuilder.toString());
                }
            }
        });


        backspce.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (numberBuilder.length() > 0) {
                    numberBuilder.delete(0,numberBuilder.length());
                    number.setText("");

                }
                return true;
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingCallIntent(intent);
    }
//    @Override
//    public void onBackPressed() {
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_fragment);
//        if (!(fragment instanceof IOnBackPressed) ||
//                !((IOnBackPressed) fragment).onBackPressed()) {
//            super.onBackPressed();
//        }
//    }

    private RegistrationListener registrationListener() {
        return new RegistrationListener() {
            @Override
            public void onRegistered(String accessToken, String fcmToken) {
                Log.d(TAG, "Successfully registered FCM " + fcmToken);
            }

            @SuppressLint("WrongConstant")
            @Override
            public void onError(RegistrationException error, String accessToken, String fcmToken) {
                String message = String.format("Registration Error: %d, %s", error.getErrorCode(), error.getMessage());
                Log.e(TAG, message);
                Snackbar.make(coordinatorLayout, message, SNACKBAR_DURATION).show();
            }
        };
    }

    private Call.Listener callListener() {
        return new Call.Listener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onConnectFailure(Call call, CallException error) {
                setAudioFocus(false);
                Log.d(TAG, "Connect failure");
                String message = String.format("Call Error: %d, %s", error.getErrorCode(), error.getMessage());
                Log.e(TAG, message);
                Snackbar.make(coordinatorLayout, message, SNACKBAR_DURATION).show();
                resetUI();
            }

            @Override
            public void onConnected(Call call) {
                setAudioFocus(true);
                Log.d(TAG, "Connected");
                activeCall = call;
            }

            @SuppressLint("WrongConstant")
            @Override
            public void onDisconnected(Call call, CallException error) {
                setAudioFocus(false);
                Log.d(TAG, "Disconnected");
                if (error != null) {
                    String message = String.format("Call Error: %d, %s", error.getErrorCode(), error.getMessage());
                    Log.e(TAG, message);
                    Snackbar.make(coordinatorLayout, message, SNACKBAR_DURATION).show();
                }
                resetUI();
            }
        };
    }

    /*
     * The UI state when there is an active call
     */
    private void setCallUI() {
        //content_fragment.setVisibility(View.GONE);
        dialpad_rel.setVisibility(View.GONE);
        callActionFab.hide();
        hangupActionFab.show();
        /*muteActionFab.show();*/
        muteButton.setVisibility(View.VISIBLE);
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        imageBackground.setVisibility(View.VISIBLE);
    }

    /*
     * Reset UI elements
     */
    private void resetUI() {
       // content_fragment.setVisibility(View.GONE);
        dialpad_rel.setVisibility(View.VISIBLE);
        callActionFab.hide();
        /*muteActionFab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_mic_white_24dp));*/
     //   muteActionFab.setBackgroundResource( R.drawable.ic_mic);
       /* muteActionFab.hide();*/
        muteButton.setVisibility(View.INVISIBLE);
        hangupActionFab.hide();
        chronometer.setVisibility(View.INVISIBLE);
        chronometer.stop();
        imageBackground.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onDestroy() {
        soundPoolManager.release();
        super.onDestroy();
    }

    private void handleIncomingCallIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_INCOMING_CALL)) {
                activeCallInvite = intent.getParcelableExtra(INCOMING_CALL_INVITE);
                if (activeCallInvite != null && (activeCallInvite.getState() == CallInvite.State.PENDING)) {
                    soundPoolManager.playRinging();
//                    alertDialog = createIncomingCallDialog(VoiceActivity.this,
//                            activeCallInvite,
//                            answerCallClickListener(),
//                            cancelCallClickListener());
//                    alertDialog.show();
                    showIncommingDialog(VoiceActivity.this,
                            activeCallInvite,
                            answerCallClickListenernew(),
                            cancelCallClickListenerNew());
                    activeCallNotificationId = intent.getIntExtra(INCOMING_CALL_NOTIFICATION_ID, 0);
                } else {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        soundPoolManager.stopRinging();
                        alertDialog.cancel();
                    }
                    if (dialog2 != null && dialog2.isShowing()) {
                        soundPoolManager.stopRinging();
                        dialog2.cancel();
                    }
                }
            } else if (intent.getAction().equals(ACTION_FCM_TOKEN)) {
                retrieveAccessToken();
            }
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_INCOMING_CALL);
            intentFilter.addAction(ACTION_FCM_TOKEN);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
            isReceiverRegistered = false;
        }
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_INCOMING_CALL)) {
                /*
                 * Handle the incoming call invite
                 */
                handleIncomingCallIntent(intent);
            }
        }
    }

    private DialogInterface.OnClickListener answerCallClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                soundPoolManager.stopRinging();
                answer();
                setCallUI();
                alertDialog.dismiss();
            }
        };
    }

    private View.OnClickListener answerCallClickListenernew() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View dialog) {
                soundPoolManager.stopRinging();
                answer();
                setCallUI();
                dialog2.dismiss();
            }
        };
    }

    private DialogInterface.OnClickListener callClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Place a call
                EditText contact = (EditText) ((AlertDialog) dialog).findViewById(R.id.contact);
                twiMLParams.put("to", contact.getText().toString());
                activeCall = Voice.call(VoiceActivity.this, accessToken, twiMLParams, callListener);
                setCallUI();
                alertDialog.dismiss();
            }
        };
    }
    private View.OnClickListener callClickListenerNew() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Place a call
                twiMLParams.put("to", number.getText().toString());
                activeCall = Voice.call(VoiceActivity.this, accessToken, twiMLParams, callListener);
                setCallUI();
            }
        };
    }


    private DialogInterface.OnClickListener cancelCallClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                soundPoolManager.stopRinging();
                if (activeCallInvite != null) {
                    activeCallInvite.reject(VoiceActivity.this);
                    notificationManager.cancel(activeCallNotificationId);
                }
                alertDialog.dismiss();
            }
        };
    }

    private View.OnClickListener cancelCallClickListenerNew() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View dialogInterface) {
                soundPoolManager.stopRinging();
                if (activeCallInvite != null) {
                    activeCallInvite.reject(VoiceActivity.this);
                    notificationManager.cancel(activeCallNotificationId);
                }
                dialog2.dismiss();
            }
        };
    }

    public static AlertDialog createIncomingCallDialog(
            Context context,
            CallInvite callInvite,
            DialogInterface.OnClickListener answerCallClickListener,
            DialogInterface.OnClickListener cancelClickListener) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);
        alertDialogBuilder.setTitle("Incoming Call");
        alertDialogBuilder.setPositiveButton("Accept", answerCallClickListener);
        alertDialogBuilder.setNegativeButton("Reject", cancelClickListener);
        alertDialogBuilder.setMessage(callInvite.getFrom() + " is calling.");
        return alertDialogBuilder.create();
    }

    void showIncommingDialog( Context context,
                                         CallInvite callInvite,
                                         View.OnClickListener answerCallClickListener,
                                         View.OnClickListener cancelClickListener) {


        TextView textDisplayName;
        ImageView buttonHangup;
        ImageView buttonAnswer;

        dialog2 = new Dialog(context);
        dialog2.setCancelable(false);
        dialog2.setCanceledOnTouchOutside(false);

        dialog2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.incomming_call_dialog_layout);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        textDisplayName = dialog2.findViewById(R.id.textDisplayName);
        buttonHangup = dialog2.findViewById(R.id.buttonHangup);
        buttonAnswer = dialog2.findViewById(R.id.buttonAnswer);

        textDisplayName.setText(callInvite.getFrom());
        // if button is clicked, close the custom dialog
        buttonHangup.setOnClickListener(cancelClickListener);
        // if button is clicked, close the custom dialog
        buttonAnswer.setOnClickListener(answerCallClickListener);
        dialog2.show();
    }

    /*
     * Register your FCM token with Twilio to receive incoming call invites
     *
     * If a valid google-services.json has not been provided or the FirebaseInstanceId has not been
     * initialized the fcmToken will be null.
     *
     * In the case where the FirebaseInstanceId has not yet been initialized the
     * VoiceFirebaseInstanceIDService.onTokenRefresh should result in a LocalBroadcast to this
     * activity which will attempt registerForCallInvites again.
     *
     */
    private void registerForCallInvites() {
        final String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken != null) {
            Log.i(TAG, "Registering with FCM");
            Voice.register(this, accessToken, Voice.RegistrationChannel.FCM, fcmToken, registrationListener);
        }
    }

    private View.OnClickListener callActionFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = createCallDialog(callClickListener(), cancelCallClickListener(), VoiceActivity.this);
                alertDialog.show();
            }
        };
    }

    private View.OnClickListener hangupActionFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPoolManager.playDisconnect();
                resetUI();
                disconnect();
            }
        };
    }

    private View.OnClickListener muteActionFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mute();
            }
        };
    }

    /*
     * Accept an incoming Call
     */
    private void answer() {
        activeCallInvite.accept(this, callListener);
        notificationManager.cancel(activeCallNotificationId);
    }

    /*
     * Disconnect from Call
     */
    private void disconnect() {
        if (activeCall != null) {
            activeCall.disconnect();
            activeCall = null;
        }
    }

    private void mute() {
        if (activeCall != null) {
            boolean mute = !activeCall.isMuted();
            activeCall.mute(mute);
            if (mute) {
              //  muteActionFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_mic_off));
              //  muteActionFab.setBackgroundResource(R.drawable.ic_mic_off);
               // muteActionFab.setImageResource(R.drawable.ic_mic_off);
               // muteActionFab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_mic_white_off_24dp));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    muteButton.setForeground(this.getResources().getDrawable(R.drawable.ic_mic_white_24dp));
                }

            } else {
              //  muteActionFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_mic));
               // muteActionFab.setBackgroundResource( R.drawable.ic_mic);
              //  muteActionFab.setImageResource( R.drawable.ic_mic);
                //muteActionFab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_mic_white_24dp));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    muteButton.setForeground(this.getResources().getDrawable(R.drawable.ic_mic_white_off_24dp));
                }
            }
        }
    }

    private void setAudioFocus(boolean setFocus) {
        if (audioManager != null) {
            if (setFocus) {
                savedAudioMode = audioManager.getMode();
                // Request audio focus before making any device switch.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();
                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                                @Override
                                public void onAudioFocusChange(int i) {
                                }
                            })
                            .build();
                    audioManager.requestAudioFocus(focusRequest);
                } else {
                    audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                }
                /*
                 * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
                 * required to be in this mode when playout and/or recording starts for
                 * best possible VoIP performance. Some devices have difficulties with speaker mode
                 * if this is not set.
                 */
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(savedAudioMode);
                audioManager.abandonAudioFocus(null);
            }
        }
    }

    private boolean checkPermissionForMicrophone() {
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultMic == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("WrongConstant")
    private void requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(coordinatorLayout,
                    "Microphone permissions needed. Please allow in your application settings.",
                    SNACKBAR_DURATION).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if microphone permissions is granted
         */
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(coordinatorLayout,
                        "Microphone permissions needed. Please allow in your application settings.",
                        SNACKBAR_DURATION).show();
            } else {
                retrieveAccessToken();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.speaker_menu_item:
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    item.setIcon(R.drawable.ic_phonelink_ring_white_24dp);
                } else {
                    audioManager.setSpeakerphoneOn(true);
                    item.setIcon(R.drawable.ic_volume_up_white_24dp);
                }
                break;
        }
        return true;
    }

    public static AlertDialog createCallDialog(final DialogInterface.OnClickListener callClickListener,
                                               final DialogInterface.OnClickListener cancelClickListener,
                                               final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);
        alertDialogBuilder.setTitle("Call");
        alertDialogBuilder.setPositiveButton("Call", callClickListener);
        alertDialogBuilder.setNegativeButton("Cancel", cancelClickListener);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.dialog_call, null);
        final EditText contact = (EditText) dialogView.findViewById(R.id.contact);
        contact.setHint(R.string.callee);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();

    }

    /*
     * Get an access token from your Twilio access token server
     */
    private void retrieveAccessToken() {
        Ion.with(this).load(TWILIO_ACCESS_TOKEN_SERVER_URL + "?identity=" + identity).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String accessToken) {
                if (e == null) {
                    Log.d(TAG, "Access token: " + accessToken);
                    VoiceActivity.this.accessToken = accessToken;
                    registerForCallInvites();
                } else {
                    Snackbar.make(coordinatorLayout,
                            "Error retrieving access token. Unable to make calls",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
