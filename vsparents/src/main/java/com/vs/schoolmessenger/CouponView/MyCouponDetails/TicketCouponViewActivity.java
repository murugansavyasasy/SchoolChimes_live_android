package com.vs.schoolmessenger.CouponView.MyCouponDetails;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.CouponController.CategoryController;
import com.vs.schoolmessenger.CouponModel.TicketCouponSummary.TicketSummary;
import com.vs.schoolmessenger.CouponView.Adapter.TicketCouponAdapter;
import com.vs.schoolmessenger.CouponView.CouponDashboard.CouponMainClassActivity;
import com.vs.schoolmessenger.R;

import java.util.List;

public class TicketCouponViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TicketCouponAdapter adapter;
    private CategoryController categoryController;
    private ImageView btnHome;
    private ImageView btnTicket;
    private RelativeLayout homeBackground, ticketBackground;
    private TextView alltext, activetext, expiredtext, redeemedtext;
    private int blueColor;
    private int defaultColor;

    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_coupon);


        btnHome = findViewById(R.id.btnHome);
        back = findViewById(R.id.back);
        btnTicket = findViewById(R.id.btnTicket);
        homeBackground = findViewById(R.id.homeBackground);
        ticketBackground = findViewById(R.id.ticketBackground);
        alltext = findViewById(R.id.alltext);
        activetext = findViewById(R.id.activetext);
        expiredtext = findViewById(R.id.expiredtext);
        redeemedtext = findViewById(R.id.redeemedtext);

        blueColor = ContextCompat.getColor(this, R.color.gnt_blue);
        defaultColor = ContextCompat.getColor(this, R.color.gnt_gray);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryController = new CategoryController(this);


        HomeLoad();
        selectTabAndFetch(alltext, "all");

        back.setOnClickListener(view -> {
            Intent intent = new Intent(TicketCouponViewActivity.this, CouponMainClassActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnHome.setOnClickListener(view -> {
            homeBackground.setBackgroundResource(R.drawable.bg_selected);
            ticketBackground.setBackgroundColor(Color.TRANSPARENT);
            btnHome.setColorFilter(blueColor, PorterDuff.Mode.SRC_IN);
            btnTicket.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
            Intent intent = new Intent(TicketCouponViewActivity.this, CouponMainClassActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnTicket.setOnClickListener(view -> {
            ticketBackground.setBackgroundResource(R.drawable.bg_selected);
            homeBackground.setBackgroundColor(Color.TRANSPARENT);
            btnTicket.setColorFilter(blueColor, PorterDuff.Mode.SRC_IN);
            btnHome.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
            Intent intent = new Intent(TicketCouponViewActivity.this, TicketCouponViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        alltext.setOnClickListener(view -> selectTabAndFetch(alltext, "all"));
        activetext.setOnClickListener(view -> selectTabAndFetch(activetext, "activated"));
        expiredtext.setOnClickListener(view -> selectTabAndFetch(expiredtext, "expired"));
        redeemedtext.setOnClickListener(view -> selectTabAndFetch(redeemedtext, "claimed"));
    }

    private void HomeLoad() {
        btnTicket.setColorFilter(ContextCompat.getColor(this, R.color.gnt_blue), PorterDuff.Mode.SRC_IN);
        ticketBackground.setBackgroundResource(R.drawable.bg_selected);
        homeBackground.setBackgroundColor(Color.TRANSPARENT);
    }

    private void selectTabAndFetch(TextView selectedTab, String couponStatus) {

        alltext.setBackgroundColor(Color.TRANSPARENT);
        activetext.setBackgroundColor(Color.TRANSPARENT);
        expiredtext.setBackgroundColor(Color.TRANSPARENT);
        redeemedtext.setBackgroundColor(Color.TRANSPARENT);


        selectedTab.setBackgroundResource(R.drawable.white_radious);


        categoryController.fetchTicketCouponSummary(couponStatus, new CategoryController.TicketCouponSummaryCallback() {
            @Override
            public void onSuccess(List<TicketSummary> coupon_list) {
                adapter = new TicketCouponAdapter(TicketCouponViewActivity.this, coupon_list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(TicketCouponViewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
