package com.vs.schoolmessenger.rest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RazorpayClient {
    public static String BASE_URL = "https://api.razorpay.com/v1/payments/";   //live_URL

    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        {
            retrofit = builder.build();
        }
        return retrofit;
    }
    public static OkHttpClient getOK_Client() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        return client;
    }
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .client(getOK_Client())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        BASE_URL = newApiBaseUrl;

        builder = new Retrofit.Builder()
                .client(getOK_Client())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
    }

}

