package com.digits.business.activities;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
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
import com.digits.business.adapter.RecyclerVoiceEmailCardAdapter;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.classes.StaticMethod;
import com.digits.business.entities.Mp3File;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.digits.business.classes.JsonTAG.TAG_CREATED_AT;
import static com.digits.business.classes.JsonTAG.TAG_DATA;
import static com.digits.business.classes.JsonTAG.TAG_ID;
import static com.digits.business.classes.JsonTAG.TAG_SENDER_ID;
import static com.digits.business.classes.JsonTAG.TAG_UPDATED_AT;
import static com.digits.business.classes.JsonTAG.TAG_URL;
import static com.digits.business.classes.JsonTAG.TAG_USER_ID;
import static com.digits.business.classes.URLTAG.URL_GET_VOICE_MAIL;


public class VoiceMailActivity extends BaseActivity {

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
    TextView tiltle_base;
    SwipeRefreshLayout pullToRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_mail);

        tiltle_base = findViewById(R.id.tiltle_base);
        tiltle_base.setText(getResources().getString(R.string.title_activity_mp3_files));
        CircularProgress = findViewById(R.id.progressbar_mp3file);
        context = this;
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(context);
        nofiles = findViewById(R.id.no_file);
        mp3Files = new ArrayList<>();
        menuRed = findViewById(R.id.menu);
        pullToRefresh =findViewById(R.id.pullToRefresh);
        //hide no file textview
        nofiles.setVisibility(View.GONE);


        if (StaticMethod.ConnectChecked(context)) {
            PreferenceHelper helper = new PreferenceHelper(context);

            getVoiceMail(helper.getSettingValueId());
        } else {

            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        //setting an setOnRefreshListener on the SwipeDownLayout
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
              @Override
            public void onRefresh() {
                mp3Files.clear();
                if (adapter!=null)
                    adapter.notifyDataSetChanged();
                if (StaticMethod.ConnectChecked(context)) {
                    PreferenceHelper helper = new PreferenceHelper(context);
                    getVoiceMail(helper.getSettingValueId());
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void getVoiceMail(final String id) {

        CircularProgress.setVisibility(View.VISIBLE);


        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_VOICE_MAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {


                        mp3fileArray = obj.getJSONArray(TAG_DATA);

                        for (int i = 0; i < mp3fileArray.length(); i++) {

                            JSONObject objc = mp3fileArray.getJSONObject(i);
                            String id = objc.getString(TAG_ID);
                            String basic_="";
                            String url_;
                            String url = objc.getString(TAG_URL);
                            String user_id = objc.getString(TAG_USER_ID);
                            String sender_id = objc.getString(TAG_SENDER_ID);
                            String created_at = objc.getString(TAG_CREATED_AT);
                            String updated_at = objc.getString(TAG_UPDATED_AT);
                            int ind=0;
                            String protocol="https://";
                            url_=url;
                            if(url.contains("@api.twilio.com"))
                            {   ind= url.indexOf("api.twilio.com");
                                url_=url.substring(ind,url.length());
                                url_=url_.concat(".mp3");
                                url_=protocol+url_;

                                basic_=url.substring(8,ind-1);
                            }

                            Calendar cal = Calendar.getInstance();
                            TimeZone tz = cal.getTimeZone();
                            final String format = "yyyy-MM-dd HH:mm:ss";
                            final String format2 = "yyyy-MM-dd HH:mm";
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            SimpleDateFormat sdf2 = new SimpleDateFormat(format2);

                            Date c_date=null;
                            try {
                                c_date = sdf.parse(created_at);//covert string date to date object
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date offsetTime = new Date(c_date.getTime() + tz.getRawOffset());


                            Date u_date=null;
                            try {
                                u_date = sdf.parse(updated_at);//covert string date to date object
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date offsetTime2 = new Date(u_date.getTime() + tz.getRawOffset());

                           // Mp3File mp3File = new Mp3File(id, name, url, user_id, sdf2.format(offsetTime), sdf2.format(offsetTime2));
                            Mp3File mp3File = new Mp3File(id, sender_id, url_, user_id, sdf2.format(offsetTime), sdf2.format(offsetTime2));
                            mp3File.setBasic(basic_);
                            mp3Files.add(mp3File);
                        }

                      //  Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        if(mp3Files.size()>0)
                        nofiles.setVisibility(View.GONE);
                        else
                         nofiles.setVisibility(View.VISIBLE);

                        CircularProgress.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new RecyclerVoiceEmailCardAdapter(mp3Files, context);
                        recyclerView.setAdapter(adapter);
                        pullToRefresh.setRefreshing(false);
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

}
