package com.vs.schoolmessenger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.databinding.StaffListItemBinding;
import com.vs.schoolmessenger.interfaces.SubjectSelectedListener;
import com.vs.schoolmessenger.model.StaffListChat;
import com.vs.schoolmessenger.model.SubjectDetail;
import com.vs.schoolmessenger.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class StaffChatListAdapter extends RecyclerView.Adapter<BaseViewHolders<StaffListItemBinding>> {

    ArrayList<SubjectDetail> subjectDetails;
    SubjectSelectedListener listener;

    public StaffChatListAdapter(ArrayList<SubjectDetail> subjectDetails, SubjectSelectedListener listener) {
        this.subjectDetails = subjectDetails;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolders<StaffListItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolders<StaffListItemBinding>(StaffListItemBinding.bind(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_list_item, parent, false))) {
            @Override
            public int getVariable() {
                return 0;
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolders<StaffListItemBinding> holder, final int position) {
          holder.getBinding().name.setText(String.format("Name : %s", subjectDetails.get(position).staffname));
        if (subjectDetails.get(position).subjectname.equalsIgnoreCase(Constants.CLASS_TEACHER))
            holder.getBinding().subject.setText(subjectDetails.get(position).subjectname);
        else
          holder.getBinding().subject.setText(String.format("Sub : %s", subjectDetails.get(position).subjectname));
          if(subjectDetails.get(position).staffname.equalsIgnoreCase("Not Assigned"))
              holder.getBinding().interact.setVisibility(View.GONE);
          else
              holder.getBinding().interact.setVisibility(View.VISIBLE);
          holder.getBinding().interact.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  listener.click(subjectDetails.get(position));
              }
          });
    }

    @Override
    public int getItemCount() {
        return subjectDetails.size();
    }

    public void updateList(ArrayList<SubjectDetail> temp) {
        subjectDetails = temp;
        notifyDataSetChanged();
    }
}
