package com.vs.schoolmessenger.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.util.ArrayList;

public class FeePendingAlertAdapter extends RecyclerView.Adapter<FeePendingAlertAdapter.MyViewHolder> {

    ArrayList<TeacherSchoolsModel> imagelist = new ArrayList<>();
    ArrayList<String> descriptionlist = new ArrayList<>();
    private Context context;

    private static final String IMAGE_FOLDER = "School Voice/Images";

    String name;
    private ArrayList<TeacherSchoolsModel> myList = new ArrayList<>();

    public FeePendingAlertAdapter(ArrayList<TeacherSchoolsModel> imagelist,Context context) {
        this.imagelist = imagelist;
        this.context = context;
    }

    @Override
    public FeePendingAlertAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_payment_alert, parent, false);

        return new FeePendingAlertAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FeePendingAlertAdapter.MyViewHolder holder, final int position) {

        if(!imagelist.get(position).getIsPaymentPending().equals("0"))
        {
            holder.lnrParent.setVisibility(View.VISIBLE);
            holder.lblSchoolName.setText(imagelist.get(position).getStrSchoolName());
            holder.lblAmount.setText(" - "+imagelist.get(position).getIsPaymentPending());
        }
        else {
            holder.lnrParent.setVisibility(View.GONE);

        }

    }


    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lblSchoolName;
        TextView lblAmount;
        LinearLayout lnrParent;

        public MyViewHolder(View view) {
            super(view);

            lblSchoolName = (TextView) view.findViewById(R.id.lblSchoolName);
            lblAmount = (TextView) view.findViewById(R.id.lblAmount);
            lnrParent = (LinearLayout) view.findViewById(R.id.lnrParent);

        }
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllData() {
        int size = this.imagelist.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.imagelist.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    private void showAlert(String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_image);

        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

}
