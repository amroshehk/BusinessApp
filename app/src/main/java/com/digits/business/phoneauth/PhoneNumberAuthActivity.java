package com.digits.business.phoneauth;

//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.digits.business.activities.LoginActivity;
import com.digits.business.activities.MainActivity;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.R;

import androidx.appcompat.app.AppCompatActivity;

public class PhoneNumberAuthActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText editText;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_auth);
        context=this;
        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        editText = findViewById(R.id.editTextPhone);

       // FirebaseApp.initializeApp(this);

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                String counterName=CountryData.countryNames[spinner.getSelectedItemPosition()];

                String number = editText.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }

                String phoneNumber = "+" + code + number;

                Intent intent = new Intent(PhoneNumberAuthActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", phoneNumber);
                intent.putExtra("counterName", counterName);
                startActivity(intent);

            }
        });

        findViewById(R.id.loginbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneNumberAuthActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //check login state
        PreferenceHelper helper = new PreferenceHelper(context);
        String loginInState = helper.getLoginState();

      //  if (FirebaseAuth.getInstance().getCurrentUser() != null && !loginInState.isEmpty()) {
        if (!loginInState.isEmpty()) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        }
    }
}
