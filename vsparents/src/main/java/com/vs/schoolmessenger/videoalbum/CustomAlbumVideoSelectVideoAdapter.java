package com.vs.schoolmessenger.videoalbum;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;

import java.util.ArrayList;

public class CustomAlbumVideoSelectVideoAdapter extends CustomGenericVideoAdapter<AlbumVideo> {

    public CustomAlbumVideoSelectVideoAdapter(Context context, ArrayList<AlbumVideo> albumVideos) {
        super(context, albumVideos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_video_album_select, null);

            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_album_image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_album_name);
            viewHolder.textView1 = (TextView) convertView.findViewById(R.id.gallery_count);



            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;

        viewHolder.textView.setText(arrayList.get(position).name);
        viewHolder.textView1.setText(arrayList.get(position).count);

        Glide.with(context)
                .load(arrayList.get(position).cover)
                .placeholder(R.drawable.image_placeholder).centerCrop().into(viewHolder.imageView);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textView1;
    }
}
