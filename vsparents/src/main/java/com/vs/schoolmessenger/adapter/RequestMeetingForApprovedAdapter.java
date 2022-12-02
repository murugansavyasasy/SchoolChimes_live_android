package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.RequestMeetingApprovedModel;

import java.util.List;

public class RequestMeetingForApprovedAdapter extends RecyclerView.Adapter<RequestMeetingForApprovedAdapter.MyViewHolder> {


    private List<RequestMeetingApprovedModel> lib_list;
    Context context;


    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView lblApprovalStatus, lblStaffName,lblRequestDate,lblRequestedFor,lblStaffComments,lblStaffCommentsLabel,
                lblSheduledTime,lblClassName;
        public ImageView imgApprovalStatus;
        public LinearLayout lnrAprovalStatus,lnrStaffName, lnrRequestDate, lnrRequestedFor, lnrStaffComments, lnrDateTime,
                lnrClass,lnrApproveAndReject;
        public Button btnApprove,btnReject;

        public MyViewHolder(View view) {
            super(view);

            lblApprovalStatus = (TextView) view.findViewById(R.id.lblApprovalStatus);
            lblStaffName = (TextView) view.findViewById(R.id.lblStaffName);
            lblRequestDate = (TextView) view.findViewById(R.id.lblRequestDate);
            lblRequestedFor = (TextView) view.findViewById(R.id.lblRequestedFor);
            lblStaffComments = (TextView) view.findViewById(R.id.lblStaffComments);
            lblStaffCommentsLabel = (TextView) view.findViewById(R.id.lblStaffCommentsLabel);
            lblSheduledTime = (TextView) view.findViewById(R.id.lblSheduledTime);
            lblClassName = (TextView) view.findViewById(R.id.lblClassName);
            lnrAprovalStatus = (LinearLayout) view.findViewById(R.id.lnrAprovalStatus);
            lnrStaffName = (LinearLayout) view.findViewById(R.id.lnrStaffName);
            lnrRequestDate = (LinearLayout) view.findViewById(R.id.lnrRequestDate);
            lnrRequestedFor = (LinearLayout) view.findViewById(R.id.lnrRequestedFor);
            lnrStaffComments = (LinearLayout) view.findViewById(R.id.lnrStaffComments);
            lnrDateTime = (LinearLayout) view.findViewById(R.id.lnrDateTime);
            lnrClass = (LinearLayout) view.findViewById(R.id.lnrClass);
            lnrApproveAndReject = (LinearLayout) view.findViewById(R.id.lnrApproveAndReject);

            btnApprove = (Button) view.findViewById(R.id.btnApprove);
            btnReject = (Button) view.findViewById(R.id.btnReject);
            imgApprovalStatus = (ImageView) view.findViewById(R.id.imgApprovalStatus);

        }
    }

    public RequestMeetingForApprovedAdapter(List<RequestMeetingApprovedModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public RequestMeetingForApprovedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_meeting_school_list, parent, false);
        return new RequestMeetingForApprovedAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RequestMeetingForApprovedAdapter.MyViewHolder holder, final int position) {
        final RequestMeetingApprovedModel history = lib_list.get(position);
        if (history.getApprovalStatus().equals("0")) {

            holder.lnrApproveAndReject.setVisibility(View.VISIBLE);
            holder.lblStaffName.setText(history.getStaffName());
            holder.lblRequestDate.setText(history.getRequestedON());
            holder.lblRequestedFor.setText(history.getParentComment());
            holder.lblStaffComments.setText(history.getStaffComment());
            holder.lblSheduledTime.setText(history.getScheduleDate()+"/"+history.getScheduleTime());
            holder.lblClassName.setText(history.getCLS());

            holder.lnrStaffComments.setVisibility(View.GONE);
            holder.lnrDateTime.setVisibility(View.GONE);

            holder.lnrAprovalStatus.setBackgroundColor(Color.parseColor("#B9B9B9"));
            holder.imgApprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.waiting_tick));

            holder.lblApprovalStatus.setText(R.string.mt_waiting_for);
            holder.lblApprovalStatus.setTextColor(Color.parseColor("#000000"));

        } else if (history.getApprovalStatus().equals("1")) {

            holder.lblStaffName.setText(": "+history.getStaffName());
            holder.lblRequestDate.setText(": "+history.getRequestedON());
            holder.lblRequestedFor.setText(": "+history.getParentComment());
            holder.lblStaffComments.setText(": "+history.getStaffComment());
            holder.lblSheduledTime.setText(": "+history.getScheduleDate()+"/"+history.getScheduleTime());
            holder.lblClassName.setText(": "+history.getCLS());

            holder.lnrAprovalStatus.setBackgroundColor(Color.parseColor("#24BB59"));
            holder.imgApprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.green_tick));
            holder.lblApprovalStatus.setText(R.string.mt_approved);

        } else if (history.getApprovalStatus().equals("2")) {

            holder.lblStaffName.setText(history.getStaffName());
            holder.lblRequestDate.setText(history.getRequestedON());
            holder.lblRequestedFor.setText(history.getParentComment());
            holder.lblStaffComments.setText(history.getStaffComment());
            holder.lblSheduledTime.setText(history.getScheduleDate()+"/"+history.getScheduleTime());
            holder.lblClassName.setText(history.getCLS());

            holder.lblRequestDate.setVisibility(View.GONE);
            holder.lnrDateTime.setVisibility(View.GONE);

            holder.lblStaffCommentsLabel.setText(R.string.mt_rejected_reason);

            holder.imgApprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.wrong_tick));
            holder.lnrAprovalStatus.setBackgroundColor(Color.parseColor("#E34545"));

            holder.lblApprovalStatus.setText(R.string.mt_rejected);

        }
    }
    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}

