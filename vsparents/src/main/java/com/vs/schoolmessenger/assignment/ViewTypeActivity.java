package com.vs.schoolmessenger.assignment;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_ASSIGNMENT;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewTypeActivity extends AppCompatActivity {
    WebView pdfview;
    ImageView imgBack,img1,img2,img3,img4,popclose;
    RelativeLayout rytTitle, rytVoiceLayout, rytTextLayout, rytPdfLayout, rytImageLayout;
    TextView txtTitle, txtmsg, txtType,lblImageCount;
    LinearLayout lnrImages;
    RecyclerView rvimg;
    ImageCircularassgn imgAdapter;
    PopupWindow popupWindow;
    ImageAdapter imageAdapter;
    AssignmentViewClass assignmentView_class;
    String path,assignmentId, assignmentType, detailid, Content,type,userType,FileType,userID,isappread,date;
    ProgressDialog pDialog;
    ArrayList<String> imagelist = new ArrayList<>();
    ArrayList<String> descriptionlist = new ArrayList<>();

    ImageButton imgplaypause;
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean bIsRecording = false;
    int iRequestCode;
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();
    int recTime;
    int voiceduration;
    File futureStudioIconFile;
int Count;

    MediaRecorder recorder;
    int iMediaDuration = 0;
    SeekBar myplayerseekber;

    FrameLayout frameLayout;

    String isNewVersion;
    Boolean is_Archive;


    private static final String VOICE_FOLDER = "School Voice/Voice";
//    LinearLayout lnrImages;
@Override
protected void attachBaseContext(Context newBase) {
    String savedLanguage = LocaleHelper.getLanguage(newBase);
    Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
    Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
    super.attachBaseContext(wrappedContext);
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_viewtype);

        imgBack = findViewById(R.id.imgBack);
//        img1 = findViewById(R.id.img1);
//        img2 = findViewById(R.id.img2);
//        img3 = findViewById(R.id.img3);
//        img4 = findViewById(R.id.img4);
        imgplaypause = findViewById(R.id.imgplaypause);
        txtmsg = findViewById(R.id.txtmsg);
        registerForContextMenu(txtmsg);
//        lblImageCount = findViewById(R.id.lblImageCount);
//        frameLayout = findViewById(R.id.frameLayout);
        txtType = findViewById(R.id.txtType);
        rytTitle = findViewById(R.id.rytTitle);
        rytVoiceLayout = findViewById(R.id.rytVoiceLayout);
        rytTextLayout = findViewById(R.id.rytTextLayout);
        rytPdfLayout = findViewById(R.id.rytPdfLayout);
        rytImageLayout = findViewById(R.id.rytImageLayout);
        myplayerseekber = findViewById(R.id.myplayerseekber);
        lnrImages = findViewById(R.id.lnrImages);
        rvimg = findViewById(R.id.image_rvCircularList);

        pdfview = findViewById(R.id.pdfwebview);
        lnrImages.setVisibility(View.GONE);


        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(ViewTypeActivity.this);
        is_Archive  = getIntent().getExtras().getBoolean("is_Archive",false);

        assignmentId=getIntent().getExtras().getString("ID","");
        type=getIntent().getExtras().getString("TYPE","");
        assignmentType = getIntent().getExtras().getString("assignment","");
        userType = getIntent().getExtras().getString("USER_TYPE","");
        userID = getIntent().getExtras().getString("USER_ID","");
        FileType  = getIntent().getExtras().getString("FileType","");
        isappread  = getIntent().getExtras().getString("isappread","");
        date  = getIntent().getExtras().getString("date","");
        detailid  = getIntent().getExtras().getString("detailid","");

        if(userType.equals("parent")){
            userID= Util_SharedPreference.getChildIdFromSP(ViewTypeActivity.this);
        }
        else if(userType.equals("staff")) {
            userID= TeacherUtil_Common.Principal_staffId;
        }
        if(userType.equals("parent")&& isappread.equals("0")){
            ChangeMsgReadStatus.changeReadStatus(ViewTypeActivity.this, detailid, MSG_TYPE_ASSIGNMENT,date,isNewVersion,is_Archive, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {
//                    pdfModel.setMsgReadStatus("1");
//                    if (pdfModel.getMsgReadStatus().equals("0"))
//                        tvStatus.setVisibility(View.VISIBLE);
//                    else tvStatus.setVisibility(View.GONE);
                }
            });

        }
        onBackControl();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackControl();
                onBackPressed();
            }
        });

        ViewAssignmentContent();

