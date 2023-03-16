package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CollectionData{

    @SerializedName("totalCollected")
    private TotalCollected totalCollected;

    @SerializedName("paymentTypeWise")
    private List<PaymentTypeWiseItem> paymentTypeWise;

    @SerializedName("previousYearFee")
    private List<PreviousYearFeeItem> previousYearFee;

    @SerializedName("currentYearFee")
    private List<CurrentYearFeeItem> currentYearFee;

        public void setTotalCollected(TotalCollected totalCollected){
            this.totalCollected = totalCollected;
        }

        public TotalCollected getTotalCollected(){
            return totalCollected;
        }

        public void setCurrentYearFee(List<CurrentYearFeeItem> currentYearFee){
            this.currentYearFee = currentYearFee;
        }

        public List<CurrentYearFeeItem> getCurrentYearFee(){
            return currentYearFee;
        }

        public void setPaymentTypeWise(List<PaymentTypeWiseItem> paymentTypeWise){
            this.paymentTypeWise = paymentTypeWise;
        }

        public List<PaymentTypeWiseItem> getPaymentTypeWise(){
            return paymentTypeWise;
        }

        public void setPreviousYearFee(List<PreviousYearFeeItem> previousYearFee){
            this.previousYearFee = previousYearFee;
        }

        public List<PreviousYearFeeItem> getPreviousYearFee(){
            return previousYearFee;
        }
}