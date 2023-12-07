package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ImageCircularPopUp;
import com.vs.schoolmessenger.activity.ImageCircularPopUpParent;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.DownloadFileFromURL;

import java.util.ArrayList;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_IMAGE;

/**
 * Created by devi on 5/3/2017.
 */

public class ImageCircularListAdapterNEW extends RecyclerView.Adapter<ImageCircularListAdapterNEW.MyViewHolder> {

    private ArrayList<MessageModel> imageDataList;
    private Context context;

    private static final String IMAGE_FOLDER = "//SchoolChimesImages";

    String name;
    private ArrayList<String> myList = new ArrayList<>();

    public ImageCircularListAdapterNEW(ArrayList<MessageModel> imageDataList, Context context) {
        this.imageDataList = imageDataList;
        this.context = context;
    }

    @Override
    public ImageCircularListAdapterNEW.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_image, parent, false);

        return new ImageCircularListAdapterNEW.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageCircularListAdapterNEW.MyViewHolder holder, final int position) {
        final MessageModel msgModel = imageDataList.get(position);

        holder.tvTitle.setText(msgModel.getMsgTitle());
        holder.tvDate.setText(msgModel.getMsgDate());
        holder.tvTime.setText(msgModel.getMsgTime());

        holder.tvdescription.setText(msgModel.getMsgdescription());
        if(msgModel.getMsgdescription().equals("")){
            holder.tvdescription.setVisibility(View.GONE);
        }
        else{
            holder.tvdescription.setVisibility(View.VISIBLE);
        }
        if (msgModel.getMsgReadStatus().equals("0"))
            holder.tvStatus.setVisibility(View.VISIBLE);
        else holder.tvStatus.setVisibility(View.GONE);

        Glide.with(context)
                .load(msgModel.getMsgContent())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.btnSaveImage.setEnabled(false);
                        holder.tvSaveAlert.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.btnSaveImage.setEnabled(true);
                        holder.tvSaveAlert.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.img);

        holder.tvSaveAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inTextPopup = new Intent(context, ImageCircularPopUpParent.class);
                inTextPopup.putExtra("IMAGE_ITEM", msgModel);
                context.startActivity(inTextPopup);
            }
        });

        holder.btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFileFromURL.downloadSampleFile((Activity) context, msgModel, IMAGE_FOLDER, msgModel.getMsgID() + ".png", MSG_TYPE_IMAGE,"");

            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }


    public void updateList(ArrayList<MessageModel> temp) {
        imageDataList = temp;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        Button btnSaveImage;
        ProgressBar progressBar;
        TextView tvTitle, tvDate, tvTime, tvStatus, tvSaveAlert,tvdescription;

        public MyViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.cardImage_ivImage);
            btnSaveImage = (Button) view.findViewById(R.id.cardImage_btnSave);
            progressBar = (ProgressBar) view.findViewById(R.id.cardImage_progress);

            tvTitle = (TextView) view.findViewById(R.id.cardImage_tvTitle);
            tvDate = (TextView) view.findViewById(R.id.cardImage_tvDate);
            tvTime = (TextView) view.findViewById(R.id.cardImage_tvTime);
            tvStatus = (TextView) view.findViewById(R.id.cardImage_tvNew);
            tvSaveAlert = (TextView) view.findViewById(R.id.cardImage_tvDownloadAlert);
            tvdescription = (TextView) view.findViewById(R.id.tv_description_image);

            tvSaveAlert.setVisibility(View.GONE);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllData() {
        int size = this.imageDataList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.imageDataList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    private void showAlert(String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_image);

        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

}

