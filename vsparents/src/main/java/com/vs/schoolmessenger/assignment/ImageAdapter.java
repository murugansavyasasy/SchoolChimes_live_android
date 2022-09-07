package com.vs.schoolmessenger.assignment;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ImagePreviewActivity;


import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by devi on 6/5/2019.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    ArrayList<String> Imagelist;
    ArrayList<String> desclist;
    Context context;
    String File_img;
    String File_pdf;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView Image;
        TextView text,lblDescription;

        LinearLayout Layout_img;


        public MyViewHolder(View v) {
            super(v);

            Image = (ImageView) v.findViewById(R.id.list_image);
            Layout_img=(LinearLayout)v.findViewById(R.id.image_layout2);
            lblDescription=(TextView)v.findViewById(R.id.lblDescription);


        }
    }

    public ImageAdapter(Context context, ArrayList<String> Imagelist) {
//        super(context,Imagelist);
        this.Imagelist = Imagelist;
        this.context = context;
    }
    public ImageAdapter(Context context, ArrayList<String> Imagelist,ArrayList<String> desclist) {
//        super(context,Imagelist);
        this.Imagelist = Imagelist;
        this.context = context;
        this.desclist = desclist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.actvity_adpter_imagelist, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final String image = Imagelist.get(position);
        final String description = desclist.get(position);
        if(description.isEmpty()||description.equals("")) {
            holder.lblDescription.setVisibility(View.GONE);
        }
        else{
            holder.lblDescription.setVisibility(View.VISIBLE);
            holder.lblDescription.setText(desclist.get(position));
        }

            Glide.with(context)
                    .load(image)
                    .into(holder.Image);
            holder.Layout_img.setVisibility(View.VISIBLE);

        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePreview=new Intent(context, ImagePreviewActivity.class);
                imagePreview.putExtra("ImageURl",String.valueOf(image));
                context.startActivity(imagePreview);
            }
        });

    }
    @Override
    public int getItemCount() {
        return Imagelist.size();

    }

}
