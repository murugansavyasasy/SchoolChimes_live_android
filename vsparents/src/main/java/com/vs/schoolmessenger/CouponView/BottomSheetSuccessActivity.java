package com.vs.schoolmessenger.CouponView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vs.schoolmessenger.R;

public class BottomSheetSuccessActivity extends AppCompatActivity {

    private BottomSheetBehavior<View> bottomSheetBehavior;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.layout_bottomsheet_success);
        ImageView imageBanner = findViewById(R.id.image_banner);

        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int topGap = 230;

        // Set ImageView height to half of the screen
        ViewGroup.LayoutParams params = imageBanner.getLayoutParams();
        params.height = screenHeight / 2;
        imageBanner.setLayoutParams(params);

        // Adjust Bottom Sheet Layout Height to Allow Top Gap
        ViewGroup.LayoutParams bottomSheetParams = bottomSheet.getLayoutParams();
        bottomSheetParams.height = screenHeight - topGap;
        bottomSheet.setLayoutParams(bottomSheetParams);

        // Configure Bottom Sheet
        bottomSheetBehavior.setFitToContents(true);
        bottomSheetBehavior.setPeekHeight((int) (screenHeight * 0.6));



        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


    }

}

