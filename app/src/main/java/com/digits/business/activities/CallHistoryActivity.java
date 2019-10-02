package com.digits.business.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.digits.business.R;
import com.digits.business.adapter.PagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class CallHistoryActivity extends BaseActivity {
    TextView tiltle_base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);
        tiltle_base = findViewById(R.id.tiltle_base);
        tiltle_base.setText(getResources().getString(R.string.call_log_manager));
        TabLayout tabLayout =  findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("All calls"));
        tabLayout.addTab(tabLayout.newTab().setText("Outgoing"));
        tabLayout.addTab(tabLayout.newTab().setText("Incoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Missed"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager =  findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

}
