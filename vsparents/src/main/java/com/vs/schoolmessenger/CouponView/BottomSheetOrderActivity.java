package com.vs.schoolmessenger.CouponView;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
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
import com.vs.schoolmessenger.CouponModel.TicketActivateCoupon.ActivateCoupon;
import com.vs.schoolmessenger.R;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BottomSheetOrderActivity extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Button btnactivatecoupon,btn_activate_coupon2;
    private LottieAnimationView lottieAnimationView;
    private Handler animationHandler = new Handler();
    private Runnable animationRunnable;

    private TextView expandableText,frame_text;
    private ImageView rememberSymbol, remembersymbol1, rememberSymbol2,thumbnailimage;
    private boolean isExpanded = false, isExpanded1 = false, isExpanded2 = false;
    private TextView expandabletext1, expandabletext5,offer_text1,offer_text2,expiry_text,desc_text;
    private String merchant_logo;
    private String offer;
    private String coupon_code;
    private String CTAname;
    private String CTAredirect;
    private String redirect_url;
    private String howToUseText;
    private String termsAndConditions;
    private String thumbnail;
    private String merchant_name;
    private String category_name;

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
         offer_text1 = findViewById(R.id.offer_text1);
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        expandableText = findViewById(R.id.expandable_text2);
        thumbnailimage = findViewById(R.id.thumbnail);

        expandabletext5 = findViewById(R.id.expandable_text5);
//        rememberSymbol = findViewById(R.id.remember_symbol3);
        remembersymbol1 = findViewById(R.id.remember_symbol4);
        rememberSymbol2 = findViewById(R.id.remember_symbol5);
        expiry_text = findViewById(R.id.expiry_text);
        offer_text2 = findViewById(R.id.offer_text2);
        desc_text = findViewById(R.id.desc_text);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        btnactivatecoupon = findViewById(R.id.btn_activate_coupon);
        btn_activate_coupon2 = findViewById(R.id.btn_activate_coupon2);
        ImageView imagetopleft = findViewById(R.id.image_top_left);

        animationView = findViewById(R.id.animationView);

        animationView.playAnimation();

        frame_text = findViewById(R.id.frame_text);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;


        ViewGroup.LayoutParams params = imageBanner.getLayoutParams();
        params.height = screenHeight / 2;
        imageBanner.setLayoutParams(params);

        ViewGroup.LayoutParams bottomSheetParams = bottomSheet.getLayoutParams();
        bottomSheet.setLayoutParams(bottomSheetParams);

        bottomSheetBehavior.setFitToContents(true);
        bottomSheetBehavior.setPeekHeight((int) (screenHeight * 0.6));
        merchant_logo = getIntent().getStringExtra("merchant_logo");
        offer = getIntent().getStringExtra("offer");
        coupon_code = getIntent().getStringExtra("coupon_code");
        CTAname = getIntent().getStringExtra("CTAname");
        CTAredirect = getIntent().getStringExtra("CTAredirect");
        redirect_url = getIntent().getStringExtra("redirect_url");
        howToUseText = getIntent().getStringExtra("how_to_use");
        thumbnail = getIntent().getStringExtra("thumbnail");
        merchant_name = getIntent().getStringExtra("merchant_name");
        termsAndConditions = getIntent().getStringExtra("Terms and Conditions");
        category_name = getIntent().getStringExtra("category_name");
        @SuppressWarnings("unchecked")
        List<ActivateCoupon> couponList = (List<ActivateCoupon>) getIntent().getSerializableExtra("coupon_list");
        if (couponList != null && !couponList.isEmpty()) {
            ActivateCoupon firstCoupon = couponList.get(0);
            String expiryDateString = firstCoupon.getExpiry_date();

            try {
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date expiryDate = apiFormat.parse(expiryDateString);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(expiryDate);

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(expiryDate);

                String suffix = getDaySuffix(day);

                String displayText = "Expires on " + day + suffix + " " + month;

                expiry_text.setText(displayText);

            } catch (ParseException e) {
                e.printStackTrace();
                expiry_text.setText("Invalid expiry date");
            }
        }

        frame_text.setText(coupon_code);
        expandableText.setText(convertHtmlToBullets(howToUseText));
        expandabletext5.setText(convertHtmlToBullets(termsAndConditions));
        offer_text2.setText(offer);
        offer_text1.setText(merchant_name);
        desc_text.setText(category_name);





        Glide.with(BottomSheetOrderActivity.this)
                .load(thumbnail)
                .into(imageBanner);

        Glide.with(BottomSheetOrderActivity.this)
                .load(merchant_logo)
                .into(thumbnailimage);




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

        imagetopleft.setOnClickListener(view -> onBackPressed());



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

    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (animationHandler != null && animationRunnable != null) {
            animationHandler.removeCallbacks(animationRunnable);
        }
    }
}
