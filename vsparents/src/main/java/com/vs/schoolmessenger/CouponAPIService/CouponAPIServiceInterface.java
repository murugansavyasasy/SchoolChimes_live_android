package com.vs.schoolmessenger.CouponAPIService;

import com.vs.schoolmessenger.CouponResponse.CouponMenuResponse;
import com.vs.schoolmessenger.CouponResponse.CouponSummaryResponse;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;


public interface CouponAPIServiceInterface {
    @Headers({
            "Partner-Name: voicesnaps",
            "api-key: b9634e2c3aa9b6fdc392527645c43871"
    })
    @GET("get_category_list")
    Call<CouponSummaryResponse> getCategories();
    @GET("get_campaigns")
    Call<CouponMenuResponse> getCoupons(
            @HeaderMap Map<String, String> headers,
            @QueryMap Map<String, String> params
    );
}


