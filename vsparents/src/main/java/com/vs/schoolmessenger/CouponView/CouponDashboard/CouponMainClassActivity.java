package com.vs.schoolmessenger.CouponView.CouponDashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.CouponController.CategoryController;
import com.vs.schoolmessenger.CouponModel.CouponCoin.PointsData;
import com.vs.schoolmessenger.CouponModel.CouponMenu.Category;
import com.vs.schoolmessenger.CouponModel.CouponSummary.Summary;
import com.vs.schoolmessenger.CouponView.Adapter.CouponMenuAdapter;
import com.vs.schoolmessenger.CouponView.Adapter.CouponSummaryAdapter;
import com.vs.schoolmessenger.CouponView.MyCouponDetails.TicketCouponViewActivity;
import com.vs.schoolmessenger.R;

import java.util.ArrayList;
import java.util.List;

public class CouponMainClassActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private CouponSummaryAdapter adapter1;
    private ImageView btnHome;
    private EditText editSearch;
    private ImageView btnTicket;
    private CouponMenuAdapter adapter;
    private CategoryController categoryController;
    private RelativeLayout homeBackground, ticketBackground;
    private RelativeLayout relative_layout;
    private TextView totalcoins, usedcoins, availablecoins;
    private int pointsRemaining = 0;
    private int pointPerCoupon = 0;

    ProgressBar isProgressBar;
    TextView lblNoRecord, text_view;
    LinearLayout lnrPoints;
    int blueColor;
    int defaultColor;
    private List<Summary> originalSummaryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_dashboard);
        recyclerView = findViewById(R.id.recyclerview1);
        recyclerView1 = findViewById(R.id.recyclerView);
        btnHome = findViewById(R.id.btnHome);
        btnTicket = findViewById(R.id.btnTicket);
        totalcoins = findViewById(R.id.totalcoins);
        usedcoins = findViewById(R.id.usedcoins);
        availablecoins = findViewById(R.id.availablecoins);
        homeBackground = findViewById(R.id.homeBackground);
        ticketBackground = findViewById(R.id.ticketBackground);
        relative_layout = findViewById(R.id.relative_layout);
        editSearch = findViewById(R.id.editSearch);
        isProgressBar = findViewById(R.id.isProgressBar);
        lblNoRecord = findViewById(R.id.lblNoRecord);
        text_view = findViewById(R.id.text_view);
        lnrPoints = findViewById(R.id.lnrPoints);

        blueColor = ContextCompat.getColor(this, R.color.gnt_blue);
        defaultColor = ContextCompat.getColor(this, R.color.gnt_gray);

        btnHome.setImageDrawable(getDrawable(R.drawable.homeimage));
        btnTicket.setImageDrawable(getDrawable(R.drawable.ticketimage));

        homeBackground.setBackgroundResource(R.drawable.bg_selected);
        ticketBackground.setBackgroundColor(Color.TRANSPARENT);

        btnHome.setOnClickListener(view -> {
            homeBackground.setBackgroundResource(R.drawable.bg_selected);
            ticketBackground.setBackgroundColor(Color.TRANSPARENT);

            btnHome.setImageDrawable(getDrawable(R.drawable.homeimage));
            btnTicket.setImageDrawable(getDrawable(R.drawable.ticketimage));
            Intent intent = new Intent(CouponMainClassActivity.this, CouponMainClassActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnTicket.setOnClickListener(view -> {
            ticketBackground.setBackgroundResource(R.drawable.bg_selected);
            homeBackground.setBackgroundColor(Color.TRANSPARENT);

            btnHome.setImageDrawable(getDrawable(R.drawable.home_gray));
            btnTicket.setImageDrawable(getDrawable(R.drawable.ticket_blue));
            Intent intent = new Intent(CouponMainClassActivity.this, TicketCouponViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView1.setLayoutManager(new GridLayoutManager(this, 2));


        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadInitialData() {
        categoryController = new CategoryController(this);

        categoryController.fetchCoinDetails(new CategoryController.PointsCouponCallback() {
            @Override
            public void onSuccess(PointsData pointsData) {
                if (pointsData != null) {
                    lnrPoints.setVisibility(View.VISIBLE);
                    totalcoins.setText("" + pointsData.getPointsEarned());
                    usedcoins.setText("Used: " + pointsData.getPointsSpent());
                    availablecoins.setText("Available: " + pointsData.getPointsRemaining());

                    pointsRemaining = pointsData.getPointsRemaining();
                    pointPerCoupon = pointsData.getPointsPerCoupon();
                } else {
                    lnrPoints.setVisibility(View.GONE);
                    Toast.makeText(CouponMainClassActivity.this, "No point data available", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(String errorMessage) {
                lnrPoints.setVisibility(View.GONE);
                Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        CouponMenuAdapter.OnCategoryClickListener categoryClickListener = category -> {
            if (category.getId() == -1) {
                text_view.setText("All Rewards");
                isProgressBar.setVisibility(View.VISIBLE);
                categoryController.fetchCouponSummary(new CategoryController.CouponSummaryCallback() {
                    @Override
                    public void onSuccess(List<Summary> campaigns) {
                        originalSummaryList = new ArrayList<>(campaigns);
                        isProgressBar.setVisibility(View.GONE);
                        if (campaigns.isEmpty()) {
                            lblNoRecord.setVisibility(View.VISIBLE);
                            recyclerView1.setVisibility(View.GONE);
                        } else {
                            lblNoRecord.setVisibility(View.GONE);
                            recyclerView1.setVisibility(View.VISIBLE);
                        }

                        adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns,pointsRemaining,pointPerCoupon);
                        recyclerView1.setAdapter(adapter1);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                isProgressBar.setVisibility(View.VISIBLE);
                text_view.setText(category.getCategoryName() + " " + "Rewards");
                String categoryId = String.valueOf(category.getId());
                categoryController.fetchCategoryCouponSummary(categoryId, new CategoryController.CategoryCouponSummaryCallback() {
                    @Override
                    public void onSuccess(List<Summary> campaigns) {
                        originalSummaryList = new ArrayList<>(campaigns);
                        isProgressBar.setVisibility(View.GONE);
                        if (campaigns.isEmpty()) {
                            lblNoRecord.setVisibility(View.VISIBLE);
                            recyclerView1.setVisibility(View.GONE);
                        } else {
                            lblNoRecord.setVisibility(View.GONE);
                            recyclerView1.setVisibility(View.VISIBLE);
                        }
                        adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns,pointsRemaining,pointPerCoupon);
                        recyclerView1.setAdapter(adapter1);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        isProgressBar.setVisibility(View.VISIBLE);
        categoryController.fetchCategories(new CategoryController.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                Category hardcodedCategory = new Category();
                hardcodedCategory.setId(-1);
                hardcodedCategory.setCategoryName("All");
                text_view.setText("All Rewards");
                isProgressBar.setVisibility(View.GONE);
                hardcodedCategory.setDrawableResId(R.drawable.allimage);
                categories.add(0, hardcodedCategory);

                adapter = new CouponMenuAdapter(CouponMainClassActivity.this, categories, 0, category -> {
                    if (category.getId() == -1) {
                        text_view.setText("All Rewards");
                        categoryController.fetchCouponSummary(new CategoryController.CouponSummaryCallback() {
                            @Override
                            public void onSuccess(List<Summary> campaigns) {
                                originalSummaryList = new ArrayList<>(campaigns);
                                if (campaigns.isEmpty()) {
                                    lblNoRecord.setVisibility(View.VISIBLE);
                                    recyclerView1.setVisibility(View.GONE);
                                } else {
                                    lblNoRecord.setVisibility(View.GONE);
                                    recyclerView1.setVisibility(View.VISIBLE);
                                }
                                adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns,pointsRemaining,pointPerCoupon);
                                recyclerView1.setAdapter(adapter1);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        text_view.setText(category.getCategoryName() + " " + "Rewards");
                        isProgressBar.setVisibility(View.VISIBLE);
                        String categoryId = String.valueOf(category.getId());
                        categoryController.fetchCategoryCouponSummary(categoryId, new CategoryController.CategoryCouponSummaryCallback() {
                            @Override
                            public void onSuccess(List<Summary> campaigns) {
                                isProgressBar.setVisibility(View.GONE);
                                if (campaigns.isEmpty()) {
                                    lblNoRecord.setVisibility(View.VISIBLE);
                                    recyclerView1.setVisibility(View.GONE);
                                } else {
                                    lblNoRecord.setVisibility(View.GONE);
                                    recyclerView1.setVisibility(View.VISIBLE);
                                }
                                originalSummaryList = new ArrayList<>(campaigns);

                                adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns,pointsRemaining,pointPerCoupon);
                                recyclerView1.setAdapter(adapter1);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                recyclerView.setAdapter(adapter);
                if (!categories.isEmpty()) {
                    categoryClickListener.onCategoryClick(categories.get(0));
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        List<Summary> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(originalSummaryList);
        } else {
            String searchText = text.toLowerCase().trim();
            for (Summary item : originalSummaryList) {
                if (item.getMerchantName().toLowerCase().contains(searchText) || item.getCategoryName().toLowerCase().contains(searchText)) {
                    filteredList.add(item);
                }
            }
        }

        if (adapter1 != null) {
            adapter1.updateList(filteredList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInitialData();
    }

}


