package com.digits.business.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.business.R;
import com.digits.business.adapter.RecyclerLogCardAdapter;
import com.digits.business.adapter.RecyclerNumberLogCardAdapter;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.classes.SwipeToDeleteCallback;
import com.digits.business.database.DbHelper;
import com.digits.business.entities.Log;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import static com.digits.business.classes.JsonTAG.TAG_PHONE;

public class CallerNumberDetailsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerNumberLogCardAdapter adapter;
    Context context;
    public ArrayList<Log> logs;
    DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller_number_details);

        context = this;
        recyclerView =findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(context);
        Intent intent=getIntent();
        String number=intent.getStringExtra(TAG_PHONE);
        PreferenceHelper helper = new PreferenceHelper(context);
        final String email = helper.getSettingValueEmail();
        dbHelper = new DbHelper(context);
        logs = dbHelper.getAllLogByEmailAndNumber(email,number);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerNumberLogCardAdapter(logs, context, this);
        recyclerView.setAdapter(adapter);

    }

}
