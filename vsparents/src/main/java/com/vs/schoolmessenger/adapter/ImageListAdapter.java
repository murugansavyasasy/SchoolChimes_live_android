package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ImagePreviewActivity;

import java.io.File;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MyViewHolder> {

    private List<String> lib_list;
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
        public ImageView imgImages;

        public MyViewHolder(View view) {
            super(view);
            imgImages = (ImageView) view.findViewById(R.id.imgImages);

        }
    }

    public ImageListAdapter(List<String> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public ImageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_list_item, parent, false);
        return new ImageListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageListAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final String imageUrl = lib_list.get(position);

        File file = new File(imageUrl);
        final Uri imageUri = Uri.fromFile(file);
        Glide.with(context)
                .load(imageUri)
                .into(holder.imgImages);

        holder.imgImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePreview=new Intent(context, ImagePreviewActivity.class);
                imagePreview.putExtra("ImageURl",String.valueOf(imageUri));
                context.startActivity(imagePreview);
            }
        });


    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}


