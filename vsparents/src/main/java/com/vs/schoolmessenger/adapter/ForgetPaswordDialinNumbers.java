package com.vs.schoolmessenger.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.SubjectAndMarkList;

import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ForgetPaswordDialinNumbers extends RecyclerView.Adapter<ForgetPaswordDialinNumbers.MyViewHolder> {


    private List<String> number_list;
    Context context;
    Dialog dialog;

    public void clearAllData() {
        int size = this.number_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.number_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView DialNumber;
        public RelativeLayout rytFirstNumber;

        public MyViewHolder(View view) {
            super(view);
            DialNumber = (TextView) view.findViewById(R.id.DialNumber);
            rytFirstNumber = (RelativeLayout) view.findViewById(R.id.rytFirstNumber);

        }
    }

    public ForgetPaswordDialinNumbers(List<String> lib_list, Context context,Dialog dia) {
        this.number_list = lib_list;
        this.context = context;
        this.dialog = dia;
    }

    @Override
    public ForgetPaswordDialinNumbers.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.number_list, parent, false);
        return new ForgetPaswordDialinNumbers.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ForgetPaswordDialinNumbers.MyViewHolder holder, final int position) {

        final String number = number_list.get(position);
        holder.DialNumber.setText(number);
        holder.rytFirstNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                ((Activity)context).startActivity(intent);
                dialog.dismiss();
                ((Activity)context).startActivity(intent);
                ((Activity)context).finishAffinity();

            }
        });

    }
    @Override
    public int getItemCount() {
        return number_list.size();

    }
}

