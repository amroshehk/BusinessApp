package com.firatnet.businessapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firatnet.businessapp.BuildConfig;
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.classes.GalleryUtil;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.classes.ProcessProfilePhotoTask;
import com.firatnet.businessapp.phoneauth.PhoneNumberAuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Context context;


    TextView name_tv, email_tv, status_tv, generated_id_tv, phone_tv, country_tv, created_at_tv, updated_at_tv;
    ImageButton edit_btn;
    ImageLoaderConfiguration config;
    public static final ImageLoader imageLoader = ImageLoader.getInstance();
    private CircleImageView pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = this;
        name_tv = findViewById(R.id.name_tv);
        email_tv = findViewById(R.id.email_tv);
        status_tv = findViewById(R.id.status_tv);
        generated_id_tv = findViewById(R.id.generated_id_tv);
        phone_tv = findViewById(R.id.phone_tv);
        country_tv = findViewById(R.id.country_tv);
        created_at_tv = findViewById(R.id.created_at_tv);
        updated_at_tv = findViewById(R.id.updated_at_tv);



        pic =findViewById(R.id.photo);

        config = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(config);

        PreferenceHelper helper = new PreferenceHelper(context);

        name_tv.setText(helper.getSettingValueName());
        email_tv.setText(helper.getSettingValueEmail());

        if (helper.getSettingValueStatus().equals("ready_to_call"))
            status_tv.setText("Ready To Call");
        else
            status_tv.setText("VM");
        generated_id_tv.setText(helper.getSettingValueGeneratedId());
        phone_tv.setText(helper.getSettingValue_phone());
        country_tv.setText(helper.getSettingValueCountry());
        created_at_tv.setText(helper.getSettingValueCreatedAt());
        updated_at_tv.setText(helper.getSettingValueUpdatedAt());

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        if (!helper.getSettingValuePhotoUrl().isEmpty())
            imageLoader.displayImage(helper.getSettingValuePhotoUrl(), pic, options);

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ImagesViewerActivity.class);
               // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        edit_btn = findViewById(R.id.edit_btn);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfileActivity.this, EditMyProfileActivity.class);
               // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageLoader.getInstance().init(config);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        PreferenceHelper helper = new PreferenceHelper(context);
        if (!helper.getSettingValuePhotoUrl().isEmpty())
            imageLoader.displayImage(helper.getSettingValuePhotoUrl(), pic, options);
    }
}
