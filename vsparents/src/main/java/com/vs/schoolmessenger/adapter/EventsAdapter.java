package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;

import com.vs.schoolmessenger.fragments.EventsFragment;
import com.vs.schoolmessenger.fragments.HolidaysFragment;

public class EventsAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    String userId;
    Context mcontext;
    LayoutInflater mLayoutInflater;

    SharedPreferences shpRemember;

    public EventsAdapter(FragmentManager fm, int NumOfTabs, String userId) {//, String invno, String dmdid) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.userId = userId;

    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                EventsFragment tab0 = new EventsFragment();
                return tab0;
            case 1:
                HolidaysFragment tab1 = new HolidaysFragment();
                return tab1;




            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


