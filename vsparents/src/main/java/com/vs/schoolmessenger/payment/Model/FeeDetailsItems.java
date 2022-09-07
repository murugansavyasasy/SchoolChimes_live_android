package com.vs.schoolmessenger.payment.Model;

import java.io.Serializable;
import java.util.List;

public class FeeDetailsItems implements Serializable {


    /**
     * Status : 1
     * Message : Success
     * StudentId : 5
     * SchoolID : 2
     * onlinepayment : Yes
     * RazorPay : 1.7
     * VSpay : 0
     * VSpercentage : 0.5
     * TermFeesDisplayName : Term Fees
     * OtherFeesDisplayName : Other / Monthly Fees
     * TermFees : [{"TermName":"Term 1","TermGroupTypeId":"2","FeesDetails":[{"FeeId":"1","FeeName":"Tuition fee","FeeAmount":"7","IsChecked":"NO","Paid":"","Pending":"7","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"1.1","TermGroupTypeId":"2","AmountToBePaid":"5.9","AmountToBePaidRP":590,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.10030000000000001","InProgress":"0","Processed":"0","BankCharges":"0.45"},{"FeeId":"2","FeeName":"Book fee","FeeAmount":"20","IsChecked":"NO","Paid":"","Pending":"20","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"2.4","TermGroupTypeId":"2","AmountToBePaid":"17.6","AmountToBePaidRP":1760.0000000000002,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.2992","InProgress":"0","Processed":"0","BankCharges":"0.45"},{"FeeId":"3","FeeName":"Application Fee","FeeAmount":"25","IsChecked":"NO","Paid":"","Pending":"25","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"11.1","TermGroupTypeId":"2","AmountToBePaid":"13.9","AmountToBePaidRP":1390,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.2363","InProgress":"0","Processed":"0","BankCharges":"0.45"}]},{"TermName":"Term 2","TermGroupTypeId":"3","FeesDetails":[{"FeeId":"1","FeeName":"Tuition fee","FeeAmount":"9","IsChecked":"NO","Paid":"","Pending":"9","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"1","TermGroupTypeId":"3","AmountToBePaid":"8","AmountToBePaidRP":800,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.136","InProgress":"0","Processed":"0","BankCharges":"0.45"},{"FeeId":"3","FeeName":"Application Fee","FeeAmount":"12","IsChecked":"NO","Paid":"","Pending":"12","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"2","TermGroupTypeId":"3","AmountToBePaid":"10","AmountToBePaidRP":1000,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.17","InProgress":"0","Processed":"0","BankCharges":"0.45"},{"FeeId":"2","FeeName":"Book fee","FeeAmount":"20","IsChecked":"NO","Paid":"","Pending":"20","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"2","TermGroupTypeId":"3","AmountToBePaid":"18","AmountToBePaidRP":1800,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.30600000000000005","InProgress":"0","Processed":"0","BankCharges":"0.45"}]}]
     * OtherFees : [{"TermName":"Others","TermGroupTypeId":"1","FeeDetails":[{"FeeId":"6","FeeName":"Dance Fee","FeeAmount":"25","TermGroupTypeId":"1","Paid":"0","Pending":"25","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"5","AmountToBePaid":"20","InProgress":"0","Processed":"0","BankCharges":"0.45","MonthDetails":[{"MonthName":"November","MonthId":"5","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"December","MonthId":"6","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"January","MonthId":"7","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"February","MonthId":"8","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"}]}]},{"TermName":"Others","TermGroupTypeId":"1","FeeDetails":[{"FeeId":"5","FeeName":"Yoga fee","FeeAmount":"20","TermGroupTypeId":"1","Paid":"0","Pending":"20","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"4","AmountToBePaid":"16","InProgress":"0","Processed":"0","BankCharges":"0.45","MonthDetails":[{"MonthName":"December","MonthId":"1","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"5","DiscountAmount":"1","Paid":"0","AmountToBePaid":"4","AmountToBePaidRP":400,"RazorPayAmount":"0.068","Pending":"0","isPaid":"No"},{"MonthName":"January","MonthId":"2","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"5","DiscountAmount":"1","Paid":"0","AmountToBePaid":"4","AmountToBePaidRP":400,"RazorPayAmount":"0.068","Pending":"0","isPaid":"No"},{"MonthName":"February","MonthId":"3","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"5","DiscountAmount":"1","Paid":"0","AmountToBePaid":"4","AmountToBePaidRP":400,"RazorPayAmount":"0.068","Pending":"0","isPaid":"No"},{"MonthName":"March","MonthId":"4","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"5","DiscountAmount":"1","Paid":"0","AmountToBePaid":"4","AmountToBePaidRP":400,"RazorPayAmount":"0.068","Pending":"0","isPaid":"No"}]}]}]
     */

