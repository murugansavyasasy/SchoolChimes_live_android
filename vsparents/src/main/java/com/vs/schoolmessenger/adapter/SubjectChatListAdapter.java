package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.databinding.SubjectListItemBinding;
import com.vs.schoolmessenger.interfaces.TeacherSelectListener;
import com.vs.schoolmessenger.model.Subject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class SubjectChatListAdapter extends RecyclerView.Adapter<BaseViewHolders<SubjectListItemBinding>> {

    ArrayList<Subject> subjects;
    TeacherSelectListener listener;

    public SubjectChatListAdapter(ArrayList<Subject> subjects, TeacherSelectListener listener) {
        this.subjects = subjects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolders<SubjectListItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolders<SubjectListItemBinding>(SubjectListItemBinding.bind(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_list_item, parent, false))) {
            @Override
            public int getVariable() {
                return 0;
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolders<SubjectListItemBinding> holder, final int position) {
        if (subjects.get(position).SubjectName != null)
            holder.getBinding().subject.setText(String.format("%s-%s %s", subjects.get(position).Standard, subjects.get(position).Section, subjects.get(position).SubjectName));
        else {
            listener.showToast(subjects.get(position).Standard);
            holder.getBinding().subjectTeacher.setVisibility(View.GONE);
        }

        holder.getBinding().subjectTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.click(subjects.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }
}
