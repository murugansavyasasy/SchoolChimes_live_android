package com.vs.schoolmessenger.CouponView.CouponDashboard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
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

    private List<Summary> originalSummaryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_dashboard);
        initializeViews();
        setupRecyclerViews();
        setupButtonListeners();
        loadInitialData();
        setupSearchFunctionality();
    }

    private void initializeViews() {
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
    }

    private void setupRecyclerViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView1.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setupButtonListeners() {
        int blueColor = ContextCompat.getColor(this, R.color.gnt_blue);
        int defaultColor = ContextCompat.getColor(this, R.color.gnt_gray);

        btnHome.setOnClickListener(view -> {
            homeBackground.setBackgroundResource(R.drawable.bg_selected);
            ticketBackground.setBackgroundColor(Color.TRANSPARENT);
            btnHome.setColorFilter(blueColor, PorterDuff.Mode.SRC_IN);
            btnTicket.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
            Intent intent = new Intent(CouponMainClassActivity.this, CouponMainClassActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnTicket.setOnClickListener(view -> {
            ticketBackground.setBackgroundResource(R.drawable.bg_selected);
            homeBackground.setBackgroundColor(Color.TRANSPARENT);
            btnTicket.setColorFilter(blueColor, PorterDuff.Mode.SRC_IN);
            btnHome.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
            Intent intent = new Intent(CouponMainClassActivity.this, TicketCouponViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        relative_layout.setOnClickListener(view -> onBackPressed());
    }

    private void loadInitialData() {
        Homeload();
        categoryController = new CategoryController(this);

        categoryController.fetchCoinDetails(new CategoryController.PointsCouponCallback() {
            @Override
            public void onSuccess(PointsData pointsData) {
                if (pointsData != null) {
                    totalcoins.setText("" + pointsData.getPointsEarned());
                    usedcoins.setText("Used: " + pointsData.getPointsSpent());
                    availablecoins.setText("Available: " + pointsData.getPointsRemaining());
                } else {
                    Toast.makeText(CouponMainClassActivity.this, "No point data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        CouponMenuAdapter.OnCategoryClickListener categoryClickListener = category -> {
            if (category.getId() == -1) {
                categoryController.fetchCouponSummary(new CategoryController.CouponSummaryCallback() {
                    @Override
                    public void onSuccess(List<Summary> campaigns) {
                        originalSummaryList = new ArrayList<>(campaigns);
                        adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns);
                        recyclerView1.setAdapter(adapter1);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                String categoryId = String.valueOf(category.getId());
                categoryController.fetchCategoryCouponSummary(categoryId, new CategoryController.CategoryCouponSummaryCallback() {
                    @Override
                    public void onSuccess(List<Summary> campaigns) {
                        originalSummaryList = new ArrayList<>(campaigns);
                        adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns);
                        recyclerView1.setAdapter(adapter1);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        categoryController.fetchCategories(new CategoryController.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                Category hardcodedCategory = new Category();
                hardcodedCategory.setId(-1);
                hardcodedCategory.setCategoryName("All");
                hardcodedCategory.setDrawableResId(R.drawable.allimage);
                categories.add(0, hardcodedCategory);

                adapter = new CouponMenuAdapter(CouponMainClassActivity.this, categories, 0, category -> {
                    if (category.getId() == -1) {
                        categoryController.fetchCouponSummary(new CategoryController.CouponSummaryCallback() {
                            @Override
                            public void onSuccess(List<Summary> campaigns) {
                                originalSummaryList = new ArrayList<>(campaigns);
                                adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns);
                                recyclerView1.setAdapter(adapter1);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        String categoryId = String.valueOf(category.getId());
                        categoryController.fetchCategoryCouponSummary(categoryId, new CategoryController.CategoryCouponSummaryCallback() {
                            @Override
                            public void onSuccess(List<Summary> campaigns) {
                                originalSummaryList = new ArrayList<>(campaigns);
                                adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns);
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

    private void setupSearchFunctionality() {
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

    private void filter(String text) {
        List<Summary> filteredList = new ArrayList<>();

        if (text.isEmpty()) {
            filteredList.addAll(originalSummaryList);
        } else {
            String searchText = text.toLowerCase().trim();
            for (Summary item : originalSummaryList) {
                if (item.getMerchantName().toLowerCase().contains(searchText) ||
                        item.getCategoryName().toLowerCase().contains(searchText)) {
                    filteredList.add(item);
                }
            }
        }

        if (adapter1 != null) {
            adapter1.updateList(filteredList);
        }
    }

    private void Homeload() {
        btnHome.setColorFilter(ContextCompat.getColor(this, R.color.gnt_blue), PorterDuff.Mode.SRC_IN);
        homeBackground.setBackgroundResource(R.drawable.bg_selected);
        ticketBackground.setBackgroundColor(Color.TRANSPARENT);
    }
}


