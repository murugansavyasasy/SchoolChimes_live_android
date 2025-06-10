package com.vs.schoolmessenger.CouponView.MycouponDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.CouponView.MyCouponDetails.TicketCouponViewActivity;
import com.vs.schoolmessenger.R;

public class MycouponViewActivity extends AppCompatActivity {

    private ImageView back;
    private String merchant_name;
    private String offer_to_show;
    private String how_to_use;
    private String coupon_code;
    private TextView header;
    private TextView offer,description,couponCode;
    private ImageView logo;
    private String cover_image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mycoupon_view);
        back = findViewById(R.id.back);

        header = findViewById(R.id.header);
        offer = findViewById(R.id.offer);
        description = findViewById(R.id.description);
        couponCode =findViewById(R.id.couponCode);
        logo = findViewById(R.id.logo);

        merchant_name = getIntent().getStringExtra("merchant_name");
        offer_to_show = getIntent().getStringExtra("offer_to_show");
        how_to_use = getIntent().getStringExtra("how_to_use");
        coupon_code = getIntent().getStringExtra("coupon_code");
        cover_image = getIntent().getStringExtra("cover_image");

        header.setText(merchant_name);
        offer.setText(offer_to_show);
        description.setText(how_to_use);
        couponCode.setText(coupon_code);

        Glide.with(MycouponViewActivity.this)
                .load(cover_image)
                .into(logo);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(MycouponViewActivity.this, TicketCouponViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });


    }
}
