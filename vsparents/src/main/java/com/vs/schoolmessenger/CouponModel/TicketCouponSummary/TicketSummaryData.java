package com.vs.schoolmessenger.CouponModel.TicketCouponSummary;

import com.google.gson.annotations.SerializedName;

public class TicketSummaryData  {
    @SerializedName("coupon_list")
    private TicketSummaryWrapper coupon_list;
    public TicketSummaryWrapper getCoupon_list() {
        return coupon_list;
    }

}
