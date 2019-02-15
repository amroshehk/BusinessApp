package com.firatnet.businessapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_COUNTRY;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_EMAIL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_GENERATED_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_IP_ADDRESS;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PASSWORD;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHONE;
import static com.firatnet.businessapp.classes.URLTAG.REGISTER_URL;

public class RegisterActivity extends AppCompatActivity {

    private String phonenumber,counterName,ip;
    private Context context;
    private TextInputEditText name_et,email_et,pw_signup_et,confpw_signup_et;
    private Button btnSignup;
    private TextView error2;
    private ProgressDialog progressDialog;

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
                if(email.equals("") || pw.equals("")||pw.equals("")||conf_pw.equals(""))
                {
                    error_m=" Please Enter All Filed";
                }
                else if(!isValidEmail(email))
                {
                    error_m="Please Enter Valid Email";
                }
                error2.setText(error_m);
                if(error_m.equals(""))
                {
                    if (StaticMethod.ConnectChecked(context))
                    {
                        Register register=new Register(name,email,phonenumber,counterName,pw,ip);
                        RegisterNewUserServer(register);
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
         String  ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));

        return ip;
    }

    private void RegisterNewUserServer(final Register register) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Register.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<NetworkResponse>() {


                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            progressDialog.dismiss();

                            if(obj.getString("success").equals("success"))
                            {

                                Toast.makeText(getApplicationContext(), obj.getString("message").toUpperCase(), Toast.LENGTH_LONG).show();

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


                    params.put(TAG_NAME, register.getName());
                    params.put(TAG_EMAIL, register.getEmail());
                    params.put(TAG_PASSWORD, register.getPassword());
                    params.put(TAG_PHONE, register.getPhone());
                    params.put(TAG_COUNTRY, register.getCountry());
                    params.put(TAG_IP_ADDRESS, register.getIpaddres());
                    params.put(TAG_GENERATED_ID, "123456789");


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

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }


}
