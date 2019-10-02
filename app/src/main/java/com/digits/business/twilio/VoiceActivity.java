package com.digits.business.twilio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.ContactsContract;
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
import com.digits.business.activities.CallHistoryActivity;
import com.digits.business.activities.LoginActivity;
import com.digits.business.activities.MainActivity;
import com.digits.business.activities.ProfileActivity;
import com.digits.business.activities.SaveTTSActivity;
import com.digits.business.activities.SettingActivity;
import com.digits.business.activities.UploadGreetingActivity2;
import com.digits.business.activities.VoiceMailActivity;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.database.DbHelper;
import com.digits.business.dialpad.view.DialPadKey;
import com.digits.business.fcm.MyNotificationManager;
import com.digits.business.phoneauth.PhoneNumberAuthActivity;
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

import java.util.Calendar;
import java.util.Date;
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
    private static VoiceBroadcastReceiver voiceBroadcastReceiver;

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
    private ImageButton contact_btn;
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
    private ImageButton keypad_action_fab;

    private ProgressDialog progressDialog;
    private Context context;
    private String email,name,num_called;
    ImageLoaderConfiguration config;
    CircleImageView photo_nav_header;
    Toolbar toolbar;


    static PreferenceHelper helper;
    User user;

    private static  int seconds = 0;

    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int CONTACT_PERMISSION_REQUEST_CODE = 7;

    private ToneGenerator mToneGenerator;

    DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        helper = new PreferenceHelper(context);
        dbHelper=new DbHelper(context);
        session = new SessionHandler(context);

        user = session.getUserDetails();

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

        keypad_action_fab = findViewById(R.id.keypad_action_fab);
        keypad_action_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialpad_rel.getVisibility()==View.VISIBLE)
                dialpad_rel.setVisibility(View.GONE);
                else
                dialpad_rel.setVisibility(View.VISIBLE);
                callActionFab.hide();
            }
        });


        /*
         * Ensure the microphone permission is enabled
         */
        if (!checkPermissionForMicrophone()) {
            requestPermissionForMicrophone();
        } else {
            retrieveAccessToken();
        }

      //  number.setEnabled(false);

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
        contact_btn = findViewById(R.id.contact_btn);

        numberBuilder = new StringBuilder();

        number1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("1");
                number.setText(numberBuilder.toString());

                playTone(context,1);

                if(activeCall!=null)
                    activeCall.sendDigits("1");
            }
        });

        number2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("2");
                number.setText(numberBuilder.toString());

                playTone(context,2);

                if(activeCall!=null)
                    activeCall.sendDigits("2");
            }
        });

        number3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("3");
                number.setText(numberBuilder.toString());

                playTone(context,3);

                if(activeCall!=null)
                    activeCall.sendDigits("3");

            }
        });

        number4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("4");
                number.setText(numberBuilder.toString());

                playTone(context,4);

                if(activeCall!=null)
                    activeCall.sendDigits("4");
            }
        });

        number5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("5");
                number.setText(numberBuilder.toString());

                playTone(context,5);

                if(activeCall!=null)
                    activeCall.sendDigits("5");
            }
        });

        number6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("6");
                number.setText(numberBuilder.toString());

                playTone(context,6);

                if(activeCall!=null)
                    activeCall.sendDigits("6");
            }
        });

        number7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("7");
                number.setText(numberBuilder.toString());

                playTone(context,7);

                if(activeCall!=null)
                    activeCall.sendDigits("7");
            }
        });

        number8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("8");
                number.setText(numberBuilder.toString());

                playTone(context,8);

                if(activeCall!=null)
                    activeCall.sendDigits("8");
            }
        });

        number9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("9");
                number.setText(numberBuilder.toString());

                playTone(context,9);

                if(activeCall!=null)
                    activeCall.sendDigits("9");
            }
        });

        number10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("*");
                number.setText(numberBuilder.toString());
                //TONE_DTMF_S
                playTone(context,10);

                if(activeCall!=null)
                    activeCall.sendDigits("*");
            }
        });

        number11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("0");
                number.setText(numberBuilder.toString());

                playTone(context,0);

                if(activeCall!=null)
                    activeCall.sendDigits("0");
            }
        });


        number11.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numberBuilder.append("+");
                number.setText(numberBuilder.toString());

                if(activeCall!=null)
                    activeCall.sendDigits("+");
                return true;
            }
        } );


        number12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append("#");
                number.setText(numberBuilder.toString());

                //TONE_DTMF_P
                playTone(context,11);

                if(activeCall!=null)
                    activeCall.sendDigits("#");
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

        contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableContactsRuntimePermission();

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

        setupDrawer();

        checkSecond();

        Intent intent=getIntent();
        String generatedid=intent.getStringExtra("GeneratedID");
        if( generatedid!=null && !generatedid.equals("null"))
        {
            number.setText(generatedid);
            numberBuilder.append(generatedid);

        }

    }
    private  void playTone(Context context, int mediaFileRawId) {
        Log.d(TAG, "playTone");
        try {
            if (mToneGenerator == null) {
                mToneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 50);
            }
            mToneGenerator.startTone(mediaFileRawId, 200);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mToneGenerator != null) {
                        Log.d(TAG, "ToneGenerator released");
                        mToneGenerator.release();
                        mToneGenerator = null;
                    }
                }

            }, 200);
        } catch (Exception e) {
            Log.d(TAG, "Exception while playing sound:" + e);
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

                Date currentTime = Calendar.getInstance().getTime();
                Long tsLong = currentTime.getTime();
                if(activeCall!=null)
                {
                    com.digits.business.entities.Log log=new com.digits.business.entities.Log(email,tsLong,2,num_called,elapsedSeconds,num_called);
                    dbHelper.saveLogEntry(log);
                }
                else if(activeCallInvite!=null)
                {
                    com.digits.business.entities.Log log=new com.digits.business.entities.Log(email,tsLong,1,activeCallInvite.getFrom(),elapsedSeconds,activeCallInvite.getFrom());
                    dbHelper.saveLogEntry(log);
                }

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
        contact_btn.setVisibility(View.GONE);
        hangupActionFab.setVisibility(View.VISIBLE);
        muteActionFab.setVisibility(View.VISIBLE);
        speacker_action_fab.setVisibility(View.VISIBLE);
        keypad_action_fab.setVisibility(View.VISIBLE);

     //   muteButton.setVisibility(View.VISIBLE);
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        imageBackground.setVisibility(View.VISIBLE);
    }


    /*
     * Ensure the Bluetooth permission is enabled
     */
    public void enableBluetooth()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                    REQUEST_ENABLE_BT);
        }
    }
    }
    /*
     * Ensure the Bluetooth permission is enabled
     */
    @SuppressLint("WrongConstant")
    public void enableContactsRuntimePermission(){

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_REQUEST_CODE);

        } else {

            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 7);

        }
    }
    /*
     * Reset UI elements
     */
    private void resetUI() {
       // content_fragment.setVisibility(View.GONE);
        dialpad_rel.setVisibility(View.VISIBLE);
        callActionFab.hide();
        contact_btn.setVisibility(View.VISIBLE);
        muteActionFab.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_mic_white_24dp));
     //   muteActionFab.setBackgroundResource( R.drawable.ic_mic);
