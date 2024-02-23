package com.vs.schoolmessenger.assignment;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VIDEO_GALLERY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherSchoolList;
import com.vs.schoolmessenger.activity.ToStaffGroupList;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.FileUtils;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.videoalbum.AlbumVideoSelectVideoActivity;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Future;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoUpload extends AppCompatActivity {
    private static final int REQUEST = 1;
    private static final int SELECT_IMAGE = 2;

    Button btnchange, btnRecipients, btnStdSec, btnSchool;
    RelativeLayout frmImageClick;
    VideoView img1, img2, img3, img4;
    ImageView imgback, imgplay1, imgplay2, imgplay3, imgplay4;
    public static ArrayList<String> imagePathList = new ArrayList<>();
    public static ArrayList<String> contentlist = new ArrayList<>();
    String path;
    Boolean alertshow = false;
    LinearLayout lnrImages;
    TextView lblClickImage;
    EditText edgallery, edtitle;

    private PopupWindow popupWindow;

    String OrganisationId, ManagementID;
    String filetype = "1", recievertype = "2", description, imagesize, filecontent;
    String result;
    String imageFilePath, loginType;
    File photoFile, file;
    long length, sizekb, total;
    Intent resume;
    ContentAdapter contentadapter;
    private Future<Void> mFuture;
    Button btnStaffGroups;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(VideoUpload.this);

        img1 = (VideoView) findViewById(R.id.img1);
        img2 = (VideoView) findViewById(R.id.img2);
        img3 = (VideoView) findViewById(R.id.img3);
        img4 = (VideoView) findViewById(R.id.img4);
        imgback = (ImageView) findViewById(R.id.videos_ToolBarIvBack);
        imgplay1 = findViewById(R.id.lblImageCount1);
        imgplay2 = findViewById(R.id.lblImageCount2);
        imgplay3 = findViewById(R.id.lblImageCount3);
        imgplay4 = findViewById(R.id.lblImageCount4);

        edgallery = (EditText) findViewById(R.id.abtgallery);
        edtitle = (EditText) findViewById(R.id.title);

        lblClickImage = (TextView) findViewById(R.id.lblClickImage);
        lnrImages = (LinearLayout) findViewById(R.id.lnrImages);
        frmImageClick = (RelativeLayout) findViewById(R.id.frmImageClick);
        btnchange = (Button) findViewById(R.id.btnchange);
        btnRecipients = (Button) findViewById(R.id.btnRecipients);
        btnStdSec = (Button) findViewById(R.id.emergVoice_btnToSections);
        btnSchool = (Button) findViewById(R.id.emergVoice_btnToStudents);

        lblClickImage.setVisibility(View.VISIBLE);
        btnchange.setEnabled(false);
        imgplay1.setVisibility(View.GONE);
        imgplay2.setVisibility(View.GONE);
        imgplay3.setVisibility(View.GONE);
        imgplay4.setVisibility(View.GONE);

        btnStaffGroups = (Button) findViewById(R.id.btnStaffGroups);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (loginType.equals(LOGIN_TYPE_TEACHER)) {
            btnStdSec.setVisibility(View.VISIBLE);
            btnSchool.setVisibility(View.VISIBLE);
            btnRecipients.setVisibility(View.GONE);

            btnStaffGroups.setVisibility(View.VISIBLE);

        } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            btnStdSec.setVisibility(View.GONE);

            btnSchool.setVisibility(View.GONE);
            btnRecipients.setVisibility(View.VISIBLE);
        }

        btnRecipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("filepath", String.valueOf(file));
                length = file.length();

                sizekb = (1000) * Integer.parseInt(String.valueOf(length));
                Log.d("length", String.valueOf(sizekb));
                if (edtitle.getText().toString().equals("")) {
                    alert("Please Enter Title");
                } else if (edgallery.getText().toString().equals("")) {
                    alert("Please Enter Description");

                } else if (imagePathList.isEmpty()) {
                    alert("Please Choose Video");
                } else {
                    if (listschooldetails.size() == 1) {
                        Intent inAtten = new Intent(VideoUpload.this, VideoPrincipalRecipient.class);
                        inAtten.putExtra("REQUEST_CODE", VIDEO_GALLERY);
                        inAtten.putExtra("TITLE", edtitle.getText().toString());
                        inAtten.putExtra("CONTENT", edgallery.getText().toString());
                        inAtten.putExtra("FILEPATH", String.valueOf(file));
                        inAtten.putExtra("FILE_SIZE", String.valueOf(length));
                        startActivity(inAtten);
                    } else {
                        Intent i = new Intent(VideoUpload.this, TeacherSchoolList.class);
                        i.putExtra("REQUEST_CODE", VIDEO_GALLERY);
                        i.putExtra("Type", "Recipient");
                        i.putExtra("TITLE", edtitle.getText().toString());
                        i.putExtra("CONTENT", edgallery.getText().toString());
                        i.putExtra("FILEPATH", String.valueOf(file));
                        i.putExtra("FILE_SIZE", String.valueOf(length));
                        startActivity(i);
                    }
                }
            }
        });
        btnSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("filepath", String.valueOf(file));
                sizekb = file.length();
                if (edtitle.getText().toString().equals("")) {
                    alert("Please Enter Title");
                } else if (edgallery.getText().toString().equals("")) {
                    alert("Please Enter Description");

                } else if (imagePathList.isEmpty()) {
                    alert("Please Choose Video");

                } else {
                    Intent i = new Intent(VideoUpload.this, RecipientVideoActivity.class);
                    i.putExtra("REQUEST_CODE", VIDEO_GALLERY);
                    i.putExtra("SEC", "0");
                    i.putExtra("TITLE", edtitle.getText().toString());
                    i.putExtra("CONTENT", edgallery.getText().toString());
                    i.putExtra("FILEPATH", String.valueOf(file));
                    i.putExtra("FILE_SIZE", String.valueOf(sizekb));
                    startActivity(i);
                }
            }
        });
        btnStdSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("filepath", String.valueOf(file));
                length = file.length();

                total = 0;
                sizekb = (1000) * Integer.parseInt(String.valueOf(length));
                if (edtitle.getText().toString().equals("")) {
                    alert("Please Enter Title");
                } else if (edgallery.getText().toString().equals("")) {
                    alert("Please Enter Description");

                } else if (imagePathList.isEmpty()) {
                    alert("Please Choose Video");

                } else {
                    Intent i = new Intent(VideoUpload.this, RecipientVideoActivity.class);
                    i.putExtra("REQUEST_CODE", VIDEO_GALLERY);
                    i.putExtra("SEC", "1");
                    i.putExtra("TITLE", edtitle.getText().toString());
                    i.putExtra("CONTENT", edgallery.getText().toString());
                    i.putExtra("FILEPATH", String.valueOf(file));
                    i.putExtra("FILE_SIZE", String.valueOf(length));
                    startActivity(i);
                }

            }
        });

        btnStaffGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("filepath", String.valueOf(file));
                length = file.length();

                total = 0;
                sizekb = (1000) * Integer.parseInt(String.valueOf(length));
                if (edtitle.getText().toString().equals("")) {
                    alert("Please Enter Title");
                } else if (edgallery.getText().toString().equals("")) {
                    alert("Please Enter Description");

                } else {
                    Intent i = new Intent(VideoUpload.this, ToStaffGroupList.class);
                    i.putExtra("REQUEST_CODE", VIDEO_GALLERY);
                    i.putExtra("TITTLE", edtitle.getText().toString());
                    i.putExtra("MESSAGE", edgallery.getText().toString());
                    i.putExtra("FILEPATH", String.valueOf(file));
                    i.putExtra("FILE_SIZE", String.valueOf(length));
                    startActivity(i);
                }

            }
        });

        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePathList.clear();
                Intent intent;
                intent = new Intent(VideoUpload.this, AlbumVideoSelectVideoActivity.class);
                lnrImages.setVisibility(View.GONE);
                lblClickImage.setVisibility(View.VISIBLE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });
        lblClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePathList.clear();
                videotermsapi();
            }
        });

        imgplay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("enter1", "enter1");
                if (!imagePathList.isEmpty()) {
                    Intent tostart = new Intent(Intent.ACTION_VIEW);
                    tostart.setDataAndType(Uri.parse(imagePathList.get(0)), "video/*");
                    startActivity(tostart);
                } else {
                    alert("Please Choose Video");
                }
            }
        });
    }


    private void dialogbox() {
        final CharSequence[] items = {"Take Video", "Choose Video From Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoUpload.this);
        builder.setTitle("Add Video!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Video")) {
                    imagePathList.clear();

                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, REQUEST);
                    }


                } else if (items[item].equals("Choose Video From Gallery")) {
                    imagePathList.clear();

                    Intent intent;
                    intent = new Intent(VideoUpload.this, AlbumVideoSelectVideoActivity.class);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, SELECT_IMAGE);

                }


            }

        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("requestcode", String.valueOf(requestCode));
        long total = 0;
        long sizekb = 0;
        imagesize = TeacherUtil_SharedPreference.getVideosize(VideoUpload.this);

        Log.d("length", String.valueOf(imagesize));
        sizekb = (1024 * 1024) * Integer.parseInt(imagesize);
        Log.d("length", String.valueOf(sizekb));

        if (resultCode != Activity.RESULT_CANCELED) {


            Log.d("tester_entry", String.valueOf(resultCode));

            if (requestCode == REQUEST && resultCode == RESULT_OK) {
                Log.d("entered", String.valueOf(requestCode));
                lnrImages.setVisibility(View.VISIBLE);
                img1.setVisibility(View.VISIBLE);
                imgplay1.setVisibility(View.VISIBLE);
                imgplay2.setVisibility(View.GONE);
                imgplay3.setVisibility(View.GONE);
                imgplay4.setVisibility(View.GONE);

                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);

                lblClickImage.setVisibility(View.GONE);
                frmImageClick.setEnabled(false);

                Uri videoUri = data.getData();
                imageFilePath = FileUtils.getPath(this, videoUri);

                imagePathList.add(imageFilePath);
                Log.d("File_path_image", imageFilePath);

                img1.setVideoPath(imageFilePath);
                img1.getHolder().setSizeFromLayout();
                img1.seekTo(1);

            } else if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK
                    && null != data) {
                lnrImages.setVisibility(View.VISIBLE);
                lblClickImage.setVisibility(View.GONE);
                frmImageClick.setEnabled(false);


                ArrayList<String> path;

                path = data.getStringArrayListExtra("images");

                Log.d("imagelist", String.valueOf(imagePathList));
                Log.d("pathpre", String.valueOf(path));

                img1.setVisibility(View.GONE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);

                for (int y = 0; y < path.size(); y++) {

                    if (y == 0) {

                        File img = new File(path.get(0));
                        long pathlength = img.length();

                        String check = img.toString();
                        check = check.substring(check.lastIndexOf(".") + 1);
                        Log.d("check", check);
                        if (check.equalsIgnoreCase("mp4") || check.equalsIgnoreCase("mov") ||
                                check.equalsIgnoreCase("flv") || check.equalsIgnoreCase("avi") ||
                                check.equalsIgnoreCase("wmv")) {
                            if (pathlength <= sizekb) {
                                img1.setVideoPath(path.get(0));
                                img1.setVisibility(View.VISIBLE);
                                img1.getHolder().setSizeFromLayout();
                                img1.seekTo(1);
                                imgplay1.setVisibility(View.VISIBLE);

                                imagePathList.add(path.get(0));
                                try {
                                    File outputDir = new File(getExternalFilesDir(null), "outputs");
                                    //noinspection ResultOfMethodCallIgnored
                                    outputDir.mkdir();
                                    file = File.createTempFile("transcode_test", ".mp4", outputDir);
                                } catch (IOException e) {
                                    Log.e("fail", "Failed to create temporary file.", e);
                                    return;
                                }
                                ContentResolver resolver = getContentResolver();
                                final ParcelFileDescriptor parcelFileDescriptor;
                                File file1 = new File(imagePathList.get(0));
                                try {

                                    parcelFileDescriptor = resolver.openFileDescriptor(Uri.fromFile(file1), "r");
                                } catch (FileNotFoundException e) {
                                    Log.w("Could not open '" + Uri.fromFile(file1) + "'", e);
                                    return;
                                }
                                assert parcelFileDescriptor != null;
                                final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                                final ProgressDialog progressBar = new ProgressDialog(VideoUpload.this);
                                progressBar.setCancelable(false);
                                progressBar.setMessage("loading");
                                progressBar.show();
                                final long startTime = SystemClock.uptimeMillis();
                                MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
                                    @Override
                                    public void onTranscodeProgress(double progress) {
                                        if (progress < 0) {
                                            progressBar.setIndeterminate(true);
                                        } else {
                                            progressBar.setIndeterminate(false);
                                            progressBar.setProgress((int) Math.round(progress * 100));
                                        }
                                    }

                                    @Override
                                    public void onTranscodeCompleted() {
                                        progressBar.dismiss();
                                        Log.d("test", "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                                        onTranscodeFinished(true, parcelFileDescriptor);

                                    }

                                    @Override
                                    public void onTranscodeCanceled() {
                                        progressBar.dismiss();
                                        onTranscodeFinished(false, parcelFileDescriptor);
                                        file = new File(imagePathList.get(0));
                                    }

                                    @Override
                                    public void onTranscodeFailed(Exception exception) {
                                        progressBar.dismiss();
                                        onTranscodeFinished(false, parcelFileDescriptor);
                                        file = new File(imagePathList.get(0));

                                    }
                                };
                                Log.d("TAG", "transcoding into " + file);
                                mFuture = MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, file.getAbsolutePath(),
                                        MediaFormatStrategyPresets.createExportPreset960x540Strategy(), listener);
                            } else {
                                filecontent = TeacherUtil_SharedPreference.getVideoalert(VideoUpload.this);
                                alert(filecontent);
                            }
                        } else {
                            alert("Please Choose Valid Video format to send");
                        }

                    }
                }


            }
            if (imagePathList.size() == 0) {
                btnchange.setEnabled(false);
                btnRecipients.setEnabled(false);
                btnStdSec.setEnabled(false);
                btnSchool.setEnabled(false);
                btnStaffGroups.setEnabled(false);
                lblClickImage.setVisibility(View.VISIBLE);

            } else {
                btnchange.setEnabled(true);
                btnRecipients.setEnabled(true);
                btnStdSec.setEnabled(true);
                btnStaffGroups.setEnabled(true);
                btnSchool.setEnabled(true);
            }
        }
    }

    private void onTranscodeFinished(boolean isSuccess, ParcelFileDescriptor parcelFileDescriptor) {
        final ProgressDialog progressBar = new ProgressDialog(VideoUpload.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("loading");
        progressBar.setCancelable(false);
        progressBar.show();
        progressBar.setIndeterminate(false);
        try {
            progressBar.dismiss();
            parcelFileDescriptor.close();
        } catch (IOException e) {
            progressBar.dismiss();
            Log.w("Error while closing", e);
        }
    }

    private void showFilePickPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.video_terms, null);
        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(layout);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);


        RecyclerView ryccontent = (RecyclerView) layout.findViewById(R.id.rycContent);
        Button btnAgree = (Button) layout.findViewById(R.id.btnAgree);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        ryccontent.setLayoutManager(layoutManager);
        ryccontent.setItemAnimator(new DefaultItemAnimator());
        contentadapter = new ContentAdapter(contentlist, VideoUpload.this);
        ryccontent.setAdapter(contentadapter);
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent;
                intent = new Intent(VideoUpload.this, AlbumVideoSelectVideoActivity.class);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });


    }

    private void videotermsapi() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(VideoUpload.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(VideoUpload.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(VideoUpload.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call = apiService.GetVideoContentRestriction();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());
                    contentlist.clear();
                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String strStatus = jsonObject.getString("result");
                                String strMsg = jsonObject.getString("Message");

                                if ((strStatus.toLowerCase()).equals("1")) {
                                    String strcontent = jsonObject.getString("Content");
                                    contentlist.add(strcontent);
                                    Log.d("siz", String.valueOf(contentlist.size()));

                                }
                            }
                        }
                        showFilePickPopup();

                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoUpload.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();
    }

}