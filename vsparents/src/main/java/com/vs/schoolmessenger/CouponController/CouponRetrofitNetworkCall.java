package com.vs.schoolmessenger.CouponController;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CouponRetrofitNetworkCall {

    private static final String BASE_URL = "https://api.pauket.com/api/partner/";
    private static Retrofit defaultRetrofit = null;

    public static Retrofit getClient() {
        if (defaultRetrofit == null) {
            defaultRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return defaultRetrofit;
    }


    public static Retrofit getClientWithBaseUrl(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