//        Resources resources = getBaseContext().getResources();
//        Resources.Theme theme = getBaseContext().getTheme();
//        Drawable drawable = VectorDrawableCompat.create(resources, R.drawable.ic_mic_white_24dp, theme);
//        muteActionFab.setImageDrawable(drawable);
        muteActionFab.setVisibility(View.GONE);

        speacker_action_fab.setVisibility(View.GONE);
        keypad_action_fab.setVisibility(View.GONE);
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
       // unregisterReceiver();
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
                        //creating an intent for the notification
                        Intent intent_to = new Intent(getApplicationContext(), CallHistoryActivity.class);
                        MyNotificationManager myNotificationManager=new MyNotificationManager(context);
                        myNotificationManager.showSmallNotification("9 Digist","Miss call: "+activeCallInvite.getFrom(),intent_to);
                    }
                    if (dialog2 != null && dialog2.isShowing()) {
                        soundPoolManager.stopRinging();
                        dialog2.cancel();
                        Date currentTime = Calendar.getInstance().getTime();
                        Long tsLong = currentTime.getTime();
                        com.digits.business.entities.Log log=new com.digits.business.entities.Log(email,tsLong,0,activeCallInvite.getFrom(),0,activeCallInvite.getFrom());
                        dbHelper.saveLogEntry(log);
                        //creating an intent for the notification
                        Intent intent_to = new Intent(getApplicationContext(), CallHistoryActivity.class);
                        MyNotificationManager myNotificationManager=new MyNotificationManager(context);
                        myNotificationManager.showSmallNotification("9 Digist","Miss call: "+activeCallInvite.getFrom(),intent_to);
                    }
                }
            } else if (intent.getAction().equals(ACTION_FCM_TOKEN)) {
                retrieveAccessToken();
            }
        }
    }




    private void registerReceiver() {
        helper = new PreferenceHelper(context);
        String isReceiverRegistered=helper.getKeyIsReceverRegistered();
        if (isReceiverRegistered.equals(""))
            helper.setKeyIsReceverRegistered("1");

        if (!isReceiverRegistered.equals("1")) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_INCOMING_CALL);
            intentFilter.addAction(ACTION_FCM_TOKEN);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    voiceBroadcastReceiver, intentFilter);
            //isReceiverRegistered = true;
        }
    }

    public static void unregisterReceiver(Context context) {
        helper = new PreferenceHelper(context);
        String isReceiverRegistered=helper.getKeyIsReceverRegistered();
        if (isReceiverRegistered.equals(""))
            helper.setKeyIsReceverRegistered("0");
        if (isReceiverRegistered.equals("1")) {
            helper.setKeyIsReceverRegistered("0");
            LocalBroadcastManager.getInstance(context).unregisterReceiver(voiceBroadcastReceiver);
          //  isReceiverRegistered = false;
        }
    }



    public class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_INCOMING_CALL)) {
                //enable bluetooth when receive call
               // enableBluetooth();
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

                   //    enableBluetooth();
                    checkSecond();

                    if (seconds > 0) {
                    // Place a call
                    twiMLParams.put("to", number.getText().toString());
                    twiMLParams.put("from", helper.getSettingValue_phone());

                    activeCall = Voice.call(VoiceActivity.this, accessToken, twiMLParams, callListener);
                    setCallUI();

                        if (numberBuilder.length() > 0) {
                            numberBuilder.delete(0,numberBuilder.length());
                            num_called=number.getText().toString();
                            number.setText("");

                        }
                    } else {
                        Date currentTime = Calendar.getInstance().getTime();
                        Long tsLong = currentTime.getTime();
                        com.digits.business.entities.Log log=new com.digits.business.entities.Log(email,tsLong,2,num_called,0,num_called);
                        dbHelper.saveLogEntry(log);
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
                    Date currentTime = Calendar.getInstance().getTime();
                    Long tsLong = currentTime.getTime();
                    com.digits.business.entities.Log log=new com.digits.business.entities.Log(email,tsLong,1,activeCallInvite.getFrom(),0,activeCallInvite.getFrom());
                    dbHelper.saveLogEntry(log);
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
        ImageView textDuration;
        ImageView textStatus;

        dialog2 = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog2.setCancelable(false);
        dialog2.setCanceledOnTouchOutside(false);

        dialog2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.incomming_call_dialog_layout);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        textDisplayName = dialog2.findViewById(R.id.textDisplayName);
        buttonHangup = dialog2.findViewById(R.id.buttonHangup);
        buttonAnswer = dialog2.findViewById(R.id.buttonAnswer);
        textDuration = dialog2.findViewById(R.id.textDuration);
        textStatus = dialog2.findViewById(R.id.textStatus);

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

            int elapsedSeconds = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) /1000);
            /*deductSecond(elapsedSeconds);*/
            Date currentTime = Calendar.getInstance().getTime();
            Long tsLong = currentTime.getTime();
            if(activeCall!=null)
            {
                com.digits.business.entities.Log log=new com.digits.business.entities.Log(email,tsLong,2,num_called,elapsedSeconds,num_called);
                dbHelper.saveLogEntry(log);
            }
            else if(activeCallInvite!=null)
            {
                com.digits.business.entities.Log log=new com.digits.business.entities.Log(email,tsLong,1,activeCallInvite.getFrom(),elapsedSeconds,activeCallInvite.getFrom());
                dbHelper.saveLogEntry(log);
            }
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
        switch (requestCode) {
            case REQUEST_ENABLE_BT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Intent enableIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

                } else {
                    // permission denied,! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(coordinatorLayout,
                            "Permission denied for bluetooth. Please allow in your application settings.",
                            SNACKBAR_DURATION).show();
                }
            }
            break;
            case MIC_PERMISSION_REQUEST_CODE: {
                if (permissions.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    retrieveAccessToken();
                  } else {

                    Snackbar.make(coordinatorLayout,
                            "Microphone permissions needed. Please allow in your application settings.",
                            SNACKBAR_DURATION).show();
                    }
                }
            break;
            case CONTACT_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 7);
