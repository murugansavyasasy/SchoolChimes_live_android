package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;

import com.vs.schoolmessenger.fragments.FeesPaidFragment;
import com.vs.schoolmessenger.fragments.UpcommingFeesFragment;

/**
 * Created by voicesnap on 5/17/2018.
 */

public class FeesAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    String userId;
    Context mcontext;
    LayoutInflater mLayoutInflater;

    SharedPreferences shpRemember;

    public FeesAdapter(FragmentManager fm, int NumOfTabs, String userId) {//, String invno, String dmdid) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.userId = userId;

    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                FeesPaidFragment tab0 = new FeesPaidFragment();
                return tab0;
            case 1:
                UpcommingFeesFragment tab1 = new UpcommingFeesFragment();
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


