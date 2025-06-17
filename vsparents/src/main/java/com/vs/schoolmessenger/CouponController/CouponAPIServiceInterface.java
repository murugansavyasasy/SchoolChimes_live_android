package com.vs.schoolmessenger.CouponController;

import com.vs.schoolmessenger.CouponModel.CouponCoin.PointsResponse;
import com.vs.schoolmessenger.CouponModel.CouponMenu.CouponMenuResponse;
import com.vs.schoolmessenger.CouponModel.CouponSummary.CouponSummaryResponse;
import com.vs.schoolmessenger.CouponModel.LogactiveapiResponse.LogActiveApiResponse;
import com.vs.schoolmessenger.CouponModel.TicketActivateCoupon.ActivateCouponResponse;
import com.vs.schoolmessenger.CouponModel.TicketActivateCouponSummary.ActivateCouponSummaryResponse;
import com.vs.schoolmessenger.CouponModel.TicketCouponSummary.TicketSummaryResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface CouponAPIServiceInterface {
    @GET("get_category_list")
    Call<CouponMenuResponse> getCategories(
            @Header("Partner-Name") String parentName,
            @Header("api-key") String apiKey
    );

    @POST("get_campaigns")
    Call<CouponSummaryResponse> getCoupons(
            @Header("Partner-Name") String parentName,
            @Header("api-key") String apiKey,
            @QueryMap Map<String, String> params
    );

    @POST("get_campaign_details")
    Call<ActivateCouponSummaryResponse> getActivateCouponsDetails(
            @Header("Partner-Name") String parentName,
            @Header("api-key") String apiKey,
            @QueryMap Map<String, String> params
    );

    @POST("activate_coupon")
    Call<ActivateCouponResponse> getactivateCoupon(
            @Header("Partner-Name") String parentName,
            @Header("api-key") String apiKey,
            @QueryMap Map<String, String> params
    );

    @POST("my_coupons")
    Call<TicketSummaryResponse> getTicketCoupons(
            @Header("Partner-Name") String parentName,
            @Header("api-key") String apiKey,
            @QueryMap Map<String, String> params
    );

    @GET("get-Points")
    Call<PointsResponse> getPointsCoupons(
            @Query("user_type") int user_type,
            @Query("mobile_number") String mobile_number
    );


    @POST("spent-points")
    Call<LogActiveApiResponse> getlogactiveresponse(
            @Body Map<String, Object> body
    );


    @POST("get_campaigns")
    Call<CouponSummaryResponse> getCategoryCoupons(
            @Header("Partner-Name") String parentName,
            @Header("api-key") String apiKey,
            @Body Map<String, String> body
    );

}


