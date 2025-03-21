package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.OnItemClickOnlineClass;
import com.vs.schoolmessenger.interfaces.OnlineClassStaffListener;
import com.vs.schoolmessenger.model.OnlineClassByStaffModel;
import com.vs.schoolmessenger.model.OnlineClassModel;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class OnlineClassByStaffAdapter extends RecyclerView.Adapter<OnlineClassByStaffAdapter.MyViewHolder> {

    private ArrayList<OnlineClassByStaffModel> textDataList;
    private Context context;
    private OnlineClassStaffListener onMsgItemClickListener;

    public OnlineClassByStaffAdapter(ArrayList<OnlineClassByStaffModel> textDataList, Context context, OnlineClassStaffListener onItemClickOnlineClass) {
        this.textDataList = textDataList;
        this.context = context;
        this.onMsgItemClickListener = onItemClickOnlineClass;
    }

    @Override
    public OnlineClassByStaffAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.online_class_list_item_by_staff, parent, false);

        return new OnlineClassByStaffAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OnlineClassByStaffAdapter.MyViewHolder holder, final int position) {

        final OnlineClassByStaffModel msgModel = textDataList.get(position);
        holder.bind(msgModel, onMsgItemClickListener);

        holder.lblTopic.setText(msgModel.getTopic());
        holder.lblDescription.setText(msgModel.getDescription());
        holder.lblDateTime.setText( msgModel.getMeetingdatetime());
        holder.lblMeetingType.setText(msgModel.getMeetingtype());
        holder.lblURL.setText(msgModel.getUrl());
        holder.lblSubject.setText( msgModel.getSubject_name());
        holder.lblcreatedon.setText(msgModel.getCreated_on());
        holder.lblTarget.setText(msgModel.getTarget_type());


        holder.lblTar.setTypeface(holder.lblTar.getTypeface(), Typeface.BOLD);
        holder.lblCreated.setTypeface(holder.lblCreated.getTypeface(), Typeface.BOLD);
        holder.lblSub.setTypeface(holder.lblSub.getTypeface(), Typeface.BOLD);
        holder.lblMeeting.setTypeface(holder.lblMeeting.getTypeface(), Typeface.BOLD);
        holder.lblMeetingDate.setTypeface(holder.lblMeetingDate.getTypeface(), Typeface.BOLD);


        if (msgModel.getSubject_name().equals("")) {
            holder.rytSubject.setVisibility(View.GONE);
        } else {
            holder.rytSubject.setVisibility(View.VISIBLE);
        }

        if (msgModel.getCan_cancel() == 1) {
            holder.lblCancel.setVisibility(View.VISIBLE);
        } else {
            holder.lblCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return textDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lblTopic, lblDescription, lblURL, lblSubject, lblMeetingType, lblDateTime, lblNew, lblCancel, lblTarget, lblcreatedon;
        RelativeLayout rytSubject;
        LinearLayout lnrParent;

        TextView lblTar, lblCreated, lblSub, lblMeeting, lblMeetingDate;

        public MyViewHolder(View view) {
            super(view);

            lblTopic = (TextView) view.findViewById(R.id.lblTopic);
            lblDescription = (TextView) view.findViewById(R.id.lblDescription);
            lblURL = (TextView) view.findViewById(R.id.lblURL);
            lblSubject = (TextView) view.findViewById(R.id.lblSubject);
            lblCancel = (TextView) view.findViewById(R.id.lblCancel);
            lblMeetingType = (TextView) view.findViewById(R.id.lblMeetingType);
            lblDateTime = (TextView) view.findViewById(R.id.lblDateTime);
            lblNew = (TextView) view.findViewById(R.id.lblNew);
            lblTarget = (TextView) view.findViewById(R.id.lblTarget);
            lblcreatedon = (TextView) view.findViewById(R.id.lblcreatedon);
            rytSubject = (RelativeLayout) view.findViewById(R.id.rytSubject);
            lnrParent = (LinearLayout) view.findViewById(R.id.lnrParent);

            lblTar = (TextView) view.findViewById(R.id.lblTar);
            lblCreated = (TextView) view.findViewById(R.id.lblCreated);
            lblSub = (TextView) view.findViewById(R.id.lblSub);
            lblMeeting = (TextView) view.findViewById(R.id.lblMeeting);
            lblMeetingDate = (TextView) view.findViewById(R.id.lblMeetingDate);

        }

        public void bind(final OnlineClassByStaffModel item, final OnlineClassStaffListener listener) {
            lblCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMsgItemClickListener.onItemClick(item);
                }
            });
        }
    }

    public void clearAllData() {
        int size = this.textDataList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.textDataList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }
}

