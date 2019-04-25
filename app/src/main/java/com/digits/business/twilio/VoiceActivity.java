package com.digits.business.twilio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
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
import android.net.Uri;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digits.business.R;
import com.digits.business.activities.AboutUsActivity;
import com.digits.business.activities.LoginActivity;
import com.digits.business.activities.MainActivity;
import com.digits.business.activities.ProfileActivity;
import com.digits.business.activities.SaveTTSActivity;
import com.digits.business.activities.SettingActivity;
import com.digits.business.activities.UploadGreetingActivity2;
import com.digits.business.activities.VoiceMailActivity;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.dialpad.view.DialPadKey;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.digits.business.classes.JsonTAG.TAG_EMAIL;
import static com.digits.business.classes.JsonTAG.TAG_ID;
import static com.digits.business.classes.JsonTAG.TAG_SECONDS;
import static com.digits.business.classes.URLTAG.LOGOUT_URL;
import static com.digits.business.classes.URLTAG.URL_DEDUCT_SECONDS;
import static com.digits.business.classes.URLTAG.URL_GET_SECONDS;

public class VoiceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

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

    private RelativeLayout coordinatorLayout;
    public static FloatingActionButton callActionFab;
    private ImageButton hangupActionFab;
    private ImageButton muteActionFab;
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

  //  private ImageButton muteButton;
    private ImageView imageBackground;

    private ImageButton speacker_action_fab;

    private ProgressDialog progressDialog;
    private Context context;
    private String email,name;
    ImageLoaderConfiguration config;
    CircleImageView photo_nav_header;
    Toolbar toolbar;


    PreferenceHelper helper;


    private static  int seconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        helper = new PreferenceHelper(context);

        session = new SessionHandler(context);

        User user = session.getUserDetails();

        identity = helper.getSettingValueGeneratedId();
        email= helper.getSettingValueEmail();
        name= helper.getSettingValueName();
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
        muteActionFab = findViewById(R.id.mute_action_fab);
//        muteButton = findViewById(R.id.muteButton);
        chronometer = findViewById(R.id.chronometer);

        //---------------new Dialpad
        dialpad_rel = findViewById(R.id.dialpad_rel);
        number = findViewById(R.id.number);
        mCallBtn = findViewById(R.id.call_button);
        mCallBtn.setOnClickListener(callClickListenerNew());
        //---------------new Dialpad End

        callActionFab.setOnClickListener(callActionFabClickListener());
        hangupActionFab.setOnClickListener(hangupActionFabClickListener());
        muteActionFab.setOnClickListener(muteActionFabClickListener());
      //  muteButton.setOnClickListener(muteActionFabClickListener());

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

        speacker_action_fab = findViewById(R.id.speacker_action_fab);
        speacker_action_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    speacker_action_fab.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_volume_up_white_24dp));
                } else {
                    audioManager.setSpeakerphoneOn(true);
                    speacker_action_fab.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_phonelink_ring_white_24dp));
                }
            }
        });

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

        setupDrawer();

        checkSecond();

        Intent intent=getIntent();
        String generatedid=intent.getStringExtra("GeneratedID");
        if( generatedid!=null && !generatedid.equals("null"))
        {
            number.setText(generatedid);
        }

    }





    void setupDrawer() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        photo_nav_header = hView.findViewById(R.id.photo);
        TextView name_tv=hView.findViewById(R.id.name_tv);
        TextView  email_tv=hView.findViewById(R.id.email_tv);

        name_tv.setText(name);
        email_tv.setText(email);

        getUserPhoto();

        hView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VoiceActivity.this, ProfileActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }




    void getUserPhoto() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        config = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(config);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        if (!helper.getSettingValuePhotoUrl().isEmpty())
            imageLoader.displayImage(helper.getSettingValuePhotoUrl(), photo_nav_header, options);
        else
            photo_nav_header.setBackgroundResource(R.drawable.user512);

    }




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingCallIntent(intent);
    }




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

                int elapsedSeconds = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) /1000);
                deductSecond(elapsedSeconds);

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
        hangupActionFab.setVisibility(View.VISIBLE);
        muteActionFab.setVisibility(View.VISIBLE);
        speacker_action_fab.setVisibility(View.VISIBLE);

     //   muteButton.setVisibility(View.VISIBLE);
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
        muteActionFab.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_mic_white_24dp));
     //   muteActionFab.setBackgroundResource( R.drawable.ic_mic);
