package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.DatesListListener;
import com.vs.schoolmessenger.model.DatesModel;

import java.util.List;

/**
 * Created by voicesnap on 8/31/2016.
 */
public class DatesListAdapter extends RecyclerView.Adapter<DatesListAdapter.MyViewHolder> {

    private List<DatesModel> dateList;
    Context context;
    private final DatesListListener listener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_absentees_date_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener);

        final DatesModel profile = dateList.get(position);

        holder.tvDate.setText(profile.getDate());
        holder.tvDay.setText(profile.getDay());
        holder.tvCount.setText(profile.getCount());
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvDay, tvCount;

        public MyViewHolder(View view) {
            super(view);

            tvDate = (TextView) view.findViewById(R.id.cardAbsen_tvDate);
            tvDay = (TextView) view.findViewById(R.id.cardAbsen_tvDay);
            tvCount = (TextView) view.findViewById(R.id.cardAbsen_tvCount);
        }

        public void bind(final DatesModel item, final DatesListListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public DatesListAdapter(Context context, List<DatesModel> dateList, DatesListListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllData() {
        int size = this.dateList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.dateList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }
}
