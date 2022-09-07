package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.AppController;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.DownloadFileFromURL;

import java.util.ArrayList;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_IMAGE;

/**
 * Created by devi on 5/3/2017.
 */

public class ImageCircularListAdapter extends RecyclerView.Adapter<ImageCircularListAdapter.MyViewHolder> {

    private ArrayList<MessageModel> imageDataList;
    private Context context;

    private static final String IMAGE_FOLDER = "School Voice/Images";

    public ImageCircularListAdapter(ArrayList<MessageModel> imageDataList, Context context) {
        this.imageDataList = imageDataList;
        this.context = context;
    }

    @Override
    public ImageCircularListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_image, parent, false);

        return new ImageCircularListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageCircularListAdapter.MyViewHolder holder, final int position) {
        final MessageModel msgModel = imageDataList.get(position);

        holder.tvDate.setText(msgModel.getMsgDate());
        holder.tvTime.setText(msgModel.getMsgTime());

        if (msgModel.getMsgReadStatus().equals("0"))
            holder.tvStatus.setVisibility(View.VISIBLE);
        else holder.tvStatus.setVisibility(View.GONE);




        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (msgModel.getMsgContent() != "") {
            holder.img.setVisibility(View.VISIBLE);

        } else {
            holder.btnSaveImage.setEnabled(false);
            holder.tvSaveAlert.setVisibility(View.GONE);
        }

        holder.btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadFileFromURL.downloadSampleFile((Activity) context, msgModel, IMAGE_FOLDER, msgModel.getMsgID() + ".png", MSG_TYPE_IMAGE,"");


            }
        });
    }


    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        Button btnSaveImage;
        ProgressBar progressBar;
        TextView tvDate, tvTime, tvStatus, tvSaveAlert;

        public MyViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.cardImage_ivImage);
            btnSaveImage = (Button) view.findViewById(R.id.cardImage_btnSave);
            progressBar = (ProgressBar) view.findViewById(R.id.cardImage_progress);

            tvDate = (TextView) view.findViewById(R.id.cardImage_tvDate);
            tvTime = (TextView) view.findViewById(R.id.cardImage_tvTime);
            tvStatus = (TextView) view.findViewById(R.id.cardImage_tvNew);
            tvSaveAlert = (TextView) view.findViewById(R.id.cardImage_tvDownloadAlert);

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

