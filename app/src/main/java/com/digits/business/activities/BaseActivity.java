package com.digits.business.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
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
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.twilio.VoiceActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.digits.business.classes.JsonTAG.TAG_EMAIL;
import static com.digits.business.classes.URLTAG.LOGOUT_URL;



public class BaseActivity extends AppCompatActivity {

    public Toolbar toolbar;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle drawerToggle;
    public NavigationView navigationView;
    public Context mContext;

    private ProgressDialog progressDialog;

    private String email,name;
    private CircleImageView photo_nav_header;
    ImageLoaderConfiguration config;

    private  String titile;

    public NavigationView getNavigationView() {
        return navigationView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = BaseActivity.this;
        setContentView(R.layout.base_activity);

        PreferenceHelper helper = new PreferenceHelper(mContext);
        email= helper.getSettingValueEmail();
        name = helper.getSettingValueName();

    }

    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullView = (DrawerLayout)getLayoutInflater().inflate(R.layout.base_activity, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        initToolbar();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_base);
        setSupportActionBar(toolbar);
    }

    private void setUpNav() {
        drawerLayout =  findViewById(R.id.activity_container);
        drawerToggle = new ActionBarDrawerToggle(BaseActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = findViewById(R.id.nav_view);

        View hView =  navigationView.getHeaderView(0);
        photo_nav_header= hView.findViewById(R.id.photo);
        TextView name_tv = hView.findViewById(R.id.name_tv);
        TextView email_tv = hView.findViewById(R.id.email_tv);

        name_tv.setText(name);
        email_tv.setText(email);
        getUserPhoto();

        hView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Setting Navigation View Item Selected Listener to handle the item
        // click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(MenuItem menuItem) {

                // Checking if the item is in checked state or not, if not make
                // it in checked state
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                // Closing drawer on item click
                drawerLayout.closeDrawers();

                // Check to see which item was being clicked and perform
                // appropriate action

//                Intent intent;
//                switch (menuItem.getItemId()) {
//                    case R.id.xxxx:
//                        return true;
//                }
                int id = menuItem.getItemId();
                if (id == R.id.nav_home) {
                    Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }
                else if (id == R.id.nav_mp3) {

                    Intent intent = new Intent(BaseActivity.this, VoiceMailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else if (id == R.id.nav_mp3_2) {

                    Intent intent = new Intent(BaseActivity.this, UploadGreetingActivity2.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }else if (id == R.id.nav_voice) {

                    Intent intent = new Intent(BaseActivity.this, VoiceActivity.class);
                    /*intent.putExtra(TAG_GENERATED_ID, generated_id);*/
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                } else if (id == R.id.nav_TTS) {
                    Intent intent = new Intent(BaseActivity.this, SaveTTSActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (id == R.id.nav_log) {
                    Intent intent = new Intent(BaseActivity.this, CallHistoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (id == R.id.nav_aboutus) {
                    Intent intent = new Intent(BaseActivity.this, AboutUsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }  else if (id == R.id.nav_manage) {
                    Intent intent = new Intent(BaseActivity.this, SettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

                return false;
            }
        });

        // Setting the actionbarToggle to drawer layout

        // calling sync state is necessay or else your hamburger icon wont show
        // up
        drawerToggle.syncState();

    }

    void getUserPhoto() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        config = new ImageLoaderConfiguration.Builder(mContext)
                .build();
        ImageLoader.getInstance().init(config);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (!helper.getSettingValuePhotoUrl().isEmpty())
            imageLoader.displayImage(helper.getSettingValuePhotoUrl(), photo_nav_header, options);
        else
            photo_nav_header.setBackgroundResource(R.drawable.user512);

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpNav();

        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.nav_home) {

            Intent intent = new Intent(BaseActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.nav_mp3) {

            Intent intent = new Intent(BaseActivity.this, VoiceMailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        else if (id == R.id.nav_mp3_2) {

            Intent intent = new Intent(BaseActivity.this, UploadGreetingActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else if (id == R.id.nav_voice) {

            Intent intent = new Intent(BaseActivity.this, VoiceActivity.class);
            /*intent.putExtra(TAG_GENERATED_ID, generated_id);*/
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        } else if (id == R.id.nav_TTS) {
            Intent intent = new Intent(BaseActivity.this, SaveTTSActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_log) {
            Intent intent = new Intent(BaseActivity.this, CallHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_aboutus) {
            Intent intent = new Intent(BaseActivity.this, AboutUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        }  else if (id == R.id.nav_manage) {
            Intent intent = new Intent(BaseActivity.this, SettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        return super.onOptionsItemSelected(item);
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

                        PreferenceHelper helper=new PreferenceHelper(mContext);
                        helper.setLoginState(false);
                        helper.deleteUser();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                    } else  {
                        //Toast.makeText(getApplicationContext(), "User logged out Not successfully", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    //e.printStackTrace();
                    progressDialog.dismiss();
                    //Toast.makeText(getApplicationContext(), "error JSONException", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//        requestQueue.getCache().clear();
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

}

