package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.UpdatesListener;

import java.util.ArrayList;
import java.util.List;

/**
public class CallScheduleAdapter extends RecyclerView.Adapter<CallScheduleAdapter.MyViewHolder> {

    Context context;
    List<String> isSelectedDateList = new ArrayList<>();

    @NonNull
    @Override
    public CallScheduleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedulecalldate_layoaut, parent, false);
        return new CallScheduleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CallScheduleAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.lblSelectedDate.setText(isSelectedDateList.get(position));

        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectedDateList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    public CallScheduleAdapter(Context context, List<String> isSelectedDateList) {
        this.context = context;
        this.isSelectedDateList = isSelectedDateList;
    }

    @Override
    public int getItemCount() {
        return isSelectedDateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblSelectedDate;
        public ImageView imgClose;

        public MyViewHolder(View view) {
            super(view);

            lblSelectedDate = view.findViewById(R.id.lblSelectedDate);
            imgClose = view.findViewById(R.id.imgClose);
        }
    }
}
*/
public class CallScheduleAdapter extends ArrayAdapter {

    private final UpdatesListener listener;
    List<String> isSelectedDate = new ArrayList<>();
    Context context;

    public CallScheduleAdapter(Context context, int id, ArrayList isSelectedDate,UpdatesListener listener) {
        super(context, id, isSelectedDate);
        this.context = context;
        this.isSelectedDate = isSelectedDate;
        this.listener=listener;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.schedulecalldate_layoaut, parent, false);
        }
        TextView textView = (TextView) v.findViewById(R.id.lblSelectedDate);
        ImageView isRemoveDate = (ImageView) v.findViewById(R.id.imgClose);

        textView.setText(isSelectedDate.get(position));

        isRemoveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMsgItemClick(textView.getText().toString());
                isSelectedDate.remove(position);
                notifyDataSetChanged();
            }
        });
        return v;
    }
}
