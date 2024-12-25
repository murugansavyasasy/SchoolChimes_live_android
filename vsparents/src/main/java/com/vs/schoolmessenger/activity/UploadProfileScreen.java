package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.UploadedDocsAdapter;
import com.vs.schoolmessenger.aws.UploadDocS3Uploader;
import com.vs.schoolmessenger.aws.UploadDocUtils;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.UploadDocListener;
import com.vs.schoolmessenger.model.UploadFilesModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.AwsUploadingPreSigned;
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
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadProfileScreen extends AppCompatActivity implements UploadDocListener {

    public static PopupWindow popupWindow;
    RecyclerView recycleUploadedDocs;
    ConstraintLayout parentBrowseFile;
    ConstraintLayout parentProfile;
    Button btnUploadFileToAWS;
    Button btnSubmit;
    TextView lblBrowse;
    TextView lblSelectedFilePath;
    CircleImageView imgProfile;
    TextView lblClickUpload;
    TextView lblUploadedDocuments;
    TextView lblAddProfile;
    File photoFile;
    String imageFilePath;
    String DocFilePath;
    ProgressDialog progressDialog;
    UploadDocS3Uploader s3uploaderObj;
    String fileNameDateTime;
    String urlFromS3 = null;
    String contentType = "";
    UploadedDocsAdapter uploadAdapter;
    String profileAwsFilePath = "";
    EditText txtFileName;
    TextView lblStudentProfile,
            lblUploadDoc,
            lblFileName;
    private ArrayList<String> imagePathList = new ArrayList<>();
    private final List<UploadFilesModel> UploadedS3URlList = new ArrayList<>();
    AwsUploadingPreSigned isAwsUploadingPreSigned;

    boolean isProfile = true;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.profile_screen);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity((Activity) UploadProfileScreen.this)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.CAMERA
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                            } else {
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).
                    withErrorListener(new PermissionRequestErrorListener() {
                        @Override
                        public void onError(DexterError error) {

                        }
                    })
                    .onSameThread()
                    .check();

        } else {
            Dexter.withActivity((Activity) UploadProfileScreen.this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                            } else {
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).
                    withErrorListener(new PermissionRequestErrorListener() {
                        @Override
                        public void onError(DexterError error) {

                        }
                    })
                    .onSameThread()
                    .check();

        }

        isAwsUploadingPreSigned = new AwsUploadingPreSigned();
        String Title = TeacherUtil_SharedPreference.getUploadProfileTitle(UploadProfileScreen.this);
        getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText(Title);
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recycleUploadedDocs = (RecyclerView) findViewById(R.id.recycleUploadedDocs);
        parentBrowseFile = (ConstraintLayout) findViewById(R.id.parentBrowseFile);
        parentProfile = (ConstraintLayout) findViewById(R.id.parentProfile);
        btnUploadFileToAWS = (Button) findViewById(R.id.btnUploadFileToAWS);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        lblBrowse = (TextView) findViewById(R.id.lblBrowse);
        imgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        lblSelectedFilePath = (TextView) findViewById(R.id.lblSelectedFilePath);
        lblClickUpload = (TextView) findViewById(R.id.lblClickUpload);
        lblUploadedDocuments = (TextView) findViewById(R.id.lblUploadedDocuments);
        lblAddProfile = (TextView) findViewById(R.id.lblAddProfile);
        txtFileName = (EditText) findViewById(R.id.txtFileName);

        lblStudentProfile = (TextView) findViewById(R.id.lblStudentProfile);
        lblUploadDoc = (TextView) findViewById(R.id.lblUploadDoc);
        lblFileName = (TextView) findViewById(R.id.lblFileName);
        lblStudentProfile.setTypeface(lblStudentProfile.getTypeface(), Typeface.BOLD);
        lblUploadDoc.setTypeface(lblUploadDoc.getTypeface(), Typeface.BOLD);
        lblFileName.setTypeface(lblFileName.getTypeface(), Typeface.BOLD);

        btnUploadFileToAWS.setVisibility(View.GONE);
        btnUploadFileToAWS.setEnabled(false);

        UploadedS3URlList.clear();

        s3uploaderObj = new UploadDocS3Uploader(UploadProfileScreen.this);
        btnUploadFileToAWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtFileName.getText().toString().equals("")) {
                    isProfile = false;
                    //  UploadFileToAWS(txtFileName.getText().toString());

                    isUploadAWS("pdf", ".pdf", "", txtFileName.getText().toString());

                } else {
                    showToast("Please enter your file name");
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!profileAwsFilePath.equals("") || UploadedS3URlList.size() > 0) {
                    submitStudentDetatils();
                } else {
                    showToast("Please upload the profile and documents");
                }
            }
        });

        parentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePopup(v);

            }
        });
        parentBrowseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] mimeTypes =
                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "text/csv",
                                "application/csv",
                                "application/pdf",
                                "image/jpeg",
                                "image/jpg",
                                "image/png",
                                "image/gif",
                                "application/rar",
                                "application/zip",
                        };

                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);


            }
        });

    }

    private void submitStudentDetatils() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(UploadProfileScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);


        final ProgressDialog mProgressDialog = new ProgressDialog(UploadProfileScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = submitJson();
        Call<JsonArray> call;
        call = apiService.submitStudentDocuments(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);
                            } else {
                                showAlert(strMsg, strStatus);
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
                Log.d("Upload error:", t.getMessage() + "\n" + t);
                showToast(t.toString());
            }
        });
    }

    private void showAlert(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UploadProfileScreen.this);

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

    private JsonObject submitJson() {

        String childID = Util_SharedPreference.getChildIdFromSP(UploadProfileScreen.this);
        String schoolid = Util_SharedPreference.getSchoolIdFromSP(UploadProfileScreen.this);

        Log.d("UploadFileList", String.valueOf(UploadedS3URlList.size()));

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("StudentImage", profileAwsFilePath);
            jsonObjectSchoolstdgrp.addProperty("schoolId", schoolid);
            jsonObjectSchoolstdgrp.addProperty("studentid", childID);
            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < UploadedS3URlList.size(); i++) {
                JsonObject jsonObjectclass = new JsonObject();
                String fileName = UploadedS3URlList.get(i).getWsUploadedDoc().substring(UploadedS3URlList.get(i).getWsUploadedDoc().lastIndexOf('/') + 1);
                jsonObjectclass.addProperty("documentPath", UploadedS3URlList.get(i).getWsUploadedDoc());
                jsonObjectclass.addProperty("documentName", fileName);
                jsonObjectclass.addProperty("documentDisplayName", UploadedS3URlList.get(i).getDisplayname());
                jsonArrayschoolstd.add(jsonObjectclass);

            }

            jsonObjectSchoolstdgrp.add("StudentDocumentUpload", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void UploadFileToAWS(final String FileDisplayname) {
        String schoolid = Util_SharedPreference.getSchoolIdFromSP(UploadProfileScreen.this);

        progressDialog = new ProgressDialog(UploadProfileScreen.this);
        if (DocFilePath != null) {
            showLoading();
            fileNameDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
            fileNameDateTime = "File_" + fileNameDateTime;
            Log.d("S3contentType", contentType);
            s3uploaderObj.initUpload(DocFilePath, contentType, fileNameDateTime, schoolid, false);
            s3uploaderObj.setOns3UploadDone(new UploadDocS3Uploader.UploadDocS3UploadInterface() {
                @Override
                public void onUploadSuccess(String response) {
                    hideLoading();
                    if (response.equalsIgnoreCase("Success")) {
                        urlFromS3 = UploadDocUtils.generates3ShareUrl(getApplicationContext(), DocFilePath, fileNameDateTime, schoolid, false);
                        Log.d("urlFromS3", urlFromS3);
                        if (!TextUtils.isEmpty(urlFromS3)) {
                            btnUploadFileToAWS.setVisibility(View.GONE);
                            lblClickUpload.setVisibility(View.GONE);
                            btnUploadFileToAWS.setEnabled(false);
                            lblBrowse.setText("Browse File");
                            lblSelectedFilePath.setVisibility(View.GONE);
                            lblUploadedDocuments.setVisibility(View.VISIBLE);

                            UploadFilesModel files;
                            files = new UploadFilesModel(urlFromS3, FileDisplayname);
                            UploadedS3URlList.add(files);
                            txtFileName.setText("");
                            setAdapter();
                        }
                    }
                }

                @Override
                public void onUploadError(String response) {
                    hideLoading();
                    Log.d("error", "Error Uploading");
                }
            });
        }
    }

    private void setAdapter() {
        uploadAdapter = new UploadedDocsAdapter(UploadProfileScreen.this, UploadedS3URlList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleUploadedDocs.setHasFixedSize(true);
        recycleUploadedDocs.setLayoutManager(mLayoutManager);
        recycleUploadedDocs.setItemAnimator(new DefaultItemAnimator());
        recycleUploadedDocs.setAdapter(uploadAdapter);
        uploadAdapter.notifyDataSetChanged();
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showLoading() {
        {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Uploading..");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }
    }

    private void showFilePopup(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.profile_popup, null);
        final PopupWindow popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(layout);

        RelativeLayout rytGallery = (RelativeLayout) layout.findViewById(R.id.rytGallery);
        RelativeLayout rytCamera = (RelativeLayout) layout.findViewById(R.id.rytCamera);
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        rytGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UploadProfileScreen.this, AlbumSelectActivity.class);
                i.putExtra("ProfileScreen", "Profile");
                startActivityForResult(i, 1);
                popupWindow.dismiss();
            }
        });
        rytCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                photoFile = createImageFile();
