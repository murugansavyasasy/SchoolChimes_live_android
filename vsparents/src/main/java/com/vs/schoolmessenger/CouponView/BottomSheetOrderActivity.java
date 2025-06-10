package com.vs.schoolmessenger.CouponView;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vs.schoolmessenger.R;

public class BottomSheetOrderActivity extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Button btnactivatecoupon,btn_activate_coupon2;
    private LottieAnimationView lottieAnimationView;
    private Handler animationHandler = new Handler();
    private Runnable animationRunnable;

    private TextView expandableText,frame_text;
    private ImageView rememberSymbol, remembersymbol1, rememberSymbol2;
    private boolean isExpanded = false, isExpanded1 = false, isExpanded2 = false;
    private TextView expandabletext1, expandabletext5;
    private String merchant_logo;
    private String offer;
    private String coupon_code;
    private String CTAname;
    private String CTAredirect;
    private String redirect_url;
    private String howToUseText;
    private String termsAndConditions;

    private LottieAnimationView animationView;
    private Handler handler = new Handler();

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

        expandabletext5 = findViewById(R.id.expandable_text5);
//        rememberSymbol = findViewById(R.id.remember_symbol3);
        remembersymbol1 = findViewById(R.id.remember_symbol4);
        rememberSymbol2 = findViewById(R.id.remember_symbol5);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        btnactivatecoupon = findViewById(R.id.btn_activate_coupon);
        btn_activate_coupon2 = findViewById(R.id.btn_activate_coupon2);
        animationView = findViewById(R.id.animationView);

        animationView.playAnimation();

        frame_text = findViewById(R.id.frame_text);
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
        merchant_logo = getIntent().getStringExtra("merchant_logo");
        offer = getIntent().getStringExtra("offer");
        coupon_code = getIntent().getStringExtra("coupon_code");
        CTAname = getIntent().getStringExtra("CTAname");
        CTAredirect = getIntent().getStringExtra("CTAredirect");
        redirect_url = getIntent().getStringExtra("redirect_url");
        howToUseText = getIntent().getStringExtra("how_to_use");

        termsAndConditions= getIntent().getStringExtra("Terms and Conditions");
        frame_text.setText(coupon_code);
        expandableText.setText(howToUseText);
        expandabletext5.setText(termsAndConditions);

        Glide.with(BottomSheetOrderActivity.this)
                .load(merchant_logo)
                .into(imageBanner);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animationView.cancelAnimation();
            }
        }, 3000);


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
                if (redirect_url != null && !redirect_url.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(BottomSheetOrderActivity.this, "No URL to redirect", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn_activate_coupon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BottomSheetOrderActivity.this,BottomSheetOrderConfirmActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

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
