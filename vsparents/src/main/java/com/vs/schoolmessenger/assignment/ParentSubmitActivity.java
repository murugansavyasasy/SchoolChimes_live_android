package com.vs.schoolmessenger.assignment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.AlbumSelectActivity;
import com.vs.schoolmessenger.adapter.ImageListAdapter;
import com.vs.schoolmessenger.aws.AwsUploadingPreSigned;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.CurrentDatePicking;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.UploadCallback;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParentSubmitActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, View.OnClickListener {
    ImageView img1, img2, img3, img4, imgPdf,imgColorShaddow;
    RelativeLayout frmImageClick;
    TextView lblClickImage, lblPdfFileName, lblImageCount,tvDate,photos_tv1;
    FrameLayout frmImageContainer;
    private PopupWindow pwindow;
    EditText edtitle;
    Button btnNext,btnChangeImage;
    LinearLayout lnrImages, lnrPdf;
    PopupWindow popupWindow;
    String strPDfFilePath="";
    boolean bEnableListView = false;
    private ArrayList<String> imagePathList = new ArrayList<>();
    String strDate, strCurrentDate, timeString, strTime;//strDuration
    int selDay, selMonth, selYear;
    String selHour, selMin;
    int minimumHour, minimumMinute;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    String imageFilePath;
    File photoFile;
    LinearLayout lnrDate;
    String Id,filetype,EndDateAlert;
    Boolean alertshow=false;

    String fileNameDateTime;
    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType = "";

    String flag = "";
    String uploadFilePath = "";
    String SuccessFilePath = "";
    int pathIndex = 0;
    AwsUploadingPreSigned isAwsUploadingPreSigned;
    String[] UploadedURLStringArray;

    private ArrayList<String> UploadedS3URlList = new ArrayList<>();
    ImageView imageview;

    String isNewVersion;
    Boolean is_Archive;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_assignment);

        edtitle = (EditText) findViewById(R.id.photos_txtTitle);
        imageview = (ImageView) findViewById(R.id.imageview);
        edtitle.setHint("Description");
        btnNext = (Button) findViewById(R.id.photos_btnNext);
        btnNext.setText("Send");

        btnChangeImage = (Button) findViewById(R.id.photos_tvChangeImg);
        ImageView ivBack = (ImageView) findViewById(R.id.photos_ToolBarIvBack);
        frmImageContainer = (FrameLayout) findViewById(R.id.frmImageContainer);
        lblImageCount = (TextView) findViewById(R.id.lblImageCount);
        imgColorShaddow = (ImageView) findViewById(R.id.imgColorShaddow);
        lnrDate = (LinearLayout) findViewById(R.id.lnrDate);
        lnrDate.setVisibility(View.GONE);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        imgPdf = (ImageView) findViewById(R.id.imgPdf);
        frmImageClick = (RelativeLayout) findViewById(R.id.frmImageClick);
        lblClickImage = (TextView) findViewById(R.id.lblClickImage);
        photos_tv1 = (TextView) findViewById(R.id.photos_tv1);
        lblPdfFileName = (TextView) findViewById(R.id.lblPdfFileName);
        lnrImages = (LinearLayout) findViewById(R.id.lnrImages);
        lnrPdf = (LinearLayout) findViewById(R.id.lnrPdf);
        lblClickImage.setVisibility(View.VISIBLE);
        lblClickImage.setText("Click here to Upload Assignment");
        photos_tv1.setText("Submit Assignment");
        btnChangeImage.setText("CHANGE IMAGE/PDF");
        isAwsUploadingPreSigned = new AwsUploadingPreSigned();

        Id = getIntent().getExtras().getString("ID", "");
        EndDateAlert = getIntent().getExtras().getString("ENDDATE", "");

        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(ParentSubmitActivity.this);

        TeacherUtil_SharedPreference.putOnBackPressedAssignmentParent(ParentSubmitActivity.this, "1");


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressedAssignmentParent(ParentSubmitActivity.this,"1");

                onBackPressed();
            }
        });

        frmImageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showFilePickPopup();
                 popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            }
        });

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePickPopup();
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            }
        });

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img1.setEnabled(false);
        img2.setEnabled(false);
        img3.setEnabled(false);
        img4.setEnabled(false);

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageListPopup();
                pwindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   if(EndDateAlert.equals("1"))
                    alert("Submission date crossed. you still want to submit the Assignment?");
                   else
                       alert("Are you sure you want to send the Assignment?");

            }
        });


    }
    private void SendImageToSectionApi() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ParentSubmitActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[imagePathList.size()];
        for (int index = 0; index < imagePathList.size(); index++) {
            File file = new File(imagePathList.get(index));
            RequestBody surveyBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            surveyImagesParts[index] = MultipartBody.Part.createFormData("file", file.getName(), surveyBody);
        }

        JsonObject jsonReqArray = constructJsonArrayImage("1");
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(ParentSubmitActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SubmitAssignmentFromApp(requestBody, surveyImagesParts);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {

                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }
    private void showAlert(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ParentSubmitActivity.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status.equals("1")) {
                    dialog.cancel();
                    finish();
                } else {
                    dialog.cancel();
                }



            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private JsonObject constructJsonArrayImage(String multiple) {
        String childID= Util_SharedPreference.getChildIdFromSP(ParentSubmitActivity.this);
        String schoolid= Util_SharedPreference.getSchoolIdFromSP(ParentSubmitActivity.this);
        Log.d("parentschlid",schoolid);
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            jsonObjectSchoolstdgrp.addProperty("AssignmentId",Id);
            jsonObjectSchoolstdgrp.addProperty("SchoolID", schoolid);
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",childID);
            jsonObjectSchoolstdgrp.addProperty("description",edtitle.getText().toString());
            jsonObjectSchoolstdgrp.addProperty("FileType",filetype);

            Log.d("image:req", jsonObjectSchoolstdgrp.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }
    private void imageListPopup() {


        final LayoutInflater inflater = (LayoutInflater) ParentSubmitActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.image_popup_recycle, null);

        pwindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pwindow.setContentView(layout);

        RecyclerView imageRecyclerview = (RecyclerView) layout.findViewById(R.id.Image_History_recycle);

        ImageListAdapter mAdapter = new ImageListAdapter(imagePathList, ParentSubmitActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        imageRecyclerview.setLayoutManager(mLayoutManager);
        imageRecyclerview.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerview.setAdapter(mAdapter);
        imageRecyclerview.getRecycledViewPool().setMaxRecycledViews(0, 80);


    }
    private void showFilePickPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.choose_file_popup_sngpr, null);
        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(layout);

        TextView lblChooseGallery = (TextView) layout.findViewById(R.id.lblChooseGallery);
        TextView lblChoosePdfFile = (TextView) layout.findViewById(R.id.lblChoosePdfFile);

        final LinearLayout   lnrGalleryOrCamera = (LinearLayout) layout.findViewById(R.id.lnrGalleryOrCamera);
        TextView  lblGallery = (TextView) layout. findViewById(R.id.lblGallery);
        TextView lblCamera = (TextView)  layout.findViewById(R.id.lblCamera);

        lblChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrGalleryOrCamera.setVisibility(View.VISIBLE);
                filetype="IMAGE";
                contentType="image/png";

            }
        });


        lblCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentType="image/png";
                filetype="IMAGE";
                img1.setImageBitmap(null);
                img2.setImageBitmap(null);
                img3.setImageBitmap(null);
                img4.setImageBitmap(null);
                lblImageCount.setText("");
                imagePathList.clear();
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("photoFile", photoFile.toString());
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(ParentSubmitActivity.this, "com.vs.schoolmessenger.provider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(intent, 3);
                    popupWindow.dismiss();
                }


            }
        });


        lblGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filetype="IMAGE";
                contentType="image/png";
                Intent i = new Intent(ParentSubmitActivity.this, AlbumSelectActivity.class);
                startActivityForResult(i, 1);

                img1.setImageBitmap(null);
                img2.setImageBitmap(null);
                img3.setImageBitmap(null);
                img4.setImageBitmap(null);
                lblImageCount.setText("");
                imagePathList.clear();


                popupWindow.dismiss();


            }
        });

        lblChoosePdfFile.setOnClickListener(new View.OnClickListener(){
        @Override
            public void onClick(View v) {
            filetype="PDF";
            contentType="application/pdf";
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
                popupWindow.dismiss();

            }
        });

    }
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TeacherUtil_SharedPreference.putOnBackPressedAssignmentParent(ParentSubmitActivity.this,"1");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && null != data) {

            try {
                if (resultCode == RESULT_OK) {
                    imagePathList = data.getStringArrayListExtra("images");
                    long total = 0;
                    long sizekb = 0;
                    String imagesize = TeacherUtil_SharedPreference.getImagesize(ParentSubmitActivity.this);

                    sizekb = (1024 * 1024) * Integer.parseInt(imagesize);

                    for (int y = 0; y < imagePathList.size(); y++) {

                        lnrImages.setVisibility(View.VISIBLE);
                        lnrPdf.setVisibility(View.GONE);
                        btnNext.setEnabled(true);
                        bEnableListView = true;
                        btnChangeImage.setEnabled(true);
                        lblClickImage.setVisibility(View.GONE);

                        frmImageClick.setEnabled(false);

                        if (y == 0) {
                            File img = new File(imagePathList.get(0));
                            long pathlength = img.length();

                            if (pathlength <= sizekb) {
                                img1.setEnabled(true);
                                File file = new File(imagePathList.get(0));
                                Uri imageUri = Uri.fromFile(file);
                                Glide.with(ParentSubmitActivity.this)
                                        .load(imageUri)
                                        .into(img1);
                            } else {
                                String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitActivity.this);
                                alert(filecontent);
                                alertshow = true;
                            }


                        } else if (y == 1) {
                            File img = new File(imagePathList.get(1));
                            long pathlength = img.length();

                            if (pathlength <= sizekb) {
                                File file = new File(imagePathList.get(1));
                                Uri imageUri = Uri.fromFile(file);
                                img2.setEnabled(true);
                                Glide.with(ParentSubmitActivity.this)
                                        .load(imageUri)
                                        .into(img2);
                            } else {
                                String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitActivity.this);
                                if (alertshow == false) {
                                    alert(filecontent);
                                    alertshow = true;
                                }

                            }

                        } else if (y == 2) {
                            File img = new File(imagePathList.get(2));
                            long pathlength = img.length();

                            if (pathlength <= sizekb) {
                                File file = new File(imagePathList.get(2));
                                Uri imageUri = Uri.fromFile(file);
                                img3.setEnabled(true);
                                Glide.with(ParentSubmitActivity.this)
                                        .load(imageUri)
                                        .into(img3);
                            } else {
                                String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitActivity.this);
                                if (alertshow == false) {
                                    alert(filecontent);
                                    alertshow = true;
                                }
                            }

                        } else if (y == 3) {
                            File img = new File(imagePathList.get(3));
                            long pathlength = img.length();

                            if (pathlength <= sizekb) {
                                File file = new File(imagePathList.get(3));
                                Uri imageUri = Uri.fromFile(file);


                                lblImageCount.setVisibility(View.VISIBLE);

                                if (imagePathList.size() > 4) {
                                    imgColorShaddow.setBackgroundColor(getResources().getColor(R.color.clr_black_trans_50));
                                    lblImageCount.setText("+" + String.valueOf(imagePathList.size() - 3));
                                }


                                img4.setEnabled(true);
                                Glide.with(ParentSubmitActivity.this)
                                        .load(imageUri)
                                        .into(img4);

                            }
                        } else {
                            String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitActivity.this);
                            if (alertshow == false) {
                                alert(filecontent);
                                alertshow = true;
                            }
                        }

                    }

                }

            }
            catch (Exception e){

            }

        }
       else if (requestCode == 2) {
           try {

               if (data != null && resultCode == RESULT_OK) {
                   String pdfsize = TeacherUtil_SharedPreference.getPdfsize(ParentSubmitActivity.this);

                   long sizekb = (1024 * 1024) * Integer.parseInt(pdfsize);
                   Uri uri = data.getData();

                   File outputDir = ParentSubmitActivity.this.getCacheDir(); // context being the Activity pointer
                   File outputFile = File.createTempFile("School_document", ".pdf", outputDir);
                   try (InputStream in = getContentResolver().openInputStream(uri)) {
                       if(in == null) return;
                       try (OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(outputFile))) {
                           if(out == null) return;
                           // Transfer bytes from in to out
                           byte[] buf = new byte[1024];
                           int len;
                           while ((len = in.read(buf)) > 0) {
                               out.write(buf, 0, len);
                           }
                       }
                   }

//                   String path = "";
//                   long pathlength = 0;
//                   final String id = DocumentsContract.getDocumentId(uri);
//                   if (id != null && id.startsWith("msf:")) {
//                       final File file = new File(ParentSubmitActivity.this.getCacheDir(), "assignment." + Objects.requireNonNull(ParentSubmitActivity.this.getContentResolver().getType(uri)).split("/")[1]);
//                       try (final InputStream inputStream = ParentSubmitActivity.this.getContentResolver().openInputStream(uri); OutputStream output = new FileOutputStream(file)) {
//                           final byte[] buffer = new byte[4 * 1024]; // or other buffer size
//                           int read;
//
//                           while ((read = inputStream.read(buffer)) != -1) {
//                               output.write(buffer, 0, read);
//                           }
//
//                           output.flush();
//                           path = file.getPath();
//                           File img = new File(path);
//                           pathlength = img.length();
//
//                       } catch (IOException ex) {
//                           ex.printStackTrace();
//                       }
//                   } else {
//                       path = FileUtils.getPath(ParentSubmitActivity.this, uri);
//                       File img = new File(path);
//                       pathlength = img.length();
//                   }

                   long pathlength = outputFile.length();

                   if (pathlength <= sizekb) {
                       bEnableListView = true;
                       lblPdfFileName.setVisibility(View.VISIBLE);

                       lnrImages.setVisibility(View.GONE);
                       lnrPdf.setVisibility(View.VISIBLE);

                       imgPdf.setImageResource(R.drawable.pdf_image);

                       btnChangeImage.setEnabled(true);
                       btnNext.setEnabled(true);
                       lblClickImage.setVisibility(View.GONE);


                       imagePathList.add(outputFile.getPath());

                       lblPdfFileName.setText(outputFile.getPath());
                       strPDfFilePath = outputFile.getPath();
                   } else {
                       String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitActivity.this);
                       alertdialog(filecontent);
                   }
               }

           }
           catch (Exception e){
               alertdialog("Please choose pdf file to send");
           }
        }
        else if (requestCode == 3) {
            try {

                if (resultCode == RESULT_OK) {
                    String imagesize = TeacherUtil_SharedPreference.getImagesize(ParentSubmitActivity.this);

                    long sizekb = (1024 * 1024) * Integer.parseInt(imagesize);

                    File img = new File(imageFilePath);
                    long length = img.length();

                    if (length <= sizekb) {

                        lnrImages.setVisibility(View.VISIBLE);
                        lnrPdf.setVisibility(View.GONE);
                        btnNext.setEnabled(true);
                        bEnableListView = true;
                        btnChangeImage.setEnabled(true);
                        lblClickImage.setVisibility(View.GONE);
                        img1.setEnabled(true);

                        imagePathList.add(imageFilePath);
                        Glide.with(this).load(imageFilePath).into(img1);
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitActivity.this);
                        alertdialog(filecontent);
                    }


                } else if (resultCode == RESULT_CANCELED) {

                    lnrImages.setVisibility(View.GONE);
                    lnrPdf.setVisibility(View.GONE);

                    bEnableListView = false;
                    imagePathList.clear();
                    lblClickImage.setVisibility(View.VISIBLE);
                    frmImageClick.setEnabled(true);
                    btnChangeImage.setEnabled(false);
                }

            }
            catch (Exception e){

            }
        }
        else {
            lnrImages.setVisibility(View.GONE);
            lnrPdf.setVisibility(View.GONE);

            bEnableListView = false;
            imagePathList.clear();
            lblClickImage.setVisibility(View.VISIBLE);
            frmImageClick.setEnabled(true);
            btnChangeImage.setEnabled(false);
            btnNext.setEnabled(false);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    private void alertdialog(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ParentSubmitActivity.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();
    }
    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ParentSubmitActivity.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UploadedS3URlList.clear();
                showLoading();
                isUploadAWS("", "", "");
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void isUploadAWS(String contentType, String isType, String value) {

        if (contentType.equals("application/pdf")) {
            contentType = "pdf";
            isType = ".pdf";
        } else {
            contentType = "image";
            isType = "IMG";
        }

        Log.d("selectedImagePath", String.valueOf(imagePathList.size()));

        String currentDate = CurrentDatePicking.getCurrentDate();
        String schoolid = Util_SharedPreference.getSchoolIdFromSP(ParentSubmitActivity.this);

        for (int i = 0; i < imagePathList.size(); i++) {
            AwsUploadingFile(String.valueOf(imagePathList.get(i)),  schoolid, contentType, isType, value);
        }
    }

    private void AwsUploadingFile(String isFilePath, String bucketPath, String isFileExtension, String filetype, String type) {
        String countryID = TeacherUtil_SharedPreference.getCountryID(ParentSubmitActivity.this);

        isAwsUploadingPreSigned.getPreSignedUrl(isFilePath, bucketPath, isFileExtension, this,countryID,true,false, new UploadCallback() {
            @Override
            public void onUploadSuccess(String response, String isAwsFile) {
                Log.d("Upload Success", response);
                UploadedS3URlList.add(isAwsFile);

                if (UploadedS3URlList.size() == imagePathList.size()) {
                    SubmitAssignmentFromAppWithCloudURL();
                }
            }

            @Override
            public void onUploadError(String error) {
                Log.e("Upload Error", error);
            }
        });
    }

    private void SubmitAssignmentFromAppWithCloudURL() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ParentSubmitActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = submitAssignmentJson();

        Call<JsonArray> call;
        if(isNewVersion.equals("1") && is_Archive){
            call = apiService.SubmitAssignmentFromAppWithCloudURL_Archive(jsonReqArray);
        }
        else {
            call = apiService.SubmitAssignmentFromAppWithCloudURL(jsonReqArray);
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

             hideLoading();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
              hideLoading();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject submitAssignmentJson() {

        String childID= Util_SharedPreference.getChildIdFromSP(ParentSubmitActivity.this);
        String schoolid= Util_SharedPreference.getSchoolIdFromSP(ParentSubmitActivity.this);
        Log.d("parentschlid",schoolid);

        Log.d("UploadFileList",String.valueOf(UploadedS3URlList.size()));

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("AssignmentId", Id);
            jsonObjectSchoolstdgrp.addProperty("ProcessBy", childID);
            jsonObjectSchoolstdgrp.addProperty("SchoolID", schoolid);
            jsonObjectSchoolstdgrp.addProperty("description", edtitle.getText().toString());
            jsonObjectSchoolstdgrp.addProperty("FileType", filetype);

            JsonArray jsonArrayschoolstd = new JsonArray();

            for (int i = 0; i < UploadedS3URlList.size(); i++) {
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("FileName", UploadedS3URlList.get(i));
                jsonArrayschoolstd.add(jsonObjectclass);

            }

            jsonObjectSchoolstdgrp.add("FileNameArray", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showLoading() {
        if (progressDialog == null) {
            // Initialize the ProgressDialog if it hasn't been created yet
            progressDialog = new ProgressDialog(this); // Replace 'this' with your Context if not in an Activity
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Uploading..");
            progressDialog.setCancelable(false);
        }

        // Show the ProgressDialog if it is not already showing
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        } else {
            Log.d("ProgressBar", "ProgressDialog is already showing");
        }
    }

    private void datePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener(ParentSubmitActivity.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(selYear, selMonth, selDay)
                .setDateRange(minDate, null)
                .setDoneText(getResources().getString(R.string.teacher_btn_ok))
                .setCancelText(getResources().getString(R.string.btn_sign_cancel));
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    private String dateFormater(long dateInMillis, String formatString) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        String dateString = formatter.format(new Date(dateInMillis));
        Log.d("dateString", dateString);
        return dateString;
    }


    private void setMinDateTime() {
        tvDate.setText(dateFormater(System.currentTimeMillis(), "dd MMM yyyy"));
        strDate = dateFormater(System.currentTimeMillis(), "dd/MM/yyyy");
        strCurrentDate = strDate;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        selDay = cal.get(Calendar.DAY_OF_MONTH);
        selMonth = cal.get(Calendar.MONTH);// - 1;
        selYear = cal.get(Calendar.YEAR);

        minimumHour = cal.get(Calendar.HOUR_OF_DAY);
        minimumMinute = cal.get(Calendar.MINUTE);
        selHour = Integer.toString(minimumHour);
        selMin = Integer.toString(minimumMinute);



    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selDay = dayOfMonth;
        selMonth = monthOfYear;
        selYear = year;

        tvDate.setText(dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd MMM yyyy"));
        strDate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd/MM/yyyy");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img1:
                imageListPopup();
                pwindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                break;

            case R.id.img2:
                imageListPopup();
                pwindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                break;

            case R.id.img3:
                imageListPopup();
                pwindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                break;

            default:
                break;
        }
    }
}
