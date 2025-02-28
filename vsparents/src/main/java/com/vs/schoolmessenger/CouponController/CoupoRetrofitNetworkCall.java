package com.vs.schoolmessenger.CouponController;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoupoRetrofitNetworkCall {

        private static final String BASE_URL = "https://stage-api.pauket.com/api/partner/voicesnaps/";
        private static Retrofit retrofit = null;

        public static Retrofit getClient() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
