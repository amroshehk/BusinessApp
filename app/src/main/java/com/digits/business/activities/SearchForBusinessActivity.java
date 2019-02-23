package com.digits.business.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digits.business.R;
import com.digits.business.adapter.BusinessAdapter;
import com.digits.business.classes.StaticMethod;
import com.digits.business.entities.Business;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.digits.business.classes.JsonTAG.BUSINESS_ADDRESS;
import static com.digits.business.classes.JsonTAG.BUSINESS_CITY;
import static com.digits.business.classes.JsonTAG.BUSINESS_COUNTRY;
import static com.digits.business.classes.JsonTAG.BUSINESS_EMPLOYESS_NUMBER;
import static com.digits.business.classes.JsonTAG.BUSINESS_KEYWORDS;
import static com.digits.business.classes.JsonTAG.BUSINESS_NAME;
import static com.digits.business.classes.JsonTAG.BUSINESS_PARTNERSHIP_TYPE;
import static com.digits.business.classes.JsonTAG.BUSINESS_PIN_CODE;
import static com.digits.business.classes.JsonTAG.BUSINESS_PRODUCTS;
import static com.digits.business.classes.JsonTAG.BUSINESS_TURNOVER;
import static com.digits.business.classes.JsonTAG.BUSINESS_TYPE;
import static com.digits.business.classes.JsonTAG.BUSINESS_YEAR_ESTABLISHED;
import static com.digits.business.classes.JsonTAG.TAG_DATA;
import static com.digits.business.classes.URLTAG.URL_SEARCH_BUSINESS;

public class SearchForBusinessActivity extends AppCompatActivity {


    private ProgressBar circularProgress;
    private Context context;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    private TextView nofiles;

    private static JSONArray businessesArray = null;

    ArrayList<Business> businessesList;

    SearchView searchView;

    private EditText countries;
    private EditText cities;
    private EditText pinCode;
    private EditText businessType;
    private EditText partnershipType;

    private static HashMap<String, String> paramsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_business);

        circularProgress = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerview);

        nofiles = findViewById(R.id.no_file);
        searchView = findViewById(R.id.search);

        countries = findViewById(R.id.countries);
        cities = findViewById(R.id.cities);
        pinCode = findViewById(R.id.pinCode);
        businessType = findViewById(R.id.businessType);
        partnershipType = findViewById(R.id.partnershipType);

        context = this;
        layoutManager = new LinearLayoutManager(context);
        businessesList = new ArrayList<>();


        nofiles.setVisibility(View.GONE);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if ( StaticMethod.ConnectChecked(context))  {

                    nofiles.setVisibility(View.GONE);
                    businessesList.clear();

                    paramsMap = new HashMap<>();

                    paramsMap.put("Content-Type", "application/json; charset=utf-8");
                    paramsMap.put(BUSINESS_KEYWORDS, query);
                    paramsMap.put("countries", countries.getText().toString() );
                    paramsMap.put("cities", cities.getText().toString() );
                    paramsMap.put(BUSINESS_PIN_CODE, pinCode.getText().toString());
                    paramsMap.put(BUSINESS_TYPE, businessType.getText().toString());
                    paramsMap.put(BUSINESS_PARTNERSHIP_TYPE, partnershipType.getText().toString());

                    searchBusiness(query);

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




    private void searchBusiness(final String query) {

        circularProgress.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, URL_SEARCH_BUSINESS,

            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {

                        //Files retrieved successfully
                        if( obj.getString("message").equals("Business retrieved successfully")) {

                            businessesArray = obj.getJSONArray(TAG_DATA);

                            for (int i = 0; i < businessesArray.length(); i++) {

                                JSONObject jsonObject = businessesArray.getJSONObject(i);

                                Business business = new Business();

                                business.setBusinessName(jsonObject.getString(BUSINESS_NAME));
                                business.setBusinessType(jsonObject.getString(BUSINESS_TYPE));
                                business.setPartnershipType(jsonObject.getString(BUSINESS_PARTNERSHIP_TYPE));
                                business.setYearEstablished(jsonObject.getString(BUSINESS_YEAR_ESTABLISHED));
                                business.setEmployeesNumber(jsonObject.getString(BUSINESS_EMPLOYESS_NUMBER));
                                business.setTurnover(jsonObject.getString(BUSINESS_TURNOVER));
                                business.setAddress(jsonObject.getString(BUSINESS_ADDRESS));
                                business.setCountry(jsonObject.getString(BUSINESS_COUNTRY));
                                business.setCity(jsonObject.getString(BUSINESS_CITY));
                                business.setPinCode(jsonObject.getString(BUSINESS_PIN_CODE));
                                business.setProducts(jsonObject.getString(BUSINESS_PRODUCTS));
                                business.setKeywords(jsonObject.getString(BUSINESS_KEYWORDS));

                                businessesList.add(business);
                            }

                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            nofiles.setVisibility(View.GONE);
                            circularProgress.setVisibility(View.GONE);

                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new BusinessAdapter(context, businessesList);
                            recyclerView.setAdapter(adapter);

                        } else {
                            nofiles.setVisibility(View.VISIBLE);
                            circularProgress.setVisibility(View.GONE);
                        }

                    } else  {
                        nofiles.setVisibility(View.VISIBLE);
                        circularProgress.setVisibility(View.GONE);
                        //  Toast.makeText(getApplicationContext(), "error ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    circularProgress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "error JSONException", Toast.LENGTH_SHORT).show();
                }

            }
        },

            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    try {
                        String responseBody = new String(volleyError.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        circularProgress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        //Handle a malformed json response
                    } catch (UnsupportedEncodingException error) {

                    }
                }
        })

        {
            @Override
            protected Map<String, String> getParams() {

                return new HashMap<>(paramsMap);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        requestQueue.add(request);

    }




}
