package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.AppController;
import com.vs.schoolmessenger.interfaces.OnProfileItemClickListener;
import com.vs.schoolmessenger.model.Image;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.util.TeacherUtil_Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by voicesnap on 8/31/2016.
 */
public class ChildProfileAdapter extends RecyclerView.Adapter<ChildProfileAdapter.MyViewHolder> {

    private List<Profiles> childList;
    Context context;
    private final OnProfileItemClickListener listener;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_child_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(childList.get(position), listener);

        final Profiles profile = childList.get(position);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        holder.thumbNailSchoolImg.setImageUrl(profile.getSchoolThumbnailImgUrl(), imageLoader);
        holder.tvName.setText(" "+profile.getChildName());
        holder.tvID.setText(" "+profile.getRollNo());
        holder.tvStandard.setText(" "+profile.getStandard());
        holder.tvSection.setText(profile.getSection());
        holder.tvMsgCount.setText(profile.getMsgCount());
        holder.tvSchoolName.setText(profile.getSchoolName());
        holder.tvSchoolAddress.setText(profile.getSchoolAddress());

        holder.lblRegionalSchoolName.setText(profile.getSchoolNameRegional());
        if(!profile.getSchoolNameRegional().equals("") && profile.getSchoolNameRegional() !=null && !profile.getSchoolNameRegional().equals("null")){
           holder.lblRegionalSchoolName.setVisibility(View.VISIBLE);
        }
        else {
            holder.lblRegionalSchoolName.setVisibility(View.GONE);
        }

        if(profile.getIsNotAllow().equals("1")){
            holder.rytDisplayMessage.setVisibility(View.VISIBLE);
            holder.imageView6.setVisibility(View.GONE);
            holder.lblDisplayMessage.setText(profile.getDisplayMessage());
        }
        else {
            holder.rytDisplayMessage.setVisibility(View.GONE);
            holder.imageView6.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvID, tvStandard, tvSection, tvSchoolName, tvSchoolAddress, tvMsgCount,lblDisplayMessage,lblRegionalSchoolName;
        NetworkImageView thumbNailSchoolImg;
        ImageView imageView6;

        RelativeLayout rytDisplayMessage;


        public MyViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.cardProfile_tvChildName);
            tvID = (TextView) view.findViewById(R.id.cardProfile_tvChildID);
            tvStandard = (TextView) view.findViewById(R.id.cardProfile_tvStandard);
            tvSection = (TextView) view.findViewById(R.id.cardProfile_tvSection);
            tvSchoolName = (TextView) view.findViewById(R.id.cardProfile_tvSchoolName);
            lblRegionalSchoolName = (TextView) view.findViewById(R.id.lblRegionalSchoolName);
            tvSchoolAddress = (TextView) view.findViewById(R.id.cardProfile_tvSchoolAddress);
            tvMsgCount = (TextView) view.findViewById(R.id.cardProfile_tvMsgCount);
            lblDisplayMessage = (TextView) view.findViewById(R.id.lblDisplayMessage);
            rytDisplayMessage = (RelativeLayout) view.findViewById(R.id.rytDisplayMessage);
            imageView6 = (ImageView) view.findViewById(R.id.imageView6);

            thumbNailSchoolImg = (NetworkImageView) view
                    .findViewById(R.id.cardProfile_nivThumbnailSchoolImg);
        }
        public void bind(final Profiles item, final OnProfileItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherUtil_Common.scroll_to_position = 0;
                    listener.onItemClick(item);

                }
            });
        }
    }

    public ChildProfileAdapter(Context context, ArrayList<Profiles> childList, OnProfileItemClickListener listener) {
        this.context = context;
        this.childList = childList;
        this.listener = listener;
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
