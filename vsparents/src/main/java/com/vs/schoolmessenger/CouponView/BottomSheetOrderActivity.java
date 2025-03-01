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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vs.schoolmessenger.R;

public class BottomSheetOrderActivity extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Button btnactivatecoupon;
    private LottieAnimationView lottieAnimationView;
    private Handler animationHandler = new Handler();
    private Runnable animationRunnable;

    private TextView expandableText;
    private ImageView rememberSymbol, remembersymbol1, rememberSymbol2;
    private boolean isExpanded = false, isExpanded1 = false, isExpanded2 = false;
    private TextView expandabletext1, expandabletext5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.bottom_sheet_order);
        ImageView imageBanner = findViewById(R.id.image_banner);
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        expandableText = findViewById(R.id.expandable_text2);
        expandabletext1 = findViewById(R.id.expandable_text1);
        expandabletext5 = findViewById(R.id.expandable_text5);
        rememberSymbol = findViewById(R.id.remember_symbol3);
        remembersymbol1 = findViewById(R.id.remember_symbol4);
        rememberSymbol2 = findViewById(R.id.remember_symbol5);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        btnactivatecoupon = findViewById(R.id.btn_activate_coupon);
        lottieAnimationView = findViewById(R.id.lottie_animation);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;


        // Set ImageView height to half of the screen
        ViewGroup.LayoutParams params = imageBanner.getLayoutParams();
        params.height = screenHeight / 2;
        imageBanner.setLayoutParams(params);

        // Adjust Bottom Sheet Layout Height to Allow Top Gap
        ViewGroup.LayoutParams bottomSheetParams = bottomSheet.getLayoutParams();
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

                Intent intent = new Intent(BottomSheetOrderActivity.this,BottomSheetOrderConfirmActivity.class);
                startActivity(intent);

            }
        });


        rememberSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    startAnimationLoop();

                    expandabletext1.setVisibility(View.GONE);
                    rememberSymbol.setImageResource(R.drawable.ic_down_black);
                } else {
                    expandabletext1.setVisibility(View.VISIBLE);
                    rememberSymbol.setImageResource(R.drawable.ic_up_arrow);
                }
                isExpanded = !isExpanded;
            }
        });

        remembersymbol1.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded1) {
                    expandableText.setVisibility(View.GONE);
                    remembersymbol1.setImageResource(R.drawable.ic_down_black);
                } else {
                    expandableText.setVisibility(View.VISIBLE);
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

    private void startAnimationLoop() {
        animationRunnable = new Runnable() {
            @Override
            public void run() {
                lottieAnimationView.playAnimation(); // Play animation

            }
        };
      // Start after 5 seconds
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animationHandler != null && animationRunnable != null) {
            animationHandler.removeCallbacks(animationRunnable);
        }
    }
}
