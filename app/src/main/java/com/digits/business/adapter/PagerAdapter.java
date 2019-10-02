package com.digits.business.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.digits.business.fragments.Tab1AllCallerFragment;
import com.digits.business.fragments.Tab2OutgoingCallFragment;
import com.digits.business.fragments.Tab3IncomingCallFragment;
import com.digits.business.fragments.Tab4MissCallFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Tab1AllCallerFragment tab1 = new Tab1AllCallerFragment();
                return tab1;
            case 1:
                Tab2OutgoingCallFragment tab2 = new Tab2OutgoingCallFragment();
                return tab2;
            case 2:
                Tab3IncomingCallFragment tab3 = new Tab3IncomingCallFragment();
                return tab3;
            case 3:
                Tab4MissCallFragment tab4 = new Tab4MissCallFragment();
                return tab4;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}