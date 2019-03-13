package com.digits.business.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
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
import com.digits.business.classes.StaticMethod;
import com.digits.business.entities.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.digits.business.classes.JsonTAG.TAG_API_TOKEN;
import static com.digits.business.classes.JsonTAG.TAG_COUNTRY;
import static com.digits.business.classes.JsonTAG.TAG_EMAIL;
import static com.digits.business.classes.JsonTAG.TAG_IP_ADDRESS;
import static com.digits.business.classes.JsonTAG.TAG_NAME;
import static com.digits.business.classes.JsonTAG.TAG_PASSWORD;
import static com.digits.business.classes.JsonTAG.TAG_PHONE;
import static com.digits.business.classes.JsonTAG.TAG_PHOTO_URL;
import static com.digits.business.classes.URLTAG.REGISTER_URL;

public class RegisterActivity extends AppCompatActivity {

    private String phonenumber,counterName,ip;
    private Context context;
    private TextInputEditText name_et,email_et,pw_signup_et,confpw_signup_et;
    private Button btnSignup;
    private TextView error2;
    private ProgressDialog progressDialog;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name_et=findViewById(R.id.name_et);
        email_et=findViewById(R.id.email_et);
        pw_signup_et=findViewById(R.id.pw_signup_et);
        confpw_signup_et=findViewById(R.id.confpw_signup_et);
        btnSignup=findViewById(R.id.btnSignup);
        error2=findViewById(R.id.error2);

        context=this;

        //get intent Extra
        phonenumber = getIntent().getStringExtra("phonenumber");
        counterName = getIntent().getStringExtra("counterName");

        //get Ip Address
        ip=getIpAddress();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name= name_et.getText().toString();
                String email= email_et.getText().toString();
                String pw=  pw_signup_et.getText().toString();
                String conf_pw=  confpw_signup_et.getText().toString();

                String error_m="";
                if(email.equals("") || name.equals("")||pw.equals("")||conf_pw.equals(""))
                {
                    error_m=" Please Enter All Filed";
                }
                else if(!isValidEmail(email))
                {
                    error_m="Please Enter Valid Email";
                }
                else if(!pw.equals(conf_pw))
                {
                    error_m="Password does not match Confirm Password";
                }
                error2.setText(error_m);
                if(error_m.equals(""))
                {

                    if (StaticMethod.ConnectChecked(context))
                    {
                        final Register register=new Register(name,email,phonenumber,counterName,ip,pw);
                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w( "RegisterActivity", "getInstanceId failed", task.getException());
                                            return;
                                        }

                                        // Get new Instance ID token
                                        token = task.getResult().getToken();

                                        // Log and toast
                                        String msg = getString(R.string.msg_token_fmt, token);
                                        Log.d("RegisterActivity", msg);
                                       // Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                                        RegisterNewUserServer(register,token);
                                    }
                                });

                    } else {

                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });


    }
    public  boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public String getIpAddress()
    {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));


    }

    private void RegisterNewUserServer(final Register register, final String token) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Register.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        StringRequest request = new StringRequest( Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            progressDialog.dismiss();

                            if(obj.getBoolean("success")) {

                        //        Toast.makeText(getApplicationContext(), obj.getString("message").toUpperCase(), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("email", register.getEmail());
                                intent.putExtra("password", register.getPassword());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                         //       Toast.makeText(getApplicationContext(),obj.getString("message").toUpperCase(), Toast.LENGTH_LONG).show();
//                                Toast.makeText(getApplicationContext(),"The phone has already been taken", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                    }
                }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded; charset=UTF-8";
//            }

//            @Override
//            protected String getParamsEncoding() {
//                return "utf-8";
//            }

            //            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> header = new HashMap<String, String>();
//                header.put("Content-Type", "application/json; charset=utf-8");
//                return header;
//            }
            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                    params.put("Content-Type", "application/json; charset=utf-8");
                    params.put(TAG_NAME, register.getName());
                    params.put(TAG_EMAIL, register.getEmail());
                    params.put(TAG_PHONE, register.getPhone());
                    params.put(TAG_COUNTRY, register.getCountry());
                    params.put(TAG_PASSWORD, register.getPassword());
                    params.put(TAG_IP_ADDRESS, register.getIp());
                    params.put(TAG_PHOTO_URL, "");
                    params.put(TAG_API_TOKEN, token);


                return params;

            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                long imagename = System.currentTimeMillis();
//                params.put(TAG_PIC, new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
//                return params;
//            }
        };

        //adding the request to volley

        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


}
