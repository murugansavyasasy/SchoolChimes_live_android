
package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.vs.schoolmessenger.R;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AssignmentActivity extends AppCompatActivity {
    ImageView imgBack;
    TabLayout assignTab;
    LinearLayout viewPager;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        imgBack=findViewById(R.id.imgBack);
        assignTab=findViewById(R.id.assignTablayout);
        viewPager=findViewById(R.id.viewpager);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        assignTab.addTab(assignTab.newTab().setText("Create"));
        assignTab.addTab(assignTab.newTab().setText("View"));

        assignTab.setTabGravity(TabLayout.GRAVITY_FILL);

        assignTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {

                    CreateAssignment fragment = new CreateAssignment();

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
                    ft.replace(R.id.viewpager, fragment);
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();


                }
                else if (tab.getPosition() == 1) {

                    ViewAssignment fragment1 = new ViewAssignment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
                    ft.replace(R.id.viewpager, fragment1);
                    ft.detach(fragment1);
                    ft.attach(fragment1);
                    ft.commit();

                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        if (savedInstanceState == null) {

            CreateAssignment firstFragment = new CreateAssignment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
            ft.add(R.id.viewpager, firstFragment);
            ft.commit();
        }
    }
}
