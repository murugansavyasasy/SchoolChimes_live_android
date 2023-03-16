package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.vs.schoolmessenger.R;


public class NewupdatesAdapter extends RecyclerView.Adapter<NewupdatesAdapter.MyViewHolder> {
    private String[] lib_list;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView lblName;
        public MyViewHolder(View view) {
            super(view);
            lblName = (TextView) view.findViewById(R.id.text);
        }
    }

    public NewupdatesAdapter(String[] lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public NewupdatesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_updates_lable, parent, false);
        return new NewupdatesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewupdatesAdapter.MyViewHolder holder, final int
            position) {

        holder.lblName.setText("\u25CF"+" "+lib_list[position]);
    }


    @Override
    public int getItemCount() {
        return lib_list.length;
    }


}