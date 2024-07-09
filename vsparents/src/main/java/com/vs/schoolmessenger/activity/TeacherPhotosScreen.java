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
import com.google.android.material.bottomsheet.BottomSheetDialog;
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


    private int iRequestCode;
    RecyclerView rvSchoolsList;
    String loginType;

    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private int i_schools_count = 0;


    String strCompressedImagePath, strPDfFilePath = "";

    boolean bEnableListView = false;

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


    String imageFilePath;
    File photoFile;
    Button btnStaffGroups;


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

        btnStaffGroups = (Button) findViewById(R.id.btnStaffGroups);
        btnStaffGroups.setOnClickListener(this);
        btnStaffGroups.setEnabled(false);



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

            }
        });

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePickPopup();
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
            btnStaffGroups.setVisibility(View.VISIBLE);

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

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_attachments);
        TextView close = bottomSheetDialog.findViewById(R.id.lblClose);
        RelativeLayout rytGallery = bottomSheetDialog.findViewById(R.id.rytGallery);
        RelativeLayout rytCamera = bottomSheetDialog.findViewById(R.id.rytCamera);
        RelativeLayout rytPdf = bottomSheetDialog.findViewById(R.id.rytPdf);
        RelativeLayout rytVoice = bottomSheetDialog.findViewById(R.id.rytVoice);
        rytVoice.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

            }
        });

        rytGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TeacherPhotosScreen.this, AlbumSelectActivity.class);
                startActivityForResult(i, 1);
                img1.setImageBitmap(null);
                img2.setImageBitmap(null);
                img3.setImageBitmap(null);
                img4.setImageBitmap(null);
                lblImageCount.setText("");
                imagePathList.clear();
                bottomSheetDialog.dismiss();


            }
        });

        rytCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    bottomSheetDialog.dismiss();
                }

            }
        });

        rytPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
                bottomSheetDialog.dismiss();


            }
        });

        bottomSheetDialog.show();
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
            TeacherSchoolsModel ss = listschooldetails.get(i);
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(),ss.getIsPaymentPending(),ss.getIsSchoolType());
            arrSchoolList.add(ss);
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
        btnStaffGroups.setEnabled(true);
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

                        imagePathList.add(imageFilePath);
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


            case R.id.btnStaffGroups:


                if (!strPDfFilePath.equals("")) {
                    Intent intoSec = new Intent(TeacherPhotosScreen.this, ToStaffGroupList.class);
                    String strtittle = et_tittle.getText().toString().trim();
                    intoSec.putExtra("REQUEST_CODE", iRequestCode);
                    intoSec.putExtra("FILEPATH", strCompressedImagePath);
                    intoSec.putExtra("FILE_PATH_PDF", strPDfFilePath);
                    intoSec.putExtra("PATH_LIST", imagePathList);
                    intoSec.putExtra("TITTLE", strtittle);
                    startActivityForResult(intoSec, iRequestCode);
                } else {
                    if (imagePathList.size() <= Integer.parseInt(ImageCount)) {
                        Intent intoSec = new Intent(TeacherPhotosScreen.this, ToStaffGroupList.class);
                        String strtittle = et_tittle.getText().toString().trim();
                        intoSec.putExtra("REQUEST_CODE", iRequestCode);
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

