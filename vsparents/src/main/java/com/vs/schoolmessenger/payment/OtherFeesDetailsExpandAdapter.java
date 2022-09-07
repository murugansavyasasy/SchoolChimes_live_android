package com.vs.schoolmessenger.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;

public class OtherFeesDetailsExpandAdapter extends BaseExpandableListAdapter {

    public static ArrayList<ArrayList<HashMap<String, String>>> OtherchildItems;
    public static ArrayList<HashMap<String, String>> OtherparentItems;
    //    private final ArrayList<HashMap<String, String>> childItems;
    private LayoutInflater inflater;
    private Activity activity;
    private HashMap<String, String> child;
    private int count = 0;
    private boolean isFromMyCategoriesFragment;

    private String OtherFeesMonthDisplayName;
    private String OtherFeesAmountPerMonthDisplayName;
    private String OtherFeesPaidDisplayName;
    private String OtherFeesPendingDisplayName;
    private String OtherFeesDiscountDisplayName;
    private String OtherFeesAmounttobePaidDisplayName;
    private String PaymentStatus;
    private String FeeType;
    private String OnlinePaymentEnable;


    public OtherFeesDetailsExpandAdapter(Activity activity, ArrayList<HashMap<String, String>> parentItems,
                                             ArrayList<ArrayList<HashMap<String, String>>> childItems, boolean isFromMyCategoriesFragment,
                                         String OtherFeesMonthDisplayName,String OtherFeesAmountPerMonthDisplayName,String OtherFeesPaidDisplayName,String OtherFeesPendingDisplayName,
                                         String OtherFeesDiscountDisplayName,String OtherFeesAmounttobePaidDisplayName,String status,String type,String onlinePayment) {

        this.OtherparentItems = parentItems;
        this.OtherchildItems = childItems;
        this.activity = activity;
        this.isFromMyCategoriesFragment = isFromMyCategoriesFragment;

        this.OtherFeesMonthDisplayName=OtherFeesMonthDisplayName;
       this.OtherFeesAmountPerMonthDisplayName=OtherFeesAmountPerMonthDisplayName;
       this.OtherFeesPaidDisplayName=OtherFeesPaidDisplayName;
       this.OtherFeesPendingDisplayName=OtherFeesPendingDisplayName;
       this.OtherFeesDiscountDisplayName=OtherFeesDiscountDisplayName;
        this.OtherFeesAmounttobePaidDisplayName=OtherFeesAmounttobePaidDisplayName;
        this.PaymentStatus=status;
        this.FeeType=type;
        this.OnlinePaymentEnable=onlinePayment;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getGroupCount() {
        return OtherparentItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (OtherchildItems.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        final OtherFeesDetailsExpandAdapter.ViewHolderParent viewHolderParent;
        if (convertView == null) {

            if(isFromMyCategoriesFragment) {
                convertView = inflater.inflate(R.layout.group_list_layout_my_categories, null);
            }else {
                convertView = inflater.inflate(R.layout.group_list_layout_choose_categories, null);
            }
            viewHolderParent = new OtherFeesDetailsExpandAdapter.ViewHolderParent();

            viewHolderParent.tvMainCategoryName = convertView.findViewById(R.id.tvMainCategoryName);
            viewHolderParent.cbMainCategory = convertView.findViewById(R.id.cbMainCategory);
            viewHolderParent.ivCategory = convertView.findViewById(R.id.ivCategory);
            viewHolderParent.rytParentHeader = convertView.findViewById(R.id.rytParentHeader);
            convertView.setTag(viewHolderParent);
        } else {
            viewHolderParent = (OtherFeesDetailsExpandAdapter.ViewHolderParent) convertView.getTag();
        }

        if (OtherparentItems.get(groupPosition).get(ConstantManager.Parameter.MONTH_IS_CHECKED).equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
            viewHolderParent.cbMainCategory.setChecked(true);
            notifyDataSetChanged();

        } else {
            viewHolderParent.cbMainCategory.setChecked(false);
            notifyDataSetChanged();
        }

        viewHolderParent.cbMainCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolderParent.cbMainCategory.isChecked()) {
                    OtherparentItems.get(groupPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);

                    for (int i = 0; i < OtherchildItems.get(groupPosition).size(); i++) {
                        OtherchildItems.get(groupPosition).get(i).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                    }
                    notifyDataSetChanged();

                }
                else {
                    OtherparentItems.get(groupPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                    for (int i = 0; i < OtherchildItems.get(groupPosition).size(); i++) {
                        OtherchildItems.get(groupPosition).get(i).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                    }
                    notifyDataSetChanged();
                }
            }
        });

