package com.vs.schoolmessenger.assignment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.MyViewHolder> {

    ArrayList<String> imagelist = new ArrayList<>();
    ArrayList<String> descriptionlist = new ArrayList<>();
    Context context;
    RefreshInterface listener;
    String assignmentid,userid;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //holder.lblPDF.setText(imagelist.get(position));
        holder.lblPDF.setText("PDF_ "+String.valueOf(position+1));

        Log.d("pdf",imagelist.get(position));
          if(descriptionlist.get(position).equals("")||descriptionlist.isEmpty()){
              holder.lblPDFcontent.setVisibility(View.GONE);
          }
          else{
             holder.lblPDFcontent.setVisibility(View.VISIBLE);
             holder.lblPDFcontent.setText(descriptionlist.get(position));
          }
        holder.lnrPDFtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imagelist.get(position)));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblPDF,lblPDFcontent;
        public RelativeLayout rytTextMessage;
        public LinearLayout lnrPDFtext;
        Button btnView;

        public MyViewHolder(View view) {
            super(view);

            lblPDF = (TextView) view.findViewById(R.id.lblPDF);
            lblPDFcontent = (TextView) view.findViewById(R.id.lblPDFcontent);
            rytTextMessage = (RelativeLayout) view.findViewById(R.id.rytTextMessage);
            lnrPDFtext = (LinearLayout) view.findViewById(R.id.lnrPDFtext);

        }
    }

    public PdfListAdapter(Context context, ArrayList<String> imagelist ,ArrayList<String> descriptionlist ,String assignmentid,String userid) {
        this.context = context;
        this.imagelist = imagelist;
        this.descriptionlist = descriptionlist;
        this.assignmentid = assignmentid;
        this.userid = userid;
    }
}
