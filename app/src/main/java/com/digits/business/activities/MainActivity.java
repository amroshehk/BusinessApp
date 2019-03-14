package com.digits.business.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
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
import com.digits.business.adapter.BusinessAdapter;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.classes.StaticMethod;
import com.digits.business.entities.Business;
import com.digits.business.twilio.VoiceActivity;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.digits.business.classes.JsonTAG.BUSINESS_ADDRESS;
import static com.digits.business.classes.JsonTAG.BUSINESS_CITY;
import static com.digits.business.classes.JsonTAG.BUSINESS_COUNTRY;
import static com.digits.business.classes.JsonTAG.BUSINESS_EMPLOYESS_NUMBER;
import static com.digits.business.classes.JsonTAG.BUSINESS_KEYWORDS;
import static com.digits.business.classes.JsonTAG.BUSINESS_NAME;
import static com.digits.business.classes.JsonTAG.BUSINESS_PARTNERSHIP_TYPE;
import static com.digits.business.classes.JsonTAG.BUSINESS_PIN_CODE;
import static com.digits.business.classes.JsonTAG.BUSINESS_PRODUCTS;
import static com.digits.business.classes.JsonTAG.BUSINESS_TURNOVER;
import static com.digits.business.classes.JsonTAG.BUSINESS_TYPE;
import static com.digits.business.classes.JsonTAG.BUSINESS_YEAR_ESTABLISHED;
import static com.digits.business.classes.JsonTAG.TAG_DATA;
import static com.digits.business.classes.JsonTAG.TAG_EMAIL;
import static com.digits.business.classes.URLTAG.LOGOUT_URL;
import static com.digits.business.classes.URLTAG.URL_RECENT_BUSINESS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static JSONArray businessArray = null;
    private ProgressBar CircularProgress;
    private TextView no_business;
    private ProgressDialog progressDialog;
    private Context context;
    private String name,email;
    private CircleImageView photo_nav_header;
    private TextView name_tv,email_tv;

    private static final String TAG = "MainActivity";
    //keys
    private static final String SETTING_KEY_PUSH = "com.digits.business.SETTING_KEY_PUSH";
    private static final String SETTING_KEY_SOUND = "com.digits.business.SETTING_KEY_SOUND";
    private static final String SETTING_KEY_VIBRATE = "com.digits.business.SETTING_KEY_VIBRATE";

    //getString
    private   String SETTING_PUSH;
    private   String SETTING_SOUND;
    private   String SETTING_VIBRATE;
    private ProgressDialog progressDialog2;


    ImageLoaderConfiguration config;

    private ArrayList<Business> businesses;

    private RecyclerView businessRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    Dialog dialog;
    Button cancel;
    Button ensure;
    Button eval;

    private FloatingActionMenu searchMenu;
    private com.github.clans.fab.FloatingActionButton searchItem;
    private com.github.clans.fab.FloatingActionButton addItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CircularProgress = findViewById(R.id.progressbar_mp3file);
        no_business = findViewById(R.id.no_business);
        searchMenu = findViewById(R.id.menu);
        searchItem = findViewById(R.id.searchItem);
        addItem = findViewById(R.id.addItem);

        context = this;
        no_business.setVisibility(View.GONE);

//        check login data expired
        Long tsLong = System.currentTimeMillis()/1000;

        PreferenceHelper helper = new PreferenceHelper(context);
        email= helper.getSettingValueEmail();
        name= helper.getSettingValueName();

        Long validdate=Long.parseLong(helper.getSettingValueLoginDataExpired());
        if( validdate>tsLong) {
          //  Toast.makeText(getApplicationContext(),"Your Login Valid Till : "+getDate(validdate) , Toast.LENGTH_LONG).show();
        }
        else
        {
            LogoutServer(email);
            Toast.makeText(getApplicationContext(), "You must login again to renew the validity", Toast.LENGTH_LONG).show();
        }




        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SaveBusinessActivity.class);
                startActivity(intent);
            }
        });


        searchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchForBusinessActivity.class);
                startActivity(intent);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });




        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        photo_nav_header= hView.findViewById(R.id.photo);
        name_tv=hView.findViewById(R.id.name_tv);
        email_tv=hView.findViewById(R.id.email_tv);

        name_tv.setText(name);
        email_tv.setText(email);

        getUserPhoto();

        hView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
               // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        businesses = new ArrayList<>();

        businessRecyclerView = findViewById(R.id.businessRecycleView);
        layoutManager = new LinearLayoutManager(context);

        if (StaticMethod.ConnectChecked(context)) {
            getRecentBusiness();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
            // custom dialog
            dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.exit_dialog_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


            cancel = dialog.findViewById(R.id.cancel);
            ensure =  dialog.findViewById(R.id.ensure);
            eval = dialog.findViewById(R.id.eval);

            // if button is clicked, close the custom dialog
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            // if button is clicked, close the custom dialog
            ensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    finishAffinity();


                }
            });

            // if button is clicked, close the custom dialog
            eval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        dialog.dismiss();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        dialog.dismiss();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    }

                }
            });

            //dialog.show();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        getUserPhoto();
