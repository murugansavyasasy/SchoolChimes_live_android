package com.vs.schoolmessenger.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
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

/**
 * Created by zerones on 04-Oct-17.
 */

public class MyCategoriesExpandableListAdapter extends BaseExpandableListAdapter {

    public static ArrayList<ArrayList<HashMap<String, String>>> childItems;
    public static ArrayList<HashMap<String, String>> parentItems;
    //    private final ArrayList<HashMap<String, String>> childItems;
    private LayoutInflater inflater;
    private Activity activity;
    private HashMap<String, String> child;
    private int count = 0;
    private boolean isFromMyCategoriesFragment;

    private String TermFeesFeeNameDisplayName;
    private String TermFeesFeeCostDisplayName;
    private String TermFeesAmountPerMonthDisplayName;
    private String TermFeesPaidDisplayName;
    private String TermFeesPendingDisplayName;
    private String TermFeesDiscountDisplayName;
    private String TermFeesAmounttobePaidDisplayName;
    private String PaymentStatus;
    private String FeeType;

    String ExpandHandling="";
    String OnlinePaymentEnable="";


    String groupExpandHandling="";
    String ExpandGroupPosition="";

    public MyCategoriesExpandableListAdapter(Activity activity, ArrayList<HashMap<String, String>> parentItems,
                                             ArrayList<ArrayList<HashMap<String, String>>> childItems, boolean isFromMyCategoriesFragment,
                                             String TermFeesFeeNameDisplayName,String TermFeesFeeCostDisplayName,String TermFeesAmountPerMonthDisplayName,
                                             String TermFeesPaidDisplayName,String TermFeesPendingDisplayName,String TermFeesDiscountDisplayName,
                                             String TermFeesAmounttobePaidDisplayName,String status,String Type,String onlinePayment) {

        this.parentItems = parentItems;
        this.childItems = childItems;
        this.activity = activity;
        this.isFromMyCategoriesFragment = isFromMyCategoriesFragment;
        this.TermFeesFeeNameDisplayName = TermFeesFeeNameDisplayName;
        this.TermFeesFeeCostDisplayName = TermFeesFeeCostDisplayName;
        this.TermFeesAmountPerMonthDisplayName = TermFeesAmountPerMonthDisplayName;
        this.TermFeesPaidDisplayName = TermFeesPaidDisplayName;
        this.TermFeesPendingDisplayName = TermFeesPendingDisplayName;
        this.TermFeesDiscountDisplayName = TermFeesDiscountDisplayName;
        this.TermFeesAmounttobePaidDisplayName = TermFeesAmounttobePaidDisplayName;
        this.PaymentStatus = status;
        this.FeeType = Type;
        this.OnlinePaymentEnable = onlinePayment;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getGroupCount() {
        return parentItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (childItems.get(groupPosition)).size();
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
         final ViewHolderParent viewHolderParent;
        if (convertView == null) {

            if(isFromMyCategoriesFragment) {
                convertView = inflater.inflate(R.layout.group_list_layout_my_categories, null);
            }else {
                convertView = inflater.inflate(R.layout.group_list_layout_choose_categories, null);
            }
            viewHolderParent = new ViewHolderParent();

            viewHolderParent.tvMainCategoryName = convertView.findViewById(R.id.tvMainCategoryName);
            viewHolderParent.cbMainCategory = convertView.findViewById(R.id.cbMainCategory);
            viewHolderParent.ivCategory = convertView.findViewById(R.id.ivCategory);
            viewHolderParent.rytParentHeader = convertView.findViewById(R.id.rytParentHeader);
            convertView.setTag(viewHolderParent);
        } else {
            viewHolderParent = (ViewHolderParent) convertView.getTag();
        }

        if (parentItems.get(groupPosition).get(ConstantManager.Parameter.IS_CHECKED).equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
            viewHolderParent.cbMainCategory.setChecked(true);
            notifyDataSetChanged();

        } else {
            viewHolderParent.cbMainCategory.setChecked(false);
            notifyDataSetChanged();
        }

        viewHolderParent.cbMainCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(FeeType.equals("PENDING")) {
                    int a = groupPosition - 1;

                    if (a != -1) {
                        if (MyCategoriesExpandableListAdapter.childItems.get(a) != null) {
                            for (int j = 0; j < MyCategoriesExpandableListAdapter.childItems.get(a).size(); j++) {
                                String isChildChecked = MyCategoriesExpandableListAdapter.childItems.get(a).get(j).get(ConstantManager.Parameter.IS_CHECKED);
                                String total = MyCategoriesExpandableListAdapter.childItems.get(a).get(j).get(ConstantManager.Parameter.FeeName);

                                String isPaid = MyCategoriesExpandableListAdapter.childItems.get(a).get(j).get(ConstantManager.Parameter.IsPaid);

                                if (!total.equals("VSTotal")) {

                                    if(isPaid.equals("No")) {
                                        if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_FALSE)) {
                                            ExpandHandling = "1";

                                        }
                                    }
                                }

                            }
                        }
                    }

                    else {

                        if (viewHolderParent.cbMainCategory.isChecked()) {
                            parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);

                            for (int i = 0; i < childItems.get(groupPosition).size(); i++) {
                                childItems.get(groupPosition).get(i).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                            }
                            notifyDataSetChanged();

                        }
                        else {
                            parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                            for (int i = 0; i < childItems.get(groupPosition).size(); i++) {
                                childItems.get(groupPosition).get(i).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                            }




                            if (MyCategoriesExpandableListAdapter.parentItems != null) {
                                for (int i = groupPosition+1; i < MyCategoriesExpandableListAdapter.parentItems.size(); i++) {
                                    parentItems.get(i).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                                    for (int j = 0; j < childItems.get(i).size(); j++) {
                                        childItems.get(i).get(j).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                                    }
                                }
                            }



                            notifyDataSetChanged();
                        }

                    }