    private String Status;
    private String Message;
    private String StudentId;
    private String SchoolID;
    private String onlinepayment;
    private String RazorPay;
    private String RazorPayKeyId;
    private String RazorPayApiKey;
    private String VSpay;
    private String VSpayRP;
    private String VSpercentage;
    private String VSaccountid;
    private String TermFeesDisplayName;
    private String OtherFeesDisplayName;
    private String isLateFeeConfig;
    private  String LateFeeDisplayName;

    private String OtherFeesMonthDisplayName;
    private String OtherFeesAmountPerMonthDisplayName;
    private String OtherFeesPaidDisplayName;
    private String OtherFeesPendingDisplayName;
    private String OtherFeesDiscountDisplayName;
    private String OtherFeesAmounttobePaidDisplayName;
    private String TermFeesFeeNameDisplayName;
    private String TermFeesFeeCostDisplayName;
    private String TermFeesAmountPerMonthDisplayName;
    private String TermFeesPaidDisplayName;
    private String TermFeesPendingDisplayName;
    private String TermFeesDiscountDisplayName;
    private String TermFeesAmounttobePaidDisplayName;
    private String OnlineFeesTotalAmountDisplayName;
    private String OnlineFeesOtherAmountDisplayName;
    private String PaymentStatusDisplayName;
    private String OnlineFeesAmountToBePaidDisplayName;


    public String getVSaccountid() {
        return VSaccountid;
    }
    public String getVSpayRP() {
        return VSpayRP;
    }

    public String getRazorPayKeyId() {
        return RazorPayKeyId;
    }

    public String getRazorPayApiKey() {
        return RazorPayApiKey;
    }

    public String getOnlineFeesAmountToBePaidDisplayName() {
        return OnlineFeesAmountToBePaidDisplayName;
    }


    public String getPaymentStatusDisplayName() {
        return PaymentStatusDisplayName;
    }


    public String getTermFeesFeeNameDisplayName() {
        return TermFeesFeeNameDisplayName;
    }
    public String getTermFeesFeeCostDisplayName() {
        return TermFeesFeeCostDisplayName;
    }
    public String getTermFeesAmountPerMonthDisplayName() {
        return TermFeesAmountPerMonthDisplayName;
    }
    public String getTermFeesPaidDisplayName() {
        return TermFeesPaidDisplayName;
    }
    public String getTermFeesPendingDisplayName() {
        return TermFeesPendingDisplayName;
    }
    public String getTermFeesDiscountDisplayName() {
        return TermFeesDiscountDisplayName;
    }
    public String getTermFeesAmounttobePaidDisplayName() {
        return TermFeesAmounttobePaidDisplayName;
    }
    public String getOnlineFeesTotalAmountDisplayName() {
        return OnlineFeesTotalAmountDisplayName;
    }
    public String getOnlineFeesOtherAmountDisplayName() {
        return OnlineFeesOtherAmountDisplayName;
    }


    public String getOtherFeesMonthDisplayName() {
        return OtherFeesMonthDisplayName;
    }

    public String getOtherFeesAmountPerMonthDisplayName() {
        return OtherFeesAmountPerMonthDisplayName;
    }
    public String getOtherFeesPaidDisplayName() {
        return OtherFeesPaidDisplayName;
    }
    public String getOtherFeesPendingDisplayName() {
        return OtherFeesPendingDisplayName;
    }
    public String getOtherFeesDiscountDisplayName() {
        return OtherFeesDiscountDisplayName;
    }
    public String getOtherFeesAmounttobePaidDisplayName() {
        return OtherFeesAmounttobePaidDisplayName;
    }

