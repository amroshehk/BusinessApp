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
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.business.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.digits.business.classes.JsonTAG.TAG_DATA;
import static com.digits.business.classes.JsonTAG.TAG_ID;
import static com.digits.business.classes.JsonTAG.TAG_TTS_TEXT;
import static com.digits.business.classes.URLTAG.GET_TTS_URL;
import static com.digits.business.classes.URLTAG.SAVE_TTS_URL;

public class SaveTTSActivity extends BaseActivity {
    Context context;
    private ProgressDialog progressDialog;
    TextInputEditText mess_text;
    Button add;
    TextView tiltle_base;
    PreferenceHelper helper;

    private static JSONArray ttsArray = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_tts);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar_base);

        context = this;
        mess_text = findViewById(R.id.mess_text);
        add = findViewById(R.id.add);
        tiltle_base = findViewById(R.id.tiltle_base);
        tiltle_base.setText(getResources().getString(R.string.title_activity_save_tts));

        helper = new PreferenceHelper(context);

        if( helper.getSettingValueTtsMessage().equals(""))
            if (StaticMethod.ConnectChecked(context)) {
                getTTSServer(helper.getSettingValueId());
            } else {

                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

        else
            mess_text.setText(helper.getSettingValueTtsMessage());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StaticMethod.ConnectChecked(context)) {
                    String message = mess_text.getText().toString();

                    if (!message.equals("")) {

                        SaveTTSServer(helper.getSettingValueId(), message);
                    } else {

                        Snackbar.make(mess_text, "Please enter your TTS.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void SaveTTSServer(final String id, final String tts) {


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Save TTS.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, SAVE_TTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    progressDialog.dismiss();
                    if (obj.getBoolean("success")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        helper.setSettingValueTtsMessage(tts);

                    } else {
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
                params.put(TAG_ID, id);
                params.put(TAG_TTS_TEXT, tts);


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

    private void getTTSServer(final String id) {


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Refresh TTS.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, GET_TTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    progressDialog.dismiss();
                    if (obj.getBoolean("success")) {

                        ttsArray = obj.getJSONArray(TAG_DATA);

                        JSONObject jsonObject = ttsArray.getJSONObject(0);
                        if(jsonObject!=null)
                        {
                            helper.setSettingValueTtsMessage(jsonObject.getString(TAG_TTS_TEXT));
                            mess_text.setText(jsonObject.getString(TAG_TTS_TEXT));
                        }
                        else

                            mess_text.setText(getResources().getString(R.string.default_tts));

                    } else {
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
                params.put(TAG_ID, id);
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