//        businesses.clear();
//        getRecentBusiness();
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
        PreferenceHelper helper = new PreferenceHelper(context);
        if (!helper.getSettingValuePhotoUrl().isEmpty())
            imageLoader.displayImage(helper.getSettingValuePhotoUrl(), photo_nav_header, options);
        else
            photo_nav_header.setBackgroundResource(R.drawable.user512);

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (id == R.id.nav_home) {


        }
        else if (id == R.id.nav_mp3) {

            Intent intent = new Intent(MainActivity.this, VoiceEmailActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_mp3_2) {

            Intent intent = new Intent(MainActivity.this, UploadGreetingActivity2.class);
            startActivity(intent);

        }else if (id == R.id.nav_voice) {

            Intent intent = new Intent(MainActivity.this, VoiceActivity.class);
            /*intent.putExtra(TAG_GENERATED_ID, generated_id);*/
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        } else if (id == R.id.nav_TTS) {
            Intent intent = new Intent(MainActivity.this, SaveTTSActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_aboutus) {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);


        }  else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        } else if (id == R.id.nav_send) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
            catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
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

                        PreferenceHelper helper=new PreferenceHelper(context);
                        helper.setLoginState(false);
                        helper.deleteUser();

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
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



    private void getRecentBusiness() {
        CircularProgress.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_RECENT_BUSINESS,

            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("success")) {

                            businessArray = obj.getJSONArray(TAG_DATA);

                            for (int i = 0; i < businessArray.length(); i++) {

                                JSONObject jsonObject = businessArray.getJSONObject(i);

                                Business business = new Business();

                                business.setBusinessName(jsonObject.getString(BUSINESS_NAME));
                                business.setBusinessType(jsonObject.getString(BUSINESS_TYPE));
                                business.setPartnershipType(jsonObject.getString(BUSINESS_PARTNERSHIP_TYPE));
                                business.setYearEstablished(jsonObject.getString(BUSINESS_YEAR_ESTABLISHED));
                                business.setEmployeesNumber(jsonObject.getString(BUSINESS_EMPLOYESS_NUMBER));
                                business.setTurnover(jsonObject.getString(BUSINESS_TURNOVER));
                                business.setAddress(jsonObject.getString(BUSINESS_ADDRESS));
                                business.setCountry(jsonObject.getString(BUSINESS_COUNTRY));
                                business.setCity(jsonObject.getString(BUSINESS_CITY));
                                business.setPinCode(jsonObject.getString(BUSINESS_PIN_CODE));
                                business.setProducts(jsonObject.getString(BUSINESS_PRODUCTS));
                                business.setKeywords(jsonObject.getString(BUSINESS_KEYWORDS));

                                businesses.add(business);

                            }



                            no_business.setVisibility(View.GONE);
                            CircularProgress.setVisibility(View.GONE);
                            businessRecyclerView.setLayoutManager(layoutManager);
                            adapter = new BusinessAdapter(context, businesses);
                            businessRecyclerView.setAdapter(adapter);
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            no_business.setVisibility(View.VISIBLE);
                            CircularProgress.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        CircularProgress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "error JSONException", Toast.LENGTH_SHORT).show();
                    }

                }
            },

            new Response.ErrorListener() {
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
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }

//                    String responseBody = new String(volleyError.networkResponse.data, "utf-8");
//                    JSONObject jsonObject = new JSONObject(responseBody);
                    CircularProgress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                }
            } )


        {
            @Override
            protected Map<String, String> getParams() {
                return null;
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
