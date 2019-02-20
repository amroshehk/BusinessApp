package com.firatnet.businessapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.firatnet.businessapp.R;
import com.firatnet.businessapp.entities.Business;

public class BusinessDetailsActivity extends AppCompatActivity {


    private TextView businessName;
    private TextView businessType;
    private TextView partnershipType;
    private TextView yearEstablished;
    private TextView employeesNumber;
    private TextView turnover;
    private TextView address;
    private TextView country;
    private TextView city;
    private TextView pinCode;
    private TextView products;
    private TextView keywords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        businessName = findViewById(R.id.businessName);
        businessType = findViewById(R.id.businessType);
        partnershipType = findViewById(R.id.partnershipType);
        yearEstablished = findViewById(R.id.yearEstablished);
        employeesNumber = findViewById(R.id.employeesNumber);
        turnover = findViewById(R.id.turnover);
        address = findViewById(R.id.address);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        pinCode = findViewById(R.id.pinCode);
        products = findViewById(R.id.products);
        keywords = findViewById(R.id.keywords);

        Business business = (Business) getIntent().getSerializableExtra("BUSINESS");

        businessName.setText(business.getBusinessName());
        businessType.setText(business.getBusinessType());
        partnershipType.setText(business.getPartnershipType());
        yearEstablished.setText(business.getYearEstablished());
        employeesNumber.setText(business.getEmployeesNumber());
        turnover.setText(business.getTurnover());
        address.setText(business.getAddress());
        country.setText(business.getCountry());
        city.setText(business.getCity());
        pinCode.setText(business.getPinCode());
        products.setText(business.getProducts());
        keywords.setText(business.getKeywords());

//        Toast.makeText( getBaseContext(), business.getBusinessName(), Toast.LENGTH_LONG ).show();
    }


}
