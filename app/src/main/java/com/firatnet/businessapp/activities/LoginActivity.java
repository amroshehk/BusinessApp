package com.firatnet.businessapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.classes.StaticMethod;
import com.firatnet.businessapp.entities.Register;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_COUNTRY;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_CREATED_AT;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_DATA;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_DEFAULT_FILE;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_EMAIL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_GENERATED_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_IP;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PASSWORD;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHONE;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHOTO_URL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_STATUS;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_UPDATED_AT;
import static com.firatnet.businessapp.classes.URLTAG.LOGIN_URL;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email_et, pw_signin_et;
    private Button btnSignin;
    private TextView error;
    private Context context;
    private ProgressDialog progressDialog;
    private static JSONArray jsonArray = null;
    private String email, password;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ads------here----------
        mAdView = findViewById(R.id.adView);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        email_et = findViewById(R.id.email_et);
        pw_signin_et = findViewById(R.id.pw_signin_et);
        btnSignin = findViewById(R.id.btnSignin);
        error = findViewById(R.id.error);
        context = this;
        try {
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
            email_et.setText(email);
            pw_signin_et.setText(password);
        } catch (Exception e) {

        }


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_et.getText().toString();
                String pw = pw_signin_et.getText().toString();
                String error_m = "";

                if (email.equals(""))
                    error_m = "Please Enter Email ";
                else if (pw.equals(""))
                    error_m = "Please Enter Password";
                else if (!isValidEmail(email)) {
                    error_m = "Please Enter Valid Email";
                }

                error.setText(error_m);

                if (error_m.equals("")) {
                    if (StaticMethod.ConnectChecked(context)) {
                        LoginServer(email, pw);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    private void LoginServer(final String email, final String pw) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    progressDialog.dismiss();
                    if (obj.getString("message").equals("User logged in successfully")) {


                        obj = obj.getJSONObject(TAG_DATA);


                        String id = obj.getString(TAG_ID);
                        String name = obj.getString(TAG_NAME);
                        String email = obj.getString(TAG_EMAIL);

                        String created_at = obj.getString(TAG_CREATED_AT);
                        String updated_at = obj.getString(TAG_UPDATED_AT);


                        String phone = obj.getString(TAG_PHONE);
                        String country = obj.getString(TAG_COUNTRY);

                        String generated_id = obj.getString(TAG_GENERATED_ID);
                        String status = obj.getString(TAG_STATUS);
                        String photo_url = obj.getString(TAG_PHOTO_URL);

                        String ip = obj.getString(TAG_IP);

                        String default_file = "";
                        if (obj.getString(TAG_DEFAULT_FILE) != null)
                            default_file = obj.getString(TAG_DEFAULT_FILE);

                        Register register=new Register(id,name,email,created_at,updated_at,phone,country,generated_id,status,photo_url,ip,default_file);


                        PreferenceHelper helper=new PreferenceHelper(context);
                        helper.setLoginState(true);
                        helper.saveUser(register);

                        //30 day
                        Long tsLong = System.currentTimeMillis()/1000;//time stamp
                        long cuuentTimeexpired=tsLong+(30*24*60*60)+1;//
                        helper.setSettingValueLoginDataExpired(Long.toString(cuuentTimeexpired));

                        Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, VoiceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    } else if (obj.getString("message").equals("Incorrect email or password")) {
                        error.setText("Incorrect email or password");
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
                params.put(TAG_PASSWORD, pw);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        requestQueue.add(request);

    }

}
