package com.digits.business.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.widget.TextView;

import com.digits.business.R;
import com.digits.business.entities.Business;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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


    private CircleImageView image;

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
        image = findViewById(R.id.photo);

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

        getBusinessPhoto(image, business.getImageURL());

//        Toast.makeText( getBaseContext(), business.getBusinessName(), Toast.LENGTH_LONG ).show();
    }


    private void getBusinessPhoto(CircleImageView image, String url) {

        ImageLoader imageLoader = ImageLoader.getInstance();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();

        ImageLoader.getInstance().init(config);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();


        imageLoader.displayImage(url, image, options);

        //image.setBackgroundResource(R.drawable.user512);

    }


}