                    if (ExpandHandling.equals("1")) {

                        viewHolderParent.cbMainCategory.setChecked(false);
                        parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                        for (int i = 0; i < childItems.get(groupPosition).size(); i++) {
                            childItems.get(groupPosition).get(i).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                        }
                        notifyDataSetChanged();

                        termAlertt();
                    }
                    else {
                        if (viewHolderParent.cbMainCategory.isChecked()) {
                            parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);

                            for (int i = 0; i < childItems.get(groupPosition).size(); i++) {
                                childItems.get(groupPosition).get(i).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                            }
                            notifyDataSetChanged();

                        }
                        else {
                            parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                            for (int i = 0; i < childItems.get(groupPosition).size(); i++) {
                                childItems.get(groupPosition).get(i).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                            }


                            if (MyCategoriesExpandableListAdapter.parentItems != null) {
                                for (int i = groupPosition+1; i < MyCategoriesExpandableListAdapter.parentItems.size(); i++) {
                                    parentItems.get(i).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                                    for (int j = 0; j < childItems.get(i).size(); j++) {
                                        childItems.get(i).get(j).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                                    }
                                }
                            }


                            notifyDataSetChanged();
                        }

                    }
                    ExpandHandling = "";

                }





            }
        });

        ConstantManager.childItems = childItems;
        ConstantManager.parentItems = parentItems;

        viewHolderParent.tvMainCategoryName.setText(parentItems.get(groupPosition).get(ConstantManager.Parameter.TermFeesName));





        if(OnlinePaymentEnable.equalsIgnoreCase("Yes")) {

            if (FeeType.equals("PENDING")) {

                if (parentItems.get(groupPosition).get(ConstantManager.Parameter.TermFullyPaid).equals("Yes")) {
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

    private void termAlertt() {
        LayoutInflater li = LayoutInflater.from(activity);
        final View promptsView = li.inflate(R.layout.custom_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setView(promptsView);
        Button btnOk = (Button) promptsView.findViewById(R.id.btnOk);
        TextView lblMessages = (TextView) promptsView.findViewById(R.id.lblMessages);
        TextView lblAlertTitle = (TextView) promptsView.findViewById(R.id.lblAlertTitle);
        lblAlertTitle.setText("Alert");
        lblMessages.setText("Please pay previous Term Fees");
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
    public View getChildView(final int groupPosition, final int childPosition, final boolean b, View convertView, ViewGroup viewGroup) {

        final ViewHolderChild viewHolderChild;
        child = childItems.get(groupPosition).get(childPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_list_layout_choose_category, null);
            viewHolderChild = new ViewHolderChild();

            viewHolderChild.lblDiscount = convertView.findViewById(R.id.lblDiscount);
            viewHolderChild.lblAmountTobePaid = convertView.findViewById(R.id.lblAmountTobePaid);
            viewHolderChild.cbSubCategory = convertView.findViewById(R.id.cbSubCategory);
            viewHolderChild.lblfeename = convertView.findViewById(R.id.lblfeename);

            viewHolderChild.txtFeeName = convertView.findViewById(R.id.txtFeeName);
            viewHolderChild.txtDiscount = convertView.findViewById(R.id.txtDiscount);
            viewHolderChild.txtAmountToBePaid = convertView.findViewById(R.id.txtAmountToBePaid);
            viewHolderChild.lblDetails = convertView.findViewById(R.id.lblDetails);
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


            convertView.setTag(viewHolderChild);
        } else {
            viewHolderChild = (ViewHolderChild) convertView.getTag();
        }

        if (childItems.get(groupPosition).get(childPosition).get(ConstantManager.Parameter.IS_CHECKED).equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
            viewHolderChild.cbSubCategory.setChecked(true);
            notifyDataSetChanged();
        } else {
            viewHolderChild.cbSubCategory.setChecked(false);
            notifyDataSetChanged();
        }

        if(FeeType.equals("PENDING")) {

            if (child.get(ConstantManager.Parameter.IsPaid).equals("No")) {
               viewHolderChild.rytHeader.setVisibility(View.VISIBLE);
               viewHolderChild.lblStatus.setText("PENDING");
                viewHolderChild.lblStatus.setTextColor(Color.parseColor("#fc192c"));
                viewHolderChild.lblfeename.setText("₹ "+child.get(ConstantManager.Parameter.FeeAmount));


            }

            else {

                    viewHolderChild.rytHeader.setVisibility(View.GONE);

            }

            viewHolderChild.rytVisibleDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String visible = TeacherUtil_SharedPreference.getChildFeeVisible(activity);
                    if (visible.equals("")) {
                        viewHolderChild.rytDetails.setVisibility(View.VISIBLE);
                        TeacherUtil_SharedPreference.putChildVisible(activity, "1");
                        viewHolderChild.imgHide.setImageResource(R.drawable.minus);
                    } else {
                        viewHolderChild.rytDetails.setVisibility(View.GONE);
                        TeacherUtil_SharedPreference.putChildVisible(activity, "");
                        viewHolderChild.imgHide.setImageResource(R.drawable.plus);

                    }
                }
            });
        }

        else {
            viewHolderChild.rytVisibleDetails.setVisibility(View.GONE);

            if (child.get(ConstantManager.Parameter.IsPaid).equals("Yes")) {
                viewHolderChild.rytHeader.setVisibility(View.VISIBLE);
                viewHolderChild.cbSubCategory.setVisibility(View.GONE);
                viewHolderChild.rytVisibleDetails.setVisibility(View.GONE);
                viewHolderChild.rytAmountToBePaid.setVisibility(View.GONE);
                viewHolderChild.rytStatus.setVisibility(View.GONE);
                viewHolderChild.rytDiscount.setVisibility(View.GONE);

                viewHolderChild.lblStatus.setText("PAID");
                viewHolderChild.lblStatus.setTextColor(Color.parseColor("#1dbf30"));
                viewHolderChild.lblfeename.setText("PAID");
                viewHolderChild.lblfeename.setTextColor(Color.parseColor("#1dbf30"));


                if (child.get(ConstantManager.Parameter.FeeName).equalsIgnoreCase("VSTotal")) {
                    viewHolderChild.rytHeader.setVisibility(View.GONE);

                }

            }

            else {
                   viewHolderChild.rytHeader.setVisibility(View.GONE);
            }

        }

        if(OnlinePaymentEnable.equalsIgnoreCase("Yes")&&FeeType.equals("PENDING")){
            viewHolderChild.cbSubCategory.setVisibility(View.VISIBLE);
        }
        else {
            viewHolderChild.cbSubCategory.setVisibility(View.GONE);

        }


        viewHolderChild.txtFeeName.setText(child.get(ConstantManager.Parameter.FeeName));
        viewHolderChild.txtDiscount.setText(TermFeesDiscountDisplayName);
        viewHolderChild.txtAmountToBePaid.setText(TermFeesAmounttobePaidDisplayName);
        viewHolderChild.txtStatus.setText(PaymentStatus);
        viewHolderChild.lblDiscount.setText("₹ "+child.get(ConstantManager.Parameter.DiscountAMount));
        viewHolderChild.lblAmountTobePaid.setText("₹ "+child.get(ConstantManager.Parameter.AmountToBePaid));


        viewHolderChild.cbSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(OnlinePaymentEnable.equals("Yes")) {

                    if (FeeType.equals("PENDING")) {

                        int a = groupPosition - 1;

                        if (a != -1) {
                            if (MyCategoriesExpandableListAdapter.childItems.get(a) != null) {
                                for (int j = 0; j < MyCategoriesExpandableListAdapter.childItems.get(a).size(); j++) {
                                    String isChildChecked = MyCategoriesExpandableListAdapter.childItems.get(a).get(j).get(ConstantManager.Parameter.IS_CHECKED);
                                    String total = MyCategoriesExpandableListAdapter.childItems.get(a).get(j).get(ConstantManager.Parameter.FeeName);

                                    String isPaid = MyCategoriesExpandableListAdapter.childItems.get(a).get(j).get(ConstantManager.Parameter.IsPaid);

                                    if (!total.equals("VSTotal")) {

                                        if (isPaid.equals("No")) {
                                            if (isChildChecked.equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_FALSE)) {
                                                groupExpandHandling = "1";

                                            }
                                        }
                                    }

                                }
                            }
                        }

                        if (groupExpandHandling.equals("1")) {
                            ExpandGroupPosition = "1";

                            childItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                            notifyDataSetChanged();

                            termAlert();

                        }
                        else {
                            ExpandGroupPosition = "";

                            if (viewHolderChild.cbSubCategory.isChecked()) {
                                count = 0;
                                childItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                                notifyDataSetChanged();
                            } else {
                                count = 0;
                                childItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);



                                //newly added
                                for (int i = groupPosition+1; i < parentItems.size(); i++) {
                                    parentItems.get(i).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                                    for (int j = 0; j < childItems.get(i).size(); j++) {
                                        childItems.get(i).get(j).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                                    }
                                }
                                //newly added



                                notifyDataSetChanged();
                            }

                            for (int i = 0; i < childItems.get(groupPosition).size(); i++) {
                                if (childItems.get(groupPosition).get(i).get(ConstantManager.Parameter.IS_CHECKED).equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
                                    count++;
                                }
                            }
                            if (count == childItems.get(groupPosition).size()) {
                                parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
                                notifyDataSetChanged();
                            } else {
                                parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
                                notifyDataSetChanged();
                            }
                            ConstantManager.childItems = childItems;
                            ConstantManager.parentItems = parentItems;


                        }
                        groupExpandHandling = "";

                    }

                }