//        img1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImagePOPUP(v);
//            }
//        });
//        img2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImagePOPUP(v);
//            }
//        });
//        img3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImagePOPUP(v);
//            }
//        });

    }

    private void onBackControl() {
        if(userType.equals("parent")){
            TeacherUtil_SharedPreference.putOnBackPressedAssignmentParent(ViewTypeActivity.this,"1");
        }
        else {
            TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(ViewTypeActivity.this,"1");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(),0, "Copy");
//        menu.setHeaderTitle("Copy text"); //setting header title for menu
        TextView textView = (TextView) v;
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        manager.setPrimaryClip(clipData);
    }
    private void ViewAssignmentContent() {
        final ProgressDialog mProgressDialog = new ProgressDialog(ViewTypeActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(ViewTypeActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(ViewTypeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(ViewTypeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        JsonObject object = new JsonObject();
        object.addProperty("ProcessBy", userID);
        object.addProperty("AssignmentId", assignmentId);
        object.addProperty("Type", type);
        object.addProperty("FileType", FileType );
        Log.d("Upload:Req", "" + object.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call;
        //if(isNewVersion.equals("1") && is_Archive && userType.equals("parent")){
        if(isNewVersion.equals("1") && is_Archive){
            call = apiService.ViewAssignmentContent_Archive(object);
        }
        else {
            call = apiService.ViewAssignmentContent(object);
        }

       // Call<JsonArray> call = apiService.ViewAssignmentContent(object);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());
                    try {
                        Log.d("FileResponse:Res", response.toString());
                        int statusCode = response.code();
                        Log.d("Status Code - Response", statusCode + " - " + response.body());
                        String strResponse = response.body().toString();
                           imagelist.clear();
                           descriptionlist.clear();
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String result = jsonObject.getString("result");
                                String Message = jsonObject.getString("Message");

                                if (result.equals("1")) {
                                    Content = jsonObject.getString("Content");
                                   String description = jsonObject.getString("description");
                                    Log.d("Content", Content);
                                    imagelist.add(Content);
                                    descriptionlist.add(description);

                                    Count=imagelist.size();
                                    SetVieType();

                                } else {
                                    alert(Message);
                                }
                            }


                        }
                        else {
                            alert(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        Log.e("Response Exception", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                Log.e("Response Failure", t.getMessage());
                showToast(getResources().getString(R.string.Server_Connection_Failed));
            }


        });
    }

    @Override
    public void onBackPressed() {
        onBackControl();
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        backToResultActvity("SENT");
    }
    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }
    private  void  SetVieType(){

        if (assignmentType.equals("SMS")) {
            rytTextLayout.setVisibility(View.VISIBLE);

            txtType.setText(assignmentType);
            txtmsg.setText(Content);
//            txtmsg.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                    ClipData clip = ClipData.newPlainText("label", txtmsg.getText());
//                    clipboard.setPrimaryClip(clip);
//                    return false;
//                }
//            });
        }

        else if(assignmentType.equals("VOICE")) {
            rytVoiceLayout.setVisibility(View.VISIBLE);
            txtType.setText(assignmentType);
            String url=Content;

            path=Environment.getExternalStorageDirectory() + File.separator + "SchoolAssignment/"+url.substring(url.lastIndexOf("//")+1);
            Log.d("pathfile",path);
            File file = new File(path);

            if (!file.exists()) {
                new DownloadFile().execute(url);
            } else {
                AudioPlayMethod(path);
            }
        }
        else if(assignmentType.equals("IMAGE")){
            rytImageLayout.setVisibility(View.VISIBLE);
            txtType.setText(assignmentType);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            rvimg.setLayoutManager(layoutManager);
            rvimg.setItemAnimator(new DefaultItemAnimator());
            imgAdapter = new ImageCircularassgn(imagelist,descriptionlist,userType,type,ViewTypeActivity.this);
            rvimg.setAdapter(imgAdapter);
            rvimg.getRecycledViewPool().setMaxRecycledViews(0, 20);
//            for (int j = 0; j < imagelist.size(); j++) {
//                String image=imagelist.get(j);
//
//                if(j==0){
//
//                    Glide.with(this)
//                            .load(image)
//                            .into(img1);
//
//                }else if(j==1){
//
//                    Glide.with(this)
//                            .load(image)
//                            .into(img2);
//
//                } else if (j == 2) {
//                    Glide.with(this)
//                            .load(image)
//                            .into(img3);
//
//                } else if (j == 3) {
//                    Glide.with(this)
//                            .load(image)
//                            .into(img4);
//                    if (Count > 4) {
//                        Count = Count - 3;
//                        img4.setAlpha((float) 0.4);
//                        lblImageCount.setVisibility(View.VISIBLE);
//                        lblImageCount.setText("+" + Count);
//
//                        frameLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                ImagePOPUP(v);
//                            }
//                        });
//                    }
//                }
//
//            }
        }
    }

    private  void ImagePOPUP(View v){
        final LayoutInflater inflater = (LayoutInflater) ViewTypeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.image_popup_recycle, null);

        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(layout);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);

        RecyclerView imageRecyclerview = (RecyclerView) layout.findViewById(R.id.Image_History_recycle);
        ImageAdapter imageAdapter = new ImageAdapter(ViewTypeActivity.this,imagelist,descriptionlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        imageRecyclerview.setLayoutManager(mLayoutManager);
        imageRecyclerview.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerview.setAdapter(imageAdapter);
        imageRecyclerview.getRecycledViewPool().setMaxRecycledViews(0, 80);

    }

    private void showToast(String msg) {
        Toast.makeText(ViewTypeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    private void alert(String strStudName) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewTypeActivity.this);


            alertDialog.setTitle(R.string.alert);
            alertDialog.setMessage(strStudName);
            alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            alertDialog.show();

    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(ViewTypeActivity.this);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setMessage(getResources().getString(R.string.Downloading));
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */

        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());


//                         fileName=voiceid+"_"+voicecontent+".mp3";

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf("//") + 1, f_url[0].length());
                Log.d("filename",fileName);
//
//                //Append timestamp to file name
//                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "SchoolAssignment/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }


                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));


                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();
            AudioPlayMethod(path);
        }
    }

    private void AudioPlayMethod(String path) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            imgplaypause.setImageResource(R.drawable.teacher_ic_play);
            imgplaypause.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mediaPlayer.seekTo(0);
        }


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgplaypause.setImageResource(R.drawable.teacher_ic_play);
                imgplaypause.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mediaPlayer.seekTo(0);
            }
        });
        myplayerseekber.setMax(99); // It means 100% .0-99
        myplayerseekber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.myplayerseekber) {
                    {
                        SeekBar sb = (SeekBar) v;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                        mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                }
                return false;
            }
        });


        myplayerseekber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {

                    int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * myplayerseekber.getProgress();

                    mediaPlayer.seekTo(playPositionInMillisecconds);

                }
            }
        });


        Log.d("FetchSong", "Start***************************************");
        try {
//                String filepath = Environment.getExternalStorageDirectory().getPath();
            String filepath = path;

//                File file = new File(filepath, VOICE_FOLDER_NAME);
//                File dir = new File(file.getAbsolutePath());
            File dir = new File(filepath);

            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Dir: " + dir);
            }

