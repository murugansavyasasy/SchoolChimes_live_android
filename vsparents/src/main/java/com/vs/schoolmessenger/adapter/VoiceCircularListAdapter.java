package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.Util_Common;

import java.util.ArrayList;

/**
 * Created by voicesnap on 8/31/2016.
 */
public class VoiceCircularListAdapter extends RecyclerView.Adapter<VoiceCircularListAdapter.MyViewHolder> {

    private ArrayList<MessageModel> circularList;
    Context context;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_voice, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(circularList.get(position));

        final MessageModel circular = circularList.get(position);

        holder.tvDuarion.setText("00:00");
        holder.tvTotDuration.setText("00:00");
        holder.tvTime.setText(circular.getMsgTime());
        holder.tvTitle.setText(circular.getMsgTitle());

        if (circular.getMsgReadStatus().equals("0"))
            holder.tvStatus.setVisibility(View.VISIBLE);
        else holder.tvStatus.setVisibility(View.GONE);

        holder.mediaPlayer = new MediaPlayer();
        holder.mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                holder.seekBar.setSecondaryProgress(percent);
            }
        });
        holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.imgBtnPlayPause.setImageResource(R.drawable.ic_play);
                holder.imgBtnPlayPause.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
        });

        holder.seekBar.setMax(99); // It means 100% .0-99
        holder.seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.myAudioPlayer_seekBar) {
//                    if (holder.mediaPlayer.isPlaying())
                    {
                        SeekBar sb = (SeekBar) v;
                        int playPositionInMillisecconds = (holder.mediaFileLengthInMilliseconds / 100) * sb.getProgress();
//                        Log.d("Position: ", ""+playPositionInMillisecconds);
                        holder.mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                }
                return false;
            }
        });


        holder.imgBtnPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    holder.mediaPlayer.setDataSource(circular.getMsgContent());
                    holder.mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.

                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.mediaFileLengthInMilliseconds = holder.mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
                holder.tvTotDuration.setText(Util_Common.milliSecondsToTimer(holder.mediaFileLengthInMilliseconds));

                if (!holder.mediaPlayer.isPlaying()) {
                    holder.mediaPlayer.start();
                    holder.imgBtnPlayPause.setImageResource(R.drawable.ic_pause);
                    holder.imgBtnPlayPause.setBackgroundColor(context.getResources().getColor(R.color.clr_red));
                } else {
                    holder.mediaPlayer.pause();
                    holder.imgBtnPlayPause.setImageResource(R.drawable.ic_play);
                    holder.imgBtnPlayPause.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                }

                primarySeekBarProgressUpdater(holder.seekBar, holder.tvDuarion, holder.mediaPlayer, holder.mediaFileLengthInMilliseconds, holder.handler);
            }
        });

    }

    @Override
    public int getItemCount() {
        return circularList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTime, tvTitle, tvStatus, tvDuarion, tvTotDuration;
        public ImageButton imgBtnPlayPause;
        public SeekBar seekBar;

        private MediaPlayer mediaPlayer;
        int mediaFileLengthInMilliseconds = 0;
        Handler handler = new Handler();

        public MyViewHolder(View view) {
            super(view);

            imgBtnPlayPause = (ImageButton) view.findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
            tvDuarion = (TextView) view.findViewById(R.id.myAudioPlayer_tvDuration);
            tvTotDuration = (TextView) view.findViewById(R.id.myAudioPlayer_tvTotDuration);
            seekBar = (SeekBar) view.findViewById(R.id.myAudioPlayer_seekBar);

            tvTime = (TextView) view.findViewById(R.id.cardVoice_tvTime);
            tvTitle = (TextView) view.findViewById(R.id.cardVoice_tvTitle);
            tvStatus = (TextView) view.findViewById(R.id.cardVoice_tvNew);
        }

        public void bind(final MessageModel item) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public VoiceCircularListAdapter(Context context, ArrayList<MessageModel> circularList) {
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

    private void primarySeekBarProgressUpdater(final SeekBar sb, final TextView tvDur, final MediaPlayer mp, final int fileLength, final Handler handler) {
        int iProgress = (int) (((float) mp.getCurrentPosition() / fileLength) * 100);
        sb.setProgress(iProgress); // This math construction give a percentage of "was playing"/"song length"

        if (mp.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    tvDur.setText(Util_Common.milliSecondsToTimer(mp.getCurrentPosition()));
                    primarySeekBarProgressUpdater(sb, tvDur, mp, fileLength, handler);
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }
}
