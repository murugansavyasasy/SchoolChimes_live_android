package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.databinding.TeacherChatItemBinding;
import com.vs.schoolmessenger.interfaces.PopupListener;
import com.vs.schoolmessenger.model.TeacherChat;
import com.vs.schoolmessenger.util.Constants;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class TeacherChatAdapter extends RecyclerView.Adapter<BaseViewHolders<TeacherChatItemBinding>> {
    Context context;
    ArrayList<TeacherChat> teacherChats;
    public PopupListener popupListener;
    String comeFrom;

    public TeacherChatAdapter(String comeFrom, Context context, PopupListener popupListener) {
        this.context = context;
        this.popupListener = popupListener;
        this.comeFrom = comeFrom;
    }

    @NonNull
    @Override
    public BaseViewHolders<TeacherChatItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolders<TeacherChatItemBinding>(TeacherChatItemBinding.bind(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_chat_item, parent, false))) {
            @Override
            public int getVariable() {
                return 0;
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolders<TeacherChatItemBinding> holder, final int position) {
        holder.getBinding().menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.answer_menu, popup.getMenu());
                if (teacherChats.get(position).ChangeAnswer.equals("1"))
                    popup.getMenu().findItem(R.id.answer).setVisible(false);
                else
                    popup.getMenu().findItem(R.id.change_answer).setVisible(false);
                popup.show();



                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.answer) {
                            popupListener.click("Answer", teacherChats.get(position));
                            return true;
                        } else {
                            popupListener.click("ChangeAnswer", teacherChats.get(position));
                            return true;
                        }
                    }
                });
            }
        });
        holder.getBinding().menu.setVisibility(comeFrom.equals(Constants.STAFF) ? View.VISIBLE : View.GONE);
        holder.getBinding().studentMessage.setText(teacherChats.get(position).Question);
        holder.getBinding().studentTime.setText(teacherChats.get(position).CreatedOn);
        holder.getBinding().studentName.setText(teacherChats.get(position).StudentName);

        if(teacherChats.get(position).is_staff_viewed.equals("0")){
            holder.getBinding().cardNew.setVisibility(View.VISIBLE);
        }
        else {
            holder.getBinding().cardNew.setVisibility(View.GONE);
        }

        if (teacherChats.get(position).AnsweredOn.isEmpty()) {
            holder.getBinding().teacher.setVisibility(View.GONE);
        } else {
            holder.getBinding().teacher.setVisibility(View.VISIBLE);
            holder.getBinding().message.setText(teacherChats.get(position).Answer);
            holder.getBinding().time.setText(teacherChats.get(position).AnsweredOn);
        }
    }

    @Override
    public int getItemCount() {
        return teacherChats != null ? teacherChats.size() : 0;
    }

    public void addTeacherChatList(ArrayList<TeacherChat> teacherChats) {
        this.teacherChats = teacherChats;
    }
}

