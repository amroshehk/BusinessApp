package com.digits.business.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digits.business.R;
import com.digits.business.adapter.RecyclerLogCardAdapter;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.classes.SwipeToDeleteCallback;
import com.digits.business.database.DbHelper;
import com.digits.business.entities.Log;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class Tab4MissCallFragment extends Fragment {
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerLogCardAdapter adapter;
    Context context;
    public ArrayList<Log> logs;
    TextView no_log;
    FloatingActionButton clear_log;
    DbHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview= inflater.inflate(R.layout.tab4_miss_call_fragment, container, false);
        context=rootview.getContext();
        recyclerView = rootview.findViewById(R.id.recyclerview);
        clear_log = rootview.findViewById(R.id.clear_log);
        no_log = rootview.findViewById(R.id.no_log);
        layoutManager = new LinearLayoutManager(context);
        PreferenceHelper helper=new PreferenceHelper(context);
        final String email=helper.getSettingValueEmail();
        dbHelper=new DbHelper(context);
        logs=dbHelper.getAllLogByEmailAndType(email,0);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerLogCardAdapter(logs,context,getActivity());
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter,context));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        if(logs.size()==0)
            no_log.setVisibility(View.VISIBLE);
        clear_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAllLogByEmailAndType(email,0);
                logs.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(context,"Clear Successfully",Toast.LENGTH_LONG).show();
            }
        });
        return rootview;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        // Refresh tab data:
        if (isVisibleToUser) {
        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }   }
    }
}