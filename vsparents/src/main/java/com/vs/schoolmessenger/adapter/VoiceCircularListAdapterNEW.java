package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.VoiceCircularPopup;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.DownloadFileFromURL;

import java.io.File;
import java.util.ArrayList;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VOICE;

/**
 * Created by voicesnap on 8/31/2016.
 */
public class VoiceCircularListAdapterNEW extends RecyclerView.Adapter<VoiceCircularListAdapterNEW.MyViewHolder> {

    private ArrayList<MessageModel> circularList;
    Context context;
    private static final String VOICE_FOLDER = "School Voice/Voice";
    Boolean is_Archive;


    private ArrayList<File[]> list = new ArrayList<>();
    static MessageModel msgModel;
    static String msgType;
    String name;
    private ArrayList<String> myList = new ArrayList<>();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_voice_new, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.bind(circularList.get(position));

        final MessageModel circular = circularList.get(position);

        holder.tvDate.setText(circular.getMsgDate());
        holder.tvTime.setText(circular.getMsgTime());
        holder.tvTitle.setText(circular.getMsgTitle());
        holder.tvDescription.setText(circular.getMsgdescription());

        if (circular.getMsgdescription().equals("")) {
            holder.tvDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
        }

        if (circular.getMsgReadStatus().equals("0"))
            holder.tvStatus.setVisibility(View.VISIBLE);
        else holder.tvStatus.setVisibility(View.GONE);

        holder.btnView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkConnected()) {
                    long unixTime = System.currentTimeMillis() / 1000L;
                    String timeStamp = String.valueOf(unixTime);

                    System.out.println("Title1: " + circular.getMsgTitle());
                    System.out.println("Id: " + circular.getMsgID());
                    System.out.println("msg_type1: " + MSG_TYPE_VOICE);
                    System.out.println("circular1: " + circular);
                    System.out.println("timeStamp1: " + timeStamp);
                    System.out.println("VOICE_FOLDER1: " + VOICE_FOLDER);

                    String filename = String.valueOf(circular.getMsgID());

                    DownloadFileFromURL.downloadSampleFile((Activity) context, circular, VOICE_FOLDER, filename + "_" + circular.getMsgTitle() + ".mp3", MSG_TYPE_VOICE,"");

                }
                else {
                    long unixTime = System.currentTimeMillis() / 1000L;
                    String timeStamp = String.valueOf(unixTime);

                    System.out.println("Title: " + circular.getMsgTitle());
                    System.out.println("msg_type: " + MSG_TYPE_VOICE);
                    System.out.println("circular: " + circular);
                    System.out.println("timeStamp: " + timeStamp);
                    System.out.println("VOICE_FOLDER: " + VOICE_FOLDER);
                    String filename = String.valueOf(circular.getMsgID());

                    String title = String.valueOf(circular.getMsgTitle());


                    int size = myList.size();
                    System.out.println("size12: " + size);
                    if (size > 0) {
                        for (int k = 0; k < size; k++) {
                            myList.remove(0);
                        }
                    }
                   // String root_sd = Environment.getExternalStorageDirectory().getPath();
                    String root_sd;
                    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
                    {
                        root_sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    }
                    else{
                        root_sd = Environment.getExternalStorageDirectory().getPath();
                    }

                    File yourDir1 = new File(root_sd, VOICE_FOLDER);
                    File list[] = yourDir1.listFiles();

                    System.out.println("list: " + list.length);
                    for (int i = 0; i < list.length; i++) {
                        myList.add(list[i].getName());
                    }
                    System.out.println("size1: " + myList.size());

                    for (int j = 0; j < myList.size(); j++) {
                        name = myList.get(j);
                        System.out.println("name: " + name);
                        System.out.println("nafilename: " + filename);

                        if (name.contains(filename)) {
                            Intent inPdfPopup = new Intent(context, VoiceCircularPopup.class);
                            inPdfPopup.putExtra("VOICE_ITEM", circular);
                            context.startActivity(inPdfPopup);

                        }
                    }

                    if (!myList.contains(filename+"_"+circular.getMsgTitle()+".mp3")) {
                        Toast.makeText(context, "Please connect the internet", Toast.LENGTH_LONG).show();

                    }
                    System.out.println("name1: " + name);
                    System.out.println("filename1: " + filename);
                }


            }
        });

        holder.btnDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                DownloadFileFromURL.downloadSampleFile((Activity) context, circular.getMsgContent(), VOICE_FOLDER, circular.getMsgTitle() + ".mp3");
            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }


    @Override
    public int getItemCount() {
        return circularList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvTime, tvTitle, tvStatus, tvDescription;
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

        public void bind(final MessageModel item) {
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public VoiceCircularListAdapterNEW(Context context, ArrayList<MessageModel> circularList) {
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
