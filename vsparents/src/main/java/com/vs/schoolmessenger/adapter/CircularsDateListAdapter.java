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

import com.android.volley.toolbox.ImageLoader;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TextCircular;
import com.vs.schoolmessenger.activity.VoiceCircular;
import com.vs.schoolmessenger.app.AppController;
import com.vs.schoolmessenger.model.CircularDates;
import com.vs.schoolmessenger.util.Util_Common;

import java.util.List;

import static com.vs.schoolmessenger.util.Util_Common.MENU_HW;

/**
 * Created by voicesnap on 8/31/2016.
 */
public class CircularsDateListAdapter extends RecyclerView.Adapter<CircularsDateListAdapter.MyViewHolder> {

    private List<CircularDates> datesList;
    Context context;
    int iTotalUnread;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_dates_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final CircularDates date = datesList.get(position);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        holder.tvDate.setText(date.getCircularDate());
        holder.tvDay.setText(date.getCircularDay());

        holder.tvVoiceTotal.setText("VOICE (" + date.getVoiceTotCount() + ")");
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
                    Util_Common.selectedCircularDate = date.getCircularDate();
                    Intent inVoice = new Intent(context, VoiceCircular.class);
                    inVoice.putExtra("SEL_DATE", date.getCircularDate());
                    inVoice.putExtra("REQUEST_CODE", MENU_HW);
                    inVoice.putExtra("is_Archive", date.getIs_Archive());
                    context.startActivity(inVoice);
                }
            });
        }
        if (iVoiceUnread < 1)
            holder.tvVoiceUnread.setVisibility(View.GONE);
        else holder.tvVoiceUnread.setVisibility(View.VISIBLE);

        holder.tvTextTotal.setText("TEXT (" + date.getTextTotCount() + ")");
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
                    Util_Common.selectedCircularDate = date.getCircularDate();
                    Intent inText = new Intent(context, TextCircular.class);
                    inText.putExtra("SEL_DATE", date.getCircularDate());
                    inText.putExtra("REQUEST_CODE", MENU_HW);
                    inText.putExtra("is_Archive", date.getIs_Archive());
                    context.startActivity(inText);
                }
            });
        }
        if (iTextUnread < 1)
            holder.tvTextUnread.setVisibility(View.GONE);
        else holder.tvTextUnread.setVisibility(View.VISIBLE);


        iTotalUnread = Integer.parseInt(date.getVoiceUnreadCount()) + Integer.parseInt(date.getTextUnreadCount());
        holder.tvTotalUnread.setText(String.valueOf(iTotalUnread));
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    public void updateList(List<CircularDates> temp) {
        datesList = temp;
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvDay, tvTotalUnread;
        LinearLayout llVoice, llText, llImage, llPDF;
        TextView tvVoiceTotal, tvVoiceUnread, tvTextTotal, tvTextUnread,
                tvImageTotal, tvImageUnread, tvPdfTotal, tvPdfUnread;

        public MyViewHolder(View view) {
            super(view);

            llVoice = (LinearLayout) view.findViewById(R.id.cardDates_llVoiceMsg);
            llText = (LinearLayout) view.findViewById(R.id.cardDates_llTextMsg);
            llImage = (LinearLayout) view.findViewById(R.id.cardDates_llImageMsg);
            llPDF = (LinearLayout) view.findViewById(R.id.cardDates_llPDFMsg);

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
        }
    }

    public CircularsDateListAdapter(Context context, List<CircularDates> datesList) {
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