//                    Snackbar.make(coordinatorLayout,
//                            "Permission Granted, Now your application can access CONTACTS.",
//                            SNACKBAR_DURATION).show();
                } else {

                    Snackbar.make(coordinatorLayout,
                            "Permission Canceled, Now your application cannot access CONTACTS.",
                            SNACKBAR_DURATION).show();
                    }
                break;

        }
    }

    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {

        super.onActivityResult(RequestCode, ResultCode, ResultIntent);

        switch (RequestCode) {

            case (7):
                if (ResultCode == Activity.RESULT_OK) {

                    Uri uri;
                    Cursor cursor1, cursor2;
                    String TempNameHolder, TempNumberHolder, TempContactID, IDresult = "" ;
                    int IDresultHolder ;

                    uri = ResultIntent.getData();

                    cursor1 = getContentResolver().query(uri, null, null, null, null);

                    if (cursor1.moveToFirst()) {

                        TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));

                        IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        IDresultHolder = Integer.valueOf(IDresult) ;

                        if (IDresultHolder == 1) {

                            cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);

                            while (cursor2.moveToNext()) {

                                TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                               // name.setText(TempNameHolder);

                                if (numberBuilder.length() > 0) {
                                    numberBuilder.delete(0,numberBuilder.length());
                                    number.setText("");

                                }
                                number.setText(TempNumberHolder);
                                numberBuilder.append(TempNumberHolder);

                            }
                        }

                    }
                }
                break;
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

        } else if (id == R.id.nav_log) {
            Intent intent = new Intent(VoiceActivity.this, CallHistoryActivity.class);
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
//
//                            Toast.makeText(context, "Your remaining balance is "+ seconds + " seconds",
//                                            Toast.LENGTH_LONG).show();

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
