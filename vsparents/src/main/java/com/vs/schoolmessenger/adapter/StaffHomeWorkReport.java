package com.vs.schoolmessenger.adapter;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.HomeWorkDateWise;
import com.vs.schoolmessenger.model.StaffNoticeBoard;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.downLoadPDF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffHomeWorkReport extends RecyclerView.Adapter<StaffHomeWorkReport.MyViewHolder> {
    Context context;
    RelativeLayout rytParent;
    PopupWindow popupWindow;
    public List<StaffNoticeBoard.StaffNoticeBoardData> isStaffNoticeBoardData = new ArrayList<StaffNoticeBoard.StaffNoticeBoardData>();

    public static MediaPlayer mediaPlayer;
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();
    private final String VOICE_FOLDER = "//SchoolChimesVoice";
    private static final String IMAGE_FOLDER = "//SchoolChimesHomeWorks";
    String fileName = "";
    int pos = -1;

    public void clearAllData() {
        int size = this.isStaffNoticeBoardData.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.isStaffNoticeBoardData.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblTitle, lblContent, lblImagePreview, lblPDFPreview, lblSubject;
        public RelativeLayout rytVoice, rytImages, rytPDF;
        public ImageView imgPDF, img1, img2, img3, img4;
        public ImageButton imgBtnPlayPause;
        public SeekBar seekBar;
        TextView tvDuarion, tvTotDuration;

        public MyViewHolder(View view) {
            super(view);
            lblTitle = (TextView) view.findViewById(R.id.lblTitle);
            lblSubject = (TextView) view.findViewById(R.id.lblSubject);
            lblContent = (TextView) view.findViewById(R.id.lblContent);
            lblImagePreview = (TextView) view.findViewById(R.id.lblImagePreview);
            lblPDFPreview = (TextView) view.findViewById(R.id.lblPDFPreview);
            imgPDF = (ImageView) view.findViewById(R.id.imgPDF);
            rytVoice = (RelativeLayout) view.findViewById(R.id.rytVoice);
            rytImages = (RelativeLayout) view.findViewById(R.id.rytImages);
            rytPDF = (RelativeLayout) view.findViewById(R.id.rytPDF);

            imgBtnPlayPause = (ImageButton) view.findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
            seekBar = (SeekBar) view.findViewById(R.id.myAudioPlayer_seekBar);
            tvDuarion = (TextView) view.findViewById(R.id.myAudioPlayer_tvDuration);
            tvTotDuration = (TextView) view.findViewById(R.id.myAudioPlayer_tvTotDuration);

            img1 = (ImageView) view.findViewById(R.id.img1);
            img2 = (ImageView) view.findViewById(R.id.img2);
            img3 = (ImageView) view.findViewById(R.id.img3);
            img4 = (ImageView) view.findViewById(R.id.img4);
        }
    }

    public StaffHomeWorkReport(List<StaffNoticeBoard.StaffNoticeBoardData> isStaffNoticeBoardData, Context context, RelativeLayout parent) {
        this.isStaffNoticeBoardData = isStaffNoticeBoardData;
        this.context = context;
        this.rytParent = parent;
    }

    @Override
    public StaffHomeWorkReport.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_work_list_item, parent, false);
        return new StaffHomeWorkReport.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StaffHomeWorkReport.MyViewHolder holder, final int position) {
        final StaffNoticeBoard.StaffNoticeBoardData data = isStaffNoticeBoardData.get(position);
        holder.lblTitle.setText(data.getSubjectname());
        holder.lblContent.setText(data.getHomeworkcontent());
        Typeface roboto_bold = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
        holder.lblTitle.setTypeface(roboto_bold);
        holder.lblSubject.setTypeface(roboto_bold);
        holder.lblContent.setTypeface(roboto_regular);

        if (position == 0) {
            setupAudioPlayer(holder);
        }

        holder.rytPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downLoadPDF.downloadSampleFile((Activity) context, data.getFile_path().get(0).getPath(), rytParent);
            }
        });

        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = data.getFile_path().get(0).getPath();
                openPreviewImage(path);
            }
        });

        holder.img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = data.getFile_path().get(1).getPath();
                openPreviewImage(path);
            }
        });

        holder.img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = data.getFile_path().get(2).getPath();
                openPreviewImage(path);
            }
        });

        holder.img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = data.getFile_path().get(3).getPath();
                openPreviewImage(path);
            }
        });

        holder.imgBtnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pos == -1) {
                    fetchAudio(holder, data);
                    pos = position;
                } else if (position == pos) {
                    recVoicePlayPause(holder);
                } else {
                    fetchAudio(holder, data);
                    pos = position;
                }
            }
        });

        if (data.getFile_path().size() > 0) {
            if (data.getFile_path().get(0).getType().equals("IMAGE")) {
                holder.rytPDF.setVisibility(View.GONE);
                holder.rytVoice.setVisibility(View.GONE);
                holder.rytImages.setVisibility(View.VISIBLE);

                if (data.getFile_path().size() == 1) {

                    Glide.with(context)
                            .load(data.getFile_path().get(0).getPath())
                            .into(holder.img1);

                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.GONE);
                    holder.img3.setVisibility(View.GONE);
                    holder.img4.setVisibility(View.GONE);
                } else if (data.getFile_path().size() == 2) {
                    Glide.with(context)
                            .load(data.getFile_path().get(0).getPath())
                            .into(holder.img1);

                    Glide.with(context)
                            .load(data.getFile_path().get(1).getPath())
                            .into(holder.img2);

                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.GONE);
                    holder.img4.setVisibility(View.GONE);
                } else if (data.getFile_path().size() == 3) {
                    Glide.with(context)
                            .load(data.getFile_path().get(0).getPath())
                            .into(holder.img1);

                    Glide.with(context)
                            .load(data.getFile_path().get(1).getPath())
                            .into(holder.img2);

                    Glide.with(context)
                            .load(data.getFile_path().get(2).getPath())
                            .into(holder.img3);

                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.img4.setVisibility(View.GONE);
                } else if (data.getFile_path().size() == 4) {
                    Glide.with(context)
                            .load(data.getFile_path().get(0).getPath())
                            .into(holder.img1);

                    Glide.with(context)
                            .load(data.getFile_path().get(1).getPath())
                            .into(holder.img2);

                    Glide.with(context)
                            .load(data.getFile_path().get(2).getPath())
                            .into(holder.img3);

                    Glide.with(context)
                            .load(data.getFile_path().get(3).getPath())
                            .into(holder.img4);


                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.img4.setVisibility(View.VISIBLE);

                }

            } else if (data.getFile_path().get(0).getType().equals("VOICE")) {
                holder.rytPDF.setVisibility(View.GONE);
                holder.rytVoice.setVisibility(View.VISIBLE);
                holder.rytImages.setVisibility(View.GONE);
            } else if (data.getFile_path().get(0).getType().equals("PDF")) {
                holder.rytPDF.setVisibility(View.VISIBLE);
                holder.rytVoice.setVisibility(View.GONE);
                holder.rytImages.setVisibility(View.GONE);
            }

        } else {
            holder.rytPDF.setVisibility(View.GONE);
            holder.rytVoice.setVisibility(View.GONE);
            holder.rytImages.setVisibility(View.GONE);
        }
    }


    private void fetchAudio(MyViewHolder holder, StaffNoticeBoard.StaffNoticeBoardData data) {
        String audioUrl = data.getFile_path().get(0).getPath();
        // initializing media player
        mediaPlayer = new MediaPlayer();
        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(audioUrl);
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
        holder.tvTotDuration.setText(Util_Common.milliSecondsToTimer(mediaFileLengthInMilliseconds));

    }

    private void setupAudioPlayer(MyViewHolder holder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.imgBtnPlayPause.setImageResource(R.drawable.ic_play);
                holder.imgBtnPlayPause.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                mediaPlayer.seekTo(0);
            }
        });

        holder.seekBar.setMax(99); // It means 100% .0-99
        holder.seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.myAudioPlayer_seekBar) {
                    {
                        SeekBar sb = (SeekBar) v;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                        mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                }
                return false;
            }
        });

    }

    private void recVoicePlayPause(MyViewHolder holder) {

        mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL


        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            holder.imgBtnPlayPause.setImageResource(R.drawable.ic_pause);
            holder.imgBtnPlayPause.setBackgroundColor(context.getResources().getColor(R.color.clr_red));
        } else {
            mediaPlayer.pause();
            holder.imgBtnPlayPause.setImageResource(R.drawable.ic_play);
            holder.imgBtnPlayPause.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

        primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds, holder);
    }

    private void primarySeekBarProgressUpdater(final int fileLength, MyViewHolder holder) {
        int iProgress = (int) (((float) mediaPlayer.getCurrentPosition() / fileLength) * 100);
        holder.seekBar.setProgress(iProgress); // This math construction give a percentage of "was playing"/"song length"

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    holder.tvDuarion.setText(Util_Common.milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    primarySeekBarProgressUpdater(fileLength, holder);
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    private void openPreviewImage(String path) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.preview_popup, null);
        PopupWindow popupView = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        popupView.setContentView(layout);
        popupView.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
        PhotoView img = (PhotoView) layout.findViewById(R.id.imgView);
        ImageView imgBack = (ImageView) layout.findViewById(R.id.imgBack);
        TextView lblTitle = (TextView) layout.findViewById(R.id.lblTitle);
        ImageView imgDownload = (ImageView) layout.findViewById(R.id.imgDownload);
        lblTitle.setText("IMAGE");
        imgDownload.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupView.dismiss();
            }
        });

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!path.equals("")) {
                    downLoadImage(path, context);
                }

            }
        });

        img.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(path)
                .into(img);

    }

    private void downLoadImage(String url, Context context) {
        ProgressDialog mProgressDialog;
        Log.d("File URL", url);
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<ResponseBody> call = apiService.downloadFileWithDynamicUrlAsync(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("DOWNLOADING...", "server contacted and has file");

                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), IMAGE_FOLDER, fileName + ".png", (Activity) context, mProgressDialog);

                            Log.d("DOWNLOADING...", "file download was a success? " + writtenToDisk);
                            return writtenToDisk;
                        }

                        @Override
                        protected void onPostExecute(Boolean status) {
                            super.onPostExecute(status);
                            if (status) {
                                showAlert((Activity) context, "Downloaded successfully..", "File stored in: " + IMAGE_FOLDER + "/" + fileName);
                            }
                        }
                    }.execute();
                } else {
                    Log.d("DOWNLOADING...", "server contact failed");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("DOWNLOADING...", "error: " + t.toString());
            }
        });
    }

    public void showAlert(final Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_gallery);
        alertDialog.setNeutralButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertDialog.show();
    }

    public boolean writeResponseBodyToDisk(ResponseBody body, String folder, String fileName, Activity activity, ProgressDialog mProgressDialog) {
        try {
            final File dir;
            if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
                dir = new File(Environment.getExternalStorageDirectory().getPath()
                        + folder);
            } else {
                dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath()
                        + folder);
            }
            System.out.println("body: " + body);

            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Dir: " + dir);
            }

            File futureStudioIconFile = new File(dir, fileName);//"Hai.mp3"

            if (!futureStudioIconFile.exists()) {
                File futureStudioIconFile1 = new File(dir, fileName);
                futureStudioIconFile = futureStudioIconFile1;
            }

            // todo change the file location/name according to your needs

            InputStream inputStream = null;
            OutputStream outputStream = null;

//            pdfFilePath = futureStudioIconFile.getPath();

            try {
                byte[] fileReader = new byte[4096];


                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return isStaffNoticeBoardData.size();
    }
}

