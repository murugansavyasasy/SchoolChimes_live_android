package com.vs.schoolmessenger.adapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.HomeworkListActivity;
import com.vs.schoolmessenger.interfaces.OnItemHomeworkClick;
import com.vs.schoolmessenger.model.HomeWorkData;
import com.vs.schoolmessenger.util.Constants;

import java.util.ArrayList;

public class HomeWorkGridAdapter extends BaseAdapter
{
    private ArrayList<HomeWorkData> dataList;
    private Context context;
    private RelativeLayout parent;

    private final OnItemHomeworkClick listener;


    public HomeWorkGridAdapter(Context context, ArrayList<HomeWorkData> listCountry, RelativeLayout rytParent,OnItemHomeworkClick listener) {
        super();
        this.dataList = listCountry;
        this.context = context;
        this.parent = rytParent;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public HomeWorkData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder
    {
        public TextView txtViewTitle,lblHomeworkCount;
        public CardView cardParent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;

        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_grid_list_item, parent,false);
            view.txtViewTitle = (TextView) convertView.findViewById(R.id.lblDate);
            view.lblHomeworkCount = (TextView) convertView.findViewById(R.id.lblHomeworkCount);
            view.cardParent = (CardView) convertView.findViewById(R.id.cardParent);

            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }

        view.txtViewTitle.setText(dataList.get(position).getDate());
        view.lblHomeworkCount.setText(String.valueOf(dataList.get(position).getHw().size()));
        Typeface roboto_bold=Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf");
        Typeface roboto_regular=Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
        view.txtViewTitle.setTypeface(roboto_bold);
        view.lblHomeworkCount.setTypeface(roboto_regular);

        view.cardParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMsgItemClick(dataList.get(position));
                Constants.homeWorkData = dataList.get(position);
                Intent homeworkList = new Intent(context, HomeworkListActivity.class);
                context.startActivity(homeworkList);

                //openListPopup(dataList.get(position));
            }
        });

        return convertView;
    }

    public void updateList(ArrayList<HomeWorkData> temp) {
        dataList = temp;
        notifyDataSetChanged();
    }

    private void openListPopup(HomeWorkData homeWorkData) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.home_work_popup);
        RecyclerView rvHomeWorks = bottomSheetDialog.findViewById(R.id.rvHomeWorks);
        ImageView imgClose = bottomSheetDialog.findViewById(R.id.imgClose);
        TextView lblDate = bottomSheetDialog.findViewById(R.id.lblDate);

        lblDate.setText(homeWorkData.getDate());
        Typeface roboto_bold=Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf");
        lblDate.setTypeface(roboto_bold);


        HomeWorkDateWiseAdapter mAdapter = new HomeWorkDateWiseAdapter(homeWorkData.getHw(), context,parent);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvHomeWorks.setLayoutManager(mLayoutManager);
        rvHomeWorks.setItemAnimator(new DefaultItemAnimator());
        rvHomeWorks.setAdapter(mAdapter);
        rvHomeWorks.getRecycledViewPool().setMaxRecycledViews(0, 80);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HomeWorkDateWiseAdapter.mediaPlayer != null) {
                    if (HomeWorkDateWiseAdapter.mediaPlayer.isPlaying()) {
                        HomeWorkDateWiseAdapter.mediaPlayer.stop();
                    }
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();

    }
}