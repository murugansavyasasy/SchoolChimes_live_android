package com.vs.schoolmessenger.payment;

import java.util.ArrayList;
import java.util.HashMap;

class ConstantManager {

    public static final String CHECK_BOX_CHECKED_TRUE = "YES";
    public static final String CHECK_BOX_CHECKED_FALSE = "NO";

    public static ArrayList<ArrayList<HashMap<String, String>>> childItems = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> parentItems = new ArrayList<>();

    public static ArrayList<ArrayList<HashMap<String, String>>> OtherchildItems = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> OtherparentItems = new ArrayList<>();


    public class Parameter {
        public static final String IS_CHECKED = "is_checked";
        public static final String MONTH_IS_CHECKED = "is_checked";
        public static final String SUB_CATEGORY_NAME = "sub_category_name";
        public static final String CATEGORY_NAME = "category_name";
        public static final String CATEGORY_ID = "category_id";
        public static final String SUB_ID = "sub_id";

        public static final String TermFeesName = "termName";
        public static final String TermGroupID = "termGroupID";
        public static final String TermFullyPaid = "termFullyPaid";


        public static final String FeeName = "FeeName";
        public static final String FeeId = "FeeId";
        public static final String FeeAmount = "FeeAmount";
        public static final String Paid = "paid";
        public static final String Pending = "pending";
        public static final String DiscountAMount = "discount";
        public static final String AmountToBePaid = "amounttobepaid";
        public static final String AccountID = "accountID";
        public static final String termGroupid = "groupid";
        public static final String AmountToBePaidRP = "AmountToBePaidRP";
        public static final String BankCharges = "bankcharges";
        public static final String OtherChargesRP = "otherChargesRP";
        public static final String IsPaid = "isPaid";

        //otherfees Details

        public static final String otherFeeName = "otherFeeName";
        public static final String otherFeeID = "otherFeeID";
        public static final String otherFeeAccountID = "otherFeeAccountID";
        public static final String otherFeeTermGroupID = "otherFeeTermGroupID";
        public static final String otherFeeDiscount = "otherFeeDiscount";
        public static final String otherFeePaidAmount = "otherFeePaidAmount";
        public static final String otherFeeAmountToBePaid = "otherFeeAmountToBePaid";
        public static final String otherFeeAmount = "otherFeeAmount";
        public static final String otherFeeFullyPaid = "otherFullyPaid";

        //MonthDetails



        public static final String MonthName = "monthName";
        public static final String MonthID = "monthID";
        public static final String AmountPerMonth = "permonth";
        public static final String MonthDiscount = "monthDiscount";
        public static final String MonthPaid = "monthPaid";
        public static final String MonthAmountTobePaid = "monthToBePaid";
        public static final String MonthIsPaid = "monthIspaid";
        public static final String MonthPending = "monthPending";
        public static final String MonthAccountID = "monthAccountID";
        public static final String MonthAmountToBePaidRP = "monthAmountToBePaidRP";
        public static final String MonthBankCharges = "monthBankcharges";
        public static final String MonthOtherChargesRP = "monthOtherChargesRP";

    }
}