        ConstantManager.OtherchildItems = OtherchildItems;
        ConstantManager.OtherparentItems = OtherparentItems;

        viewHolderParent.tvMainCategoryName.setText(OtherparentItems.get(groupPosition).get(ConstantManager.Parameter.otherFeeName));




        if(OnlinePaymentEnable.equalsIgnoreCase("Yes")){
            if (FeeType.equals("PENDING")) {
                if (OtherparentItems.get(groupPosition).get(ConstantManager.Parameter.otherFeeFullyPaid).equals("Yes")) {
                    viewHolderParent.cbMainCategory.setVisibility(View.INVISIBLE);

                } else {
                    viewHolderParent.cbMainCategory.setVisibility(View.VISIBLE);

                }
            }

            if (FeeType.equals("PAID")) {
                viewHolderParent.cbMainCategory.setVisibility(View.INVISIBLE);

            }
        }
        else {
            viewHolderParent.cbMainCategory.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean b, View convertView, ViewGroup viewGroup) {

        final OtherFeesDetailsExpandAdapter.ViewHolderChild viewHolderChild;
        child = OtherchildItems.get(groupPosition).get(childPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.monthe_details_child, null);
            viewHolderChild = new OtherFeesDetailsExpandAdapter.ViewHolderChild();

            viewHolderChild.lblMonthName = convertView.findViewById(R.id.lblMonthName);
            viewHolderChild.lblDiscount = convertView.findViewById(R.id.lblDiscount);
            viewHolderChild.lblAmountTobePaid = convertView.findViewById(R.id.lblAmountTobePaid);
            viewHolderChild.cbSubCategory = convertView.findViewById(R.id.cbSubCategory);
            viewHolderChild.rytDetails = convertView.findViewById(R.id.rytDetails);
            viewHolderChild.rytVisibleDetails = convertView.findViewById(R.id.rytVisibleDetails);
            viewHolderChild.imgHide = convertView.findViewById(R.id.imgHide);
            viewHolderChild.txtStatus = convertView.findViewById(R.id.txtStatus);
            viewHolderChild.lblStatus = convertView.findViewById(R.id.lblStatus);

            viewHolderChild.rytAmountToBePaid = convertView.findViewById(R.id.rytAmountToBePaid);
            viewHolderChild.rytDiscount = convertView.findViewById(R.id.rytDiscount);
            viewHolderChild.rytStatus = convertView.findViewById(R.id.rytStatus);
            viewHolderChild.rytfeename = convertView.findViewById(R.id.rytfeename);
            viewHolderChild.rytHeader = convertView.findViewById(R.id.rytHeader);

            viewHolderChild.txtMonthName = convertView.findViewById(R.id.txtMonthName);
            viewHolderChild.txtDiscount = convertView.findViewById(R.id.txtDiscount);
            viewHolderChild.txtAmountToBePaid = convertView.findViewById(R.id.txtAmountToBePaid);

            convertView.setTag(viewHolderChild);
        } else {
            viewHolderChild = (OtherFeesDetailsExpandAdapter.ViewHolderChild) convertView.getTag();
        }

        if (OtherchildItems.get(groupPosition).get(childPosition).get(ConstantManager.Parameter.MONTH_IS_CHECKED).equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
            viewHolderChild.cbSubCategory.setChecked(true);
            notifyDataSetChanged();
        } else {
            viewHolderChild.cbSubCategory.setChecked(false);
            notifyDataSetChanged();
        }