    private List<TermFeesBean> TermFees;
    private List<OtherFeesBean> OtherFees;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(String schoolID) {
        SchoolID = schoolID;
    }

    public String getOnlinepayment() {
        return onlinepayment;
    }

    public void setOnlinepayment(String onlinepayment) {
        this.onlinepayment = onlinepayment;
    }

    public String getRazorPay() {
        return RazorPay;
    }

    public void setRazorPay(String razorPay) {
        RazorPay = razorPay;
    }

    public String getVSpay() {
        return VSpay;
    }

    public void setVSpay(String vSpay) {
        VSpay = vSpay;
    }

    public String getVSpercentage() {
        return VSpercentage;
    }

    public void setVSpercentage(String vSpercentage) {
        VSpercentage = vSpercentage;
    }

    public String getTermFeesDisplayName() {
        return TermFeesDisplayName;
    }

    public void setTermFeesDisplayName(String termFeesDisplayName) {
        TermFeesDisplayName = termFeesDisplayName;
    }

    public String getOtherFeesDisplayName() {
        return OtherFeesDisplayName;
    }

    public void setOtherFeesDisplayName(String otherFeesDisplayName) {
        OtherFeesDisplayName = otherFeesDisplayName;
    }

    public String getIsLateFeeConfig() {
        return isLateFeeConfig;
    }

    public void setIsLateFeeConfig(String lateFeeConfig) {
        isLateFeeConfig = lateFeeConfig;
    }

    public String getLateFeeDisplayName() {
        return LateFeeDisplayName;
    }

    public void setLateFeeDisplayName(String lateFee) {
        LateFeeDisplayName = lateFee;
    }

    public List<TermFeesBean> getTermFees() {
        return TermFees;
    }

    public void setTermFees(List<TermFeesBean> termFees) {
        TermFees = termFees;
    }

    public List<OtherFeesBean> getOtherFees() {
        return OtherFees;
    }

    public void setOtherFees(List<OtherFeesBean> otherFees) {
        OtherFees = otherFees;
    }

    public static class TermFeesBean {
        /**
         * TermName : Term 1
         * TermGroupTypeId : 2
         * FeesDetails : [{"FeeId":"1","FeeName":"Tuition fee","FeeAmount":"7","IsChecked":"NO","Paid":"","Pending":"7","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"1.1","TermGroupTypeId":"2","AmountToBePaid":"5.9","AmountToBePaidRP":590,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.10030000000000001","InProgress":"0","Processed":"0","BankCharges":"0.45"},{"FeeId":"2","FeeName":"Book fee","FeeAmount":"20","IsChecked":"NO","Paid":"","Pending":"20","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"2.4","TermGroupTypeId":"2","AmountToBePaid":"17.6","AmountToBePaidRP":1760.0000000000002,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.2992","InProgress":"0","Processed":"0","BankCharges":"0.45"},{"FeeId":"3","FeeName":"Application Fee","FeeAmount":"25","IsChecked":"NO","Paid":"","Pending":"25","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"11.1","TermGroupTypeId":"2","AmountToBePaid":"13.9","AmountToBePaidRP":1390,"AccountID":"acc_FF6kNPQfNYq1qk","RazorPayAmount":"0.2363","InProgress":"0","Processed":"0","BankCharges":"0.45"}]
         */

        private String TermName;
        private String TermGroupTypeId;
        private String IsPaidFully;
        private List<FeesDetailsBean> FeesDetails;

        private String isChecked = "NO";


        public String getIsChecked() {
            return isChecked;
        }

        public void setIsChecked(String isChecked) {
            this.isChecked = isChecked;
        }


        public String getIsPaidFully() {
            return IsPaidFully;
        }

        public void setIsPaidFully(String termName) {
            IsPaidFully = termName;
        }

        public String getTermName() {
            return TermName;
        }

        public void setTermName(String termName) {
            TermName = termName;
        }

        public String getTermGroupTypeId() {
            return TermGroupTypeId;
        }

        public void setTermGroupTypeId(String termGroupTypeId) {
            TermGroupTypeId = termGroupTypeId;
        }

        public List<FeesDetailsBean> getFeesDetails() {
            return FeesDetails;
        }

