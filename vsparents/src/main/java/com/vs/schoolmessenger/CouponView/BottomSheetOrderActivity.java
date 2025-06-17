package com.vs.schoolmessenger.CouponView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vs.schoolmessenger.R;

import java.util.Random;

public class BottomSheetOrderActivity extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Button btnactivatecoupon, btn_activate_coupon2;

    private TextView expandableText, expandabletext5;
    private ImageView remembersymbol1, rememberSymbol2;
    private boolean isExpanded1 = false, isExpanded2 = false;

    private String merchant_logo;
    private String offer;
    private String coupon_code;
    private String CTAname;
    private String CTAredirect;
    private String redirect_url;
    private String howToUseText;
    private String termsAndConditions;


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
        FrameLayout bottomSheet = findViewById(R.id.bottom_sheet);
        final FrameLayout container = findViewById(R.id.particle_container);

        expandableText = findViewById(R.id.expandable_text2);
        expandabletext5 = findViewById(R.id.expandable_text5);
        remembersymbol1 = findViewById(R.id.remember_symbol4);
        rememberSymbol2 = findViewById(R.id.remember_symbol5);
        btnactivatecoupon = findViewById(R.id.btn_activate_coupon);
        btn_activate_coupon2 = findViewById(R.id.btn_activate_coupon2);
        showFullScreenConfetti();

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        ViewGroup.LayoutParams params = imageBanner.getLayoutParams();
        params.height = screenHeight / 2;
        imageBanner.setLayoutParams(params);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setFitToContents(true);
        bottomSheetBehavior.setPeekHeight((int) (screenHeight * 0.6));

        merchant_logo = getIntent().getStringExtra("merchant_logo");
        offer = getIntent().getStringExtra("offer");
        coupon_code = getIntent().getStringExtra("coupon_code");
        CTAname = getIntent().getStringExtra("CTAname");
        CTAredirect = getIntent().getStringExtra("CTAredirect");
        redirect_url = getIntent().getStringExtra("redirect_url");
        howToUseText = getIntent().getStringExtra("how_to_use");
        termsAndConditions = getIntent().getStringExtra("Terms and Conditions");

        Glide.with(this)
                .load(merchant_logo)
                .into(imageBanner);

        btnactivatecoupon.setOnClickListener(v -> {
            if (redirect_url != null && !redirect_url.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "No URL to redirect", Toast.LENGTH_SHORT).show();
            }
        });

        btn_activate_coupon2.setOnClickListener(v -> {
            Intent intent = new Intent(this, BottomSheetOrderConfirmActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        remembersymbol1.setOnClickListener(v -> {
            if (isExpanded1) {
                expandableText.setVisibility(View.GONE);
                remembersymbol1.setImageResource(R.drawable.ic_down_black);
            } else {
                expandableText.setVisibility(View.VISIBLE);
                remembersymbol1.setImageResource(R.drawable.ic_up_arrow);
            }
            isExpanded1 = !isExpanded1;
        });

        rememberSymbol2.setOnClickListener(v -> {
            if (isExpanded2) {
                expandabletext5.setVisibility(View.GONE);
                rememberSymbol2.setImageResource(R.drawable.ic_down_black);
            } else {
                expandabletext5.setVisibility(View.VISIBLE);
                rememberSymbol2.setImageResource(R.drawable.ic_up_arrow);
            }
            isExpanded2 = !isExpanded2;
        });
    }

    private void showFullScreenConfetti() {
        final FrameLayout container = findViewById(R.id.particle_container);

        container.post(() -> {
            int[] colors = new int[]{
                    Color.RED, Color.YELLOW, Color.BLUE,
                    Color.GREEN, Color.MAGENTA, Color.CYAN,
                    Color.parseColor("#FFA500"), // orange
                    Color.parseColor("#FF69B4")  // pink
            };

            final int particleCount = 120;
            int screenWidth = container.getWidth();
            int screenHeight = container.getHeight();

            for (int i = 0; i < particleCount; i++) {
                final View particle = new View(this);

                // Random size
                int size = getRandom(12, 30);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
                particle.setLayoutParams(params);

                // Set round shape with color
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.OVAL);
                shape.setSize(size, size);
                int color = colors[new Random().nextInt(colors.length)];
                shape.setColor(color);
                particle.setBackground(shape);

                float startX = getRandom(0, screenWidth);
                float startY = getRandom(-300, -100); // start from above screen
                particle.setX(startX);
                particle.setY(startY);

                container.addView(particle);

                // Animate to bottom with some sway and rotation
                float endY = screenHeight + getRandom(100, 300);
                float endX = startX + getRandom(-100, 100);

                particle.animate()
                        .x(endX)
                        .y(endY)
                        .rotationBy(getRandom(360, 1440))
                        .setDuration(getRandom(2200, 3200))
                        .withEndAction(() -> container.removeView(particle))
                        .start();
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> container.removeAllViews(), 3500);
        });
    }

    private int getRandom(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
