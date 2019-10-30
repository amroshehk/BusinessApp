package com.digits.business.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.classes.StaticMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.digits.business.classes.JsonTAG.TAG_DATA;
import static com.digits.business.classes.JsonTAG.TAG_NUM;
import static com.digits.business.classes.JsonTAG.TAG_Query;
import static com.digits.business.classes.URLTAG.SEARCH_MP3;

public class PremiumPlanActivity extends BaseActivity {
    TextView tiltle_base;
    RadioButton num1;
    RadioButton num2;
    RadioButton num3;
    RadioButton num4;
    RadioButton num5;
    Button search_btn;
    Button make_payment;
    SearchView searchView;
    private ProgressBar CircularProgress;
    private Context context;
    ArrayList<String> numbers;
    private TextView nonumbers;
    private LinearLayout line_numbers;
    private static JSONArray numbersArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_plan);
        init();
    }

    void init()
    {

        num1=findViewById(R.id.num1);
        num2=findViewById(R.id.num2);
        num3=findViewById(R.id.num3);
        num4=findViewById(R.id.num4);
        num5=findViewById(R.id.num5);
        search_btn=findViewById(R.id.search_btn);
        make_payment=findViewById(R.id.make_payment);
        searchView=findViewById(R.id.search);
        nonumbers=findViewById(R.id.nonumbers);
        line_numbers=findViewById(R.id.line_numbers);
        CircularProgress=findViewById(R.id.progressbar_mp3file);
        context=this;

        line_numbers.setVisibility(View.GONE);
        make_payment.setVisibility(View.GONE);
        nonumbers.setVisibility(View.GONE);
        tiltle_base = findViewById(R.id.tiltle_base);
        tiltle_base.setText(getResources().getString(R.string.premium));

        numbers=new ArrayList<>();

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line_numbers.setVisibility(View.VISIBLE);
                make_payment.setVisibility(View.VISIBLE);
                num1.setChecked(true);
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {


                if ( StaticMethod.ConnectChecked(context))  {
//                    PreferenceHelper helper=new PreferenceHelper(context);
                    line_numbers.setVisibility(View.VISIBLE);
                    make_payment.setVisibility(View.VISIBLE);
                    num1.setChecked(true);
                    nonumbers.setVisibility(View.GONE);
                    numbers.clear();
                   // SearchNumbersServer(query);

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

    private void SearchNumbersServer(final String query) {

        CircularProgress.setVisibility(View.VISIBLE);


        StringRequest request = new StringRequest(Request.Method.POST, SEARCH_MP3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {

                        //Files retrieved successfully
                        if( obj.getString("message").equals("Files retrieved successfully")){
                            numbersArray = obj.getJSONArray(TAG_DATA);

                            for (int i = 0; i < numbersArray.length(); i++) {

                                JSONObject objc = numbersArray.getJSONObject(i);
                                String number = objc.getString(TAG_NUM);

                                String  mp3File = new String(number);
                                numbers.add(mp3File);
                            }

                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            nonumbers.setVisibility(View.GONE);
                            CircularProgress.setVisibility(View.GONE);

                        }
                        else if(obj.getString("message").equals("No files match the query string"))
                        {
                            nonumbers.setVisibility(View.VISIBLE);
                            CircularProgress.setVisibility(View.GONE);
                        }
                        else
                        {
                            nonumbers.setVisibility(View.VISIBLE);
                            CircularProgress.setVisibility(View.GONE);
                        }

                    } else  {
                        nonumbers.setVisibility(View.VISIBLE);
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
