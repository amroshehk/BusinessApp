package com.digits.business.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

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
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.classes.StaticMethod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.business.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.digits.business.classes.JsonTAG.TAG_ID;
import static com.digits.business.classes.JsonTAG.TAG_MP3_TTS;
import static com.digits.business.classes.JsonTAG.TAG_VM_CALL;
import static com.digits.business.classes.URLTAG.SET_PREFERENC_URL;

public class SettingActivity extends BaseActivity {

    Button saveSetting_btn;
    RadioButton call_rb, vm_rb, tts_rb, mp3_rb;
    String mp3_tts, vm_call;
    Context context;
    private ProgressDialog progressDialog;
    TextView tiltle_base;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        context = this;
        tiltle_base = findViewById(R.id.tiltle_base);
        tiltle_base.setText(getResources().getString(R.string.title_activity_setting));
        saveSetting_btn = findViewById(R.id.saveSetting_btn);
        call_rb = findViewById(R.id.call_rb);
        vm_rb = findViewById(R.id.vm_rb);

        tts_rb = findViewById(R.id.tts_rb);
        mp3_rb = findViewById(R.id.mp3_rb);


        PreferenceHelper helper=new PreferenceHelper(context);
        mp3_tts=helper.getSettingValueMa3Tts();
        vm_call=helper.getSettingValueCallVm();

        if(mp3_tts.equals("")||mp3_tts.equals("mp3"))
            mp3_rb.setChecked(true);
        else
            tts_rb.setChecked(true);

        if(vm_call.equals("")||vm_call.equals("call"))
            call_rb.setChecked(true);
        else
            vm_rb.setChecked(true);



        saveSetting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mp3_rb.isChecked()) {
                    mp3_tts = "mp3";
                } else {
                    mp3_tts = "tts";
                }

                if (call_rb.isChecked()) {
                    vm_call="call";
                } else {
                    vm_call="vm";
                }

                if (StaticMethod.ConnectChecked(context)) {
                    PreferenceHelper helper = new PreferenceHelper(context);
                    SaveSettingServer(helper.getSettingValueId(), mp3_tts, vm_call);
                } else {

                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void SaveSettingServer(final String id,final String mp3_tts,final String vm_call ) {


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Save.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, SET_PREFERENC_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);
                    progressDialog.dismiss();

                    if (obj.getBoolean("success")) {

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        PreferenceHelper helper=new PreferenceHelper(context);
                        helper.setSettingValueMa3Tts(mp3_tts);
                        helper.setSettingValueCallVm(vm_call);


                    } else  {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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
                params.put(TAG_ID, id);
                params.put(TAG_MP3_TTS, mp3_tts);
                params.put(TAG_VM_CALL, vm_call);

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
