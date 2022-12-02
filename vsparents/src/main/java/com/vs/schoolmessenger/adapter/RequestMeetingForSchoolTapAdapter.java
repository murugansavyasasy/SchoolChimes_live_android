package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;

import com.vs.schoolmessenger.fragments.RequestMeetingForSchoolApprovedFragment;
import com.vs.schoolmessenger.fragments.RequestMeetingForSchoolRejectedFragment;
import com.vs.schoolmessenger.fragments.RequestMeetingForSchoolWaitingFragment;

public class RequestMeetingForSchoolTapAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    String userId;
    SharedPreferences shpRemember;

    public RequestMeetingForSchoolTapAdapter(FragmentManager fm, int NumOfTabs, String userId) {//, String invno, String dmdid) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.userId = userId;
    }
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                RequestMeetingForSchoolWaitingFragment tab0 = new RequestMeetingForSchoolWaitingFragment();
                return tab0;
            case 1:

                RequestMeetingForSchoolApprovedFragment tab1 = new RequestMeetingForSchoolApprovedFragment();
                return tab1;

            case 2:
                RequestMeetingForSchoolRejectedFragment tab2 = new RequestMeetingForSchoolRejectedFragment();
                return tab2;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


