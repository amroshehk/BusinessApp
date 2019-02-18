package com.firatnet.businessapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.classes.StaticMethod;
import com.firatnet.businessapp.entities.Register;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_COUNTRY;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_EMAIL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_IP_ADDRESS;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NEW_EMAIL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NEW_PASSWORD;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NEW_PHONE;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PASSWORD;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHONE;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHOTO_URL;
import static com.firatnet.businessapp.classes.URLTAG.REGISTER_URL;
import static com.firatnet.businessapp.classes.URLTAG.UPDATE_USER_URL;

public class EditMyProfileActivity extends AppCompatActivity {
    private TextInputEditText name_et, phone_et, email_et, old_pw_signup_et, new_pw_et, conf_new_pw_et;
    private Context context;
    private TextView error2;
    private ProgressDialog progressDialog;
    private Button edit_btn;
    Dialog dialog;
    Button cancel;
    Button ensure;
    String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);

        name_et = findViewById(R.id.name_et);
        phone_et = findViewById(R.id.phone_et);
        email_et = findViewById(R.id.email_et);
        old_pw_signup_et = findViewById(R.id.old_pw_signup_et);
        new_pw_et = findViewById(R.id.new_pw_et);
        conf_new_pw_et = findViewById(R.id.conf_new_pw_et);
        edit_btn = findViewById(R.id.edit_btn);
        error2 = findViewById(R.id.error2);
        context=this;


        final PreferenceHelper helper = new PreferenceHelper(context);

        name_et.setText(helper.getSettingValueName());
        phone_et.setText(helper.getSettingValue_phone());
        email_et.setText(helper.getSettingValueEmail());
        id=helper.getSettingValueId();

        ActivityCompat.requestPermissions(EditMyProfileActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = name_et.getText().toString();
                String email = email_et.getText().toString();
                String phone = phone_et.getText().toString();
                final String old_pw = old_pw_signup_et.getText().toString();
                String new_pw = new_pw_et.getText().toString();
                String conf_new_pw = conf_new_pw_et.getText().toString();

                String error_m = "";
                if (name.equals("") || email.equals("") || phone.equals("") || old_pw.equals("") ) {
                    error_m = " Please Enter All Filed *";
                } else if (!isValidEmail(email)) {
                    error_m = "Please Enter Valid Email";
                } else if (!new_pw.equals(conf_new_pw)) {
                    error_m = "Password does not match Confirm New Password";
                }

                if(new_pw.equals("") && conf_new_pw.equals(""))
                {
                    new_pw="";
                }

                if(name.equals(helper.getSettingValueName()))
                name="";
                if(email.equals(helper.getSettingValueEmail()))
                    email="";
                if(phone.equals(helper.getSettingValue_phone()))
                    phone="";

                final Register register = new Register(id,name, email, phone, new_pw);


                error2.setText(error_m);
                if (error_m.equals("")) {
                    if (StaticMethod.ConnectChecked(context)) {


                        // custom dialog
                        dialog = new Dialog(context);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.myprofile_dialog_layout);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                        cancel = (Button) dialog.findViewById(R.id.cancel);
                        ensure = (Button) dialog.findViewById(R.id.ensure);
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


                                if (StaticMethod.ConnectChecked(context)) {
                                   // ensure.setEnabled(false);


                                    EditUserServer(register,old_pw);

                                } else {
                                    dialog.dismiss();

                                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                        dialog.show();


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




    private void EditUserServer(final Register register,final String old_pw) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Edit.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        StringRequest request = new StringRequest( Request.Method.POST, UPDATE_USER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    progressDialog.dismiss();

                    if(obj.getBoolean("success"))
                    {

                        Toast.makeText(getApplicationContext(), obj.getString("message").toUpperCase(), Toast.LENGTH_LONG).show();
                         //save new data in shared
                        PreferenceHelper  helper=new PreferenceHelper(context);
                        if(register.getName().equals(""))
                            register.setName(helper.getSettingValueName());
                        if(register.getEmail().equals(""))
                            register.setEmail(helper.getSettingValueEmail());
                        if(register.getPhone().equals(""))
                            register.setPhone(helper.getSettingValue_phone());
                        helper.editUser(register);

                        //relanch application
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);


                    }
                    if(!obj.getBoolean("success")  )
                    {

                        Toast.makeText(getApplicationContext(),obj.getString("message").toUpperCase(), Toast.LENGTH_LONG).show();
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

                        try {

                            String responseBody = new String( error.networkResponse.data, "utf-8" );
                            JSONObject jsonObject = new JSONObject( responseBody );
                            Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        } catch ( JSONException e ) {
                            //Handle a malformed json response
                        } catch (UnsupportedEncodingException error2){

                        }
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

                params.put("Content-Type", "application/json; charset=utf-8");
                params.put(TAG_ID, register.getId());
                if(!register.getName().equals(""))
                params.put(TAG_NAME, register.getName());
                if(!register.getEmail().equals(""))
                params.put(TAG_NEW_EMAIL,  register.getEmail());
                if(!register.getPhone().equals(""))
                params.put(TAG_NEW_PHONE, register.getPhone());
                if(!register.getPassword().equals(""))
                params.put(TAG_NEW_PASSWORD, register.getPassword());
                params.put(TAG_PASSWORD, old_pw);
              //  params.put(TAG_PHOTO_URL, "no url");
                return params;

            }


        };

        //adding the request to volley
        Volley.newRequestQueue(context).add(request);
    }

}