//                futureStudioIconFile = new File(dir, VOICE_FILE_NAME);
//                System.out.println("FILE_PATH:" + futureStudioIconFile.getPath());

            mediaPlayer.reset();
            mediaPlayer.setDataSource(dir.getPath());
            mediaPlayer.prepare();
            iMediaDuration = (int) (mediaPlayer.getDuration() / 1000.0);

        } catch (Exception e) {
            Log.d("in Fetch Song", e.toString());
        }



        Log.d("FetchSong", "END***************************************");

        imgplaypause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    imgplaypause.setImageResource(R.drawable.teacher_ic_pause);
                    imgplaypause.setBackgroundColor(getResources().getColor(R.color.clr_red));
                } else {
                    mediaPlayer.pause();
                    imgplaypause.setImageResource(R.drawable.teacher_ic_play);
                    imgplaypause.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                }


                primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds);
            }

            private void primarySeekBarProgressUpdater(final int fileLength) {
                int iProgress = (int) (((float) mediaPlayer.getCurrentPosition() / fileLength) * 100);
                myplayerseekber.setProgress(iProgress);
                if (mediaPlayer.isPlaying()) {
                    Runnable notification = new Runnable() {
                        public void run() {
//                                txtduration.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                            primarySeekBarProgressUpdater(fileLength);
                        }


                    };
                    handler.postDelayed(notification, 1000);
                }
            }
        });

    }


}
