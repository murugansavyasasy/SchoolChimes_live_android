package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Environment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.VoiceCircularPopup;
import com.vs.schoolmessenger.interfaces.VoiceHistoryListener;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.DownloadFileFromURL;

import java.io.File;
import java.util.ArrayList;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VOICE;

public class VoiceHistoryAdapter extends RecyclerView.Adapter<VoiceHistoryAdapter.MyViewHolder> {

    private ArrayList<MessageModel> circularList;
    Context context;
    private static final String VOICE_FOLDER = "School Voice/Voice";


    private ArrayList<File[]> list = new ArrayList<>();
    static MessageModel msgModel;
    static String msgType;
    String name;
    private ArrayList<String> myList = new ArrayList<>();

    private final VoiceHistoryListener onContactsListener;
    private int prevSelection = -1;

    @Override
    public VoiceHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.voice_history_list, parent, false);

        return new VoiceHistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VoiceHistoryAdapter.MyViewHolder holder, final int position) {
        holder.bind(circularList.get(position));

        final MessageModel circular = circularList.get(position);

        holder.lbldate.setText(circular.getMsgDate());
        holder.lblDescription.setText(circular.getMsgdescription());


        if (circular.getSelectedStatus()) {
            holder.slectCheckbox.setChecked(true);
            prevSelection = position;
        } else {
            holder.slectCheckbox.setChecked(false);
        }

        holder.slectCheckbox.setOnCheckedChangeListener(null);
        holder.slectCheckbox.setChecked(circular.getSelectedStatus());
        holder.slectCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    circular.setSelectedStaus(true);

                    onContactsListener.voiceHistoryAddList(circular);

                    if (prevSelection >= 0) {
                        circularList.get(prevSelection).setSelectedStaus(false);
                        notifyItemChanged(prevSelection);
                    }
                    prevSelection = position;
                }


                else {
                     onContactsListener.voiceHistoryRemoveList(circular);
                }

            }
        });



        holder.btnView.setOnClickListener(new View.OnClickListener() {
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

                    DownloadFileFromURL.downloadSampleFile((Activity) context, circular, VOICE_FOLDER, filename + "_" + circular.getMsgTitle() + ".mp3", MSG_TYPE_VOICE,"Voice_History");



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
                    String root_sd = Environment.getExternalStorageDirectory().getPath();
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
                        Toast.makeText(context, context.getResources().getString(R.string.connect_internet), Toast.LENGTH_LONG).show();

                    }
                    System.out.println("name1: " + name);
                    System.out.println("filename1: " + filename);
                }


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

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lbldate, lblDescription;
        public Button btnView;
        public CheckBox slectCheckbox;




        public MyViewHolder(View view) {
            super(view);

            lbldate = (TextView) view.findViewById(R.id.lbldate);
            lblDescription = (TextView) view.findViewById(R.id.lblDescription);
            btnView = (Button) view.findViewById(R.id.cardVoice_btnView);
            slectCheckbox = (CheckBox) view.findViewById(R.id.slectCheckbox);

        }

        public void bind(final MessageModel item) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public VoiceHistoryAdapter(Context context,VoiceHistoryListener listener,ArrayList<MessageModel> circularList) {
        this.context = context;
        this.circularList = circularList;
        this.onContactsListener = listener;
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