//                if (viewHolderChild.cbSubCategory.isChecked()) {
//                    count = 0;
//                    childItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
//                    notifyDataSetChanged();
//                } else {
//                    count = 0;
//                    childItems.get(groupPosition).get(childPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
//                    notifyDataSetChanged();
//                }
//
//                for (int i = 0; i < childItems.get(groupPosition).size(); i++) {
//                    if (childItems.get(groupPosition).get(i).get(ConstantManager.Parameter.IS_CHECKED).equalsIgnoreCase(ConstantManager.CHECK_BOX_CHECKED_TRUE)) {
//                        count++;
//                    }
//                }
//                if (count == childItems.get(groupPosition).size()) {
//                    parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_TRUE);
//                    notifyDataSetChanged();
//                } else {
//                    parentItems.get(groupPosition).put(ConstantManager.Parameter.IS_CHECKED, ConstantManager.CHECK_BOX_CHECKED_FALSE);
//                    notifyDataSetChanged();
//                }
//                ConstantManager.childItems = childItems;
//                ConstantManager.parentItems = parentItems;




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

        TextView lblDiscount,lblAmountTobePaid,lblfeename;

       TextView txtFeeName, txtDiscount, txtAmountToBePaid,lblDetails,txtStatus,
        lblStatus;
       RelativeLayout rytDetails,rytVisibleDetails,rytAmountToBePaid,rytDiscount,rytStatus,rytfeename,rytHeader;
       ImageView imgHide;




    }


}
