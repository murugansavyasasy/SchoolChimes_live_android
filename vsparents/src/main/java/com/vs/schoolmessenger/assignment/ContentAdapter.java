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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.DownloadImageAssgn;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_IMAGE;

/**
 * Created by devi on 5/3/2017.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

    ArrayList<String> imagelist = new ArrayList<>();
    ArrayList<String> descriptionlist = new ArrayList<>();
    private Context context;
    String type,ans;

    private static final String IMAGE_FOLDER = "School Voice/Images";

    String name;
    private ArrayList<String> myList = new ArrayList<>();

    public ContentAdapter(ArrayList<String> imagelist,Context context) {
        this.imagelist = imagelist;
        this.context = context;
    }

    @Override
    public ContentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_msg, parent, false);

        return new ContentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ContentAdapter.MyViewHolder holder, final int position) {
//        final MessageModel msgModel = imagelist.get(position);
        holder.tvStatus.setText(String.valueOf(position+1)+"."+imagelist.get(position));
    }


    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvStatus;

        public MyViewHolder(View view) {
            super(view);

            tvStatus = (TextView) view.findViewById(R.id.lblContent);

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