        public void setFeesDetails(List<FeesDetailsBean> feesDetails) {
            FeesDetails = feesDetails;
        }

        public static class FeesDetailsBean {
            /**
             * FeeId : 1
             * FeeName : Tuition fee
             * FeeAmount : 7
             * IsChecked : NO
             * Paid :
             * Pending : 7
             * DiscountAvailed : 0
             * ActualPaid : 0
             * DiscountAmount : 1.1
             * TermGroupTypeId : 2
             * AmountToBePaid : 5.9
             * AmountToBePaidRP : 590
             * AccountID : acc_FF6kNPQfNYq1qk
             * RazorPayAmount : 0.10030000000000001
             * InProgress : 0
             * Processed : 0
             * BankCharges : 0.45
             */

            private String FeeId;
            private String FeeName;
            private String FeeAmount;
            private String IsChecked;
            private String Paid;
            private String Pending;
            private String DiscountAvailed;
            private String ActualPaid;
            private String DiscountAmount;
            private String TermGroupTypeId;
            private String AmountToBePaid;
            private String AmountToBePaidRP;
            private String AccountID;
            private String RazorPayAmount;
            private String InProgress;
            private String Processed;
            private String BankCharges;
            private String OtherChargesRP;
            private String IsPaid;



            public String getIspaid() {
                return IsPaid;
            }

            public void setIspaid(String other) {
                IsPaid = other;
            }

            public String getOtherChargesRP() {
                return OtherChargesRP;
            }

            public void setOtherChargesRP(String other) {
                OtherChargesRP = other;
            }

            public String getFeeId() {
                return FeeId;
            }

            public void setFeeId(String feeId) {
                FeeId = feeId;
            }

            public String getFeeName() {
                return FeeName;
            }

            public void setFeeName(String feeName) {
                FeeName = feeName;
            }

            public String getFeeAmount() {
                return FeeAmount;
            }

            public void setFeeAmount(String feeAmount) {
                FeeAmount = feeAmount;
            }

            public String getIsChecked() {
                return IsChecked;
            }

            public void setIsChecked(String isChecked) {
                IsChecked = isChecked;
            }

            public String getPaid() {
                return Paid;
            }

            public void setPaid(String paid) {
                Paid = paid;
            }

            public String getPending() {
                return Pending;
            }

            public void setPending(String pending) {
                Pending = pending;
            }

            public String getDiscountAvailed() {
                return DiscountAvailed;
            }

            public void setDiscountAvailed(String discountAvailed) {
                DiscountAvailed = discountAvailed;
            }

            public String getActualPaid() {
                return ActualPaid;
            }

            public void setActualPaid(String actualPaid) {
                ActualPaid = actualPaid;
            }

            public String getDiscountAmount() {
                return DiscountAmount;
            }

            public void setDiscountAmount(String discountAmount) {
                DiscountAmount = discountAmount;
            }

            public String getTermGroupTypeId() {
                return TermGroupTypeId;
            }

            public void setTermGroupTypeId(String termGroupTypeId) {
                TermGroupTypeId = termGroupTypeId;
            }

            public String getAmountToBePaid() {
                return AmountToBePaid;
            }

            public void setAmountToBePaid(String amountToBePaid) {
                AmountToBePaid = amountToBePaid;
            }

            public String getAmountToBePaidRP() {
                return AmountToBePaidRP;
            }

            public void setAmountToBePaidRP(String amountToBePaidRP) {
                AmountToBePaidRP = amountToBePaidRP;
            }

            public String getAccountID() {
                return AccountID;
            }

            public void setAccountID(String accountID) {
                AccountID = accountID;
            }

            public String getRazorPayAmount() {
                return RazorPayAmount;
            }

            public void setRazorPayAmount(String razorPayAmount) {
                RazorPayAmount = razorPayAmount;
            }

            public String getInProgress() {
                return InProgress;
            }

            public void setInProgress(String inProgress) {
                InProgress = inProgress;
            }

            public String getProcessed() {
                return Processed;
            }

            public void setProcessed(String processed) {
                Processed = processed;
            }

            public String getBankCharges() {
                return BankCharges;
            }

            public void setBankCharges(String bankCharges) {
                BankCharges = bankCharges;
            }
        }
    }