        if(FeeType.equals("PENDING")) {

            if (child.get(ConstantManager.Parameter.MonthIsPaid).equals("No")) {
                viewHolderChild.rytHeader.setVisibility(View.VISIBLE);
                viewHolderChild.lblStatus.setText("PENDING");
                viewHolderChild.lblStatus.setTextColor(Color.parseColor("#fc192c"));
                viewHolderChild.lblMonthName.setText("₹ "+child.get(ConstantManager.Parameter.AmountPerMonth));

            }

            else {

                viewHolderChild.rytHeader.setVisibility(View.GONE);

            }

            viewHolderChild.rytVisibleDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String visible= TeacherUtil_SharedPreference.getChildFeeMonthVisible(activity);

                    if(visible.equals("")) {
                        viewHolderChild.rytDetails.setVisibility(View.VISIBLE);
                        TeacherUtil_SharedPreference.putChildMonthVisible(activity,"1");
                        viewHolderChild.imgHide.setImageResource(R.drawable.minus);
                    }
                    else {
                        viewHolderChild.rytDetails.setVisibility(View.GONE);
                        TeacherUtil_SharedPreference.putChildMonthVisible(activity,"");
                        viewHolderChild.imgHide.setImageResource(R.drawable.plus);

                    }
                }
            });
        }

        else {
            viewHolderChild.rytVisibleDetails.setVisibility(View.GONE);

            if (child.get(ConstantManager.Parameter.MonthIsPaid).equals("Yes")) {
                viewHolderChild.rytHeader.setVisibility(View.VISIBLE);
                viewHolderChild.cbSubCategory.setVisibility(View.GONE);
                viewHolderChild.rytVisibleDetails.setVisibility(View.GONE);
                viewHolderChild.rytAmountToBePaid.setVisibility(View.GONE);
                viewHolderChild.rytStatus.setVisibility(View.GONE);
                viewHolderChild.rytDiscount.setVisibility(View.GONE);

                viewHolderChild.lblStatus.setText("PAID");
                viewHolderChild.lblStatus.setTextColor(Color.parseColor("#1dbf30"));
                viewHolderChild.lblMonthName.setText("PAID");
                viewHolderChild.lblMonthName.setTextColor(Color.parseColor("#1dbf30"));


                if (child.get(ConstantManager.Parameter.MonthName).equalsIgnoreCase("VSTotal")) {
                    viewHolderChild.rytHeader.setVisibility(View.GONE);

                }

            }

            else {
                viewHolderChild.rytHeader.setVisibility(View.GONE);
            }

        }

        if(OnlinePaymentEnable.equalsIgnoreCase("Yes")&& FeeType.equals("PENDING")){
            viewHolderChild.cbSubCategory.setVisibility(View.VISIBLE);
        }
        else {
            viewHolderChild.cbSubCategory.setVisibility(View.GONE);
        }


        viewHolderChild.txtMonthName.setText(child.get(ConstantManager.Parameter.MonthName));
        viewHolderChild.txtDiscount.setText(OtherFeesDiscountDisplayName);
        viewHolderChild.txtAmountToBePaid.setText(OtherFeesAmounttobePaidDisplayName);
        viewHolderChild.txtStatus.setText(PaymentStatus);


        viewHolderChild.lblDiscount.setText("₹ "+child.get(ConstantManager.Parameter.MonthDiscount));
        viewHolderChild.lblAmountTobePaid.setText("₹ "+child.get(ConstantManager.Parameter.MonthAmountTobePaid));

        viewHolderChild.cbSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               int position=0;
               position=childPosition-1;
               Log.d("childPosition",String.valueOf(position));

               if(position!=-1){

                   String isChecked= OtherchildItems.get(groupPosition).get(position).get(ConstantManager.Parameter.MONTH_IS_CHECKED);
                   String isPaid= OtherchildItems.get(groupPosition).get(position).get(ConstantManager.Parameter.MonthIsPaid);

                   Log.d("Checked",isChecked);

                   if(isChecked.equals(ConstantManager.CHECK_BOX_CHECKED_TRUE) || isPaid.equals("Yes")){

                      if (viewHolderChild.cbSubCategory.isChecked()) {
                          count = 0;
                          OtherchildItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                          notifyDataSetChanged();
                      } else {
                          count = 0;
                          OtherchildItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);

                          for (int i = childPosition+1; i < OtherchildItems.get(groupPosition).size(); i++) {
                              OtherchildItems.get(groupPosition).get(i).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);

                          }

                          notifyDataSetChanged();
                      }

                      for (int i = 0; i < OtherchildItems.get(groupPosition).size(); i++) {
                          if (OtherchildItems.get(groupPosition).get(i).get(ConstantManager.Parameter.MONTH_IS_CHECKED).equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
                              count++;
                          }
                      }

                      if (count == OtherchildItems.get(groupPosition).size()) {
                          OtherparentItems.get(groupPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                          notifyDataSetChanged();
                      } else {
                          OtherparentItems.get(groupPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                          notifyDataSetChanged();
                      }
                      ConstantManager.OtherchildItems = OtherchildItems;
                      ConstantManager.OtherparentItems = OtherparentItems;

                  }

                  else {
                      viewHolderChild.cbSubCategory.setChecked(false);
                      OtherchildItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);

                      notifyDataSetChanged();
                      termAlert();
                  }




               }

               else {
                   if (viewHolderChild.cbSubCategory.isChecked()) {
                       count = 0;
                       OtherchildItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                       notifyDataSetChanged();
                   } else {
                       count = 0;
                       OtherchildItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);


                       for (int i = childPosition+1; i < OtherchildItems.get(groupPosition).size(); i++) {
                           OtherchildItems.get(groupPosition).get(i).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);

                       }

                       notifyDataSetChanged();
                   }

                   for (int i = 0; i < OtherchildItems.get(groupPosition).size(); i++) {
                       if (OtherchildItems.get(groupPosition).get(i).get(ConstantManager.Parameter.MONTH_IS_CHECKED).equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
                           count++;
                       }
                   }
                   if (count == OtherchildItems.get(groupPosition).size()) {
                       OtherparentItems.get(groupPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                       notifyDataSetChanged();
                   } else {
                       OtherparentItems.get(groupPosition).put(ConstantManager.Parameter.MONTH_IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                       notifyDataSetChanged();
                   }
                   ConstantManager.OtherchildItems = OtherchildItems;
                   ConstantManager.OtherparentItems = OtherparentItems;
               }




            }
        });

        return convertView;
    }

    private void termAlert() {
        LayoutInflater li = LayoutInflater.from(activity);
        final View promptsView = li.inflate(R.layout.custom_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setView(promptsView);
        Button btnOk = (Button) promptsView.findViewById(R.id.btnOk);
        TextView lblMessages = (TextView) promptsView.findViewById(R.id.lblMessages);
        TextView lblAlertTitle = (TextView) promptsView.findViewById(R.id.lblAlertTitle);
        lblAlertTitle.setText("Alert");
        lblMessages.setText("Please pay previous Month Fees");
        final AlertDialog alertDialog = alertDialogBuilder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.cancel();

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    private class ViewHolderParent {

        TextView tvMainCategoryName;
        CheckBox cbMainCategory;
        ImageView ivCategory;
        RelativeLayout rytParentHeader;
    }

    private class ViewHolderChild {

        TextView tvSubCategoryName;
        CheckBox cbSubCategory;
        View viewDivider;

        TextView lblMonthName,lblDiscount,lblAmountTobePaid;

        TextView txtMonthName, txtDiscount, txtAmountToBePaid,txtStatus,
        lblStatus;
        RelativeLayout rytDetails,rytVisibleDetails;
        ImageView imgHide;

        RelativeLayout rytfeename, rytAmountToBePaid, rytDiscount,
        rytStatus,rytHeader;


    }


}
