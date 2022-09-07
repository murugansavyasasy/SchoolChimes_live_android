package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.PdfWebViewPopup;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.util.TeacherDownloadFileFromURL;

import java.util.ArrayList;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_PDF;
public class StaffDisplayPDfAdapter extends RecyclerView.Adapter<StaffDisplayPDfAdapter.MyViewHolder> {

    private ArrayList<TeacherMessageModel> circularList;
    Context context;
    private static final String PDF_FOLDER = "School Voice/PDF";
    Boolean is_Archive;

    @Override
    public StaffDisplayPDfAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_pdf, parent, false);

        return new StaffDisplayPDfAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StaffDisplayPDfAdapter.MyViewHolder holder, int position) {
        holder.bind(circularList.get(position));

        final TeacherMessageModel circular = circularList.get(position);

        holder.tvDate.setText(circular.getMsgDate());
        holder.tvTime.setText(circular.getMsgTime());
        holder.tvTitle.setText(circular.getMsgTitle());

        if (circular.getMsgReadStatus().equals("0"))
            holder.tvStatus.setVisibility(View.VISIBLE);
        else holder.tvStatus.setVisibility(View.GONE);

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inPdfPopup = new Intent(context, PdfWebViewPopup.class);
                inPdfPopup.putExtra("PDF_ITEM", circular);
                inPdfPopup.putExtra("is_Archive", is_Archive);
                context.startActivity(inPdfPopup);
            }
        });

        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long unixTime = System.currentTimeMillis() / 1000L;
                String timeStamp = String.valueOf(unixTime);
                String filename=circular.getMsgID();
                TeacherDownloadFileFromURL.downloadSampleFile((Activity) context, circular, PDF_FOLDER, filename + "_" + circular.getMsgTitle() + ".pdf", MSG_TYPE_PDF,false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return circularList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvTime, tvTitle, tvStatus;
        public Button btnView, btnDownload;

        public MyViewHolder(View view) {
            super(view);

            tvDate= (TextView) view.findViewById(R.id.cardPDF_tvDate);
            tvTime = (TextView) view.findViewById(R.id.cardPDF_tvTime);
            tvTitle = (TextView) view.findViewById(R.id.cardPDF_tvTitle);
            tvStatus = (TextView) view.findViewById(R.id.cardPDF_tvNew);

            btnView = (Button) view.findViewById(R.id.cardPDF_btnView);
            btnDownload = (Button) view.findViewById(R.id.cardPDF_btnDownload);
        }

        public void bind(final TeacherMessageModel item) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public StaffDisplayPDfAdapter(Context context, ArrayList<TeacherMessageModel> circularList,Boolean archive) {
        this.context = context;
        this.circularList = circularList;
        this.is_Archive = archive;
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllData() {
        int size = this.circularList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.circularList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }
}

