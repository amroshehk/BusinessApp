package com.firatnet.businessapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firatnet.businessapp.adapter.RecyclerMP3FileCardAdapter;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.classes.StaticMethod;
import com.firatnet.businessapp.entities.Mp3File;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firatnet.businessapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_CREATED_AT;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_DATA;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_MP3_TTS;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_Query;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_UPDATED_AT;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_URL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_USER_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_VM_CALL;
import static com.firatnet.businessapp.classes.URLTAG.SEARCH_MP3;

public class SettingActivity extends AppCompatActivity {

    Button saveSetting_btn;
    RadioButton call_rb, vm_rb, tts_rb, mp3_rb;
    String mp3_tts, vm_call;
    Context context;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context=this;

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
                    PreferenceHelper helper=new PreferenceHelper(context);
                    SaveSettingServer(helper.getSettingValueId(),mp3_tts,vm_call);
                } else {

                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }


    private void SaveSettingServer(final String id,final String mp3_tts,final String vm_call ) {


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Save.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, SEARCH_MP3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    progressDialog.dismiss();
                    if (obj.getBoolean("success")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

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
                try {
                    String responseBody = new String(volleyError.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);

                    Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    //Handle a malformed json response
                    progressDialog.dismiss();
                } catch (UnsupportedEncodingException error) {
                    progressDialog.dismiss();
                }
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
        requestQueue.add(request);

    }


}
