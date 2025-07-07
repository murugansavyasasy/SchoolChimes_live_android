package com.vs.schoolmessenger.CouponView.AcivateCoupon;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vs.schoolmessenger.CouponController.CategoryController;
import com.vs.schoolmessenger.CouponModel.LogactiveapiResponse.LogActiveApiResponse;
import com.vs.schoolmessenger.CouponModel.TicketActivateCoupon.ActivateCoupon;
import com.vs.schoolmessenger.CouponModel.TicketActivateCoupon.ActivateCouponResponse;
import com.vs.schoolmessenger.CouponModel.TicketActivateCouponSummary.ActivateCouponSummary;
import com.vs.schoolmessenger.CouponView.BottomSheetOrderActivity;
import com.vs.schoolmessenger.R;

import java.io.Serializable;
import java.util.List;


public class BottomSheetActivity extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView expandableText;
    private ImageView rememberSymbol;
    private TextView expandabletext1;
    private Button btnactivatecoupon;
    private ImageView remembersymbol1;
    private boolean isExpanded = false;
    private boolean isExpanded1 = false;
    private String source_link;
    private String category_name;
    private String couponStatus;
    private String thumbnail;
    private String merchantlogo;
    private int pointsRemaining;
    private int pointPerCoupon;

    TextView offer_text;
    private  String merchant_name;
    private CategoryController categoryController;
    Handler handler = new Handler();
    Runnable konfettiRunnable;


    private String howToUseText = "";

    private String termsAndConditions = "";
    ProgressBar isProgressBar;
    ProgressBar isProgressBarImage;


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
        RelativeLayout bottomSheet = findViewById(R.id.bottom_sheet);
        btnactivatecoupon = findViewById(R.id.btn_activate_coupon);
        isProgressBar = findViewById(R.id.isProgressBar);
        isProgressBarImage = findViewById(R.id.isProgressBarImage);
        ImageView thumbnailimage = findViewById(R.id.thumbnail);
        ImageView imagetopleft = findViewById(R.id.image_top_left);
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
        source_link = getIntent().getStringExtra("source_link");
        category_name = getIntent().getStringExtra("category_name");
        merchant_name = getIntent().getStringExtra("merchant_name");
        thumbnail = getIntent().getStringExtra("thumbnail");
        merchantlogo = getIntent().getStringExtra("merchant_logo");
        couponStatus = getIntent().getStringExtra("coupon_status");
        pointsRemaining = getIntent().getIntExtra("points_remaining", 0);
        pointPerCoupon = getIntent().getIntExtra("points_percoupon", 0);

        couponStatus = couponStatus != null ? couponStatus : "";

        if ("Activated".equalsIgnoreCase(couponStatus)) {
            btnactivatecoupon.setVisibility(View.GONE);
        } else {
            btnactivatecoupon.setVisibility(View.VISIBLE);
        }


        categoryController = new CategoryController(this);
        categoryController.fetchActivateCoupondata(source_link, new CategoryController.ActivateCouponCallback() {
            @Override
            public void onSuccess(List<ActivateCouponSummary> activatelist) {
                if (activatelist != null && !activatelist.isEmpty()) {
                    ActivateCouponSummary activatecouponlist = activatelist.get(0);

                    TextView headertext = findViewById(R.id.header_textview);
                    offer_text = findViewById(R.id.offer_text);
                    TextView offer_text1 = findViewById(R.id.offer_text1);
                    TextView offer_text4 = findViewById(R.id.offer_text4);
                    TextView expandable_text = findViewById(R.id.expandable_text);
                    TextView expandable_text1 = findViewById(R.id.expandable_text1);

                    headertext.setText(category_name);
                    offer_text.setText(activatecouponlist.getOffer_to_show());
                    offer_text1.setText(activatecouponlist.getMerchant_name());
                    offer_text4.setText("Valid Until: " + activatecouponlist.getExpiry_date());
                    expandable_text.setText(convertHtmlToBullets(activatecouponlist.getHow_to_use()));
                    expandable_text1.setText(convertHtmlToBullets(activatecouponlist.getTerms_and_conditions()));


                    Glide.with(BottomSheetActivity.this)
                            .load(thumbnail)
                            .listener(new RequestListener<Drawable>() {

                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                                    isProgressBarImage.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, com.bumptech.glide.request.target.Target<Drawable> target, @NonNull com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    isProgressBarImage.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(imageBanner);

                    Glide.with(BottomSheetActivity.this)
                            .load(merchantlogo)
                            .into(thumbnailimage);

                    howToUseText = activatecouponlist.getHow_to_use();
                    termsAndConditions = activatecouponlist.getTerms_and_conditions();

                } else {
                    Toast.makeText(BottomSheetActivity.this, "No rewards available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(BottomSheetActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


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
                if (pointsRemaining < pointPerCoupon || pointsRemaining == 0) {
                    Toast.makeText(BottomSheetActivity.this, "You need more points!.Use the app to keep earning points", Toast.LENGTH_SHORT).show();
                    return;
                }

                isProgressBar.setVisibility(View.VISIBLE);
                categoryController.activateCouponWithSource(source_link, new CategoryController.CouponActivationCallback() {
                    @Override
                    public void onSuccess(ActivateCouponResponse response) {
                        if (response != null && response.getData() != null) {
                            List<ActivateCoupon> coupons = response.getData().getCoupons();
                            if (coupons != null && !coupons.isEmpty()) {
                                String merchantLogo = response.getData().getMerchant_logo();
                                String offer = response.getData().getOffer();
                                String couponCode = response.getData().getCoupon_code();
                                String CTAname = response.getData().getCTAname();
                                String CTAredirect = response.getData().getCTAredirect();
                                String redirectUrl = response.getData().getRedirect_url();

                                categoryController.logCouponActivation(source_link, new CategoryController.GenericCallback() {
                                    @Override
                                    public void onSuccess(LogActiveApiResponse response) {
                                        Log.d("BottomSheetActivity", "Second API (logCouponActivation) successful: " + response.getMessage());
                                        isProgressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Log.e("BottomSheetActivity", "Second API failed: " + errorMessage);
                                    }
                                });

                                Intent intent = new Intent(BottomSheetActivity.this, BottomSheetOrderActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("offer", offer);
                                intent.putExtra("coupon_code", couponCode);
                                intent.putExtra("CTAname", CTAname);
                                intent.putExtra("CTAredirect", CTAredirect);
                                intent.putExtra("redirect_url", redirectUrl);
                                intent.putExtra("merchant_logo", merchantLogo);
                                intent.putExtra("category_name", category_name);
                                intent.putExtra("how_to_use", howToUseText);
                                intent.putExtra("Terms and Conditions", termsAndConditions);
                                intent.putExtra("thumbnail", thumbnail);
                                intent.putExtra("merchant_name", merchant_name);
                                intent.putExtra("coupon_list", (Serializable) coupons);
                                intent.putExtra("offer_show", offer_text.getText().toString());
                                startActivity(intent);
                            } else {
                                Toast.makeText(BottomSheetActivity.this, "No rewards found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(BottomSheetActivity.this, "Coupon already activated", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(BottomSheetActivity.this, "Activation Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        imagetopleft.setOnClickListener(view -> onBackPressed());


        rememberSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    expandableText.setVisibility(View.VISIBLE);
                    rememberSymbol.setImageResource(R.drawable.ic_up_arrow);

                    expandabletext1.setVisibility(View.GONE);
                    remembersymbol1.setImageResource(R.drawable.ic_down_black);
                    isExpanded1 = false;
                } else {
                    expandableText.setVisibility(View.GONE);
                    rememberSymbol.setImageResource(R.drawable.ic_down_black);
                }
                isExpanded = !isExpanded;
            }
        });

        remembersymbol1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded1) {
                    expandabletext1.setVisibility(View.VISIBLE);
                    remembersymbol1.setImageResource(R.drawable.ic_up_arrow);

                    expandableText.setVisibility(View.GONE);
                    rememberSymbol.setImageResource(R.drawable.ic_down_black);
                    isExpanded = false;
                } else {
                    expandabletext1.setVisibility(View.GONE);
                    remembersymbol1.setImageResource(R.drawable.ic_down_black);
                }
                isExpanded1 = !isExpanded1;
            }
        });
    }



        private String convertHtmlToBullets(String htmlContent) {
        String cleaned = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT).toString();

        String[] lines = cleaned.split("\\n|(?<=\\.)\\s*");

        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                builder.append("• ").append(line).append("\n");
            }
        }
        return builder.toString().trim();
    }

    @Override
    protected void onResume() {
        isProgressBar.setVisibility(View.GONE);
        super.onResume();
    }
}

