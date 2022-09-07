package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.OnMsgItemClickListener;
import com.vs.schoolmessenger.model.MessageModel;

import java.util.ArrayList;

/**
 * Created by devi on 5/3/2017.
 */

public class TextCircularListAdapter extends RecyclerView.Adapter<TextCircularListAdapter.MyViewHolder> {

    private ArrayList<MessageModel> textDataList;
    private Context context;
    OnMsgItemClickListener onMsgItemClickListener;

    public TextCircularListAdapter(ArrayList<MessageModel> textDataList, Context context, OnMsgItemClickListener onMsgItemClickListener) {
        this.textDataList = textDataList;
        this.context = context;
        this.onMsgItemClickListener = onMsgItemClickListener;
    }

    @Override
    public TextCircularListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_text, parent, false);

        return new TextCircularListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TextCircularListAdapter.MyViewHolder holder, final int position) {

        final MessageModel msgModel = textDataList.get(position);
        holder.bind(msgModel, onMsgItemClickListener);

        holder.tvTitle.setText(msgModel.getMsgTitle());
        holder.tvDate.setText(msgModel.getMsgDate());
        holder.tvTime.setText(msgModel.getMsgTime());
        holder.tvMsgContent.setText(msgModel.getMsgContent());
        holder.tvDescription.setText(msgModel.getMsgdescription());
        if(msgModel.getMsgdescription().equals("")){
            holder.tvDescription.setVisibility(View.GONE);
        }
        else{
            holder.tvDescription.setVisibility(View.VISIBLE);
        }
        if (msgModel.getMsgReadStatus().equals("0"))
            holder.tvStatus.setVisibility(View.VISIBLE);
        else holder.tvStatus.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return textDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvTime, tvStatus, tvMsgContent,tvDescription;

        public MyViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.cardText_tvTitle);
            tvDate = (TextView) view.findViewById(R.id.cardText_tvDate);
            tvTime = (TextView) view.findViewById(R.id.cardText_tvTime);
            tvStatus = (TextView) view.findViewById(R.id.cardText_tvNew);
            tvMsgContent = (TextView) view.findViewById(R.id.cardText_tvShortMsg);
            tvDescription = (TextView) view.findViewById(R.id.tv_description_text);
        }

        public void bind(final MessageModel item, final OnMsgItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMsgItemClick(item);
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

