package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.LibraryDetails;

import java.util.List;

public class LibraryDetailsAdapter extends RecyclerView.Adapter<LibraryDetailsAdapter.MyViewHolder> {
    private List<LibraryDetails> lib_list;
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
        public TextView lblBookID, lblBookName, lblIssuedDate, lblDueDate;

        public MyViewHolder(View view) {
            super(view);

            lblBookID = (TextView) view.findViewById(R.id.lblBookID);
            lblBookName = (TextView) view.findViewById(R.id.lblBookName);
            lblIssuedDate = (TextView) view.findViewById(R.id.lblIssuedDate);
            lblDueDate = (TextView) view.findViewById(R.id.lblDueDate);
            }
    }

    public LibraryDetailsAdapter(List<LibraryDetails> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
        }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lib_details_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LibraryDetailsAdapter.MyViewHolder holder, final int position) {

            final LibraryDetails library = lib_list.get(position);
             holder.lblBookID.setText(": "+library.getId());
             holder.lblBookName.setText(": "+library.getName());
             holder.lblIssuedDate.setText(": "+library.getIssuedDate());
             holder.lblDueDate.setText(": "+library.getDueDate());

    }
    @Override
    public int getItemCount() {
        return lib_list.size();
    }
    public void updateList(List<LibraryDetails> temp) {
        lib_list = temp;
        notifyDataSetChanged();
    }
}

