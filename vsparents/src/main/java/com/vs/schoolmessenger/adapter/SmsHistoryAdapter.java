package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.SmsHistoryListener;
import com.vs.schoolmessenger.model.SmsHistoryModel;

import java.util.List;

public class SmsHistoryAdapter extends RecyclerView.Adapter<SmsHistoryAdapter.MyViewHolder> {


    private List<SmsHistoryModel> lib_list;
    Context context;
    private final SmsHistoryListener onContactsListener;

    private int prevSelection = -1;




    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }


    public void updateList(List<SmsHistoryModel> temp) {
        lib_list = temp;
        notifyDataSetChanged();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblContent, lblDescription;
        public CheckBox SelectChckbox;


        public MyViewHolder(View view) {
            super(view);

            lblContent = (TextView) view.findViewById(R.id.lblContent);
            lblDescription = (TextView) view.findViewById(R.id.lblDescription);
            SelectChckbox = (CheckBox) view.findViewById(R.id.SelectChckbox);

        }
    }

    public SmsHistoryAdapter(Context context, SmsHistoryListener onCheckListener, List<SmsHistoryModel> lib_list) {
        this.lib_list = lib_list;
        this.context = context;
        this.onContactsListener = onCheckListener;


    }

    @Override
    public SmsHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_history_list, parent, false);
        return new SmsHistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SmsHistoryAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));

        final SmsHistoryModel staffs = lib_list.get(position);

        holder.lblContent.setText(staffs.getContent());
        holder.lblDescription.setText(staffs.getDescription());



        if (staffs.getSelectedStatus()) {
            holder.SelectChckbox.setChecked(true);
            prevSelection = position;
        } else {
            holder.SelectChckbox.setChecked(false);
        }
        holder.SelectChckbox.setOnCheckedChangeListener(null);
        holder.SelectChckbox.setChecked(staffs.getSelectedStatus());
        holder.SelectChckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    staffs.setSeletedStatus(true);

                    onContactsListener.smsHistoryAddList(staffs);

                    if (prevSelection >= 0) {
                        lib_list.get(prevSelection).setSeletedStatus(false);
                        notifyItemChanged(prevSelection);
                    }
                    prevSelection = position;
                }


                else {
                     onContactsListener.smsHistoryRemoveList(staffs);
                }

            }
        });


    }





    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return lib_list.size();
    }
}

