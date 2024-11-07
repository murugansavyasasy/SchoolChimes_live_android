package com.vs.schoolmessenger.adapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.LocationClick;
import com.vs.schoolmessenger.model.StaffBiometricLocationRes;

import java.util.List;

public class LocationsHistoryAdapter extends RecyclerView.Adapter<LocationsHistoryAdapter.MyViewHolder> {
    private List<StaffBiometricLocationRes.BiometricLoationData> lib_list;
    Context context;

    private final LocationClick listener;

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
        public TextView lblPlaceName, lblLatLong, lblDistance;
        public ImageView imgDelete,imgEdit;
        public MyViewHolder(View view) {
            super(view);

            lblPlaceName = (TextView) view.findViewById(R.id.lblPlaceName);
            lblLatLong = (TextView) view.findViewById(R.id.lblLatLong);
            lblDistance = (TextView) view.findViewById(R.id.lblDistance);
            imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
            imgEdit = (ImageView) view.findViewById(R.id.imgEdit);

        }
    }
    public LocationsHistoryAdapter(List<StaffBiometricLocationRes.BiometricLoationData> lib_list, Context context, LocationClick listener) {
        this.lib_list = lib_list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.locations_list_items, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final StaffBiometricLocationRes.BiometricLoationData holiday = lib_list.get(position);
        holder.lblPlaceName.setTypeface(null, Typeface.BOLD);
        holder.lblPlaceName.setText( holiday.getLocation());
        holder.lblLatLong.setText( holiday.getLatitude() +" - "+holiday.getLongitude());
        holder.lblDistance.setText( "Distance - "+holiday.getDistance()+" Meters");

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holiday,false);
            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holiday,true);
            }
        });
    }


    @Override
    public int getItemCount() {
        return lib_list.size();
    }
}