//                Log.d("photoFile", photoFile.toString());
//                if (photoFile != null) {
//                    Uri photoURI = FileProvider.getUriForFile(UploadProfileScreen.this, "com.vs.schoolmessenger.provider", photoFile);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                            photoURI);
//                    startActivityForResult(intent, 3);
//                    popupWindow.dismiss();
//                }


                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("photoFile", photoFile.toString());
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(UploadProfileScreen.this, "com.vs.schoolmessenger.provider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(intent, 3);
                    popupWindow.dismiss();
                }

            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "PROFILE_IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && null != data) {
            try {
                if (resultCode == RESULT_OK) {

                    String imageSize = TeacherUtil_SharedPreference.getImagesize(UploadProfileScreen.this);
                    long maxSize = (1024 * 1024) * Integer.parseInt(String.valueOf(10));


                    contentType = "image/png";
                    imagePathList = data.getStringArrayListExtra("images");
                    imageFilePath = imagePathList.get(0);
                    File file = new File(imagePathList.get(0));

                    long pathlength = file.length();
                    if (pathlength <= maxSize) {
                        Uri imageUri = Uri.fromFile(file);
                        Glide.with(UploadProfileScreen.this)
                                .load(imageUri)
                                .into(imgProfile);
                        lblAddProfile.setText("Change Profile");
                        isProfile = true;
                        //   profileUploadToAWS(imageFilePath);
                        isUploadAWS("image", "IMG", "", "");
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(UploadProfileScreen.this);
                        alert(filecontent);
                    }

                }
            } catch (Exception e) {

            }
        } else if (requestCode == 2) {
            try {
                if (data != null && resultCode == RESULT_OK) {

                    String pdfSize = TeacherUtil_SharedPreference.getPdfsize(UploadProfileScreen.this);
                    long maxSize = (1024 * 1024) * Integer.parseInt(String.valueOf(20));

                    btnUploadFileToAWS.setVisibility(View.VISIBLE);
                    lblClickUpload.setVisibility(View.VISIBLE);
                    btnUploadFileToAWS.setEnabled(true);
                    lblBrowse.setText("Change File");
                    lblSelectedFilePath.setVisibility(View.VISIBLE);

                    Uri uri = data.getData();
                    String mimeType = getContentResolver().getType(uri);

                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String extension = mime.getExtensionFromMimeType(UploadProfileScreen.this.getContentResolver().getType(uri));
                    contentType = mimeType;
                    File outputDir = UploadProfileScreen.this.getCacheDir(); // context being the Activity pointer
                    File outputFile = File.createTempFile("School_Upload_document_", "." + extension, outputDir);
                    try (InputStream in = getContentResolver().openInputStream(uri)) {
                        if (in == null) return;
                        try (OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(outputFile))) {
                            if (out == null) return;
                            // Transfer bytes from in to out
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                        }
                    }
                    DocFilePath = outputFile.getPath();

                    File file = new File(DocFilePath);
                    long pathlength = file.length();
                    if (pathlength <= maxSize) {
                        lblSelectedFilePath.setText(DocFilePath);
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(UploadProfileScreen.this);
                        alert(filecontent);
                    }

                }
            } catch (Exception e) {
                Log.d("Exception", e.getMessage());
            }
        } else if (requestCode == 3) {
            try {
                if (resultCode == RESULT_OK) {
                    String imageSize = TeacherUtil_SharedPreference.getImagesize(UploadProfileScreen.this);
                    long maxSize = (1024 * 1024) * Integer.parseInt(String.valueOf(10));

                    contentType = "image/png";
                    File file = new File(imageFilePath);

                    long pathlength = file.length();
                    if (pathlength <= maxSize) {
                        Uri imageUri = Uri.fromFile(file);
                        Glide.with(UploadProfileScreen.this)
                                .load(imageFilePath)
                                .into(imgProfile);

                        lblAddProfile.setText("Change Profile");
                        isProfile = true;
                        //  profileUploadToAWS(imageFilePath);
                        isUploadAWS("image", "IMG", "", "");
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(UploadProfileScreen.this);
                        alert(filecontent);
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    imagePathList.clear();
                }

            } catch (Exception e) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void isUploadAWS(String contentType, String isType, String value, String filename) {

        Log.d("selectedImagePath", String.valueOf(imageFilePath));

        String currentDate = CurrentDatePicking.getCurrentDate();
        String schoolid = Util_SharedPreference.getSchoolIdFromSP(UploadProfileScreen.this);
        if (isProfile) {
            AwsUploadingFile(imageFilePath, currentDate + "/" + schoolid, contentType, isType, value, filename);
        } else {
            AwsUploadingFile(DocFilePath, currentDate + "/" + schoolid, contentType, isType, value, filename);
        }
    }


    private void AwsUploadingFile(String isFilePath, String bucketPath, String isFileExtension, String filetype, String type, String isFileName) {
        isAwsUploadingPreSigned.getPreSignedUrl(isFilePath, bucketPath, isFileExtension, this, "", isProfile, new UploadCallback() {
            @Override
            public void onUploadSuccess(String response, String isAwsFile) {
                Log.d("Upload Success", response);

                if (isProfile) {
                    profileAwsFilePath = isAwsFile;
                } else {
                    runThread(isAwsFile, isFileName);
                }
            }

            @Override
            public void onUploadError(String error) {
                Log.e("Upload Error", error);
            }
        });
    }


    private void runThread(String isAwsFile, String isFileName) {
        runOnUiThread(new Thread(new Runnable() {
            public void run() {
                btnUploadFileToAWS.setVisibility(View.GONE);
                lblClickUpload.setVisibility(View.GONE);
                btnUploadFileToAWS.setEnabled(false);
                lblBrowse.setText("Browse File");
                lblSelectedFilePath.setVisibility(View.GONE);
                lblUploadedDocuments.setVisibility(View.VISIBLE);

                UploadFilesModel files;
                files = new UploadFilesModel(isAwsFile, isFileName);
                UploadedS3URlList.add(files);
                Log.d("UploadedS3URlList", String.valueOf(UploadedS3URlList.size()));
                txtFileName.setText("");
                setAdapter();
            }
        }));
    }


    private void alert(String filecontent) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(UploadProfileScreen.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(filecontent);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();
    }

    private void profileUploadToAWS(final String filepath) {
        String schoolid = Util_SharedPreference.getSchoolIdFromSP(UploadProfileScreen.this);

        progressDialog = new ProgressDialog(UploadProfileScreen.this);
        if (filepath != null) {
            showLoading();
            fileNameDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
            fileNameDateTime = "File_" + fileNameDateTime;
            Log.d("S3ProfilecontentType", contentType);
            s3uploaderObj.initUpload(filepath, contentType, fileNameDateTime, schoolid, true);
            s3uploaderObj.setOns3UploadDone(new UploadDocS3Uploader.UploadDocS3UploadInterface() {
                @Override
                public void onUploadSuccess(String response) {
                    hideLoading();
                    if (response.equalsIgnoreCase("Success")) {
                        urlFromS3 = UploadDocUtils.generates3ShareUrl(getApplicationContext(), filepath, fileNameDateTime, schoolid, true);
                        Log.d("ProfileurlFromS3", urlFromS3);
                        if (!TextUtils.isEmpty(urlFromS3)) {
                            profileAwsFilePath = urlFromS3;
                        }
                    }
                }

                @Override
                public void onUploadError(String response) {
                    hideLoading();
                    Log.d("error", "Error Uploading");
                }
            });
        }
    }

    @Override
    public void Doc_addClass(UploadFilesModel student) {

    }

    @Override
    public void Doc_removeClass(UploadFilesModel student) {
        UploadedS3URlList.remove(student);
        uploadAdapter.notifyDataSetChanged();

        if (UploadedS3URlList.size() == 0) {
            lblUploadedDocuments.setVisibility(View.GONE);
        } else {
            lblUploadedDocuments.setVisibility(View.VISIBLE);
        }
        Log.d("UploadedS3URlList", String.valueOf(UploadedS3URlList.size()));
    }
}

