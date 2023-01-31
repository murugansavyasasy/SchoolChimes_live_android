package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.StaffDisplayImages;
import com.vs.schoolmessenger.activity.StaffDisplayPDF;
import com.vs.schoolmessenger.activity.StaffDisplayTextMessages;
import com.vs.schoolmessenger.activity.StaffDisplayVideos;
import com.vs.schoolmessenger.activity.TeacherVoiceCircular;
import com.vs.schoolmessenger.model.TeacherCircularDates;
import com.vs.schoolmessenger.util.TeacherUtil_Common;

import java.util.List;



/**
 * Created by voicesnap on 8/31/2016.
 */
public class TeacherCircularsDateListAdapter extends RecyclerView.Adapter<TeacherCircularsDateListAdapter.MyViewHolder> {

    private List<TeacherCircularDates> datesList;
    Context context;
    int iTotalUnread;


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_cardview_dates_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final TeacherCircularDates date = datesList.get(position);
        holder.tvDate.setText(date.getCircularDate());
        holder.tvDay.setText(date.getCircularDay());

        holder.tvVoiceTotal.setText(context.getResources().getString(R.string.caps_voice)+"(" + date.getVoiceTotCount() + ")");
        holder.tvVoiceUnread.setText(date.getVoiceUnreadCount());
        int iVoiceUnread = Integer.parseInt(date.getVoiceUnreadCount());
        int iVoiceTotal = Integer.parseInt(date.getVoiceTotCount());
        if (iVoiceTotal < 1)
            holder.llVoice.setEnabled(false);
        else {
            holder.llVoice.setEnabled(true);
            holder.llVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherUtil_Common.selectedCircularDate = date.getCircularDate();
                    Intent inVoice = new Intent(context, TeacherVoiceCircular.class);
                    inVoice.putExtra("SEL_DATE", date.getCircularDate());
                    inVoice.putExtra("is_Archive", date.getIs_Archive());
                    context.startActivity(inVoice);
                }
            });
        }


        if (iVoiceUnread < 1)
            holder.tvVoiceUnread.setVisibility(View.GONE);
        else holder.tvVoiceUnread.setVisibility(View.VISIBLE);

        //********


        holder.tvVideoTotal.setText("VIDEO"+"(" + date.getVideoTotCount() + ")");
        holder.tvVideoUnread.setText(date.getVideoUnreadCount());
        int iVideoUnread = Integer.parseInt(date.getVideoUnreadCount());
        int iVideoTotal = Integer.parseInt(date.getVideoTotCount());
        if (iVideoTotal < 1)
            holder.llVIDEO.setEnabled(false);
        else {
            holder.llVIDEO.setEnabled(true);
            holder.llVIDEO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TeacherUtil_Common.selectedCircularDate = date.getCircularDate();
                    Intent inVoice = new Intent(context, StaffDisplayVideos.class);
                    inVoice.putExtra("SEL_DATE", date.getCircularDate());
                    inVoice.putExtra("is_Archive", date.getIs_Archive());
                    context.startActivity(inVoice);

                }
            });
        }

        if (iVideoUnread < 1)
            holder.tvVideoUnread.setVisibility(View.GONE);
        else holder.tvVideoUnread.setVisibility(View.VISIBLE);


        //************

        holder.tvTextTotal.setText(context.getResources().getString(R.string.caps_text)+"(" + date.getTextTotCount() + ")");
        holder.tvTextUnread.setText(date.getTextUnreadCount());
        int iTextUnread = Integer.parseInt(date.getTextUnreadCount());
        int iTextTotal = Integer.parseInt(date.getTextTotCount());
        if (iTextTotal < 1)
            holder.llText.setEnabled(false);
        else {
            holder.llText.setEnabled(true);
            holder.llText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherUtil_Common.selectedCircularDate = date.getCircularDate();
                    Intent inText = new Intent(context, StaffDisplayTextMessages.class);
                    inText.putExtra("SEL_DATE", date.getCircularDate());
                    inText.putExtra("is_Archive", date.getIs_Archive());
                    context.startActivity(inText);
                }
            });
        }
        if (iTextUnread < 1)
            holder.tvTextUnread.setVisibility(View.GONE);
        else holder.tvTextUnread.setVisibility(View.VISIBLE);

        //********

        holder.tvImageTotal.setText(context.getResources().getString(R.string.caps_image)+"(" + date.getImageTotCount() + ")");
        holder.tvImageUnread.setText(date.getImageUnreadCount());
        int iImageUnread = Integer.parseInt(date.getImageUnreadCount());
        int iImageTotal = Integer.parseInt(date.getImageTotCount());
        if (iImageTotal < 1)
            holder.llImage.setEnabled(false);
        else {
            holder.llImage.setEnabled(true);
            holder.llImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherUtil_Common.selectedCircularDate = date.getCircularDate();
                    Intent inImg = new Intent(context, StaffDisplayImages.class);
                    inImg.putExtra("SEL_DATE", date.getCircularDate());
                    inImg.putExtra("is_Archive", date.getIs_Archive());
                    context.startActivity(inImg);
                }
            });
        }
        if (iImageUnread < 1)
            holder.tvImageUnread.setVisibility(View.GONE);
        else holder.tvImageUnread.setVisibility(View.VISIBLE);

        //********

        holder.tvPdfTotal.setText(context.getResources().getString(R.string.caps_pdf)+"(" + date.getPdfTotCount() + ")");
        holder.tvPdfUnread.setText(date.getPdfUnreadCount());
        int iPdfUnread = Integer.parseInt(date.getPdfUnreadCount());
        int iPdfTotal = Integer.parseInt(date.getPdfTotCount());
        if (iPdfTotal < 1)
            holder.llPDF.setEnabled(false);
        else {
            holder.llPDF.setEnabled(true);
            holder.llPDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherUtil_Common.selectedCircularDate = date.getCircularDate();
                    Intent inPdf = new Intent(context, StaffDisplayPDF.class);
                    inPdf.putExtra("SEL_DATE", date.getCircularDate());
                    inPdf.putExtra("is_Archive", date.getIs_Archive());

                    context.startActivity(inPdf);
                }
            });
        }
        if (iPdfUnread < 1)
            holder.tvPdfUnread.setVisibility(View.GONE);
        else holder.tvPdfUnread.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }


    public void updateList(List<TeacherCircularDates> temp) {
        datesList = temp;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvDay, tvTotalUnread;
        LinearLayout llVoice, llText, llImage, llPDF,llVIDEO;
        TextView tvVoiceTotal, tvVoiceUnread, tvTextTotal, tvTextUnread,
                tvImageTotal, tvImageUnread, tvPdfTotal, tvPdfUnread,tvVideoUnread,tvVideoTotal;

        public MyViewHolder(View view) {
            super(view);

            llVoice = (LinearLayout) view.findViewById(R.id.cardDates_llVoiceMsg);
            llText = (LinearLayout) view.findViewById(R.id.cardDates_llTextMsg);
            llImage = (LinearLayout) view.findViewById(R.id.cardDates_llImageMsg);
            llPDF = (LinearLayout) view.findViewById(R.id.cardDates_llPDFMsg);
            llVIDEO = (LinearLayout) view.findViewById(R.id.cardDates_llVideoMsg);

            tvTotalUnread = (TextView) view.findViewById(R.id.cardDates_tvTotUnreadMsgCount);

            tvDate = (TextView) view.findViewById(R.id.cardDates_tvDate);
            tvDay = (TextView) view.findViewById(R.id.cardDates_tvDay);

            tvVoiceTotal = (TextView) view.findViewById(R.id.cardDates_tvTotVoiceMsgCount);
            tvVoiceUnread = (TextView) view.findViewById(R.id.cardDates_tvUnreadVoiceMsgCount);

            tvTextTotal = (TextView) view.findViewById(R.id.cardDates_tvTotTextMsgCount);
            tvTextUnread = (TextView) view.findViewById(R.id.cardDates_tvUnreadTextMsgCount);

            tvImageTotal = (TextView) view.findViewById(R.id.cardDates_tvTotImageMsgCount);
            tvImageUnread = (TextView) view.findViewById(R.id.cardDates_tvUnreadImageMsgCount);

            tvPdfTotal = (TextView) view.findViewById(R.id.cardDates_tvTotPDFMsgCount);
            tvPdfUnread = (TextView) view.findViewById(R.id.cardDates_tvUnreadPDFMsgCount);

            tvVideoTotal = (TextView) view.findViewById(R.id.cardDates_tvTotVIDEOMsgCount);
            tvVideoUnread = (TextView) view.findViewById(R.id.cardDates_tvUnreadVIDEOMsgCount);
        }
    }

    public TeacherCircularsDateListAdapter(Context context, List<TeacherCircularDates> datesList) {
        this.context = context;
        this.datesList = datesList;
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllData() {
        int size = this.datesList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.datesList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

}