    public static class OtherFeesBean {
        /**
         * TermName : Others
         * TermGroupTypeId : 1
         * FeeDetails : [{"FeeId":"6","FeeName":"Dance Fee","FeeAmount":"25","TermGroupTypeId":"1","Paid":"0","Pending":"25","DiscountAvailed":"0","ActualPaid":"0","DiscountAmount":"5","AmountToBePaid":"20","InProgress":"0","Processed":"0","BankCharges":"0.45","MonthDetails":[{"MonthName":"November","MonthId":"5","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"December","MonthId":"6","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"January","MonthId":"7","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"February","MonthId":"8","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"}]}]
         */

        private String TermName;
        private String TermGroupTypeId;
        private List<FeeDetailsBean> FeeDetails;

        public String getTermName() {
            return TermName;
        }

        public void setTermName(String termName) {
            TermName = termName;
        }

        public String getTermGroupTypeId() {
            return TermGroupTypeId;
        }

        public void setTermGroupTypeId(String termGroupTypeId) {
            TermGroupTypeId = termGroupTypeId;
        }

        public List<FeeDetailsBean> getFeeDetails() {
            return FeeDetails;
        }

        public void setFeeDetails(List<FeeDetailsBean> feeDetails) {
            FeeDetails = feeDetails;
        }

        public static class FeeDetailsBean {
            /**
             * FeeId : 6
             * FeeName : Dance Fee
             * FeeAmount : 25
             * TermGroupTypeId : 1
             * Paid : 0
             * Pending : 25
             * DiscountAvailed : 0
             * ActualPaid : 0
             * DiscountAmount : 5
             * AmountToBePaid : 20
             * InProgress : 0
             * Processed : 0
             * BankCharges : 0.45
             * MonthDetails : [{"MonthName":"November","MonthId":"5","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"December","MonthId":"6","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"January","MonthId":"7","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"},{"MonthName":"February","MonthId":"8","IsChecked":"NO","AccountID":"acc_FF6YIeuXG2mIcf","AmountPerMonth":"6","DiscountAmount":"1","Paid":"0","AmountToBePaid":"5","AmountToBePaidRP":500,"RazorPayAmount":"0.085","Pending":"0","isPaid":"No"}]
             */

            private String FeeId;
            private String FeeName;
            private String FeeAmount;
            private String TermGroupTypeId;
            private String Paid;
            private String Pending;
            private String DiscountAvailed;
            private String ActualPaid;
            private String DiscountAmount;
            private String AmountToBePaid;
            private String InProgress;
            private String Processed;
            private String BankCharges;
            private String IsPaidFully;
            private List<MonthDetailsBean> MonthDetails;

            private String isChecked = "NO";


            public String getIsPaidFully() {
                return IsPaidFully;
            }

            public void setIsPaidFully(String fully) {
                this.IsPaidFully = fully;
            }

            public String getIsChecked() {
                return isChecked;
            }

            public void setIsChecked(String isChecked) {
                this.isChecked = isChecked;
            }

            public String getFeeId() {
                return FeeId;
            }

            public void setFeeId(String feeId) {
                FeeId = feeId;
            }

            public String getFeeName() {
                return FeeName;
            }

            public void setFeeName(String feeName) {
                FeeName = feeName;
            }

            public String getFeeAmount() {
                return FeeAmount;
            }

            public void setFeeAmount(String feeAmount) {
                FeeAmount = feeAmount;
            }

            public String getTermGroupTypeId() {
                return TermGroupTypeId;
            }

            public void setTermGroupTypeId(String termGroupTypeId) {
                TermGroupTypeId = termGroupTypeId;
            }

            public String getPaid() {
                return Paid;
            }

            public void setPaid(String paid) {
                Paid = paid;
            }

            public String getPending() {
                return Pending;
            }

            public void setPending(String pending) {
                Pending = pending;
            }

            public String getDiscountAvailed() {
                return DiscountAvailed;
            }

            public void setDiscountAvailed(String discountAvailed) {
                DiscountAvailed = discountAvailed;
            }

            public String getActualPaid() {
                return ActualPaid;
            }

            public void setActualPaid(String actualPaid) {
                ActualPaid = actualPaid;
            }

