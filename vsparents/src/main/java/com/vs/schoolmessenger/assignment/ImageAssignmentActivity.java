package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.AlbumSelectActivity;
import com.vs.schoolmessenger.activity.TeacherPhotosScreen;

import com.vs.schoolmessenger.adapter.ImageListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.util.FileUtils;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.Constants.category_id;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_IMAGEASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXTASSIGNMENT;


public class ImageAssignmentActivity extends AppCompatActivity implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener {
    ImageView img1, img2, img3, img4;
    RelativeLayout frmImageClick;
    FrameLayout frmImageContainer;
    private PopupWindow pwindow;
    PopupWindow popupWindow;
    ImageView imgColorShaddow;
    LinearLayout lnrImages;
    TextView lblClickImage, lblImageCount, tvDate,photos_tv1;
    Button btnChangeImage, btnRecipients;
    File photoFile;
    String imageFilePath, imagesize, filecontent;
    private ArrayList<String> imagePathList = new ArrayList<>();
    boolean bEnableListView = false;
    EditText edtitle;
    String strDate, strCurrentDate;//strDuration
    int selDay, selMonth, selYear;
    String selHour, selMin;
    int minimumHour, minimumMinute;
    Boolean alertshow = false;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";


    Spinner staff_category_spin;
    List<String> listCategory = new ArrayList<>();
    ArrayAdapter<String> cateAdapter;
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
        setContentView(R.layout.activity_image_assignment);

        ImageView ivBack = (ImageView) findViewById(R.id.photos_ToolBarIvBack);
        frmImageContainer = (FrameLayout) findViewById(R.id.frmImageContainer);
        lblImageCount = (TextView) findViewById(R.id.lblImageCount);
        imgColorShaddow = (ImageView) findViewById(R.id.imgColorShaddow);
        btnChangeImage = (Button) findViewById(R.id.photos_tvChangeImg);
        btnRecipients = (Button) findViewById(R.id.photos_btnNext);
        edtitle = (EditText) findViewById(R.id.photos_txtTitle);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        edtitle.setText("");
        staff_category_spin = (Spinner) findViewById(R.id.staff_category_spin);



        photos_tv1 = (TextView) findViewById(R.id.photos_tv1);
        frmImageClick = (RelativeLayout) findViewById(R.id.frmImageClick);
        lblClickImage = (TextView) findViewById(R.id.lblClickImage);
        lnrImages = (LinearLayout) findViewById(R.id.lnrImages);
        tvDate = (TextView) findViewById(R.id.lblDate);

        listCategory.clear();
        listCategory.add("GENERAL");
        listCategory.add("CLASS WORK");
        listCategory.add("PROJECT");
        listCategory.add("RESEARCH PAPER");
        cateAdapter = new ArrayAdapter<>(ImageAssignmentActivity.this, R.layout.teacher_spin_title, listCategory);
        cateAdapter.setDropDownViewResource(R.layout.teacher_spin_dropdown);
        staff_category_spin.setAdapter(cateAdapter);

