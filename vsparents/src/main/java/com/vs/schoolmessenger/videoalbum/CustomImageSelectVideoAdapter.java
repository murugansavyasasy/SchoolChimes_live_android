package com.vs.schoolmessenger.videoalbum;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;

import java.util.ArrayList;


public class CustomImageSelectVideoAdapter extends CustomGenericVideoAdapter<Video> {

    public CustomImageSelectVideoAdapter(Context context, ArrayList<Video> videos) {
        super(context, videos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_video_item_select, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_image_select);
            viewHolder.view = convertView.findViewById(R.id.view_alpha);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;

        viewHolder.view.getLayoutParams().width = size;
        viewHolder.view.getLayoutParams().height = size;

        if (arrayList.get(position).isSelected ) {

            Log.d("seladpt", String.valueOf(arrayList.get(position).isSelected));
            viewHolder.view.setAlpha(0.5f);
            ((FrameLayout) convertView).setForeground(context.getResources().getDrawable(R.drawable.ic_action_done));


        }

        else {
            viewHolder.view.setAlpha(0.0f);
            ((FrameLayout) convertView).setForeground(null);
        }
        Glide.with(context)
                .load(arrayList.get(position).path)

                .placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);
        Log.d("arraylist", String.valueOf(arrayList.get(position).path));


//            Intent i = new Intent(context, GalleryPreview.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.putExtra("selected images", arrayList.get(position).path);
//            convertView.getContext().startActivity(i);


        return convertView;
    }


    private static class ViewHolder {
        public ImageView imageView;
        public View view;
    }
}

