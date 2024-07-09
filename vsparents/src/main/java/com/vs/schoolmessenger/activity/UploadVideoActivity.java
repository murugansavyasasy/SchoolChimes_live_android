package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.util.FileUtils;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VIDEOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

public class UploadVideoActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imgVideo,imgPlay;
    FrameLayout frmVideo;
    RelativeLayout rytVideoClick;
    VideoView videoView;
    String filePath;
    int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 1;

    Button videos_btnToSections,videos_btnToStudents,btn_tvChangeVideo;
    ImageView videos_ToolBarIvBack;
    RecyclerView videos_rvSchoolsList;
    EditText txt_Video_txtTitle;
    String loginType;
    private int i_schools_count = 0;
    private int iRequestCode;

    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();

    long VideoDuration;
    long VideoFileSize;

    EditText txt_Video_Description;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        imgPlay=(ImageView) findViewById(R.id.imgPlay);
        imgVideo=(ImageView) findViewById(R.id.imgVideo);
        frmVideo=(FrameLayout) findViewById(R.id.frmVideo);
        rytVideoClick=(RelativeLayout) findViewById(R.id.rytVideoClick);
        videoView=(VideoView) findViewById(R.id.videoView);

        videos_btnToSections=(Button) findViewById(R.id.videos_btnToSections);
        videos_btnToStudents=(Button) findViewById(R.id.videos_btnToStudents);
        btn_tvChangeVideo=(Button) findViewById(R.id.btn_tvChangeVideo);
        videos_ToolBarIvBack=(ImageView) findViewById(R.id.videos_ToolBarIvBack);
        videos_rvSchoolsList=(RecyclerView) findViewById(R.id.videos_rvSchoolsList);
        txt_Video_txtTitle=(EditText) findViewById(R.id.txt_Video_txtTitle);
        txt_Video_Description=(EditText) findViewById(R.id.txt_Video_Description);

        String countryID = TeacherUtil_SharedPreference.getCountryID(UploadVideoActivity.this);
        if(countryID.equals("11")){
            videos_btnToSections.setText("To Grade or Sections");
        }

        rytVideoClick.setVisibility(View.VISIBLE);
        frmVideo.setVisibility(View.GONE);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);

        rytVideoClick.setOnClickListener(this);
        btn_tvChangeVideo.setOnClickListener(this);
        videos_btnToSections.setOnClickListener(this);
        videos_btnToStudents.setOnClickListener(this);
        videos_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        frmVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("filePath1",filePath);
                frmVideo.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                playVideo(v);
            }
        });

        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(UploadVideoActivity.this);
        if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            listSchoolsAPI();
            videos_btnToSections.setVisibility(View.GONE);
            videos_btnToStudents.setVisibility(View.GONE);

        } else if (loginType.equals(LOGIN_TYPE_TEACHER)) {
            videos_rvSchoolsList.setVisibility(View.GONE);
            }
            }

    private void listSchoolsAPI() {
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            TeacherSchoolsModel ss = listschooldetails.get(i);
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(),ss.getIsPaymentPending(),ss.getIsSchoolType());

            arrSchoolList.add(ss);
        }
        if (iRequestCode == PRINCIPAL_VIDEOS) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(UploadVideoActivity.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            String title = txt_Video_txtTitle.getText().toString();
                            String description = txt_Video_Description.getText().toString();

                            if(!title.equals("")&&!description.equals("")) {
                                if(VideoFileSize<1000) {
                                    Intent inPrincipal = new Intent(UploadVideoActivity.this, SendToVoiceSpecificSection.class);
                                    inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                                    inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                                    inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                                    inPrincipal.putExtra("TITTLE", title);
                                    inPrincipal.putExtra("VIDEO_FILE_PATH", filePath);
                                    inPrincipal.putExtra("VIDEO_DESCRIPTION", description);
                                    startActivityForResult(inPrincipal, iRequestCode);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), R.string.video_error, Toast.LENGTH_SHORT).show();
                                    }
                                    }
                            else {
                                Toast.makeText(getApplicationContext(), R.string.fill_all_details, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

            videos_rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(UploadVideoActivity.this);
            videos_rvSchoolsList.setLayoutManager(layoutManager);
            videos_rvSchoolsList.addItemDecoration(new DividerItemDecoration(UploadVideoActivity.this, LinearLayoutManager.VERTICAL));
            videos_rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            videos_rvSchoolsList.setAdapter(schoolsListAdapter);
        }

        else {
            TeacherSchoolsListAdapter schoolsListAdapter =
                    new TeacherSchoolsListAdapter(UploadVideoActivity.this, new TeacherOnCheckSchoolsListener() {
                        @Override
                        public void school_addSchool(TeacherSchoolsModel school) {
                            if ((school != null) && (!seletedschoollist.contains(school))) {
                                seletedschoollist.add(school);
                                i_schools_count++;
                                }
                        }

                        @Override
                        public void school_removeSchool(TeacherSchoolsModel school) {
                            if ((school != null) && (seletedschoollist.contains(school))) {
                                seletedschoollist.remove(school);
                                i_schools_count--;
                                }
                        }
                    }, arrSchoolList);

            videos_rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(UploadVideoActivity.this);
            videos_rvSchoolsList.setLayoutManager(layoutManager);
            videos_rvSchoolsList.addItemDecoration(new DividerItemDecoration(UploadVideoActivity.this, LinearLayoutManager.VERTICAL));
            videos_rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            videos_rvSchoolsList.setAdapter(schoolsListAdapter);
        }
    }

    private void playVideo(View v) {

        MediaController m = new MediaController(this);
        videoView.setMediaController(m);
        String path = filePath;
        Uri u = Uri.parse(path);
        videoView.setVideoURI(u);
        videoView.start();


        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data !=null) {
            if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                filePath =  FileUtils.getPath(UploadVideoActivity.this, selectedImageUri);
                if (filePath != null) {

                    Log.d("filePath",filePath);

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(filePath);
                    VideoDuration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    Log.d("Duration", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(VideoDuration)));
                    int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    try {
                        retriever.release();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    File file = new File(filePath);
                    VideoFileSize = file.length();
                    VideoFileSize = VideoFileSize/1024;
                    VideoFileSize=VideoFileSize/1024;



                    btn_tvChangeVideo.setEnabled(true);
                    frmVideo.setVisibility(View.VISIBLE);
                    rytVideoClick.setVisibility(View.GONE);
                    videoView.setVisibility(View.GONE);

                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                    imgVideo.setImageBitmap(bMap);
                    imgPlay.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytVideoClick:
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent,"Select Video"),CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                break;


            case  R.id.btn_tvChangeVideo:
                Intent i = new Intent();
                i.setType("video/*");
                i.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(i,"Select Video"),CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                break;


            case  R.id.videos_btnToSections:

                Intent intoSec = new Intent(UploadVideoActivity.this, TeacherStaffStandardSection.class);
                String strtittle = txt_Video_txtTitle.getText().toString().trim();
                intoSec.putExtra("REQUEST_CODE", iRequestCode);
                intoSec.putExtra("TO", "SEC");


                intoSec.putExtra("TITTLE", strtittle);
                startActivityForResult(intoSec, iRequestCode);

            case  R.id.videos_btnToStudents:

                Intent intoStu = new Intent(UploadVideoActivity.this, TeacherStaffStandardSection.class);
                String strtittle1 = txt_Video_txtTitle.getText().toString().trim();
                intoStu.putExtra("REQUEST_CODE", iRequestCode);
                intoStu.putExtra("TO", "STU");


                intoStu.putExtra("TITTLE", strtittle1);
                startActivityForResult(intoStu, iRequestCode);



            default:
                break;
        }

    }
}
