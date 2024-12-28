package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.fragments.MeetingEventFragement;
import com.vs.schoolmessenger.fragments.UpComingEventFragement;
import com.vs.schoolmessenger.util.Util_Common;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class PTM extends AppCompatActivity implements View.OnClickListener {
    String Root_Frag = "root_fagment";
    TextView isEventSchedule, isUpcomingEvent;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ptm_activity);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.Meeting_Slot);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util_Common.overlappingSlots.clear();
                Util_Common.isSelectedSlotIds.clear();
                Util_Common.isHeaderSlotsIds.clear();
                Util_Common.isSelectedTime.clear();
                finish();
            }
        });


        isEventSchedule = findViewById(R.id.btnMessages);
        isUpcomingEvent = findViewById(R.id.btnStatus);

        isEventSchedule.setTypeface(null, Typeface.BOLD);
        isUpcomingEvent.setTypeface(null, Typeface.BOLD);


        loadFrag(new MeetingEventFragement(), 0);

        isEventSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util_Common.overlappingSlots.clear();
                Util_Common.isSelectedSlotIds.clear();
                Util_Common.isHeaderSlotsIds.clear();
                Util_Common.isSelectedTime.clear();

                isEventSchedule.setBackgroundColor(ContextCompat.getColor(PTM.this, R.color.colorPrimaryDark));
                isUpcomingEvent.setBackgroundColor(ContextCompat.getColor(PTM.this, R.color.clr_grey));
                loadFrag(new MeetingEventFragement(), 0);
            }
        });

        isUpcomingEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util_Common.overlappingSlots.clear();
                Util_Common.isSelectedSlotIds.clear();
                Util_Common.isHeaderSlotsIds.clear();
                Util_Common.isSelectedTime.clear();

                isUpcomingEvent.setBackgroundColor(ContextCompat.getColor(PTM.this, R.color.colorPrimaryDark));
                isEventSchedule.setBackgroundColor(ContextCompat.getColor(PTM.this, R.color.clr_grey));
                loadFrag(new UpComingEventFragement(), 1);
            }
        });
    }

    public void updateFragmentLabel() {
        isUpcomingEvent.setBackgroundColor(ContextCompat.getColor(PTM.this, R.color.colorPrimaryDark));
        isEventSchedule.setBackgroundColor(ContextCompat.getColor(PTM.this, R.color.clr_grey));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util_Common.isHeaderSlotsIds.clear();
        Util_Common.isSelectedTime.clear();
        Util_Common.overlappingSlots.clear();
        Util_Common.isSelectedSlotIds.clear();
        finish();
    }

    public void loadFrag(Fragment fragment_name, int flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (flag == 0) {
            ft.add(R.id.FL, fragment_name);
            fm.popBackStack(Root_Frag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack(Root_Frag);
        } else {
            ft.replace(R.id.FL, fragment_name);
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onClick(View view) {

    }
}
