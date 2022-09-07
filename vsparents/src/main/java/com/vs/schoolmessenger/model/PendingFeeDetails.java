package com.vs.schoolmessenger.model;

public class PendingFeeDetails {
    private String FeeName, Term_I,Term_II,Term_III,Term_IV,Total,Term1_From,Term1_To,Term2_From,Term2_To,Term3_From,Term3_To,Term4_From,Term4_To,FeeTerms;
    private String Monthly,yearlyFees,FeesID;

    public PendingFeeDetails(String feeName, String term_I,String term_II,String term_III, String term_IV,String total,String term1_From,String term1_To,
                             String term2_From,String term2_To,String term3_From,String term3_To, String term4_From,String term4_To,String feeTerms,String monthly,String yearlyfees,String feesID ) {
        this.FeeName = feeName;
        this.Term_I = term_I;
        this.Term_II = term_II;
        this.Term_III = term_III;
        this.Term_IV = term_IV;
        this.Total = total;
        this.Term1_From = term1_From;
        this.Term1_To = term1_To;
        this.Term2_From = term2_From;
        this.Term2_To = term2_To;
        this.Term3_From = term3_From;
        this.Term3_To = term3_To;
        this.Term4_From = term4_From;
        this.Term4_To = term4_To;
        this.FeeTerms = feeTerms;
        this.Monthly = monthly;
        this.yearlyFees = yearlyfees;
        this.FeesID = feesID;

        }


        public PendingFeeDetails() {}

    public String getFeeName() {
        return FeeName;
    }

    public void setFeeName(String fee) {
        this.FeeName = fee;
    }

    public String getTerm_I() {
        return Term_I;
    }

    public void setTerm_I(String term1) {
        this.Term_I = term1;
    }

    public String getTerm_II() {
        return Term_II;
    }

    public void setTerm_II(String term2) {
        this.Term_II = term2;
    }

    public String getTerm_III() {
        return Term_III;
    }

    public void setTerm_III(String term3) {
        this.Term_III = term3;
    }
    public String getTerm_IV() {
        return Term_IV;
    }

    public void setTerm_IV(String term4) {
        this.Term_IV = term4;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        this.Total = total;
    }


    public String getTerm1_From() {
        return Term1_From;
    }

    public void setTerm1_From(String term1from) {
        this.Term1_From = term1from;
    }


    public String getTerm1_To() {
        return Term1_To;
    }

    public void setTerm1_To(String term1To) {
        this.Term1_To = term1To;
    }




    public String getTerm2_From() {
        return Term2_From;
    }

    public void setTerm2_From(String term2from) {
        this.Term2_From = term2from;
    }

    public String getTerm2_To() {
        return Term2_To;
    }

    public void setTerm2_To(String term2To) {
        this.Term2_To = term2To;
    }

    public String getTerm3_From() {
        return Term3_From;
    }

    public void setTerm3_From(String term3from) {
        this.Term3_From = term3from;
    }
    public String getTerm3_To() {
        return Term3_To;
    }

    public void setTerm3_To(String term3To) {
        this.Term3_To = term3To;
    }

    public String getTerm4_From() {
        return Term4_From;
    }

    public void setTerm4_From(String term4from) {
        this.Term4_From = term4from;
    }

    public String getTerm4_To() {
        return Term4_To;
    }

    public void setTerm4_To(String term4to) {
        this.Term4_To = term4to;
    }

    public String getFeeTerms() {
        return FeeTerms;
    }

    public void setFeeTerms(String feeterm) {
        this.FeeTerms = feeterm;
    }


    public String getMonthly() {
        return Monthly;
    }

    public void setMonthly(String month) {
        this.Monthly = month;
    }



    public String getYearlyFees() {
        return yearlyFees;
    }

    public void setYearlyFees(String year) {
        this.yearlyFees = year;
    }

    public String getFeesID() {
        return FeesID;
    }

    public void setFeesID(String id) {
        this.FeesID = id;
    }
}




