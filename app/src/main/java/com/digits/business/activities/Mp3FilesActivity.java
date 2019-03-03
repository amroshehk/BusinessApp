package com.digits.business.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.digits.business.adapter.RecyclerMP3FileCardAdapter;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.classes.StaticMethod;
import com.digits.business.entities.Mp3File;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.digits.business.classes.JsonTAG.TAG_CREATED_AT;
import static com.digits.business.classes.JsonTAG.TAG_DATA;
import static com.digits.business.classes.JsonTAG.TAG_ID;
import static com.digits.business.classes.JsonTAG.TAG_NAME;
import static com.digits.business.classes.JsonTAG.TAG_UPDATED_AT;
import static com.digits.business.classes.JsonTAG.TAG_URL;
import static com.digits.business.classes.JsonTAG.TAG_USER_ID;
import static com.digits.business.classes.URLTAG.GET_MP3;


public class Mp3FilesActivity extends AppCompatActivity {

    private ProgressBar CircularProgress;
    private Context context;

    boolean checkfirstresume=false;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private TextView nofiles;
    private static JSONArray mp3fileArray = null;
    ArrayList<Mp3File> mp3Files;
    private FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton search_item;
    private com.github.clans.fab.FloatingActionButton add_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3_files);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//         setSupportActionBar(toolbar);
        CircularProgress = findViewById(R.id.progressbar_mp3file);
        context = this;
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(context);
        nofiles = findViewById(R.id.no_file);
        mp3Files = new ArrayList<>();
        menuRed = findViewById(R.id.menu);
        search_item = findViewById(R.id.search_item);
        add_item = findViewById(R.id.add_item);

        //hide no file textview
        nofiles.setVisibility(View.GONE);


        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Mp3FilesActivity.this, UploadeMp3FileActivity.class);
                startActivity(intent);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        search_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Mp3FilesActivity.this, SearchMp3FilesActivity.class);
                startActivity(intent);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });


        if (StaticMethod.ConnectChecked(context)) {
            PreferenceHelper helper = new PreferenceHelper(context);

            GetMp3FileServer(helper.getSettingValueId());
        } else {

            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void GetMp3FileServer(final String id) {

        CircularProgress.setVisibility(View.VISIBLE);


        StringRequest request = new StringRequest(Request.Method.POST, GET_MP3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {


                        mp3fileArray = obj.getJSONArray(TAG_DATA);

                        for (int i = 0; i < mp3fileArray.length(); i++) {

                            JSONObject objc = mp3fileArray.getJSONObject(i);
                            String id = objc.getString(TAG_ID);
                            String name = objc.getString(TAG_NAME);
                            String url = objc.getString(TAG_URL);
                            String user_id = objc.getString(TAG_USER_ID);
                            String created_at = objc.getString(TAG_CREATED_AT);
                            String updated_at = objc.getString(TAG_UPDATED_AT);


                            Mp3File mp3File = new Mp3File(id, name, url, user_id, created_at, updated_at);
                            mp3Files.add(mp3File);
                        }

                      //  Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        if(mp3Files.size()>0)
                        nofiles.setVisibility(View.GONE);
                        else
                         nofiles.setVisibility(View.VISIBLE);
                        CircularProgress.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new RecyclerMP3FileCardAdapter(mp3Files, context);
                        recyclerView.setAdapter(adapter);

                    } else {
                        nofiles.setVisibility(View.VISIBLE);
                        CircularProgress.setVisibility(View.GONE);
                        //  Toast.makeText(getApplicationContext(), "error ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CircularProgress.setVisibility(View.GONE);
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
                CircularProgress.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        if(!checkfirstresume)
            checkfirstresume=true;
        else
        {
            mp3Files.clear();
        if (StaticMethod.ConnectChecked(context)) {
            PreferenceHelper helper = new PreferenceHelper(context);
            GetMp3FileServer(helper.getSettingValueId());
        } else {

            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        }
    }
}
