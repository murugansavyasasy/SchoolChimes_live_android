package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.util.TeacherDownloadFileFromURL;

import java.util.ArrayList;

import static com.vs.schoolmessenger.util.TeacherUtil_UrlMethods.MSG_TYPE_VOICE;


/**
 * Created by voicesnap on 8/31/2016.
 */
public class TeacherVoiceCircularListAdapterNEW extends RecyclerView.Adapter<TeacherVoiceCircularListAdapterNEW.MyViewHolder> {

    private ArrayList<TeacherMessageModel> circularList;
    Context context;
    Boolean is_Archive;
    private static final String VOICE_FOLDER = "//SchoolVoiceVoice";

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_list_item_voice_new, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(circularList.get(position));

        final TeacherMessageModel circular = circularList.get(position);

        holder.tvDate.setText(circular.getMsgDate());
        holder.tvTime.setText(circular.getMsgTime());
        holder.tvTitle.setText(circular.getMsgTitle());
        holder.tvDescription.setText(circular.getMsgdescription());

        if(circular.getMsgdescription().equals("")){
            holder.tvDescription.setVisibility(View.GONE);
        }
        else{
            holder.tvDescription.setVisibility(View.VISIBLE);
        }

        if (circular.getMsgReadStatus().equals("0"))
            holder.tvStatus.setVisibility(View.VISIBLE);
        else holder.tvStatus.setVisibility(View.GONE);

        holder.btnView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                long unixTime = System.currentTimeMillis() / 1000L;
                String timeStamp = String.valueOf(unixTime);

                String filename=circular.getMsgID();

                TeacherDownloadFileFromURL.downloadSampleFile((Activity) context, circular, VOICE_FOLDER, filename + "_" + circular.getMsgTitle() + ".mp3", MSG_TYPE_VOICE, circular.getIs_Archive());
            }
        });

        holder.btnDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                showToast("Download");
//                TeacherDownloadFileFromURL.downloadSampleFile((Activity) context, circular.getMsgContent(), VOICE_FOLDER, circular.getMsgTitle() + ".mp3");
            }
        });

    }

    @Override
    public int getItemCount() {
        return circularList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvTime, tvTitle, tvStatus,tvDescription;
        public Button btnView, btnDownload;

        public MyViewHolder(View view) {
            super(view);

            tvDate = (TextView) view.findViewById(R.id.cardVoice_tvDate);
            tvTime = (TextView) view.findViewById(R.id.cardVoice_tvTime);
            tvTitle = (TextView) view.findViewById(R.id.cardVoice_tvTitle);
            tvStatus = (TextView) view.findViewById(R.id.cardVoice_tvNew);
            tvDescription = (TextView) view.findViewById(R.id.tv_description_voice);
            btnView = (Button) view.findViewById(R.id.cardVoice_btnView);
            btnDownload = (Button) view.findViewById(R.id.cardVoice_btnDownload);
        }

        public void bind(final TeacherMessageModel item) {
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public TeacherVoiceCircularListAdapterNEW(Context context, ArrayList<TeacherMessageModel> circularList,Boolean archive) {
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
