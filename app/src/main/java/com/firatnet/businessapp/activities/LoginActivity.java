package com.firatnet.businessapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.classes.StaticMethod;
import com.firatnet.businessapp.classes.VolleyMultipartRequest;
import com.firatnet.businessapp.entities.Register;
import com.firatnet.businessapp.phoneauth.VerifyPhoneActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<NetworkResponse>() {


                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            progressDialog.dismiss();
                            Register register=new Register();
                            if(obj.getString("message").equals("User logged in successfully"))
                            {



//                                jsonArray=  obj.getJSONArray(TAG_RESULTS);
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject c = jsonArray.getJSONObject(i);
//
//                                    String email = c.getString(TAG_EMAIL);
//                                    String password = c.getString(TAG_PASSWORD);

//
//                                    String  verify_note  = c.getString(TAG_VERIFY_NOTE);
//                                    String  blocked      = c.getString(TAG_BLOCKDE);
//                                    String  blocked_note = c.getString(TAG_BLOCKDE_NOTE);
//                                    String  code         = c.getString(TAG_CODE);
//                                    String  xlocation         = c.getString(TAG_XLOCATION);
//                                    String  ylocation         = c.getString(TAG_YLOCATION);
//
//
//                                    student_signin=new Student(idstudent,imageurl,email,password,universityid,fname,lname,father,college,specialization,
//                                            year,gender,birthday,profession,address,mobile,trusted,verify_imageurl,verify_note,blocked,blocked_note,code,xlocation,ylocation);


//                                }
                                Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);






                            }
                            else if(obj.getString("message").equals("Incorrect email or password")) {

                                Toast.makeText(getApplicationContext(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
                                error.setText("Incorrect email or password");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(TAG_EMAIL, email);
                params.put(TAG_PASSWORD,pw);

                return params;

            }


        };

        //adding the request to volley

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }



}
