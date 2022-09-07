package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.FAQModel;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.MyViewHolder> {


    private List<FAQModel> lib_list;
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
        public TextView lblQuestion, lblAnswer ;




        public MyViewHolder(View view) {
            super(view);

            lblQuestion = (TextView) view.findViewById(R.id.lblQuestion);
            lblAnswer = (TextView) view.findViewById(R.id.lblAnswer);

        }
    }

    public FAQAdapter(List<FAQModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public FAQAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_list, parent, false);
        return new FAQAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FAQAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final FAQModel questions = lib_list.get(position);

        holder.lblQuestion.setText(questions.getQuestion());
        holder.lblAnswer.setText(" : "+questions.getAnswer());

        }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}

