package com.vs.schoolmessenger.adapter.Ptm;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.GetSectionData;
import com.vs.schoolmessenger.model.SectionAndClass;

import java.util.ArrayList;
import java.util.List;

public class SectionListAdapter extends BaseAdapter {

    private final Context context;
    private final List<GetSectionData> dateList;
    public ArrayList<SectionAndClass> selectedIds;
    private final Drawable selectedBackground;
    private final Drawable defaultBackground;

    public SectionListAdapter(List<GetSectionData> dateList, Context context) {
        this.context = context;
        this.dateList = dateList;
        this.selectedIds = new ArrayList<>();
        this.selectedBackground = context.getDrawable(R.drawable.section_selected);
        this.defaultBackground = context.getDrawable(R.drawable.unselected_sectionptm);
    }

    @Override
    public int getCount() {
        return dateList.size();
    }

    @Override
    public Object getItem(int position) {
        return dateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sectionitem, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GetSectionData data = dateList.get(position);
        holder.lblSectionName.setText(data.getClass_name() + " - " + data.getSection_name());

        holder.lblSectionName.setOnClickListener(v -> {
            SectionAndClass item = new SectionAndClass(data.getClass_id(), data.getSection_id());
            boolean userExists = false;
            int remove_position = 0;
            for (SectionAndClass user : selectedIds) {
                if (user.getSection_id() == item.getSection_id()) {
                    remove_position = selectedIds.indexOf(user);
                    userExists = true;
                    break;
                }
            }

            if (userExists) {
                selectedIds.remove(remove_position);
                holder.lblSectionName.setBackground(defaultBackground);
                holder.lblSectionName.setTextColor(ContextCompat.getColor(holder.lblSectionName.getContext(), R.color.clr_black));
            } else {
                selectedIds.add(item);
                holder.lblSectionName.setBackground(selectedBackground);
                holder.lblSectionName.setTextColor(ContextCompat.getColor(holder.lblSectionName.getContext(), R.color.clr_white));
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView lblSectionName;

        ViewHolder(View view) {
            lblSectionName = view.findViewById(R.id.lblSectionName);
        }
    }

    public ArrayList<SectionAndClass> getSelectedIds() {
        Log.d("selectedIds", String.valueOf(selectedIds));
        return selectedIds;
    }


}

