package com.vs.schoolmessenger.activity;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.ImageListAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.util.FileUtils;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;


public class TeacherPhotosScreen extends AppCompatActivity implements View.OnClickListener {

    Uri picUri;
    Button btnNext, btnChangeImage;
    Button btnToSections, btnToStudents;
    ImageView ivImage;
    EditText et_tittle;
    String strSelectedImgFilePath;
    public static final String IMAGE_FOLDER_NAME = "School Messenger/image";
    public static final String IMAGE_FILE_NAME = "imageCircular.png";

    public static int IMAGE_UPLOAD_STATUS = 123;

    // school list
//    boolean bIsPrincipal;
    private int iRequestCode;
    RecyclerView rvSchoolsList;
    String loginType;

    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private int i_schools_count = 0;

    // Compress...
    private File actualImage;
    private File compressedImage;
    String strCompressedImagePath, strPDfFilePath = "";

    boolean bEnableListView = false;

    String imageEncoded;
    List<String> imagesEncodedList;

    private ArrayList<String> imagePathList = new ArrayList<>();

    ImageView img1, img2, img3, img4, imgPdf;
    RelativeLayout frmImageClick;
    TextView lblClickImage, lblPdfFileName, lblImageCount;


    LinearLayout lnrImages, lnrPdf;
    public static PopupWindow popupWindow;

    String ImageCount = "4";
    FrameLayout frmImageContainer;
    private PopupWindow pwindow;
    ImageView imgColorShaddow;
    Boolean alertshow=false;

    private Uri fileUri;

    ClipData mClipData;
    String imageFilePath;
    File photoFile;

