package com.firatnet.businessapp.app;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
     //   FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
    }
}
