package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;

import com.vs.schoolmessenger.fragments.MeetingRequestADDFragment;
import com.vs.schoolmessenger.fragments.RequestMeetingHistoryForParentFragment;

public class RequestMeetingForpParentTapAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    String userId;

    public RequestMeetingForpParentTapAdapter(FragmentManager fm, int NumOfTabs, String userId) {//, String invno, String dmdid) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.userId = userId;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                MeetingRequestADDFragment tab0=new MeetingRequestADDFragment();
                return tab0;
            case 1:
                RequestMeetingHistoryForParentFragment tab1 = new RequestMeetingHistoryForParentFragment();
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


