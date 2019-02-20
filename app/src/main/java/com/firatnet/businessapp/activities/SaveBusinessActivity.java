package com.firatnet.businessapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.firatnet.businessapp.entities.Business;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.firatnet.businessapp.classes.URLTAG.URL_SAVE_BUSINESS;

import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_ID;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_COUNTRY;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_ADDRESS;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_CITY;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_EMPLOYESS_NUMBER;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_KEYWORDS;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_PARTNERSHIP_TYPE;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_TYPE;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_YEAR_ESTABLISHED;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_TURNOVER;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_PRODUCTS;
import static com.firatnet.businessapp.classes.JsonTAG.BUSINESS_PIN_CODE;



public class SaveBusinessActivity extends AppCompatActivity {

    private String userId;

    private Context context;

    private ProgressDialog progressDialog;

    private TextInputEditText businessNameET;
    private TextInputEditText businessTypeET;
    private TextInputEditText partnershipTypeET;
    private TextInputEditText yearEstablishedET;
    private TextInputEditText employeesNumberET;
    private TextInputEditText turnoverET;
    private TextInputEditText addressET;
    private TextInputEditText countryET;
    private TextInputEditText cityET;
    private TextInputEditText pinCodeET;
    private TextInputEditText productsET;
    private TextInputEditText keywordsET;

    private TextView errors;

    private Button saveBusiness;


    private View.OnClickListener saveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String name = businessNameET.getText().toString();
            String businessType = businessTypeET.getText().toString();
            String partnershipType = partnershipTypeET.getText().toString();
            String yearEstablished = yearEstablishedET.getText().toString();
            String employeesNumber = employeesNumberET.getText().toString();
            String turnover = turnoverET.getText().toString();
            String address = addressET.getText().toString();
            String country = countryET.getText().toString();
            String city = cityET.getText().toString();
            String pinCode = pinCodeET.getText().toString();
            String products = productsET.getText().toString();
            String keywords = keywordsET.getText().toString();

            errors.setText(" ");

            if ( name.isEmpty() || businessType.isEmpty() || partnershipType.isEmpty() ||
                 yearEstablished.isEmpty() || employeesNumber.isEmpty() || turnover.isEmpty() ||
                 address.isEmpty() || country.isEmpty() || city.isEmpty() || pinCode.isEmpty() ||
                 products.isEmpty() || keywords.isEmpty() ) {

                errors.setText("Please Enter All The Fields");

            } else {


                if (StaticMethod.ConnectChecked(context)) {

//                    saveBusiness.setEnabled(false);

                    Business business = new Business();

                    business.setBusinessName(name);
                    business.setBusinessType(businessType);
                    business.setPartnershipType(partnershipType);
                    business.setYearEstablished(yearEstablished);
                    business.setEmployeesNumber(employeesNumber);
                    business.setTurnover(turnover);
                    business.setAddress(address);
                    business.setCountry(country);
                    business.setCity(city);
                    business.setPinCode(pinCode);
                    business.setProducts(products);
                    business.setKeywords(keywords);

                    postBusinessDetails(business);

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_business);

        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        userId = preferenceHelper.getSettingValueId();


        businessNameET = findViewById(R.id.businessName);
        businessTypeET = findViewById(R.id.businessType);
        partnershipTypeET = findViewById(R.id.partnershipType);
        yearEstablishedET = findViewById(R.id.yearEstablished);
        employeesNumberET = findViewById(R.id.employeesNumber);
        turnoverET = findViewById(R.id.turnover);
        addressET = findViewById(R.id.address);
        countryET = findViewById(R.id.country);
        cityET = findViewById(R.id.city);
        pinCodeET = findViewById(R.id.pinCode);
        productsET = findViewById(R.id.productsAndServices);
        keywordsET = findViewById(R.id.keywords);

        errors = findViewById(R.id.errors);

        saveBusiness = findViewById(R.id.saveBusiness);
        saveBusiness.setOnClickListener(saveClickListener);

    }


    /**
     * Upload Business details to the server
     *
     * @param business
     */
    private void postBusinessDetails(final Business business) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        StringRequest request = new StringRequest( Request.Method.POST, URL_SAVE_BUSINESS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    progressDialog.dismiss();

                    if(obj.getBoolean("success")) {

                        Toast.makeText(getApplicationContext(), obj.getString("message").toUpperCase(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(SaveBusinessActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                    else {

                        JSONArray message = obj.getJSONArray("message");
                        JSONObject error = message.getJSONObject(0);
                        Toast.makeText(getApplicationContext(), error.getString("year_established"), Toast.LENGTH_LONG).show();
                        errors.setText(error.getString("year_established"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        try {
                            String responseBody = new String( error.networkResponse.data, "utf-8" );
                            JSONObject jsonObject = new JSONObject( responseBody );
                            progressDialog.dismiss();
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

                params.put(BUSINESS_ID, userId);
                params.put(BUSINESS_NAME, business.getBusinessName());
                params.put(BUSINESS_TYPE, business.getBusinessType());
                params.put(BUSINESS_PARTNERSHIP_TYPE, business.getPartnershipType());
                params.put(BUSINESS_YEAR_ESTABLISHED, business.getYearEstablished());
                params.put(BUSINESS_EMPLOYESS_NUMBER, business.getEmployeesNumber());
                params.put(BUSINESS_TURNOVER, business.getTurnover());
                params.put(BUSINESS_ADDRESS, business.getAddress());
                params.put(BUSINESS_COUNTRY, business.getCountry());
                params.put(BUSINESS_CITY, business.getCity());
                params.put(BUSINESS_PIN_CODE, business.getPinCode());
                params.put(BUSINESS_PRODUCTS, business.getProducts());
                params.put(BUSINESS_KEYWORDS, business.getKeywords());

                return params;

            }



        };

        //adding the request to volley
        Volley.newRequestQueue(context).add(request);
    }


}
