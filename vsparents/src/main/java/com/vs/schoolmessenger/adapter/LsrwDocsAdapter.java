package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.UploadProfileScreen;
import com.vs.schoolmessenger.interfaces.UploadDocListener;
import com.vs.schoolmessenger.model.UploadFilesModel;

import java.util.List;

public  class LsrwDocsAdapter extends RecyclerView.Adapter<LsrwDocsAdapter.MyViewHolder> {

    private List<UploadFilesModel> dateList;
    Context context;
    private final UploadDocListener listener;
    String type;

    @Override
    public LsrwDocsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lsrw_doc_list, parent, false);

        return new LsrwDocsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LsrwDocsAdapter.MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener);
        final UploadFilesModel profile = dateList.get(position);

        if(type.equals("1")) {
            holder.lblFilePath.setText(profile.getIsFileName());
            if (profile.getDisplayname().equals("IMAGE")) {
                Glide.with(context)
                        .load(R.drawable.ic_image)
                        .into(holder.imgview);
            } else if (profile.getDisplayname().equals("PDF")) {
                Glide.with(context)
                        .load(R.drawable.pdf_image)
                        .into(holder.imgview);
            } else if (profile.getDisplayname().equals("VIDEO")) {
                Glide.with(context)
                        .load(R.drawable.videoimg)
                        .into(holder.imgview);
            } else if (profile.getDisplayname().equals("VOICE")) {
                Glide.with(context)
                        .load(R.drawable.c_genvoice)
                        .into(holder.imgview);
            } else if (profile.getDisplayname().equals("TEXT")) {
                Glide.with(context)
                        .load(R.drawable.teacher_ic_text)
                        .into(holder.imgview);
            }
        }
        else{
            holder.rytDelete.setVisibility(View.GONE);
            if (profile.getDisplayname().equals("IMAGE")) {
                Glide.with(context)
                        .load(profile.getWsUploadedDoc())
                        .into(holder.imgview);
                holder.lblFilePath.setText(profile.getIsFileName());
            }
            else if(profile.getDisplayname().equals("PDF")){
                Glide.with(context)
                        .load(R.drawable.pdf_image)
                        .into(holder.imgview);
                holder.lblFilePath.setText(profile.getIsFileName());
            }
            else{
                holder.constraint.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblFilePath;
        public RelativeLayout rytDelete;
        ImageView imgview;
        ConstraintLayout constraint;
        public MyViewHolder(View view) {
            super(view);

            lblFilePath = (TextView) view.findViewById(R.id.lblFilePath);
            rytDelete = (RelativeLayout) view.findViewById(R.id.rytDelete);
            imgview = (ImageView) view.findViewById(R.id.imgview);
            constraint = (ConstraintLayout) view.findViewById(R.id.constraint);
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

    public LsrwDocsAdapter(Context context, List<UploadFilesModel> dateList,String type, UploadDocListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.type = type;
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
