package com.vs.schoolmessenger.CouponView.MyCouponDetails;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;

public class TicketCouponViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TicketCouponAdapter adapter;
    private CategoryController categoryController;
    private ImageView btnHome;
    private ImageView btnTicket;
    private RelativeLayout homeBackground, ticketBackground;
    private TextView alltext, activetext, expiredtext, redeemedtext,backtext;
    int blueColor;
    int defaultColor;
    private EditText editSearch;
    private List<TicketSummary> originalSummaryViewList = new ArrayList<>();
    private ImageView back;
    ProgressBar isProgressBar;
    TextView lblNoRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_coupon);



        btnHome = findViewById(R.id.btnHome);
        back = findViewById(R.id.back);
        backtext = findViewById(R.id.backtext);
        btnTicket = findViewById(R.id.btnTicket);
        homeBackground = findViewById(R.id.homeBackground);
        ticketBackground = findViewById(R.id.ticketBackground);
        alltext = findViewById(R.id.alltext);
        activetext = findViewById(R.id.activetext);
        expiredtext = findViewById(R.id.expiredtext);
        redeemedtext = findViewById(R.id.redeemedtext);
        editSearch = findViewById(R.id.editSearch);
        isProgressBar = findViewById(R.id.isProgressBar);
        lblNoRecord = findViewById(R.id.lblNoRecord);

        blueColor = ContextCompat.getColor(this, R.color.gnt_blue);
        defaultColor = ContextCompat.getColor(this, R.color.gnt_gray);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryController = new CategoryController(this);

        setupSearchFunctionality();
        selectTabAndFetch(alltext, "all");

        back.setOnClickListener(view -> {
            onBackPressed();
        });

        backtext.setOnClickListener(view -> {
            onBackPressed();
        });

        btnHome.setImageDrawable(getDrawable(R.drawable.home_gray));
        btnTicket.setImageDrawable(getDrawable(R.drawable.ticket_blue));
        ticketBackground.setBackgroundResource(R.drawable.bg_selected);
        homeBackground.setBackgroundColor(Color.TRANSPARENT);

        btnHome.setOnClickListener(view -> {
            homeBackground.setBackgroundResource(R.drawable.bg_selected);

            ticketBackground.setBackgroundColor(Color.TRANSPARENT);

            btnHome.setImageDrawable(getDrawable(R.drawable.homeimage));
            btnTicket.setImageDrawable(getDrawable(R.drawable.ticketimage));

            Intent intent = new Intent(TicketCouponViewActivity.this, CouponMainClassActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnTicket.setOnClickListener(view -> {
            ticketBackground.setBackgroundResource(R.drawable.bg_selected);
            homeBackground.setBackgroundColor(Color.TRANSPARENT);
            btnHome.setImageDrawable(getDrawable(R.drawable.home_gray));
            btnTicket.setImageDrawable(getDrawable(R.drawable.ticket_blue));

            Intent intent = new Intent(TicketCouponViewActivity.this, TicketCouponViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        alltext.setOnClickListener(view -> selectTabAndFetch(alltext, "all"));
        activetext.setOnClickListener(view -> selectTabAndFetch(activetext, "activated"));
        expiredtext.setOnClickListener(view -> selectTabAndFetch(expiredtext, "expired"));
        redeemedtext.setOnClickListener(view -> selectTabAndFetch(redeemedtext, "claimed"));
    }

    private void setupSearchFunctionality() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        List<TicketSummary> filteredList = new ArrayList<>();

        if (text.isEmpty()) {
            filteredList.addAll(originalSummaryViewList);
        } else {
            String searchText = text.toLowerCase().trim();
            for (TicketSummary item : originalSummaryViewList) {
                if (item.getMerchant_name().toLowerCase().contains(searchText)) {
                    filteredList.add(item);
                } else if (item.getCampaign_name().toLowerCase().contains(searchText)) {
                    filteredList.add(item);
                }
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }


    private void selectTabAndFetch(TextView selectedTab, String couponStatus) {
        isProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        alltext.setBackgroundColor(Color.TRANSPARENT);
        activetext.setBackgroundColor(Color.TRANSPARENT);
        expiredtext.setBackgroundColor(Color.TRANSPARENT);
        redeemedtext.setBackgroundColor(Color.TRANSPARENT);


        selectedTab.setBackgroundResource(R.drawable.white_radious);
        categoryController.fetchTicketCouponSummary(couponStatus, new CategoryController.TicketCouponSummaryCallback() {
            @Override
            public void onSuccess(List<TicketSummary> coupon_list) {

                if (coupon_list.isEmpty()) {
                    lblNoRecord.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                } else {
                    lblNoRecord.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                originalSummaryViewList.clear();
                originalSummaryViewList.addAll(coupon_list);
                isProgressBar.setVisibility(View.GONE);
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
