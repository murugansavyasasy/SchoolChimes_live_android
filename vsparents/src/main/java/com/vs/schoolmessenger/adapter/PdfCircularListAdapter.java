package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.PdfWebViewPopup;
import com.vs.schoolmessenger.assignment.PdfAppRead;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.DownloadFileFromURL;

import java.util.ArrayList;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_PDF;

/**
 * Created by voicesnap on 8/31/2016.
 */
public class PdfCircularListAdapter extends RecyclerView.Adapter<PdfCircularListAdapter.MyViewHolder> {

    private ArrayList<MessageModel> circularList;
    Context context;
    private static final String PDF_FOLDER = "School Voice/PDF";

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_pdf, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(circularList.get(position));

        final MessageModel circular = circularList.get(position);

        holder.tvDate.setText(circular.getMsgDate());
        holder.tvTime.setText(circular.getMsgTime());
        holder.tvTitle.setText(circular.getMsgTitle());

        holder.txtDescription.setText(circular.getMsgdescription());
        if(circular.getMsgdescription().equals("")){
            holder.txtDescription.setVisibility(View.GONE);
        }
        else {
            holder.txtDescription.setVisibility(View.VISIBLE);
        }

        if (circular.getMsgReadStatus().equals("0"))
            holder.tvStatus.setVisibility(View.VISIBLE);
        else holder.tvStatus.setVisibility(View.GONE);

        holder.btnView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inPdfPopup = new Intent(context, PdfAppRead.class);
                inPdfPopup.putExtra("PDF_ITEM", circular);
                context.startActivity(inPdfPopup);

//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(circular.getMsgContent()));
//                context.startActivity(browserIntent);
            }
        });

        holder.btnDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                long unixTime = System.currentTimeMillis() / 1000L;
                String timeStamp = String.valueOf(unixTime);
                DownloadFileFromURL.downloadSampleFile((Activity) context, circular, PDF_FOLDER, timeStamp + "_" + circular.getMsgTitle() + ".pdf", MSG_TYPE_PDF,"");
            }
        });
    }

    @Override
    public int getItemCount() {
        return circularList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvTime, tvTitle, tvStatus,txtDescription;
        public Button btnView, btnDownload;

        public MyViewHolder(View view) {
            super(view);

            tvDate= (TextView) view.findViewById(R.id.cardPDF_tvDate);
            tvTime = (TextView) view.findViewById(R.id.cardPDF_tvTime);
            tvTitle = (TextView) view.findViewById(R.id.cardPDF_tvTitle);
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            tvStatus = (TextView) view.findViewById(R.id.cardPDF_tvNew);

            btnView = (Button) view.findViewById(R.id.cardPDF_btnView);
            btnDownload = (Button) view.findViewById(R.id.cardPDF_btnDownload);
        }

        public void bind(final MessageModel item) {
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public PdfCircularListAdapter(Context context, ArrayList<MessageModel> circularList) {
        this.context = context;
        this.circularList = circularList;
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
