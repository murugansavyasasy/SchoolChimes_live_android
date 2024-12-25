package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.UploadDocListener;
import com.vs.schoolmessenger.model.UploadFilesModel;

import java.util.List;

public  class UploadedDocsAdapter extends RecyclerView.Adapter<UploadedDocsAdapter.MyViewHolder> {

    private List<UploadFilesModel> dateList;
    Context context;
    private final UploadDocListener listener;

    @Override
    public UploadedDocsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.uploaded_doc_list, parent, false);

        return new UploadedDocsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UploadedDocsAdapter.MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener);
        final UploadFilesModel profile = dateList.get(position);
        holder.lblFilePath.setText(profile.getWsUploadedDoc());

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblFilePath;
        public RelativeLayout rytDelete;


        public MyViewHolder(View view) {
            super(view);

            lblFilePath = (TextView) view.findViewById(R.id.lblFilePath);
            rytDelete = (RelativeLayout) view.findViewById(R.id.rytDelete);
        }

        public void bind(final UploadFilesModel item, final UploadDocListener listener) {
            rytDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.Doc_removeClass(item);
                }
            });
        }
    }

    public UploadedDocsAdapter(Context context, List<UploadFilesModel> dateList, UploadDocListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllData() {
        int size = this.dateList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.dateList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }
}