        staff_category_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category_id = position+1;
                Log.d("category_id",String.valueOf(category_id));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        photos_tv1.setText("Compose Image");
        lblClickImage.setText("Click here to choose Image");
        btnChangeImage.setText("Change Image");

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        setMinDateTime();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        frmImageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "click");
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

        btnRecipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtitle.getText().toString().isEmpty()) {
                    alert(getResources().getString(R.string.Please_enter_assignment_title));
                } else {
                    Intent i = new Intent(ImageAssignmentActivity.this, RecipientAssignmentActivity.class);
                    i.putExtra("REQUEST_CODE", STAFF_IMAGEASSIGNMENT);
                    i.putExtra("PATH_LIST", imagePathList);
                    i.putExtra("FILEPATH", "");
                    i.putExtra("TITLE", edtitle.getText().toString());
                    i.putExtra("CONTENT", "");
                    i.putExtra("DATE", strDate);
                    startActivity(i);
                }
            }
        });
    }

    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ImageAssignmentActivity.this);
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

    private void imageListPopup() {
        final LayoutInflater inflater = (LayoutInflater) ImageAssignmentActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.image_popup_recycle, null);

        pwindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pwindow.setContentView(layout);

        RecyclerView imageRecyclerview = (RecyclerView) layout.findViewById(R.id.Image_History_recycle);

        ImageListAdapter mAdapter = new ImageListAdapter(imagePathList, ImageAssignmentActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        imageRecyclerview.setLayoutManager(mLayoutManager);
        imageRecyclerview.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerview.setAdapter(mAdapter);
        imageRecyclerview.getRecycledViewPool().setMaxRecycledViews(0, 80);


    }

    private void showFilePickPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.choose_image_assignment, null);
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

                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("photoFile", photoFile.toString());
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(ImageAssignmentActivity.this, "com.vs.schoolmessenger.provider", photoFile);
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


                Intent i = new Intent(ImageAssignmentActivity.this, AlbumSelectActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && null != data) {

            try {
                if (resultCode == RESULT_OK) {
                    imagePathList = data.getStringArrayListExtra("images");
                    Log.d("pathpre", String.valueOf(imagePathList));

                    long total = 0;
                    long sizekb = 0;
                    imagesize = TeacherUtil_SharedPreference.getImagesize(ImageAssignmentActivity.this);

                    Log.d("length", String.valueOf(imagesize));
                    sizekb = (1024 * 1024) * Integer.parseInt(imagesize);
                    Log.d("length", String.valueOf(sizekb));

                    for (int y = 0; y < imagePathList.size(); y++) {
                        Log.d("pathsize", String.valueOf(imagePathList.size()));
                        Log.d("file_path", String.valueOf(imagePathList.get(0)));

                        lnrImages.setVisibility(View.VISIBLE);

                        bEnableListView = true;
                        btnChangeImage.setEnabled(true);
                        btnRecipients.setEnabled(true);
                        lblClickImage.setVisibility(View.GONE);
                        frmImageClick.setEnabled(false);
                        if (y == 0) {
                            File img = new File(imagePathList.get(0));
                            long pathlength = img.length();

                            if (pathlength <= sizekb) {
                                img1.setEnabled(true);
                                File file = new File(imagePathList.get(0));
                                Uri imageUri = Uri.fromFile(file);
                                Glide.with(ImageAssignmentActivity.this)
                                        .load(imageUri)
                                        .into(img1);
                            } else {
                                filecontent = TeacherUtil_SharedPreference.getFilecontent(ImageAssignmentActivity.this);
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
                                Glide.with(ImageAssignmentActivity.this)
                                        .load(imageUri)
                                        .into(img2);
                            } else {
                                imagePathList.remove(imagePathList.get(1));
                                filecontent = TeacherUtil_SharedPreference.getFilecontent(ImageAssignmentActivity.this);
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
                                Glide.with(ImageAssignmentActivity.this)
                                        .load(imageUri)
                                        .into(img3);
                            } else {
                                imagePathList.remove(imagePathList.get(2));
                                filecontent = TeacherUtil_SharedPreference.getFilecontent(ImageAssignmentActivity.this);
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
                                Glide.with(ImageAssignmentActivity.this)
                                        .load(imageUri)
                                        .into(img4);
                            } else {
                                imagePathList.remove(imagePathList.get(3));
                                filecontent = TeacherUtil_SharedPreference.getFilecontent(ImageAssignmentActivity.this);
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
                        btnRecipients.setEnabled(false);
                        lblClickImage.setVisibility(View.VISIBLE);
                        frmImageClick.setEnabled(true);
                    }


                }

            } catch (Exception e) {

            }

        } else if (requestCode == 3) {

            if (resultCode == RESULT_OK) {

                try {


                    imagesize = TeacherUtil_SharedPreference.getImagesize(ImageAssignmentActivity.this);
                    long sizekb = (1024 * 1024) * Integer.parseInt(imagesize);
                    Log.d("length", String.valueOf(sizekb));

                    File img = new File(imageFilePath);
                    long length = img.length();
                    Log.d("length", String.valueOf(length));

                    if (length <= sizekb) {

                        lnrImages.setVisibility(View.VISIBLE);
                        bEnableListView = true;
                        btnChangeImage.setEnabled(true);
                        btnRecipients.setEnabled(true);
                        lblClickImage.setVisibility(View.GONE);
                        img1.setEnabled(true);

                        Log.d("File_path", imageFilePath);
                        imagePathList.add(imageFilePath);
                        Log.d("size", String.valueOf(imagePathList.size()));
                        Glide.with(this).load(imageFilePath).into(img1);
                    } else {
                        filecontent = TeacherUtil_SharedPreference.getFilecontent(ImageAssignmentActivity.this);
                        alert(filecontent);
                    }

                } catch (Exception e) {

                }
            }

            else if (resultCode == RESULT_CANCELED) {

                lnrImages.setVisibility(View.GONE);
                bEnableListView = false;
                imagePathList.clear();
                Log.d("size1", String.valueOf(imagePathList.size()));
                lblClickImage.setVisibility(View.VISIBLE);
                frmImageClick.setEnabled(true);
                btnChangeImage.setEnabled(false);
                btnRecipients.setEnabled(false);
            }
        } else {
            lnrImages.setVisibility(View.GONE);

            bEnableListView = false;
            imagePathList.clear();
            Log.d("size1", String.valueOf(imagePathList.size()));
            lblClickImage.setVisibility(View.VISIBLE);
            frmImageClick.setEnabled(true);
            btnChangeImage.setEnabled(false);
            btnRecipients.setEnabled(false);
        }
        super.onActivityResult(requestCode, resultCode, data);

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

    private void datePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener(ImageAssignmentActivity.this)
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
}
