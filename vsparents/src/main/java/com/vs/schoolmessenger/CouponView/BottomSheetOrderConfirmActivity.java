package com.vs.schoolmessenger.CouponView;

import android.content.Intent;
import android.os.Bundle;
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

public class BottomSheetOrderConfirmActivity extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView expandableText;
    private ImageView rememberSymbol;

    private TextView expandabletext1;

    private TextView expandabletext5;
    private Button btnactivatecoupon;
    private ImageView remembersymbol1;
    private ImageView rememberSymbol2;
    private boolean isExpanded = false;
    private boolean isExpanded1 = false;

    private boolean isExpanded2 = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.layout_bottomsheetconfirm);
        ImageView imageBanner = findViewById(R.id.image_banner);

        expandableText = findViewById(R.id.expandable_text2);
        expandabletext1 = findViewById(R.id.expandable_text1);
        expandabletext5 = findViewById(R.id.expandable_text6);
        rememberSymbol = findViewById(R.id.remember_symbol4);
        remembersymbol1 = findViewById(R.id.remember_symbol3);
        rememberSymbol2 = findViewById(R.id.remember_symbol5);




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
                Intent intent = new Intent(BottomSheetOrderConfirmActivity.this, BottomSheetSuccessActivity.class);
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


        rememberSymbol2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded2) {
                    expandabletext5.setVisibility(View.GONE);
                    rememberSymbol2.setImageResource(R.drawable.ic_down_black);
                } else {
                    expandabletext5.setVisibility(View.VISIBLE);
                    rememberSymbol2.setImageResource(R.drawable.ic_up_arrow);
                }
                isExpanded2 = !isExpanded2;
            }
        }));




    }

}

