package com.digits.business.twilio;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.digits.business.R;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    private SessionHandler session;
    private String identity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        TextView welcomeText = findViewById(R.id.welcomeText);

        //Intent i = new Intent(DashboardActivity.this, VoiceActivity.class);
       // startActivity(i);

        welcomeText.setText("Welcome "+user.getFullName()+", your session will expire on "+user.getSessionExpiryDate());
        //Intent intent = new Intent(DashboardActivity.this, Voice.class);
        //startService(intent);

        Button logoutBtn = findViewById(R.id.btnLogout);
        Button homeBtn = findViewById(R.id.btnhome);
        Button dialBtn = findViewById(R.id.btndial);

        //Intent i = new Intent(DashboardActivity.this, VoiceActivity.class);
        //startActivity(i);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, vc.class);
                startActivity(i);
              //Intent intent = new Intent(DashboardActivity.this, Voice.class);
                //stopService(intent);








            }
        });


        dialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, VoiceActivity.class);
                startActivity(i);
                //Intent intent = new Intent(DashboardActivity.this, Voice.class);
                //stopService(intent);








            }
        });






        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        });
    }



}
