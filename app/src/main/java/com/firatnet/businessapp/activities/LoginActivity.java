package com.firatnet.businessapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.classes.StaticMethod;
import com.firatnet.businessapp.classes.VolleyMultipartRequest;
import com.firatnet.businessapp.entities.Register;
import com.firatnet.businessapp.phoneauth.ProfileActivity;
import com.firatnet.businessapp.phoneauth.VerifyPhoneActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_EMAIL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PASSWORD;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_RESULTS;
import static com.firatnet.businessapp.classes.URLTAG.LOGIN_URL;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email_et,pw_signin_et;
    private Button btnSignin;
    private TextView error;
    private Context context;
    private ProgressDialog progressDialog;
    private static JSONArray jsonArray = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email_et=findViewById(R.id.email_et);
        pw_signin_et=findViewById(R.id.pw_signin_et);
        btnSignin=findViewById(R.id.btnSignin);
        error=findViewById(R.id.error);
        context=this;



        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=email_et.getText().toString();
                String pw=pw_signin_et.getText().toString();
                String error_m="";

                if(email.equals(""))
                    error_m="Please Enter Email ";
                else if(pw.equals(""))
                    error_m="Please Enter Password";
                else if(!isValidEmail(email))
                {
                    error_m="Please Enter Valid Email";
                }

                error.setText(error_m);

                if(error_m.equals(""))
                {
                    if (StaticMethod.ConnectChecked(context))
                    {

                        LoginServer(email,pw);
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


    private void LoginServer(final String email, final String pw) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        JsonObjectRequest stringRequest = new JsonObjectRequest (Request.Method.POST, LOGIN_URL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_SHORT).show();
            }

//            @Override
//                    public void onResponse(String response) {
//
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            progressDialog.dismiss();
//
//                            if(obj.getString("message").equals("User logged in successfully"))
//                            {
//
//
//
////                                jsonArray=  obj.getJSONArray(TAG_RESULTS);
////                                for (int i = 0; i < jsonArray.length(); i++) {
////                                    JSONObject c = jsonArray.getJSONObject(i);
//
////                                    String email = c.getString(TAG_EMAIL);
////                                    String password = c.getString(TAG_PASSWORD);
////
////
////                                    String  verify_note  = c.getString(TAG_VERIFY_NOTE);
////                                    String  blocked      = c.getString(TAG_BLOCKDE);
////                                    String  blocked_note = c.getString(TAG_BLOCKDE_NOTE);
////                                    String  code         = c.getString(TAG_CODE);
////                                    String  xlocation         = c.getString(TAG_XLOCATION);
////                                    String  ylocation         = c.getString(TAG_YLOCATION);
////
////
////                                    student_signin=new Student(idstudent,imageurl,email,password,universityid,fname,lname,father,college,specialization,
////                                            year,gender,birthday,profession,address,mobile,trusted,verify_imageurl,verify_note,blocked,blocked_note,code,xlocation,ylocation);
//
//
////                                }
//
//
//                                Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_SHORT).show();
//
//                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
////
//
//                            }
//                            else if(obj.getString("message").equals("Incorrect email or password")) {
//
//                                Toast.makeText(getApplicationContext(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
//                                error.setText("Incorrect email or password");
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        System.out.println("volley Error ................."+error);
                    }
                }) {

//            @Override
//            public byte[] getBody() {
//
//                String requestBody = TAG_EMAIL+"="+email+"&"+TAG_PASSWORD+"="+pw;  //The request body goes in here.
//
//
//                try {
//                    return requestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                    return null;
//                }
//            }

//            @Override
//            public byte[] getBody() {
//                Map<String,String> params = new HashMap<>();
//                params.put(TAG_EMAIL, email);
//                params.put(TAG_PASSWORD,pw);
//                String postBody = createPostBody(params);
//                return postBody.getBytes();
//            }

//            @Override
//            public byte[] getBody() {
//                //return super.getBody();
//                String httpPostBody=TAG_EMAIL+"="+email+"&"+TAG_PASSWORD+"="+pw;
//                // usually you'd have a field with some values you'd want to escape, you need to do it yourself if overriding getBody. here's how you do it
//                try {
//                    httpPostBody=httpPostBody+"&randomFieldFilledWithAwkwardCharacters="+ URLEncoder.encode("{{%stuffToBe Escaped/","UTF-8");
//                } catch (UnsupportedEncodingException exception) {
//                    Log.e("ERROR", "exception", exception);
//                    // return null and don't pass any POST string if you encounter encoding error
//                    return null;
//                }
//                return httpPostBody.getBytes();
//            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(TAG_EMAIL, email);
                params.put(TAG_PASSWORD,pw);

                return params;
            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    public static final String BOUNDARY = "ANY_STRING";

//    private String createPostBody(Map<String, String> params) {
//        StringBuilder sb = new StringBuilder();
//        for (String key : params.keySet()) {
//            if (params.get(key) != null) {
//                sb.append("\r\n" + "--" + BOUNDARY + "\r\n");
//                sb.append("Content-Disposition: form-data; name=\""
//                        + key + "\"" + "\r\n\r\n");
//                sb.append(params.get(key));
//            }
//        }
//
//        return sb.toString();
//    }

}
