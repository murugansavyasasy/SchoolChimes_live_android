package com.vs.schoolmessenger.assignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.DownloadFileFromURL;
import com.vs.schoolmessenger.util.DownloadImageAssgn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_IMAGE;

/**
 * Created by devi on 5/3/2017.
 */

public class ImageCircularassgn extends RecyclerView.Adapter<ImageCircularassgn.MyViewHolder> {

    ArrayList<String> imagelist = new ArrayList<>();
    ArrayList<String> descriptionlist = new ArrayList<>();
    private Context context;
    String type,ans;



    private static final String IMAGE_FOLDER = "//SchoolAssignmentsImages";

    String name;
    private ArrayList<String> myList = new ArrayList<>();

    public ImageCircularassgn(ArrayList<String> imagelist,ArrayList<String> descriptionlist,String type,String ans, Context context) {
        this.imagelist = imagelist;
        this.descriptionlist = descriptionlist;
        this.type = type;
        this.ans = ans;
        this.context = context;
    }

    @Override
    public ImageCircularassgn.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_image, parent, false);

        return new ImageCircularassgn.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageCircularassgn.MyViewHolder holder, final int position) {
//        final MessageModel msgModel = imagelist.get(position);

        holder.rlheader.setVisibility(View.GONE);
        holder.tvTitle.setVisibility(View.GONE);
        holder.tvDate.setVisibility(View.GONE);
        holder.tvTime.setVisibility(View.GONE);
        holder.tvdescription.setText(descriptionlist.get(position));
        if(descriptionlist.get(position).equals("")||descriptionlist.get(position).isEmpty()){
            holder.tvdescription.setVisibility(View.GONE);
        }
        else{
            holder.tvdescription.setVisibility(View.VISIBLE);
        }
//        if(type.equals("parent")&& ans.equals("1")){
            holder.btnSaveImage.setVisibility(View.VISIBLE);
//        }
//        else{
//            holder.btnSaveImage.setVisibility(View.GONE);
//        }
//        if (msgModel.getMsgReadStatus().equals("0"))
//            holder.tvStatus.setVisibility(View.VISIBLE);
         holder.tvStatus.setVisibility(View.GONE);


        Glide.with(context)
                .load(imagelist.get(position))
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


//        Glide.with(context).load(imagelist.get(position))
//                .asBitmap()
//                .thumbnail(0.5f)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
//                        // for demonstration purposes, let's just set it to an ImageView
//                        holder.img.setImageBitmap(bitmap);
//
//                        holder.progressBar.setVisibility(View.GONE);
//                        holder.btnSaveImage.setEnabled(true);
//                        holder.tvSaveAlert.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                        super.onLoadFailed(e, errorDrawable);
//
//                        holder.btnSaveImage.setEnabled(false);
//                        holder.tvSaveAlert.setVisibility(View.GONE);
//                    }
//                });

        holder.tvSaveAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inTextPopup = new Intent(context, ImageCircularPop.class);
                inTextPopup.putExtra("IMAGE_ITEM", imagelist.get(position));
                inTextPopup.putExtra("DESC", descriptionlist.get(position));
                context.startActivity(inTextPopup);
            }
        });

        holder.btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //String Id = imagelist.get(position);
                    //String fileName = Id.substring(Id.lastIndexOf("//") + 1, Id.length());

                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                DownloadImageAssgn.downloadSampleFile((Activity) context, imagelist.get(position), IMAGE_FOLDER, fileName + ".png", MSG_TYPE_IMAGE);


            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlheader;
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
            rlheader =  view.findViewById(R.id.relativeLayoutHeader);

            tvSaveAlert.setVisibility(View.GONE);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllData() {
        int size = this.imagelist.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.imagelist.remove(0);
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

        alertDialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

}