//        Resources resources = getBaseContext().getResources();
//        Resources.Theme theme = getBaseContext().getTheme();
//        Drawable drawable = VectorDrawableCompat.create(resources, R.drawable.ic_mic_white_24dp, theme);
//        muteActionFab.setImageDrawable(drawable);
        muteActionFab.setVisibility(View.GONE);

        speacker_action_fab.setVisibility(View.GONE);
        audioManager.setSpeakerphoneOn(false);
        speacker_action_fab.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_volume_up_white_24dp));
        //muteButton.setVisibility(View.INVISIBLE);
        hangupActionFab.setVisibility(View.GONE);
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



    private View.OnClickListener callActionFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = createCallDialog(callClickListener(), cancelCallClickListener(), VoiceActivity.this);
                alertDialog.show();
            }
        };
    }


    private View.OnClickListener callClickListenerNew() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accessToken != null) {

                    checkSecond();

                    if (seconds > 0) {
                    // Place a call
                    twiMLParams.put("to", number.getText().toString());

                    activeCall = Voice.call(VoiceActivity.this, accessToken, twiMLParams, callListener);
                    setCallUI();
                    } else {
                        Snackbar.make(v,"Your balance is 0 seconds",Snackbar.LENGTH_SHORT).show();
                    }

                } else {
                    Snackbar.make(v,"Wait",Snackbar.LENGTH_SHORT).show();
                }
            }
        };
    }


    private DialogInterface.OnClickListener callClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    // Place a call
                    EditText contact = ((AlertDialog) dialog).findViewById(R.id.contact);
                    twiMLParams.put("to", contact.getText().toString());
                    activeCall = Voice.call(VoiceActivity.this, accessToken, twiMLParams, callListener);
                    setCallUI();
                    alertDialog.dismiss();
                }
        };
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
     * If a valid google-services-old.json has not been provided or the FirebaseInstanceId has not been
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

            /*int elapsedSeconds = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) /1000);
            deductSecond(elapsedSeconds);*/
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
//                final Handler   handler = new Handler();
//
//                final Runnable r = new Runnable() {
//                    public void run() {
//
////                        handler.postDelayed(this, 1000);
//                        Resources resources = getBaseContext().getResources();
//                        Resources.Theme theme = getBaseContext().getTheme();
//                        Drawable drawable = VectorDrawableCompat.create(resources, R.drawable.ic_mic_white_off_24dp, theme);
//                        muteActionFab.setImageDrawable(drawable);
//                    }
//                };
//
//                handler.postDelayed(r, 200);
                muteActionFab.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_mic_white_off_24dp));

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    muteButton.setForeground(this.getResources().getDrawable(R.drawable.ic_mic_white_24dp));
//                }

            } else {
              //  muteActionFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_mic));
               // muteActionFab.setBackgroundResource( R.drawable.ic_mic);
              //  muteActionFab.setImageResource( R.drawable.ic_mic);
                muteActionFab.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_mic_white_24dp));
//                final Handler   handler = new Handler();
//
//                final Runnable r = new Runnable() {
//                    public void run() {
//
////                        handler.postDelayed(this, 1000);
//                        Resources resources = getBaseContext().getResources();
//                        Resources.Theme theme = getBaseContext().getTheme();
//                        Drawable drawable = VectorDrawableCompat.create(resources, R.drawable.ic_mic_white_24dp, theme);
//                        muteActionFab.setImageDrawable(drawable);
//                    }
//                };
//
//                handler.postDelayed(r, 200);
               // muteActionFab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_mic_white_24dp));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    muteButton.setForeground(this.getResources().getDrawable(R.drawable.ic_mic_white_off_24dp));
//                }
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.speaker_menu_item:
//                if (audioManager.isSpeakerphoneOn()) {
//                    audioManager.setSpeakerphoneOn(false);
//                    item.setIcon(R.drawable.ic_phonelink_ring_white_24dp);
//                } else {
//                    audioManager.setSpeakerphoneOn(true);
//                    item.setIcon(R.drawable.ic_volume_up_white_24dp);
//                }
//                break;
//        }
//        return true;
//    }



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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Intent intent = new Intent(VoiceActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        else if (id == R.id.nav_mp3) {
            Intent intent = new Intent(VoiceActivity.this, VoiceMailActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_mp3_2) {

            Intent intent = new Intent(VoiceActivity.this, UploadGreetingActivity2.class);
            startActivity(intent);

        }else if (id == R.id.nav_voice) {

            Intent intent = new Intent(VoiceActivity.this, VoiceActivity.class);
            /*intent.putExtra(TAG_GENERATED_ID, generated_id);*/
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_TTS) {
            Intent intent = new Intent(VoiceActivity.this, SaveTTSActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_aboutus) {
            Intent intent = new Intent(VoiceActivity.this, AboutUsActivity.class);
            startActivity(intent);


        }  else if (id == R.id.nav_manage) {
            Intent intent = new Intent(VoiceActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        } /*else if (id == R.id.nav_send) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
            catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }*/
        else if (id == R.id.nav_logout) {
            LogoutServer(email);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void LogoutServer(final String email) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logout.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, LOGOUT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    progressDialog.dismiss();
                    if (obj.getBoolean("success")) {


                        FirebaseAuth.getInstance().signOut();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                        helper.setLoginState(false);
                        helper.deleteUser();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(VoiceActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                    } else  {
                        Toast.makeText(getApplicationContext(), "User logged out Not successfully", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "error JSONException", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String message = "";
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

//                    String responseBody = new String(volleyError.networkResponse.data, "utf-8");
//                    JSONObject jsonObject = new JSONObject(responseBody);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put(TAG_EMAIL, email);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }




    private void checkSecond() {

        Uri baseUri = Uri.parse( URL_GET_SECONDS );
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("id", helper.getSettingValueId());

        StringRequest request = new StringRequest(
                Request.Method.GET, uriBuilder.toString(),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                                JSONObject obj = new JSONObject(response);
                                seconds = obj.getInt("data");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

            }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = "";
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    }  else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }

                    Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }



    private void deductSecond(final int callSeconds) {


        StringRequest request = new StringRequest(Request.Method.POST, URL_DEDUCT_SECONDS,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject obj = new JSONObject(response);
                            seconds = obj.getInt("data");

                            Toast.makeText(context, "Your remaining balance is "+ seconds + " seconds",
                                            Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        String message = "";
                        if (volleyError instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (volleyError instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (volleyError instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (volleyError instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (volleyError instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }

                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                    }
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json; charset=utf-8");
                        params.put(TAG_ID, helper.getSettingValueId());
                        params.put(TAG_SECONDS, String.valueOf(callSeconds));
                        return params;
                    }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }


    }
