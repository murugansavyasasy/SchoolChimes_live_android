package com.vs.schoolmessenger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.databinding.StudentChatItemBinding;
import com.vs.schoolmessenger.model.StudentChat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class StudentChatAdapter extends RecyclerView.Adapter<BaseViewHolders<StudentChatItemBinding>> {

   public ArrayList<StudentChat> studentChats;
    public String staffName;

//    public StudentChatAdapter(String staffName) {
//        this.staffName = staffName;
//    }


    public StudentChatAdapter(ArrayList<StudentChat> studentChats,String isStaffName) {
        this.studentChats=studentChats;
        this.staffName = isStaffName;
    }

    @NonNull
    @Override
    public BaseViewHolders<StudentChatItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolders<StudentChatItemBinding>(StudentChatItemBinding.bind(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_chat_item, parent, false))) {
            @Override
            public int getVariable() {
                return 0;
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolders<StudentChatItemBinding> holder, int position) {
        holder.getBinding().studentMessage.setText(studentChats.get(position).Question);
        holder.getBinding().studentTime.setText(studentChats.get(position).CreatedOn);

        if (studentChats.get(position).AnsweredOn.isEmpty()) {
            holder.getBinding().teacher.setVisibility(View.GONE);
        } else {
            holder.getBinding().teacher.setVisibility(View.VISIBLE);
            holder.getBinding().name.setText(staffName);
            holder.getBinding().message.setText(studentChats.get(position).Answer);
            holder.getBinding().time.setText(studentChats.get(position).AnsweredOn);
        }


        if(studentChats.get(position).is_staff_viewed.equals("0")){
            holder.getBinding().cardNew.setVisibility(View.VISIBLE);
        }
        else {
            holder.getBinding().cardNew.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return studentChats != null ? studentChats.size() : 0;
    }

//    public void addStudentChatList(ArrayList<StudentChat> studentChats) {
//        this.studentChats = studentChats;
//        notifyDataSetChanged();
//    }
}
