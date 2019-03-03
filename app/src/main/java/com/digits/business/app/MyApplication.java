package com.digits.business.app;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;

import androidx.multidex.MultiDex;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
     //   FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
    }

        @Override
        protected void attachBaseContext(Context base) {
            super.attachBaseContext(base);
            MultiDex.install(this);
        }
}
