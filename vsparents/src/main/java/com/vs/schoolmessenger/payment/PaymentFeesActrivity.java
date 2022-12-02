//package com.vs.schoolmessenger.payment;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ExpandableListView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.material.tabs.TabLayout;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.razorpay.Checkout;
//import com.razorpay.PaymentData;
//import com.razorpay.PaymentResultWithDataListener;
//import com.vs.schoolmessenger.R;
//import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
//import com.vs.schoolmessenger.payment.Model.DataItem;
//import com.vs.schoolmessenger.payment.Model.FeeDetailsItems;
//import com.vs.schoolmessenger.payment.Model.SubCategoryItem;
//import com.vs.schoolmessenger.rest.RazorpayClient;
//import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
//import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
//import com.vs.schoolmessenger.util.Util_SharedPreference;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import androidx.appcompat.app.AppCompatActivity;
//import io.github.inflationx.viewpump.ViewPumpContextWrapper;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class PaymentFeesActrivity extends AppCompatActivity implements PaymentResultWithDataListener,TabLayout.OnTabSelectedListener {
//
//    private Button btn;
//    private ExpandableListView lvCategory;
//
//    private ArrayList<DataItem> arCategory;
//    private ArrayList<SubCategoryItem> arSubCategory;
//    private ArrayList<ArrayList<SubCategoryItem>> arSubCategoryFinal;
//
//    private ArrayList<HashMap<String, String>> parentItems =new ArrayList<>();
//    private ArrayList<ArrayList<HashMap<String, String>>> childItems=new ArrayList<>();
//
//
//    private ArrayList<HashMap<String, String>> OtherparentItems=new ArrayList<>();
//    private ArrayList<ArrayList<HashMap<String, String>>> OtherchildItems=new ArrayList<>();
//    private MyCategoriesExpandableListAdapter myCategoriesExpandableListAdapter;
//    private OtherFeesDetailsExpandAdapter OthermyCategoriesExpandableListAdapter;
//
//    RelativeLayout rytTermFess,rytOtherFess;
//
//    TextView lblTermsFees,lblOtherFess;
//    Button btnMakePayment;
//
//    ArrayList<FeeDetailsItems.TermFeesBean> termFeesList = new ArrayList<>();
//    ArrayList<FeeDetailsItems.OtherFeesBean> otherFeesList = new ArrayList<>();
//
//    ArrayList<FeeDetailsItems.TermFeesBean.FeesDetailsBean> feeDetailList  = new ArrayList<>();
//
//    ArrayList<FeeDetailsItems.OtherFeesBean.FeeDetailsBean> othrtFeeDetailsList  = new ArrayList<>();
//    ArrayList<FeeDetailsItems.OtherFeesBean.FeeDetailsBean.MonthDetailsBean> monthDetailsList  = new ArrayList<>();
//    ExpandableListView otherExpandable;
//
//    BigInteger totalAmount= BigInteger.valueOf(0);
//    BigInteger totalAmountPlusCharges= BigInteger.valueOf(0);
//    BigInteger OtherCharges= BigInteger.valueOf(0);
//    BigInteger VsCharges= BigInteger.valueOf(0);
//
//
//
//     String OtherFeesMonthDisplayName;
//     String OtherFeesAmountPerMonthDisplayName;
//     String OtherFeesPaidDisplayName;
//     String OtherFeesPendingDisplayName;
//     String OtherFeesDiscountDisplayName;
//     String OtherFeesAmounttobePaidDisplayName;
//     String TermFeesFeeNameDisplayName;
//     String TermFeesFeeCostDisplayName;
//     String TermFeesAmountPerMonthDisplayName;
//     String TermFeesPaidDisplayName;
//     String TermFeesPendingDisplayName;
//     String TermFeesDiscountDisplayName;
//     String TermFeesAmounttobePaidDisplayName;
//     String OnlineFeesTotalAmountDisplayName;
//     String OnlineFeesOtherAmountDisplayName;
//     String OnlineFeesAmountToBePaidDisplayName;
//     String PaymentStatus;
//     String VSaccountid;
//     String isLateFeeConfig;
//     String LateFeeDisplayName;
//    private TabLayout tabLayout;
//    String FeeType="PENDING";
//    String groupExpandHandling="";
//    String ExpandGroupPosition="";
//
//    LinearLayout lnrTermFees, lnrOtherFees;
//
//    String zeroPayment="";
//     String OnlinePaymentEnable="";
//
//     String feePaymentRequest="";
//     String feePaymentResponse="";
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fees_payment_screen);
//
//        Checkout.preload(getApplicationContext());
//
//
//        tabLayout = (TabLayout) findViewById(R.id.payment_taplayout);
//        tabLayout.addTab(tabLayout.newTab().setText("FEE DUE"));
//        tabLayout.addTab(tabLayout.newTab().setText("FEE PAID"));
//
//        tabLayout.setOnTabSelectedListener(this);
//
//        btn = findViewById(R.id.btn);
//        rytTermFess=findViewById(R.id.rytTermFess);
//        rytOtherFess=findViewById(R.id.rytOtherFess);
//        lvCategory = findViewById(R.id.lvCategory);
//        lblTermsFees = findViewById(R.id.lblTermsFees);
//        lblOtherFess = findViewById(R.id.lblOtherFess);
//         btnMakePayment= findViewById(R.id.btnMakePayment);
//        otherExpandable= findViewById(R.id.otherExpandable);
//        ImageView imgBack= findViewById(R.id.imgBack);
//        imgBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        lnrTermFees= findViewById(R.id.lnrTermFees);
//        lnrOtherFees= findViewById(R.id.lnrOtherFees);
//        rytTermFess.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                lvCategory.setVisibility(View.VISIBLE);
//                ConstantManager.parentItems.clear();
//                parentItems.clear();
//                ConstantManager.childItems.clear();
//                childItems.clear();
//
//              setExpandableListView(FeeType);
//                MyCategoriesExpandableListAdapter listAdapter = (MyCategoriesExpandableListAdapter) lvCategory.getExpandableListAdapter();
//                int totalHeight = 0;
//                int desiredWidth = View.MeasureSpec.makeMeasureSpec(lvCategory.getWidth(),
//                        View.MeasureSpec.EXACTLY);
//                for (int i = 0; i < listAdapter.getGroupCount(); i++) {
//                    View groupItem = listAdapter.getGroupView(i, false, null, lvCategory);
//                    groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//
//                    totalHeight += groupItem.getMeasuredHeight();
//                }
//                int height=totalHeight;
//                ViewGroup.LayoutParams params = lvCategory.getLayoutParams();
//                params.height = height;
//                lvCategory.setLayoutParams(params);
//                lvCategory.requestLayout();
//            }
//        });
//
//         rytOtherFess.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 otherExpandable.setVisibility(View.VISIBLE);
//                 ConstantManager.OtherparentItems.clear();
//                         OtherparentItems.clear();
//                 ConstantManager.OtherchildItems.clear();
//                         OtherchildItems.clear();
//
//                 otherFeesExpandable(FeeType);
//                 OtherFeesDetailsExpandAdapter listAdapter = (OtherFeesDetailsExpandAdapter) otherExpandable.getExpandableListAdapter();
//                 int totalHeight = 0;
//                 int desiredWidth = View.MeasureSpec.makeMeasureSpec(otherExpandable.getWidth(),
//                         View.MeasureSpec.EXACTLY);
//                 for (int i = 0; i < listAdapter.getGroupCount(); i++) {
//                     View groupItem = listAdapter.getGroupView(i, false, null, otherExpandable);
//                     groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//
//                     totalHeight += groupItem.getMeasuredHeight();
//                 }
//                 int height=totalHeight;
//                 ViewGroup.LayoutParams params = otherExpandable.getLayoutParams();
//                 params.height = height;
//                 otherExpandable.setLayoutParams(params);
//                 otherExpandable.requestLayout();
//             }
//         });
//
//
//         btnMakePayment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                totalAmount = BigInteger.valueOf(0);
//                totalAmountPlusCharges = BigInteger.valueOf(0);
//                OtherCharges = BigInteger.valueOf(0);
//
//                String schoolID= Util_SharedPreference.getSchoolIdFromSP(PaymentFeesActrivity.this);
//                JsonObject lateFeejsonObject = new JsonObject();
//                lateFeejsonObject.addProperty("SchoolID",schoolID);
//                JsonArray LateFeeRequestjsonArray= new JsonArray();
//
//                try {
//
//                    if (MyCategoriesExpandableListAdapter.parentItems != null) {
//                        for (int i = 0; i < MyCategoriesExpandableListAdapter.parentItems.size(); i++) {
//
//                            //
//                            String feeGroupTypeId=MyCategoriesExpandableListAdapter.parentItems.get(i).get(ConstantManager.Parameter.TermGroupID);
//                            String otherFeeMonthId="0";
//                            String FeeID="";
//                            //
//                            if (MyCategoriesExpandableListAdapter.childItems.get(i) != null) {
//                                for (int j = 0; j < MyCategoriesExpandableListAdapter.childItems.get(i).size(); j++) {
//
//                                    String isChildChecked = MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.IS_CHECKED);
//                                    String ispaid = MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.IsPaid);
//
//
//                                    if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE) && ispaid.equals("No")) {
//                                        zeroPayment="1";
//                                         //
//                                        FeeID=MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.FeeId);
//                                        JsonObject lateFee = new JsonObject();
//                                        lateFee.addProperty("feeId",FeeID);
//                                        lateFee.addProperty("feeGroupTypeId",feeGroupTypeId);
//                                        lateFee.addProperty("otherFeeMonthId",otherFeeMonthId);
//                                        LateFeeRequestjsonArray.add(lateFee);
//                                        //
//
//                                        BigInteger bigInteger = new BigInteger(MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.AmountToBePaidRP));
//                                        totalAmount = totalAmount.add(bigInteger);
//                                        BigInteger Charges = new BigInteger(MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.OtherChargesRP));
//                                        OtherCharges = OtherCharges.add(Charges);
//
//                                    }
//
//                                }
//                            }
//
//                        }
//                    }
//
//
//                    if (OtherFeesDetailsExpandAdapter.OtherparentItems != null) {
//                        for (int i = 0; i < OtherFeesDetailsExpandAdapter.OtherparentItems.size(); i++) {
//
//                            //
//                            String MfeeId=OtherFeesDetailsExpandAdapter.OtherparentItems.get(i).get(ConstantManager.Parameter.otherFeeID);
//                            String MfeeGroupTypeId=OtherFeesDetailsExpandAdapter.OtherparentItems.get(i).get(ConstantManager.Parameter.otherFeeTermGroupID);
//                            //
//
//                            if (OtherFeesDetailsExpandAdapter.OtherchildItems.get(i) != null) {
//                                for (int j = 0; j < OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).size(); j++) {
//
//                                    String isChildChecked = OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MONTH_IS_CHECKED);
//                                    String isPaid = OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthIsPaid);
//
//                                    if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE) && isPaid.equals("No")) {
//                                       zeroPayment="1";
//                                       //
//                                        String otherFeeMonthId=OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthID);
//                                        JsonObject lateFee = new JsonObject();
//                                        lateFee.addProperty("feeId",MfeeId);
//                                        lateFee.addProperty("feeGroupTypeId",MfeeGroupTypeId);
//                                        lateFee.addProperty("otherFeeMonthId",otherFeeMonthId);
//                                        LateFeeRequestjsonArray.add(lateFee);
//                                        //
//
//                                       BigInteger bigint = new BigInteger(OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthAmountToBePaidRP));
//                                        totalAmount = totalAmount.add(bigint);
//
//                                        BigInteger otherC = new BigInteger(OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthOtherChargesRP));
//                                        OtherCharges = OtherCharges.add(otherC);
//                                    }
//
//                                }
//                            }
//
//                        }
//                    }
//
//                    lateFeejsonObject.add("lateFeeDetails",LateFeeRequestjsonArray);
//
//                    totalAmountPlusCharges = totalAmount.add(OtherCharges);
//                    totalAmountPlusCharges=totalAmountPlusCharges.add(VsCharges);
//
//                    Log.d("totalAmountToBePaid", String.valueOf(totalAmount));
//                    Log.d("totalAmountPlusCharges", String.valueOf(totalAmountPlusCharges));
//
//                    String total = String.valueOf(totalAmount);
//                    if (!total.equals("0")) {
//
//                       // paymentPopup("0", "0");
//
//
//                        if(isLateFeeConfig.equals("1")) {
//                            getLateFeeAmount(lateFeejsonObject);
//                        }
//                        else {
//                            paymentPopup("0", "0");
//                        }
//
//                    } else {
//                        if(zeroPayment.equals("1")){
//                          feePayment(String.valueOf(totalAmount),"");
//                        }
//                        else {
//                            Toast.makeText(PaymentFeesActrivity.this, "Please choose a fees", Toast.LENGTH_SHORT).show();
//                        }
//                        zeroPayment="";
//
//                    }
//                }
//
//                catch (Exception e) {
//                    Toast.makeText(PaymentFeesActrivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e("Response Exception", e.getMessage());
//                }
//            }
//        });
//        otherExpandable.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v,
//                                        int groupPosition, long id) {
//                setListViewHeight1(parent, groupPosition);
//                return false;
//            }
//        });
//        lvCategory.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v,
//                                        int groupPosition, long id) {
//                setListViewHeight(parent, groupPosition);
//
//                return false;
//            }
//        });
//
//        lvCategory.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//
//
//            }
//        });
//
//        getFeeDetailsApi();
//    }
//
//    private void getLateFeeAmount(JsonObject lateFeejsonObject) {
//        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(PaymentFeesActrivity.this);
//        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
//
//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//        Log.d("Late_Fee_request",lateFeejsonObject.toString());
//
//        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
//        Call<JsonObject> call = apiService.lateFee(lateFeejsonObject);
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                try {
//                    if (mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                    Log.d("login:code-res", response.code() + " - " + response.toString());
//                    if (response.code() == 200 || response.code() == 201) {
//                        Log.d("Response", response.body().toString());
//
//                           JSONObject js=new JSONObject( response.body().toString());
//                           String status=js.getString("Status");
//                           String message = js.getString("Message");
//
//                           if(status.equals("1")) {
//                               String lateFeeAmount = js.getString("lateFeeAmount");
//                               String lateFeeAmountRP = js.getString("lateFeeAmountRP");
//                               paymentPopup(lateFeeAmount,lateFeeAmountRP);
//                           }
//                           else {
//                               paymentPopup("0","0");
//                               Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                           }
//
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    Log.e("Response Exception", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
//                Log.e("Response Failure", t.getMessage());
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//
//    private void termAlert() {
//        LayoutInflater li = LayoutInflater.from(PaymentFeesActrivity.this);
//        final View promptsView = li.inflate(R.layout.custom_alert, null);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                PaymentFeesActrivity.this);
//        alertDialogBuilder.setView(promptsView);
//        Button btnOk = (Button) promptsView.findViewById(R.id.btnOk);
//        TextView lblMessages = (TextView) promptsView.findViewById(R.id.lblMessages);
//        TextView lblAlertTitle = (TextView) promptsView.findViewById(R.id.lblAlertTitle);
//        lblAlertTitle.setText("Alert");
//        lblMessages.setText("Please pay previous Term Fees");
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                    alertDialog.cancel();
//
//            }
//        });
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//    }
//
//    @Override
//    public void onTabSelected(TabLayout.Tab tab) {
//        //viewPager.setCurrentItem(tab.getPosition());
//
//        if(tab.getPosition()==0){
//            makePaymentEnable();
//
//
//            ConstantManager.parentItems.clear();
//            parentItems.clear();
//            ConstantManager.childItems.clear();
//            childItems.clear();
//
//            ConstantManager.OtherparentItems.clear();
//            OtherparentItems.clear();
//            ConstantManager.OtherchildItems.clear();
//            OtherchildItems.clear();
//
//
//            FeeType="PENDING";
//            setExpandableListView(FeeType);
//            otherFeesExpandable(FeeType);
//
//        }
//        if(tab.getPosition()==1){
//            makePaymentEnable();
//
//            ConstantManager.parentItems.clear();
//            parentItems.clear();
//            ConstantManager.childItems.clear();
//            childItems.clear();
//
//            ConstantManager.OtherparentItems.clear();
//            OtherparentItems.clear();
//            ConstantManager.OtherchildItems.clear();
//            OtherchildItems.clear();
//
//            FeeType="PAID";
//            btnMakePayment.setVisibility(View.GONE);
//            setExpandableListView(FeeType);
//            otherFeesExpandable(FeeType);
//
//        }
//    }
//
//    private void makePaymentEnable() {
//        String enable=TeacherUtil_SharedPreference.getMakePayment(PaymentFeesActrivity.this);
//        if(enable.equalsIgnoreCase("Yes")){
//            btnMakePayment.setVisibility(View.VISIBLE);
//        }
//        else {
//            btnMakePayment.setVisibility(View.GONE);
//
//        }
//    }
//
//    @Override
//    public void onTabUnselected(TabLayout.Tab tab) {
//
//    }
//    @Override
//    public void onTabReselected(TabLayout.Tab tab) {
//
//    }
//
//    private void paymentPopup(String lateFeeAmount, String lateFeeAmountRP) {
//        LayoutInflater li = LayoutInflater.from(PaymentFeesActrivity.this);
//        final View promptsView = li.inflate(R.layout.payment_popup, null);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                PaymentFeesActrivity.this);
//        alertDialogBuilder.setView(promptsView);
//        TextView btnOk = (TextView) promptsView.findViewById(R.id.btnOk);
//
//        ImageView imgClose = (ImageView) promptsView.findViewById(R.id.imgClose);
//
//
//        RelativeLayout rytLateFee = (RelativeLayout) promptsView.findViewById(R.id.rytLateFee);
//        TextView lblLateFee = (TextView) promptsView.findViewById(R.id.lblLateFee);
//        lblLateFee.setText(LateFeeDisplayName);
//        TextView lblLateFeeAmount = (TextView) promptsView.findViewById(R.id.lblLateFeeAmount);
//
//        if(lateFeeAmount.equals("0")){
//            rytLateFee.setVisibility(View.GONE);
//        }
//        else {
//            rytLateFee.setVisibility(View.VISIBLE);
//        }
//
//        TextView lblAmountToBePaid = (TextView) promptsView.findViewById(R.id.lblAmountToBePaid);
//        lblAmountToBePaid.setText(OnlineFeesAmountToBePaidDisplayName);
//        TextView lblAmountToBe = (TextView) promptsView.findViewById(R.id.lblAmountToBe);
//
//
//        TextView lblTotalText = (TextView) promptsView.findViewById(R.id.lblTotalText);
//        lblTotalText.setText(OnlineFeesTotalAmountDisplayName);
//        TextView lblTotalAmount = (TextView) promptsView.findViewById(R.id.lblTotalAmount);
//
//        Double totalAmountToBePaid=Double.parseDouble(String.valueOf(totalAmount));
//        Log.d("amount",String.valueOf(totalAmountToBePaid));
//        Double finalamount=totalAmountToBePaid/100D;
//        lblAmountToBe.setText("₹ "+String.valueOf(finalamount));
//        TextView lblConvenience = (TextView) promptsView.findViewById(R.id.lblConvenience);
//        lblConvenience.setText(OnlineFeesOtherAmountDisplayName);
//        TextView lblOtherChargeAmount = (TextView) promptsView.findViewById(R.id.lblOtherChargeAmount);
//
//        BigInteger charges=OtherCharges.add(VsCharges);
//        Double otherCharges=Double.parseDouble(String.valueOf(charges));
//        Double others=otherCharges/100D;
//        lblOtherChargeAmount.setText("₹ "+String.valueOf(others));
//
//        BigInteger total=totalAmount.add(charges);
////
//        BigInteger lateFee = new BigInteger(lateFeeAmountRP);
//        total=total.add(lateFee);
//
////
//        Double totalCharges=Double.parseDouble(String.valueOf(total));
//        Double totalAmount=totalCharges/100D;
//        lblTotalAmount.setText("₹ "+String.valueOf(totalAmount));
//
//        lblLateFeeAmount.setText("₹ "+lateFeeAmount);
//        totalAmountPlusCharges = totalAmountPlusCharges.add(lateFee);
//
//
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.cancel();
//                startPayment(totalAmountPlusCharges);
//
//            }
//        });
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//
//        imgClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.cancel();
//
//            }
//        });
//    }
//
//
//    private void setListViewHeight(ExpandableListView listView,
//                                   int group) {
//        MyCategoriesExpandableListAdapter listAdapter = (MyCategoriesExpandableListAdapter) listView.getExpandableListAdapter();
//        int totalHeight = 0;
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
//                View.MeasureSpec.EXACTLY);
//        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
//            View groupItem = listAdapter.getGroupView(i, false, null, listView);
//            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//
//            totalHeight += groupItem.getMeasuredHeight();
//
//            if (((listView.isGroupExpanded(i)) && (i != group))
//                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
//                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
//                    View listItem = listAdapter.getChildView(i, j, false, null,
//                            listView);
//                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//
//                    totalHeight += listItem.getMeasuredHeight();
//
//                }
//            }
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        int height = totalHeight
//                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
//        if (height < 10)
//            height = 200;
//        params.height = height;
//        listView.setLayoutParams(params);
//        listView.requestLayout();
//
//    }
//    private void setListViewHeight1(ExpandableListView listView,
//                                   int group) {
//        OtherFeesDetailsExpandAdapter listAdapter = (OtherFeesDetailsExpandAdapter) listView.getExpandableListAdapter();
//        int totalHeight = 0;
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
//                View.MeasureSpec.EXACTLY);
//        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
//            View groupItem = listAdapter.getGroupView(i, false, null, listView);
//            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//
//            totalHeight += groupItem.getMeasuredHeight();
//
//            if (((listView.isGroupExpanded(i)) && (i != group))
//                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
//                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
//                    View listItem = listAdapter.getChildView(i, j, false, null,
//                            listView);
//                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//
//                    totalHeight += listItem.getMeasuredHeight();
//
//                }
//            }
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        int height = totalHeight
//                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
//        if (height < 10)
//            height = 200;
//        params.height = height;
//        listView.setLayoutParams(params);
//        listView.requestLayout();
//
//    }
//    private void startPayment(BigInteger Amount) {
//        String MobileNumber= Util_SharedPreference.getMobileNumberFromSP(PaymentFeesActrivity.this);
//        String amount=String.valueOf(Amount);
//
//
//
//        /*
//          You need to pass current activity in order to let Razorpay create CheckoutActivity
//         */
//        final Activity activity = this;
//        final Checkout co = new Checkout();
//        String RpkeyID=TeacherUtil_SharedPreference.getRazorPayKeyID(PaymentFeesActrivity.this);
//        co.setKeyID(RpkeyID);
//
//        String childName= Util_SharedPreference.getChildNameFromSP(PaymentFeesActrivity.this);
//        try {
//            JSONObject options = new JSONObject();
//            options.put("name", childName);
//            options.put("description", "Payment Transfer");
//            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//            options.put("currency", "INR");
//            options.put("amount", amount);
//            JSONObject preFill = new JSONObject();
//            preFill.put("email", "vsandroid@voicesnap.net");
//            preFill.put("contact", MobileNumber);
//
//            options.put("prefill", preFill);
//
//            co.open(activity, options);
//        } catch (Exception e) {
//            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
//                    .show();
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * The name of the function has to be
//     * onPaymentSuccess
//     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
//     */
//    @SuppressWarnings("unused")
//    @Override
//    public void onPaymentSuccess(String razorpayPaymentID, PaymentData data) {
//        try {
//            //Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
//            Log.d("PaymentID",razorpayPaymentID);
//            toChangeCapturePaymentStatus(razorpayPaymentID);
//
//        } catch (Exception e) {
//            // Log.e(TAG, "Exception in onPaymentSuccess", e);
//        }
//    }
//
//    /**
//     * The name of the function has to be
//     * onPaymentError
//     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
//     */
//    @SuppressWarnings("unused")
//    @Override
//    public void onPaymentError(int code, String response,PaymentData data) {
//        try {
//
//            showFailureAlert("Payment could not be completed.Please check your Network Connectivity");
//            //Log.d("response",response);
//
//            // Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            showFailureAlert("Payment could not be completed.Please check your Network Connectivity");
//
//              //Log.d("Exception",e.toString());
//        }
//    }
//
//    private void showFailureAlert(String strMsg) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentFeesActrivity.this);
//            alertDialog.setTitle(R.string.alert);
//            alertDialog.setMessage(strMsg);
//            alertDialog.setCancelable(false);
//            alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//
//
//                }
//            });
//
//            alertDialog.show();
//
//    }
//
//    private void toChangeCapturePaymentStatus(final String razorpayPaymentID) {
//
//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//
//        String RpkeyID=TeacherUtil_SharedPreference.getRazorPayKeyID(PaymentFeesActrivity.this);
//        String RpApiKEy=TeacherUtil_SharedPreference.getRazorPayAPIKey(PaymentFeesActrivity.this);
//
//
//        String username=RpkeyID;
//        String password=RpApiKEy;
//        String credentials = username + ":" + password;
//        // create Base64 encodet string
//        final String basic =
//                "Basic " + Base64.encodeToString(
//                        credentials.getBytes(), Base64.NO_WRAP);
//
//        Log.d("basic_Credential",basic);
//
//        JsonObject object=new JsonObject();
//        object.addProperty("amount",String.valueOf(totalAmountPlusCharges));
//        object.addProperty("currency","INR");
//        TeacherMessengerApiInterface apiService = RazorpayClient.getClient().create(TeacherMessengerApiInterface.class);
//        Call<JsonObject> call = apiService.changeCapturePayment(razorpayPaymentID,"application/json",basic,object);
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                try {
//                    if (mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                    Log.d("login:code-res", response.code() + " - " + response.toString());
//                    if (response.code() == 200 || response.code() == 201) {
//                        Log.d("Response", response.body().toString());
//                         JSONObject jsonobject=new JSONObject( response.body().toString());
//
//                         Boolean captured=jsonobject.getBoolean("captured");
//                         if(captured){
//                             String CapturedpaymentID=jsonobject.getString("id");
//                             sendToMultipleSubAccount(CapturedpaymentID);
//                         }
//                         else {
//                             showFailureAlert("Sorry! Your Payment could not be processed.If money debited from your bank,it will credited back");
//
//                         }
//
//                    }
//
//                    else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    Log.e("Response Exception", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
//                Log.e("Response Failure", t.getMessage());
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private void refundAPI(final String razorpayPaymentID) {
//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//
//        String RpkeyID=TeacherUtil_SharedPreference.getRazorPayKeyID(PaymentFeesActrivity.this);
//        String RpApiKEy=TeacherUtil_SharedPreference.getRazorPayAPIKey(PaymentFeesActrivity.this);
//
//        String username=RpkeyID;
//        String password=RpApiKEy;
//        String credentials = username + ":" + password;
//        // create Base64 encodet string
//        final String basic =
//                "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//
//        Log.d("basic_Credential",basic);
//
//        JsonObject jsonObjectclass = new JsonObject();
//
//        jsonObjectclass.addProperty("amount",String.valueOf(totalAmountPlusCharges));
//        jsonObjectclass.addProperty("speed","optimum");
//        jsonObjectclass.addProperty("receipt", "refunded");
//
//        Log.d("Refund_JsonObject",jsonObjectclass.toString());
//
//
//        TeacherMessengerApiInterface apiService = RazorpayClient.getClient().create(TeacherMessengerApiInterface.class);
//        Call<JsonObject> call = apiService.refundAmountToCustomer(razorpayPaymentID,"application/json",basic,jsonObjectclass);
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                try {
//                    if (mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                    Log.d("Refund:code-res", response.code() + " - " + response.toString());
//                    if (response.code() == 200) {
//                        Log.d("Refund_Succes_Response", response.body().toString());
//                        showFailureAlert("Sorry! Your Payment could not be processed.If money debited from your bank,it will credited back");
//                    }
//
//                    writePaymentLogs(razorpayPaymentID);
//
//
//                } catch (Exception e) {
//                    Log.e("Response Exception", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
//                Log.e("Response Failure", t.getMessage());
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private void writePaymentLogs(String razorpayPaymentID) {
//        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(PaymentFeesActrivity.this);
//        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
//
//        String childID= Util_SharedPreference.getChildIdFromSP(PaymentFeesActrivity.this);
//        String schoolID= Util_SharedPreference.getSchoolIdFromSP(PaymentFeesActrivity.this);
//        JsonObject jsonObjectclass = new JsonObject();
//        jsonObjectclass.addProperty("RequestId",feePaymentRequest);
//        jsonObjectclass.addProperty("ResponseId",feePaymentResponse);
//        jsonObjectclass.addProperty("PaymentId", razorpayPaymentID);
//        jsonObjectclass.addProperty("StudentId", childID);
//        jsonObjectclass.addProperty("SchoolID", schoolID);
//        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
//        Call<JsonArray> call = apiService.feePaymentLogs(jsonObjectclass);
//
//        call.enqueue(new Callback<JsonArray>() {
//            @Override
//            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                try {
//
//                    Log.d("feePaymentLogs:code-res", response.code() + " - " + response.toString());
//
//                } catch (Exception e) {
//                    Log.e("Response Exception", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonArray> call, Throwable t) {
//                Log.e("Response Failure", t.getMessage());
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//
//    private void sendToMultipleSubAccount(final String paymentID) {
//
//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//
//        String RpkeyID=TeacherUtil_SharedPreference.getRazorPayKeyID(PaymentFeesActrivity.this);
//        String RpApiKEy=TeacherUtil_SharedPreference.getRazorPayAPIKey(PaymentFeesActrivity.this);
//
//        String username=RpkeyID;
//        String password=RpApiKEy;
//        String credentials = username + ":" + password;
//        // create Base64 encodet string
//        final String basic =
//                "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//
//        JsonObject jsonBody=jsonRequestArray();
//        feePaymentRequest="Payment ID : "+paymentID+" JsonObject : "+jsonBody.toString()+" content_type : "+"application/json"+" Authorization : "+basic;
//
//        TeacherMessengerApiInterface apiService = RazorpayClient.getClient().create(TeacherMessengerApiInterface.class);
//        Call<JsonObject> call = apiService.transferToMultipleAccout(paymentID,"application/json",basic,jsonBody);
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                try {
//                    if (mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                    Log.d("login:code-res", response.code() + " - " + response.toString());
//
//                     feePaymentResponse=response.body().toString();
//
//                    if (response.code() == 200) {
//                        Log.d("Response", response.body().toString());
//                        feePayment(String.valueOf(totalAmount),paymentID);
//                    }
//                    else {
//                        refundAPI(paymentID);
//                    }
//
//                } catch (Exception e) {
//                    Log.e("Response Exception", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
//                Log.e("Response Failure", t.getMessage());
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private void feePayment(String total, final String paymentID) {
//        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(PaymentFeesActrivity.this);
//        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
//
//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//
//
//        JsonObject jsonBody=feePaymentjsonRequestArray(total,paymentID);
//        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
//        Call<JsonArray> call = apiService.feePayment(jsonBody);
//
//        call.enqueue(new Callback<JsonArray>() {
//            @Override
//            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                try {
//                    if (mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                    Log.d("login:code-res", response.code() + " - " + response.toString());
//                    if (response.code() == 200 || response.code() == 201) {
//                        Log.d("Response", response.body().toString());
//
//
//                        JSONArray js = new JSONArray(response.body().toString());
//                        if (js.length() > 0) {
//                            JSONObject jsonObject = js.getJSONObject(0);
//
//                            String status=jsonObject.getString("Status");
//                            String message=jsonObject.getString("Message");
//
//                            alert(message,status,paymentID);
//
//
//                        }
//                    } else {
//                        writePaymentLogs(paymentID);
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//                    }
//
//
//
//                } catch (Exception e) {
//                    Log.e("Response Exception", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonArray> call, Throwable t) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
//                Log.e("Response Failure", t.getMessage());
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private void alert(String message, final String status,String paymentID) {
//
//        LayoutInflater li = LayoutInflater.from(PaymentFeesActrivity.this);
//        final View promptsView = li.inflate(R.layout.custom_alert, null);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                PaymentFeesActrivity.this);
//        alertDialogBuilder.setView(promptsView);
//        Button btnOk = (Button) promptsView.findViewById(R.id.btnOk);
//        TextView lblMessages = (TextView) promptsView.findViewById(R.id.lblMessages);
//        TextView lblAlertTitle = (TextView) promptsView.findViewById(R.id.lblAlertTitle);
//        lblAlertTitle.setText("Alert");
//        lblMessages.setText(message);
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (status.equals("1")) {
//                    alertDialog.cancel();
//                    Intent refresh = new Intent(PaymentFeesActrivity.this, PaymentFeesActrivity.class);
//                    startActivity(refresh);//Start the same Activity
//                    finish(); //finish Activity
//
//                } else {
//                    alertDialog.cancel();
//
//                }
//            }
//        });
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//
//        writePaymentLogs(paymentID);
//
//
//    }
//
//    private JsonObject feePaymentjsonRequestArray(String total,String paymentID) {
//        JsonObject jsonObjectSchool = new JsonObject();
//        String childID= Util_SharedPreference.getChildIdFromSP(PaymentFeesActrivity.this);
//        String schoolID= Util_SharedPreference.getSchoolIdFromSP(PaymentFeesActrivity.this);
//
//        Double b=Double.parseDouble(total);
//        Log.d("amount",String.valueOf(b));
//
//        Double finalamount=b/100D;
//
//        jsonObjectSchool.addProperty("StudentId",childID);
//        jsonObjectSchool.addProperty("SchoolID",schoolID);
//        jsonObjectSchool.addProperty("TotalAmount",String.valueOf(finalamount));
//        jsonObjectSchool.addProperty("PaymentId",paymentID);
//        try {
//            JsonArray jsonArrayTermsFees = new JsonArray();
//
//            if(MyCategoriesExpandableListAdapter.parentItems!=null) {
//                for (int i = 0; i < MyCategoriesExpandableListAdapter.parentItems.size(); i++) {
//
//                    if(MyCategoriesExpandableListAdapter.childItems.get(i)!=null) {
//                        for (int j = 0; j < MyCategoriesExpandableListAdapter.childItems.get(i).size(); j++) {
//                            String isChildChecked = MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.IS_CHECKED);
//                            String ispaid = MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.IsPaid);
//
//                            if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE) && ispaid.equals("No")) {
//                                String amount = MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.FeeAmount);
//                                JsonObject jsonObjectclass = new JsonObject();
//                                jsonObjectclass.addProperty("TermGroupTypeId", MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.termGroupid));
//                                jsonObjectclass.addProperty("FeeId", MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.FeeId));
//                                jsonObjectclass.addProperty("FeeAmount", amount);
//                                jsonObjectclass.addProperty("PaidAmount", MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.AmountToBePaid));
//                                jsonObjectclass.addProperty("DiscountAmount", MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.DiscountAMount));
//                                String amountToBepaid=MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.AmountToBePaid);
//                                if(amountToBepaid.equals("0")) {
//                                    jsonObjectclass.addProperty("DiscountAmountFlag", "1");
//                                }
//                                else {
//                                    jsonObjectclass.addProperty("DiscountAmountFlag", "0");
//
//                                }
//
//                                jsonArrayTermsFees.add(jsonObjectclass);
//                            }
//
//                        }
//                    }
//
//                }
//            }
//            jsonObjectSchool.add("TermFees",jsonArrayTermsFees);
//
//            JsonArray jsonArrayOtherFees = new JsonArray();
//
//            if(OtherFeesDetailsExpandAdapter.OtherparentItems!=null) {
//
//                for (int i = 0; i < OtherFeesDetailsExpandAdapter.OtherparentItems.size(); i++) {
//                        String isParent = OtherFeesDetailsExpandAdapter.OtherparentItems.get(i).get(ConstantManager.Parameter.MONTH_IS_CHECKED);
//                        JsonObject otherfees = new JsonObject();
//
//                    otherfees.addProperty("TermGroupTypeId", OtherFeesDetailsExpandAdapter.OtherparentItems.get(i).get(ConstantManager.Parameter.otherFeeTermGroupID));
//                    otherfees.addProperty("FeeId", OtherFeesDetailsExpandAdapter.OtherparentItems.get(i).get(ConstantManager.Parameter.otherFeeID));
//                    otherfees.addProperty("FeeAmount", OtherFeesDetailsExpandAdapter.OtherparentItems.get(i).get(ConstantManager.Parameter.otherFeeAmount));
//                    otherfees.addProperty("DiscountAmount", OtherFeesDetailsExpandAdapter.OtherparentItems.get(i).get(ConstantManager.Parameter.otherFeeDiscount));
//
//                     Double totalPaidAmount= Double.valueOf(0);
//                     if(OtherFeesDetailsExpandAdapter.OtherchildItems.get(i) != null) {
//                         for (int j = 0; j < OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).size(); j++) {
//                             String isChildChecked = OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MONTH_IS_CHECKED);
//                             String isPaid = OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthIsPaid);
//                             if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE) && isPaid.equals("No")) {
//                                 Double a = Double.parseDouble(OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthAmountTobePaid));
//                                 totalPaidAmount = totalPaidAmount + a;
//                             }
//                         }
//                     }
//
//                    otherfees.addProperty("PaidAmount", String.valueOf(totalPaidAmount));
//
//                     if(String.valueOf(totalPaidAmount).equals("0.0")) {
//                         otherfees.addProperty("DiscountAmountFlag", "1");
//                     }
//                     else {
//                         otherfees.addProperty("DiscountAmountFlag", "0");
//
//                     }
//
//
//                         JsonArray monthDetails = new JsonArray();
//                         if (OtherFeesDetailsExpandAdapter.OtherchildItems.get(i) != null) {
//
//                            for (int j = 0; j < OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).size(); j++) {
//
//                                    String isChildChecked = OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MONTH_IS_CHECKED);
//                                    String isPaid = OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthIsPaid);
//
//                                    if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE) && isPaid.equals("No")) {
//                                        JsonObject jsonObjectclass = new JsonObject();
//                                        jsonObjectclass.addProperty("MonthId", OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthID));
//                                        jsonObjectclass.addProperty("AmountPerMonth", OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.AmountPerMonth));
//                                        jsonObjectclass.addProperty("DiscountAmount", OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthDiscount));
//                                        jsonObjectclass.addProperty("PaidAmount", OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthAmountTobePaid));
//                                        jsonObjectclass.addProperty("Pending", OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthPending));
//                                        monthDetails.add(jsonObjectclass);
//                                    }
//
//                                }
//
//                        }
//
//                        otherfees.add("monthDetails", monthDetails);
//                        jsonArrayOtherFees.add(otherfees);
//
//                    }
//
//            }
//
//            jsonObjectSchool.add("OtherFees", jsonArrayOtherFees);
//            Log.d("Final_Array", jsonObjectSchool.toString());
//        } catch (Exception e) {
//            Log.d("ASDF", e.toString());
//        }
//        return jsonObjectSchool;
//    }
//
//    private JsonObject jsonRequestArray() {
//        JsonObject jsonObjectSchool = new JsonObject();
//        try {
//            JsonArray jsonArrayschoolstd = new JsonArray();
//
//            if(MyCategoriesExpandableListAdapter.parentItems!=null) {
//    for (int i = 0; i < MyCategoriesExpandableListAdapter.parentItems.size(); i++) {
//        String termName=MyCategoriesExpandableListAdapter.parentItems.get(i).get(ConstantManager.Parameter.TermFeesName);
//        if(MyCategoriesExpandableListAdapter.childItems.get(i)!=null) {
//            for (int j = 0; j < MyCategoriesExpandableListAdapter.childItems.get(i).size(); j++) {
//                String isChildChecked = MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.IS_CHECKED);
//                String ispaid = MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.IsPaid);
//
//                String amount = (MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.AmountToBePaidRP));
//
//                if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE) && ispaid.equals("No") && !amount.equals("0")) {
//
//                    JsonObject jsonObjectclass = new JsonObject();
//                    jsonObjectclass.addProperty("account", MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.AccountID));
//                    jsonObjectclass.addProperty("amount",amount);
//                    jsonObjectclass.addProperty("currency", "INR");
//                    JsonObject notes=new JsonObject();
//                    notes.addProperty("name",termName+"-"+MyCategoriesExpandableListAdapter.childItems.get(i).get(j).get(ConstantManager.Parameter.FeeName));
//
//                    jsonObjectclass.add("notes",notes);
//                    jsonArrayschoolstd.add(jsonObjectclass);
//                }
//
//            }
//        }
//
//    }
//}
//
//            if(OtherFeesDetailsExpandAdapter.OtherparentItems!=null) {
//
//                for (int i = 0; i < OtherFeesDetailsExpandAdapter.OtherparentItems.size(); i++) {
//
//                    String feename=OtherFeesDetailsExpandAdapter.OtherparentItems.get(i).get(ConstantManager.Parameter.otherFeeName);
//
//                    if(OtherFeesDetailsExpandAdapter.OtherchildItems.get(i)!=null) {
//                        for (int j = 0; j < OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).size(); j++) {
//
//                            String isChildChecked = OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MONTH_IS_CHECKED);
//                            String isPaid = OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthIsPaid);
//
//                            String amount = (OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthAmountToBePaidRP));
//
//                            if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)  && isPaid.equals("No") && !amount.equals("0")) {
//
//                                JsonObject jsonObjectclass = new JsonObject();
//                                jsonObjectclass.addProperty("account", OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthAccountID));
//                                jsonObjectclass.addProperty("amount", amount);
//                                jsonObjectclass.addProperty("currency", "INR");
//                                JsonObject notes=new JsonObject();
//                                notes.addProperty("name",feename+"-"+OtherFeesDetailsExpandAdapter.OtherchildItems.get(i).get(j).get(ConstantManager.Parameter.MonthName));
//                                jsonObjectclass.add("notes",notes);
//                                jsonArrayschoolstd.add(jsonObjectclass);
//                            }
//
//                        }
//                    }
//
//                }
//            }
//
//            jsonObjectSchool.add("transfers", jsonArrayschoolstd);
//            Log.d("Final_Array", jsonObjectSchool.toString());
//        } catch (Exception e) {
//            Log.d("ASDF", e.toString());
//        }
//        return jsonObjectSchool;
//    }
//
//
//
//    private void otherFeesExpandable(String feeType) {
//
//        for(FeeDetailsItems.OtherFeesBean data : otherFeesList) {
//
//            for (FeeDetailsItems.OtherFeesBean.FeeDetailsBean parent : data.getFeeDetails()) {
//                ArrayList<HashMap<String, String>> childArrayList = new ArrayList<HashMap<String, String>>();
//                HashMap<String, String> mapParent = new HashMap<String, String>();
//
//
//                mapParent.put(ConstantManager.Parameter.otherFeeName, parent.getFeeName());
//                mapParent.put(ConstantManager.Parameter.otherFeeID, parent.getFeeId());
//               // mapParent.put(ConstantManager.Parameter.otherFeeAccountID, parent.getAccountID());
//                mapParent.put(ConstantManager.Parameter.otherFeeAmountToBePaid, parent.getAmountToBePaid());
//                mapParent.put(ConstantManager.Parameter.otherFeeDiscount, parent.getDiscountAmount());
//                mapParent.put(ConstantManager.Parameter.otherFeePaidAmount, parent.getPaid());
//                mapParent.put(ConstantManager.Parameter.otherFeeTermGroupID, parent.getTermGroupTypeId());
//                mapParent.put(ConstantManager.Parameter.otherFeeAmount, parent.getFeeAmount());
//                mapParent.put(ConstantManager.Parameter.otherFeeFullyPaid, parent.getIsPaidFully());
//
//                int countIsChecked = 0;
//                for (FeeDetailsItems.OtherFeesBean.FeeDetailsBean.MonthDetailsBean subCategoryItem : parent.getMonthDetails()) {
//
//                    HashMap<String, String> mapChild = new HashMap<String, String>();
//                    mapChild.put(ConstantManager.Parameter.MonthID, subCategoryItem.getMonthId());
//                    mapChild.put(ConstantManager.Parameter.MonthName, subCategoryItem.getMonthName());
//                    mapChild.put(ConstantManager.Parameter.MonthAmountTobePaid, subCategoryItem.getAmountToBePaid());
//                    mapChild.put(ConstantManager.Parameter.MonthDiscount, subCategoryItem.getDiscountAmount());
//                    mapChild.put(ConstantManager.Parameter.MonthPaid, subCategoryItem.getPaid());
//                    mapChild.put(ConstantManager.Parameter.MonthPending, subCategoryItem.getPending());
//                    mapChild.put(ConstantManager.Parameter.MonthIsPaid, subCategoryItem.getIsPaid());
//                    mapChild.put(ConstantManager.Parameter.AmountPerMonth, subCategoryItem.getAmountPerMonth());
//                    mapChild.put(ConstantManager.Parameter.MONTH_IS_CHECKED, subCategoryItem.getIsChecked());
//                    mapChild.put(ConstantManager.Parameter.MonthAccountID, subCategoryItem.getAccountID());
//                    mapChild.put(ConstantManager.Parameter.MonthAmountToBePaidRP, subCategoryItem.getAmountToBePaidRP());
//                    mapChild.put(ConstantManager.Parameter.MonthBankCharges, subCategoryItem.getBankCharges());
//                    mapChild.put(ConstantManager.Parameter.MonthOtherChargesRP, subCategoryItem.getOtherChargesRP());
//
//                    if (subCategoryItem.getIsChecked().equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
//
//                        countIsChecked++;
//                    }
//                    childArrayList.add(mapChild);
//                }
//
//                if(countIsChecked == parent.getMonthDetails().size()) {
//
//                    parent.setIsChecked(ConstantManager.CHECK_BOX_CHECKED_TRUE);
//                }else {
//                    parent.setIsChecked(ConstantManager.CHECK_BOX_CHECKED_FALSE);
//                }
//                mapParent.put(ConstantManager.Parameter.MONTH_IS_CHECKED,parent.getIsChecked());
//                OtherchildItems.add(childArrayList);
//                OtherparentItems.add(mapParent);
//
//            }
//        }
//
//
//        ConstantManager.OtherparentItems = OtherparentItems;
//        ConstantManager.OtherchildItems = OtherchildItems;
//
//
//
//        OthermyCategoriesExpandableListAdapter = new OtherFeesDetailsExpandAdapter(this,OtherparentItems,OtherchildItems,false,
//                OtherFeesMonthDisplayName,OtherFeesAmountPerMonthDisplayName,OtherFeesPaidDisplayName,OtherFeesPendingDisplayName,
//                OtherFeesDiscountDisplayName,OtherFeesAmounttobePaidDisplayName,PaymentStatus,feeType,OnlinePaymentEnable);
//        otherExpandable.setAdapter(OthermyCategoriesExpandableListAdapter);
//    }
//
//
//    private void getFeeDetailsApi() {
//        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(PaymentFeesActrivity.this);
//        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
//
//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//
//        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
//       String childID= Util_SharedPreference.getChildIdFromSP(PaymentFeesActrivity.this);
//       String schoolId= Util_SharedPreference.getSchoolIdFromSP(PaymentFeesActrivity.this);
//        Call<FeeDetailsItems> call = apiService.getFeeDetails(childID,schoolId);
//
//        call.enqueue(new Callback<FeeDetailsItems>() {
//            @Override
//            public void onResponse(Call<FeeDetailsItems> call, Response<FeeDetailsItems> response) {
//                try {
//                    if (mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                    Log.d("login:code-res", response.code() + " - " + response.toString());
//                    if (response.code() == 200 || response.code() == 201) {
//                        Log.d("Response", response.body().toString());
//
//                        Log.d("FeeDetails",new Gson().toJson(response.body()));
//
//
//                        String status=response.body().getStatus();
//                        String message=response.body().getMessage();
//
//                        if(status.equals("1")){
//
//                            BigInteger bigIntegerStr=new BigInteger(response.body().getVSpayRP());
//                        VsCharges = bigIntegerStr;
//
//                        VSaccountid=response.body().getVSaccountid();
//
//
//                        OnlinePaymentEnable=response.body().getOnlinepayment();
//                        TeacherUtil_SharedPreference.putMakePayment(PaymentFeesActrivity.this,OnlinePaymentEnable);
//
//                        makePaymentEnable();
//
//
//                                String RpKeyID=response.body().getRazorPayKeyId();
//                        TeacherUtil_SharedPreference.PutRazorPayKeyID(PaymentFeesActrivity.this,RpKeyID);
//                                String RpApiKey=response.body().getRazorPayApiKey();
//                        TeacherUtil_SharedPreference.putRazorPayAPIKey(PaymentFeesActrivity.this,RpApiKey);
//
//
//                        OtherFeesMonthDisplayName=response.body().getOtherFeesMonthDisplayName();
//                        OtherFeesAmountPerMonthDisplayName=response.body().getOtherFeesAmountPerMonthDisplayName();
//                         OtherFeesPaidDisplayName=response.body().getOtherFeesPaidDisplayName();
//                        OtherFeesPendingDisplayName=response.body().getOtherFeesPendingDisplayName();
//                       OtherFeesDiscountDisplayName=response.body().getOtherFeesDiscountDisplayName();
//                       OtherFeesAmounttobePaidDisplayName=response.body().getOtherFeesAmounttobePaidDisplayName();
//                       TermFeesFeeNameDisplayName=response.body().getTermFeesFeeNameDisplayName();
//                        TermFeesFeeCostDisplayName=response.body().getTermFeesFeeCostDisplayName();
//                        TermFeesAmountPerMonthDisplayName=response.body().getTermFeesAmountPerMonthDisplayName();
//                        TermFeesPaidDisplayName=response.body().getTermFeesPaidDisplayName();
//                        TermFeesPendingDisplayName=response.body().getTermFeesPendingDisplayName();
//                      TermFeesDiscountDisplayName=response.body().getTermFeesDiscountDisplayName();
//                       TermFeesAmounttobePaidDisplayName=response.body().getTermFeesAmounttobePaidDisplayName();
//                      OnlineFeesTotalAmountDisplayName=response.body().getOnlineFeesTotalAmountDisplayName();
//                         OnlineFeesOtherAmountDisplayName=response.body().getOnlineFeesOtherAmountDisplayName();
//                         OnlineFeesAmountToBePaidDisplayName=response.body().getOnlineFeesAmountToBePaidDisplayName();
//                         PaymentStatus=response.body().getPaymentStatusDisplayName();
//
//                            isLateFeeConfig=response.body().getIsLateFeeConfig();
//                            LateFeeDisplayName=response.body().getLateFeeDisplayName();
//
//
//
//
//
//                            termFeesList.clear();
//                            otherFeesList.clear();
//                            String termFeesName=response.body().getTermFeesDisplayName();
//                            lblTermsFees.setText(termFeesName);
//                            String otherFeesName=response.body().getOtherFeesDisplayName();
//                            lblOtherFess.setText(otherFeesName);
//                            termFeesList = (ArrayList<FeeDetailsItems.TermFeesBean>) response.body().getTermFees();
//                            otherFeesList = (ArrayList<FeeDetailsItems.OtherFeesBean>) response.body().getOtherFees();
//
//                            setExpandableListView(FeeType);
//                            otherFeesExpandable(FeeType);
//
//                            if(termFeesList.isEmpty()){
//                                lnrTermFees.setVisibility(View.GONE);
//                            }
//                            else {
//                                lnrTermFees.setVisibility(View.VISIBLE);
//
//                            }
//                            if(otherFeesList.isEmpty()){
//                                lnrOtherFees.setVisibility(View.GONE);
//                            }
//                            else {
//                                lnrOtherFees.setVisibility(View.VISIBLE);
//
//                            }
//
//                            if(termFeesList.isEmpty()&&otherFeesList.isEmpty()){
//                                showErrorAlert("No Records Found.");
//                            }
//
//                        }
//                        else {
//                            showErrorAlert(message);
//                        }
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    Log.e("Response Exception", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<FeeDetailsItems> call, Throwable t) {
//                Log.e("Response Failure", t.getMessage());
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
//                // showToast("Server Connection Failed");
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private void showErrorAlert(String message) {
//        LayoutInflater li = LayoutInflater.from(PaymentFeesActrivity.this);
//        final View promptsView = li.inflate(R.layout.custom_alert, null);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                PaymentFeesActrivity.this);
//        alertDialogBuilder.setView(promptsView);
//        Button btnOk = (Button) promptsView.findViewById(R.id.btnOk);
//        TextView lblMessages = (TextView) promptsView.findViewById(R.id.lblMessages);
//        TextView lblAlertTitle = (TextView) promptsView.findViewById(R.id.lblAlertTitle);
//        lblAlertTitle.setText("Alert");
//        lblMessages.setText(message);
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                alertDialog.cancel();
//                finish();
//
//
//            }
//        });
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//    }
//
//    private void setExpandableListView(String feeType) {
//
//
//
//        for(FeeDetailsItems.TermFeesBean data : termFeesList){
//            ArrayList<HashMap<String, String>> childArrayList =new ArrayList<HashMap<String, String>>();
//            HashMap<String, String> mapParent = new HashMap<String, String>();
//
//            mapParent.put(ConstantManager.Parameter.TermFeesName,data.getTermName());
//            mapParent.put(ConstantManager.Parameter.TermGroupID,data.getTermGroupTypeId());
//            mapParent.put(ConstantManager.Parameter.TermFullyPaid,data.getIsPaidFully());
//
//            int countIsChecked = 0;
//            for(FeeDetailsItems.TermFeesBean.FeesDetailsBean subCategoryItem : data.getFeesDetails()) {
//
//                HashMap<String, String> mapChild = new HashMap<String, String>();
//                mapChild.put(ConstantManager.Parameter.FeeId,subCategoryItem.getFeeId());
//                mapChild.put(ConstantManager.Parameter.FeeName,subCategoryItem.getFeeName());
//                mapChild.put(ConstantManager.Parameter.FeeAmount,subCategoryItem.getFeeAmount());
//                mapChild.put(ConstantManager.Parameter.DiscountAMount,subCategoryItem.getDiscountAmount());
//                mapChild.put(ConstantManager.Parameter.AmountToBePaid,subCategoryItem.getAmountToBePaid());
//                mapChild.put(ConstantManager.Parameter.AmountToBePaidRP,subCategoryItem.getAmountToBePaidRP());
//                mapChild.put(ConstantManager.Parameter.Paid,subCategoryItem.getPaid());
//                mapChild.put(ConstantManager.Parameter.Pending,subCategoryItem.getPending());
//                mapChild.put(ConstantManager.Parameter.AccountID,subCategoryItem.getAccountID());
//                mapChild.put(ConstantManager.Parameter.termGroupid,subCategoryItem.getTermGroupTypeId());
//                mapChild.put(ConstantManager.Parameter.BankCharges,subCategoryItem.getBankCharges());
//                mapChild.put(ConstantManager.Parameter.OtherChargesRP,subCategoryItem.getOtherChargesRP());
//                mapChild.put(ConstantManager.Parameter.IsPaid,subCategoryItem.getIspaid());
//                mapChild.put(ConstantManager.Parameter.IS_CHECKED,subCategoryItem.getIsChecked());
//
//                if(subCategoryItem.getIsChecked().equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
//
//                    countIsChecked++;
//                }
//
//                childArrayList.add(mapChild);
//
//            }
//
//            if(countIsChecked == data.getFeesDetails().size()) {
//
//                data.setIsChecked(ConstantManager.CHECK_BOX_CHECKED_TRUE);
//            }else {
//                data.setIsChecked(ConstantManager.CHECK_BOX_CHECKED_FALSE);
//            }
//
//            mapParent.put(ConstantManager.Parameter.IS_CHECKED,data.getIsChecked());
//            childItems.add(childArrayList);
//            parentItems.add(mapParent);
//
//        }
//
//        ConstantManager.parentItems = parentItems;
//        ConstantManager.childItems = childItems;
//
//
//
//        myCategoriesExpandableListAdapter = new MyCategoriesExpandableListAdapter(this,parentItems,childItems,false,
//                TermFeesFeeNameDisplayName,TermFeesFeeCostDisplayName,TermFeesAmountPerMonthDisplayName,
//                TermFeesPaidDisplayName,TermFeesPendingDisplayName,TermFeesDiscountDisplayName,TermFeesAmounttobePaidDisplayName,PaymentStatus,feeType,OnlinePaymentEnable);
//        lvCategory.setAdapter(myCategoriesExpandableListAdapter);
//    }
//}
