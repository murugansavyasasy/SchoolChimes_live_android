package com.vs.schoolmessenger.CouponView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vs.schoolmessenger.R;


public class BottomSheetActivity extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView expandableText;
    private ImageView rememberSymbol;

    private TextView expandabletext1;

    private Button btnactivatecoupon;

    private ImageView remembersymbol1;
    private boolean isExpanded = false;
    private boolean isExpanded1 = false;

    Handler handler = new Handler();
    Runnable konfettiRunnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.bottom_sheet);
        ImageView imageBanner = findViewById(R.id.image_banner);
        expandableText = findViewById(R.id.expandable_text);
        expandabletext1 = findViewById(R.id.expandable_text1);
        rememberSymbol = findViewById(R.id.remember_symbol);
        remembersymbol1 = findViewById(R.id.remember_symbol1);
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        btnactivatecoupon = findViewById(R.id.btn_activate_coupon);
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


        btnactivatecoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BottomSheetActivity.this,BottomSheetOrderActivity.class);
                startActivity(intent);

            }
        });



        rememberSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    expandableText.setVisibility(View.GONE);
                    rememberSymbol.setImageResource(R.drawable.ic_down_black);
                } else {
                    expandableText.setVisibility(View.VISIBLE);
                    rememberSymbol.setImageResource(R.drawable.ic_up_arrow);
                }
                isExpanded = !isExpanded;
            }
        });

        remembersymbol1.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded1) {
                    expandabletext1.setVisibility(View.GONE);
                    remembersymbol1.setImageResource(R.drawable.ic_down_black);
                } else {
                    expandabletext1.setVisibility(View.VISIBLE);
                    remembersymbol1.setImageResource(R.drawable.ic_up_arrow);
                }
                isExpanded1 = !isExpanded1;
            }
        }));

    }

}

