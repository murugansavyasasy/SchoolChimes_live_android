package com.vs.schoolmessenger.CouponView;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.CouponController.CategoryController;
import com.vs.schoolmessenger.CouponModel.CouponMenu.Category;
import com.vs.schoolmessenger.CouponModel.CouponSummary.Summary;
import com.vs.schoolmessenger.CouponView.Adapter.CouponMenuAdapter;
import com.vs.schoolmessenger.CouponView.Adapter.CouponSummaryAdapter;
import com.vs.schoolmessenger.R;

import java.util.List;

public class CouponMainClassActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private RecyclerView recyclerView1;

    private CouponSummaryAdapter adapter1;


    private CouponMenuAdapter adapter;
    private CategoryController categoryController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_dashboard);

        recyclerView = findViewById(R.id.recyclerview1);

        recyclerView1 = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView1.setLayoutManager(new GridLayoutManager(this, 2));


        categoryController = new CategoryController(this);
        categoryController.fetchCategories(new CategoryController.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                adapter = new CouponMenuAdapter(CouponMainClassActivity.this, categories);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        categoryController.fetchCouponSummary(new CategoryController.CouponSummaryCallback() {
            @Override
            public void onSuccess(List<Summary> campaigns) {
                adapter1 = new CouponSummaryAdapter(CouponMainClassActivity.this, campaigns);
                recyclerView1.setAdapter(adapter1);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CouponMainClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