            public String getDiscountAmount() {
                return DiscountAmount;
            }

            public void setDiscountAmount(String discountAmount) {
                DiscountAmount = discountAmount;
            }

            public String getAmountToBePaid() {
                return AmountToBePaid;
            }

            public void setAmountToBePaid(String amountToBePaid) {
                AmountToBePaid = amountToBePaid;
            }

            public String getInProgress() {
                return InProgress;
            }

            public void setInProgress(String inProgress) {
                InProgress = inProgress;
            }

            public String getProcessed() {
                return Processed;
            }

            public void setProcessed(String processed) {
                Processed = processed;
            }

            public String getBankCharges() {
                return BankCharges;
            }

            public void setBankCharges(String bankCharges) {
                BankCharges = bankCharges;
            }

            public List<MonthDetailsBean> getMonthDetails() {
                return MonthDetails;
            }

            public void setMonthDetails(List<MonthDetailsBean> monthDetails) {
                MonthDetails = monthDetails;
            }

            public static class MonthDetailsBean {
                /**
                 * MonthName : November
                 * MonthId : 5
                 * IsChecked : NO
                 * AccountID : acc_FF6YIeuXG2mIcf
                 * AmountPerMonth : 6
                 * DiscountAmount : 1
                 * Paid : 0
                 * AmountToBePaid : 5
                 * AmountToBePaidRP : 500
                 * RazorPayAmount : 0.085
                 * Pending : 0
                 * isPaid : No
                 */

                private String MonthName;
                private String MonthId;
                private String IsChecked;
                private String AccountID;
                private String AmountPerMonth;
                private String DiscountAmount;
                private String Paid;
                private String AmountToBePaid;
                private String AmountToBePaidRP;
                private String RazorPayAmount;
                private String Pending;
                private String IsPaid;
                private String BankCharges;
                private String OtherChargesRP;


                public String getOtherChargesRP() {
                    return OtherChargesRP;
                }

                public void setOtherChargesRP(String othercharges) {
                    OtherChargesRP = othercharges;
                }


                public String getBankCharges() {
                    return BankCharges;
                }

                public void setBankCharges(String charges) {
                    BankCharges = charges;
                }

                public String getMonthName() {
                    return MonthName;
                }

                public void setMonthName(String monthName) {
                    MonthName = monthName;
                }

                public String getMonthId() {
                    return MonthId;
                }

                public void setMonthId(String monthId) {
                    MonthId = monthId;
                }

                public String getIsChecked() {
                    return IsChecked;
                }

                public void setIsChecked(String isChecked) {
                    IsChecked = isChecked;
                }

                public String getAccountID() {
                    return AccountID;
                }

                public void setAccountID(String accountID) {
                    AccountID = accountID;
                }

                public String getAmountPerMonth() {
                    return AmountPerMonth;
                }

                public void setAmountPerMonth(String amountPerMonth) {
                    AmountPerMonth = amountPerMonth;
                }

                public String getDiscountAmount() {
                    return DiscountAmount;
                }

                public void setDiscountAmount(String discountAmount) {
                    DiscountAmount = discountAmount;
                }

                public String getPaid() {
                    return Paid;
                }

                public void setPaid(String paid) {
                    Paid = paid;
                }

                public String getAmountToBePaid() {
                    return AmountToBePaid;
                }

                public void setAmountToBePaid(String amountToBePaid) {
                    AmountToBePaid = amountToBePaid;
                }

                public String getAmountToBePaidRP() {
                    return AmountToBePaidRP;
                }

                public void setAmountToBePaidRP(String amountToBePaidRP) {
                    AmountToBePaidRP = amountToBePaidRP;
                }

                public String getRazorPayAmount() {
                    return RazorPayAmount;
                }

                public void setRazorPayAmount(String razorPayAmount) {
                    RazorPayAmount = razorPayAmount;
                }

                public String getPending() {
                    return Pending;
                }

                public void setPending(String pending) {
                    Pending = pending;
                }

                public String getIsPaid() {
                    return IsPaid;
                }

                public void setIsPaid(String isPaid) {
                    this.IsPaid = isPaid;
                }
            }
        }
    }
}
