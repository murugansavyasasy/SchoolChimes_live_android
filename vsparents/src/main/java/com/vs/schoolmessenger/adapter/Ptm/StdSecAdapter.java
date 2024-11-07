package com.vs.schoolmessenger.adapter.Ptm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.StdSecDetailsModel;

import java.util.List;

public class StdSecAdapter extends BaseAdapter {

    private final List<StdSecDetailsModel> stdSecDetailsModelList;
    private final Context context;

    public StdSecAdapter(Context context, List<StdSecDetailsModel> stdSecDetailsModelList) {
        this.context = context;
        this.stdSecDetailsModelList = stdSecDetailsModelList;
    }

    @Override
    public int getCount() {
        return stdSecDetailsModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return stdSecDetailsModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.std_sec_item, parent, false);
            holder = new ViewHolder();
            holder.classNameTextView = convertView.findViewById(R.id.className);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind standard section details
        StdSecDetailsModel stdSecDetailsModel = stdSecDetailsModelList.get(position);
        holder.classNameTextView.setText(stdSecDetailsModel.getClass_name() + " - " + stdSecDetailsModel.getSection_name());

        return convertView;
    }

    static class ViewHolder {
        TextView classNameTextView;
    }
}

