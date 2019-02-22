package com.firatnet.businessapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
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
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.adapter.RecyclerMP3FileCardAdapter;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.classes.StaticMethod;
import com.firatnet.businessapp.entities.Mp3File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_CREATED_AT;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_DATA;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_Query;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_UPDATED_AT;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_URL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_USER_ID;
import static com.firatnet.businessapp.classes.URLTAG.GET_MP3;
import static com.firatnet.businessapp.classes.URLTAG.SEARCH_MP3;

public class SearchMp3FilesActivity extends AppCompatActivity {


    private ProgressBar CircularProgress;
    private Context context;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private TextView nofiles;
    private static JSONArray mp3fileArray = null;
    ArrayList<Mp3File> mp3Files;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mp3_files);

        CircularProgress=findViewById(R.id.progressbar_mp3file);
        context=this;
        recyclerView=findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(context);
        nofiles=findViewById(R.id.no_file);
        mp3Files = new ArrayList<>();
        searchView= findViewById(R.id.search);

        nofiles.setVisibility(View.GONE);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {


                if ( StaticMethod.ConnectChecked(context))  {
                    PreferenceHelper helper=new PreferenceHelper(context);
                    nofiles.setVisibility(View.GONE);
                    mp3Files.clear();
                    SearchMp3FileServer(helper.getSettingValueId(), query);

                    return false;
                } else {

                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    private void SearchMp3FileServer(final String id,final String query) {

        CircularProgress.setVisibility(View.VISIBLE);


        StringRequest request = new StringRequest(Request.Method.POST, SEARCH_MP3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {

                        //Files retrieved successfully
                        if( obj.getString("message").equals("Files retrieved successfully")){
                        mp3fileArray = obj.getJSONArray(TAG_DATA);

                        for (int i = 0; i < mp3fileArray.length(); i++) {

                            JSONObject objc = mp3fileArray.getJSONObject(i);
                            String id = objc.getString(TAG_ID);
                            String name = objc.getString(TAG_NAME);
                            String url = objc.getString(TAG_URL);
                            String user_id = objc.getString(TAG_USER_ID);
                            String created_at = objc.getString(TAG_CREATED_AT);
                            String updated_at = objc.getString(TAG_UPDATED_AT);


                            Mp3File  mp3File = new Mp3File(id, name,url, user_id, created_at, updated_at);
                            mp3Files.add(mp3File);
                        }

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        nofiles.setVisibility(View.GONE);
                        CircularProgress.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter=new RecyclerMP3FileCardAdapter(mp3Files,context);
                        recyclerView.setAdapter(adapter);
                        }
                        else if(obj.getString("message").equals("No files match the query string"))
                        {
                            nofiles.setVisibility(View.VISIBLE);
                            CircularProgress.setVisibility(View.GONE);
                        }
                        else
                        {
                            nofiles.setVisibility(View.VISIBLE);
                            CircularProgress.setVisibility(View.GONE);
                        }

                    } else  {
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
                params.put(TAG_Query, query);

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
