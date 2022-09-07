package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.OnItemClickOnlineClass;
import com.vs.schoolmessenger.model.OnlineClassModel;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class OnlineClassAdapter extends RecyclerView.Adapter<OnlineClassAdapter.MyViewHolder> {

    private ArrayList<OnlineClassModel> textDataList;
    private Context context;
    private OnItemClickOnlineClass onMsgItemClickListener;

    public OnlineClassAdapter(ArrayList<OnlineClassModel> textDataList, Context context, OnItemClickOnlineClass onItemClickOnlineClass) {
        this.textDataList = textDataList;
        this.context = context;
        this.onMsgItemClickListener = onItemClickOnlineClass;
    }

    @Override
    public OnlineClassAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_online_classes, parent, false);

        return new OnlineClassAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OnlineClassAdapter.MyViewHolder holder, final int position) {

        final OnlineClassModel msgModel = textDataList.get(position);
        holder.bind(msgModel, onMsgItemClickListener);

        holder.lblTopic.setText(msgModel.getTopic());
        holder.lblDescription.setText(msgModel.getDescription());
        holder.lblDateTime.setText("DATE & TIME : " +msgModel.getMeetingdatetime());
        holder.lblMeetingType.setText("MEETING TYPE : "+msgModel.getMeetingtype());
        holder.lblURL.setText(msgModel.getUrl());
        holder.lblSubject.setText("SUBJECT : "+msgModel.getSubject_name());

        if(msgModel.getSubject_name().equals("")){
            holder.rytSubject.setVisibility(View.GONE);
        }
        else{
            holder.rytSubject.setVisibility(View.VISIBLE);
        }
        if (msgModel.getIs_app_viewed().equals("0"))
            holder.lblNew.setVisibility(View.VISIBLE);
        else holder.lblNew.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return textDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lblTopic, lblDescription, lblURL, lblSubject, lblMeetingType,lblDateTime,lblNew;
        RelativeLayout rytSubject;
        LinearLayout lnrParent;



        public MyViewHolder(View view) {
            super(view);

            lblTopic = (TextView) view.findViewById(R.id.lblTopic);
            lblDescription = (TextView) view.findViewById(R.id.lblDescription);
            lblURL = (TextView) view.findViewById(R.id.lblURL);
            lblSubject = (TextView) view.findViewById(R.id.lblSubject);
            lblMeetingType = (TextView) view.findViewById(R.id.lblMeetingType);
            lblDateTime = (TextView) view.findViewById(R.id.lblDateTime);
            lblNew = (TextView) view.findViewById(R.id.lblNew);
            rytSubject = (RelativeLayout) view.findViewById(R.id.rytSubject);
            lnrParent = (LinearLayout) view.findViewById(R.id.lnrParent);
        }

        public void bind(final OnlineClassModel item, final OnItemClickOnlineClass listener) {
            lnrParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMsgItemClickListener.onMsgItemClick(item);
                }
            });
        }
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
