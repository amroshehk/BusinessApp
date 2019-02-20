package com.firatnet.businessapp.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.adapter.RecyclerMP3FileCardAdapter;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.classes.StaticMethod;
import com.firatnet.businessapp.entities.Mp3File;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firatnet.businessapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_CREATED_AT;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_DATA;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_UPDATED_AT;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_URL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_USER_ID;
import static com.firatnet.businessapp.classes.URLTAG.GET_MP3;

public class Mp3FilesActivity extends AppCompatActivity {
    private ProgressBar CircularProgress;
    private Context context;

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
        //Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
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

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        nofiles.setVisibility(View.GONE);
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
                try {
                    String responseBody = new String(volleyError.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    CircularProgress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    //Handle a malformed json response
                } catch (UnsupportedEncodingException error) {

                }
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
        requestQueue.add(request);

    }

}
