package com.vs.schoolmessenger.CouponController;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CouponRetrofitNetworkCall {

    private static final String BASE_URL = "https://api.pauket.com/api/partner/";
    private static Retrofit defaultRetrofit = null;

    public static Retrofit getClient() {
        if (defaultRetrofit == null) {
            Log.d("Retrofit", "Creating Retrofit client with base URL: " + BASE_URL);

            defaultRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else {
            Log.d("Retrofit", "Returning existing Retrofit client with base URL: " + BASE_URL);
        }
        return defaultRetrofit;
    }

    public static Retrofit getClientWithBaseUrl(String baseUrl) {
        Log.d("Retrofit", "Creating Retrofit client with custom base URL: " + baseUrl);

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}