    // private ArrayList<Imagess> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_photos_screen);


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);

        et_tittle = (EditText) findViewById(R.id.photos_txtTitle);
        btnNext = (Button) findViewById(R.id.photos_btnNext);


        btnChangeImage = (Button) findViewById(R.id.photos_tvChangeImg);
        rvSchoolsList = (RecyclerView) findViewById(R.id.photos_rvSchoolsList);

        btnToSections = (Button) findViewById(R.id.photos_btnToSections);
        btnToSections.setOnClickListener(this);
        btnToStudents = (Button) findViewById(R.id.photos_btnToStudents);
        btnToStudents.setOnClickListener(this);
        btnToSections.setEnabled(false);
        btnToStudents.setEnabled(false);


        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherPhotosScreen.this);
        if(countryID.equals("11")){
            btnToSections.setText("To Grade or Sections");
        }

        ImageCount = TeacherUtil_SharedPreference.getImageCount(TeacherPhotosScreen.this);

        ImageView ivBack = (ImageView) findViewById(R.id.photos_ToolBarIvBack);
        frmImageContainer = (FrameLayout) findViewById(R.id.frmImageContainer);
        lblImageCount = (TextView) findViewById(R.id.lblImageCount);
        imgColorShaddow = (ImageView) findViewById(R.id.imgColorShaddow);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        imgPdf = (ImageView) findViewById(R.id.imgPdf);
        frmImageClick = (RelativeLayout) findViewById(R.id.frmImageClick);
        lblClickImage = (TextView) findViewById(R.id.lblClickImage);
        lblPdfFileName = (TextView) findViewById(R.id.lblPdfFileName);
        lnrImages = (LinearLayout) findViewById(R.id.lnrImages);
        lnrPdf = (LinearLayout) findViewById(R.id.lnrPdf);


        lblClickImage.setVisibility(View.VISIBLE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnNext.setOnClickListener(this);

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

        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherPhotosScreen.this);
        if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            listSchoolsAPI();
            btnToSections.setVisibility(View.GONE);
            btnToStudents.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        } else if (loginType.equals(LOGIN_TYPE_TEACHER)) {
            rvSchoolsList.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);


        }

    }


    private void imageListPopup() {

        final LayoutInflater inflater = (LayoutInflater) TeacherPhotosScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.image_popup_recycle, null);

        pwindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pwindow.setContentView(layout);

        RecyclerView imageRecyclerview = (RecyclerView) layout.findViewById(R.id.Image_History_recycle);

        ImageListAdapter mAdapter = new ImageListAdapter(imagePathList, TeacherPhotosScreen.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        imageRecyclerview.setLayoutManager(mLayoutManager);
        imageRecyclerview.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerview.setAdapter(mAdapter);
        imageRecyclerview.getRecycledViewPool().setMaxRecycledViews(0, 80);


    }

    private void showFilePickPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.choose_file_popup, null);
        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(layout);

        TextView lblChooseGallery = (TextView) layout.findViewById(R.id.lblChooseGallery);
        TextView lblChoosePdfFile = (TextView) layout.findViewById(R.id.lblChoosePdfFile);


        final LinearLayout lnrGalleryOrCamera = (LinearLayout) layout.findViewById(R.id.lnrGalleryOrCamera);
        TextView lblGallery = (TextView) layout.findViewById(R.id.lblGallery);
        TextView lblCamera = (TextView) layout.findViewById(R.id.lblCamera);

        lblChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrGalleryOrCamera.setVisibility(View.VISIBLE);

            }
        });


        lblCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    Uri photoURI = FileProvider.getUriForFile(TeacherPhotosScreen.this, "com.vs.schoolmessenger.provider", photoFile);
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
                Intent i = new Intent(TeacherPhotosScreen.this, AlbumSelectActivity.class);
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


        lblChoosePdfFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    private void showImageAndPDFPopup() {
        String[] items = {getResources().getString(R.string.choose_from_gallery), getResources().getString(R.string.choose_pdf)};
        // final String[] items = new String[]{getResources().getString(R.string.choose_from_gallery, getResources().getString(R.string.choose_pdf};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TeacherPhotosScreen.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherPhotosScreen.this);
        builder.setTitle(R.string.choose);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                } else if (item == 1) {

                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);

                }


            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void listSchoolsAPI() {
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            Log.d("test3", "test3" + listschooldetails.size());
            TeacherSchoolsModel ss = listschooldetails.get(i);
            Log.d("test4", "test4");
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(),ss.getIsPaymentPending());
            Log.d("test", ss.getStrSchoolName());
            arrSchoolList.add(ss);
            Log.d("Testing", "8***********************");
        }
        if (iRequestCode == PRINCIPAL_PHOTOS) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherPhotosScreen.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            String title = et_tittle.getText().toString();


                            if (!strPDfFilePath.equals("")) {
                                Intent inPrincipal = new Intent(TeacherPhotosScreen.this, SendToVoiceSpecificSection.class);
                                inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                                inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                                inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                                inPrincipal.putExtra("TITTLE", title);
                                inPrincipal.putExtra("FILE_PATH_PDF", strPDfFilePath);
                                inPrincipal.putExtra("PATH_LIST", imagePathList);

                                inPrincipal.putExtra("PRINCIPAL_IMAGE", "IMAGE");

                                startActivityForResult(inPrincipal, iRequestCode);
                            } else {
                                if (bEnableListView && imagePathList.size() <= Integer.parseInt(ImageCount)) {
                                    Intent inPrincipal = new Intent(TeacherPhotosScreen.this, SendToVoiceSpecificSection.class);
                                    inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                                    inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                                    inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                                    inPrincipal.putExtra("TITTLE", title);
                                    inPrincipal.putExtra("FILE_PATH_PDF", strPDfFilePath);
                                    inPrincipal.putExtra("PATH_LIST", imagePathList);

                                    inPrincipal.putExtra("PRINCIPAL_IMAGE", "IMAGE");

                                    startActivityForResult(inPrincipal, iRequestCode);
                                } else {
                                    showAlert(getResources().getString(R.string.image_maximum) + " " + ImageCount + " " + getResources().getString(R.string.images_only));
                                }

                            }
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherPhotosScreen.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherPhotosScreen.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        } else {


            TeacherSchoolsListAdapter schoolsListAdapter =
                    new TeacherSchoolsListAdapter(TeacherPhotosScreen.this, new TeacherOnCheckSchoolsListener() {
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

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherPhotosScreen.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherPhotosScreen.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
    }

    private void showAlert(String strMsg) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(TeacherPhotosScreen.this);

        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });


        android.app.AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");


        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }

            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImageFilename()));
            Log.d("outputurivalue", outputFileUri.toString());
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private String getImageFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File fileDir = new File(filepath, IMAGE_FOLDER_NAME);

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File fileNamePath = new File(fileDir, IMAGE_FILE_NAME);
        strSelectedImgFilePath = fileNamePath.getPath();
        Log.d("FILE_PATH", strSelectedImgFilePath);
        return strSelectedImgFilePath; //+ System.currentTimeMillis()
    }

    private void alert(String strStudName) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(TeacherPhotosScreen.this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        btnToSections.setEnabled(true);
        btnToStudents.setEnabled(true);
        if (requestCode == 1 && null != data) {

            try {
                if (resultCode == RESULT_OK) {
                    imagePathList = data.getStringArrayListExtra("images");
                    Log.d("pathpre", String.valueOf(imagePathList));
                    long total = 0;
                    long sizekb = 0;
                    String imagesize = TeacherUtil_SharedPreference.getImagesize(TeacherPhotosScreen.this);

                    Log.d("length", String.valueOf(imagesize));
                    sizekb = (1024 * 1024) * Integer.parseInt(imagesize);
                    Log.d("length", String.valueOf(sizekb));
                    for (int y = 0; y < imagePathList.size(); y++) {
                        Log.d("pathsize", String.valueOf(imagePathList.size()));
                        Log.d("file_path", String.valueOf(imagePathList.get(0)));

                        lnrImages.setVisibility(View.VISIBLE);
                        lnrPdf.setVisibility(View.GONE);

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
                                Glide.with(TeacherPhotosScreen.this)
                                        .load(imageUri)
                                        .into(img1);
                            } else {
                                String filecontent = TeacherUtil_SharedPreference.getFilecontent(TeacherPhotosScreen.this);
                                alert(filecontent);
                                alertshow = true;
                                imagePathList.remove(imagePathList.get(0));


                            }

                        } else if (y == 1) {
                            File img = new File(imagePathList.get(1));
                            long pathlength = img.length();

                            if (pathlength <= sizekb) {
                                File file = new File(imagePathList.get(1));
                                Uri imageUri = Uri.fromFile(file);
                                img2.setEnabled(true);
                                Glide.with(TeacherPhotosScreen.this)
                                        .load(imageUri)
                                        .into(img2);
                            } else {
                                imagePathList.remove(imagePathList.get(1));
                                String filecontent = TeacherUtil_SharedPreference.getFilecontent(TeacherPhotosScreen.this);
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
                                Glide.with(TeacherPhotosScreen.this)
                                        .load(imageUri)
                                        .into(img3);
                            } else {
                                imagePathList.remove(imagePathList.get(2));
                                String filecontent = TeacherUtil_SharedPreference.getFilecontent(TeacherPhotosScreen.this);
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
                                Glide.with(TeacherPhotosScreen.this)
                                        .load(imageUri)
                                        .into(img4);
                            } else {
                                imagePathList.remove(imagePathList.get(3));
                                String filecontent = TeacherUtil_SharedPreference.getFilecontent(TeacherPhotosScreen.this);
                                if (alertshow == false) {
                                    alert(filecontent);
                                    alertshow = true;
                                }
                            }

                        }


                    }
                    if (imagePathList.size() == 0) {
                        lnrImages.setVisibility(View.GONE);

                        bEnableListView = false;
                        btnChangeImage.setEnabled(false);

                        lblClickImage.setVisibility(View.VISIBLE);
                        frmImageClick.setEnabled(true);
                    }
                }
            }
            catch (Exception e){

            }


        } else if (requestCode == 2) {

            try {
                if (data != null && resultCode == RESULT_OK) {
                    String pdfsize = TeacherUtil_SharedPreference.getPdfsize(TeacherPhotosScreen.this);
                    long sizekb = (1024 * 1024) * Integer.parseInt(pdfsize);
                    Uri uri = data.getData();

                    File outputDir = TeacherPhotosScreen.this.getCacheDir(); // context being the Activity pointer
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

                    //  deleteTempFiles(getCacheDir());

                    long pathlength = outputFile.length();
                    if (pathlength <= sizekb) {

                        bEnableListView = true;
                        lblPdfFileName.setVisibility(View.VISIBLE);

                        lnrImages.setVisibility(View.GONE);
                        lnrPdf.setVisibility(View.VISIBLE);

                        imgPdf.setImageResource(R.drawable.pdf_image);

                        btnChangeImage.setEnabled(true);
                        lblClickImage.setVisibility(View.GONE);


                        imagePathList.add(outputFile.getPath());
                        lblPdfFileName.setText(outputFile.getPath());
                        strPDfFilePath = outputFile.getPath();
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(TeacherPhotosScreen.this);
                        alert(filecontent);
                    }
                }
            }
            catch (Exception e){
                alert("Please choose pdf file to send");
            }


        } else if (requestCode == 3) {

            try {
                if (resultCode == RESULT_OK) {
                    String imagesize = TeacherUtil_SharedPreference.getImagesize(TeacherPhotosScreen.this);

                    long sizekb = (1024 * 1024) * Integer.parseInt(imagesize);
                    Log.d("length", String.valueOf(sizekb));

                    File img = new File(imageFilePath);
                    long length = img.length();
                    Log.d("length", String.valueOf(length));

                    if (length <= sizekb) {
                        lnrImages.setVisibility(View.VISIBLE);
                        lnrPdf.setVisibility(View.GONE);

                        bEnableListView = true;
                        btnChangeImage.setEnabled(true);
                        lblClickImage.setVisibility(View.GONE);
                        img1.setEnabled(true);

//                        Bitmap photo = (Bitmap) data.getExtras().get("data");
//                        Uri mImageUri = getImageUri(getApplicationContext(), photo);
//
//                        Log.d("Uri_M", String.valueOf(mImageUri));
//                        String path = FileUtils.getPath(TeacherPhotosScreen.this, mImageUri);
//                        imagePathList.add(path);


                        Log.d("File_path", imageFilePath);
                        imagePathList.add(imageFilePath);
                        Log.d("size", String.valueOf(imagePathList.size()));
                        Glide.with(this).load(imageFilePath).into(img1);
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(TeacherPhotosScreen.this);
                        alert(filecontent);
                    }


                } else if (resultCode == RESULT_CANCELED) {

                    lnrImages.setVisibility(View.GONE);
                    lnrPdf.setVisibility(View.GONE);

                    bEnableListView = false;
                    imagePathList.clear();
                    Log.d("size1", String.valueOf(imagePathList.size()));
                    lblClickImage.setVisibility(View.VISIBLE);
                    frmImageClick.setEnabled(true);
                    btnChangeImage.setEnabled(false);
                }

            }
            catch (Exception e){

            }
        } else {
            lnrImages.setVisibility(View.GONE);
            lnrPdf.setVisibility(View.GONE);

            bEnableListView = false;
            imagePathList.clear();
            Log.d("size1", String.valueOf(imagePathList.size()));
            lblClickImage.setVisibility(View.VISIBLE);
            frmImageClick.setEnabled(true);
            btnChangeImage.setEnabled(false);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);


    }

    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(TeacherPhotosScreen.this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = TeacherPhotosScreen.this.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }




    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String getRealPathFromURI1(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_tittle.getWindowToken(), 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photos_btnNext:
                Intent inPrincipal = new Intent(TeacherPhotosScreen.this, TeacherStandardsAndGroupsList.class);
                inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                startActivityForResult(inPrincipal, iRequestCode);
                break;

            case R.id.photos_btnToSections:


                if (!strPDfFilePath.equals("")) {
                    Intent intoSec = new Intent(TeacherPhotosScreen.this, TeacherStaffStandardSection.class);
                    String strtittle = et_tittle.getText().toString().trim();
                    intoSec.putExtra("REQUEST_CODE", iRequestCode);
                    intoSec.putExtra("TO", "SEC");
                    intoSec.putExtra("FILEPATH", strCompressedImagePath);

                    intoSec.putExtra("FILE_PATH_PDF", strPDfFilePath);
                    intoSec.putExtra("PATH_LIST", imagePathList);

                    intoSec.putExtra("TITTLE", strtittle);
                    startActivityForResult(intoSec, iRequestCode);
                } else {
                    if (imagePathList.size() <= Integer.parseInt(ImageCount)) {

                        Intent intoSec = new Intent(TeacherPhotosScreen.this, TeacherStaffStandardSection.class);
                        String strtittle = et_tittle.getText().toString().trim();
                        intoSec.putExtra("REQUEST_CODE", iRequestCode);
                        intoSec.putExtra("TO", "SEC");
                        intoSec.putExtra("FILEPATH", strCompressedImagePath);

                        intoSec.putExtra("FILE_PATH_PDF", strPDfFilePath);
                        intoSec.putExtra("PATH_LIST", imagePathList);

                        intoSec.putExtra("TITTLE", strtittle);
                        startActivityForResult(intoSec, iRequestCode);
                    } else {

                        showAlert(getResources().getString(R.string.image_maximum) + " " + ImageCount + " " + getResources().getString(R.string.images_only));

                    }

                }


                break;

            case R.id.photos_btnToStudents:


                if (!strPDfFilePath.equals("")) {
                    Intent intoStu = new Intent(TeacherPhotosScreen.this, TeacherStaffStandardSection.class);
                    String strtittle1 = et_tittle.getText().toString().trim();
                    intoStu.putExtra("REQUEST_CODE", iRequestCode);
                    intoStu.putExtra("TO", "STU");
                    intoStu.putExtra("FILEPATH", strCompressedImagePath);

                    intoStu.putExtra("FILE_PATH_PDF", strPDfFilePath);
                    intoStu.putExtra("PATH_LIST", imagePathList);

                    intoStu.putExtra("TITTLE", strtittle1);
                    startActivityForResult(intoStu, iRequestCode);
                } else {
                    if (imagePathList.size() <= Integer.parseInt(ImageCount)) {

                        Intent intoStu = new Intent(TeacherPhotosScreen.this, TeacherStaffStandardSection.class);
                        String strtittle1 = et_tittle.getText().toString().trim();
                        intoStu.putExtra("REQUEST_CODE", iRequestCode);
                        intoStu.putExtra("TO", "STU");
                        intoStu.putExtra("FILEPATH", strCompressedImagePath);

                        intoStu.putExtra("FILE_PATH_PDF", strPDfFilePath);
                        intoStu.putExtra("PATH_LIST", imagePathList);

                        intoStu.putExtra("TITTLE", strtittle1);
                        startActivityForResult(intoStu, iRequestCode);

                    } else {
                        showAlert(getResources().getString(R.string.image_maximum) + " " + ImageCount + " " + getResources().getString(R.string.images_only));
                    }

                }
                break;

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

