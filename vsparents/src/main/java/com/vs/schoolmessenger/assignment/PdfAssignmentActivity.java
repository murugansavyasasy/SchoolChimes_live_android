package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.util.FileUtils;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.Constants.category_id;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PDFASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXTASSIGNMENT;

public class PdfAssignmentActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {
    ImageView img1, img2, img3, img4, imgPdf,imgColorShaddow;
    RelativeLayout frmImageClick;
    TextView lblClickImage, lblPdfFileName, lblImageCount,tvDate;
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
        setContentView(R.layout.activity_pdf_assignment);

        edtitle = (EditText) findViewById(R.id.photos_txtTitle);
        btnNext = (Button) findViewById(R.id.photos_btnNext);
        edtitle.setText("");

        btnChangeImage = (Button) findViewById(R.id.photos_tvChangeImg);
        ImageView ivBack = (ImageView) findViewById(R.id.photos_ToolBarIvBack);
        frmImageContainer = (FrameLayout) findViewById(R.id.frmImageContainer);
        lblImageCount = (TextView) findViewById(R.id.lblImageCount);
        imgColorShaddow = (ImageView) findViewById(R.id.imgColorShaddow);
        staff_category_spin = (Spinner) findViewById(R.id.staff_category_spin);


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
        tvDate = (TextView) findViewById(R.id.lblDate);


        listCategory.clear();
        listCategory.add("GENERAL");
        listCategory.add("CLASS WORK");
        listCategory.add("PROJECT");
        listCategory.add("RESEARCH PAPER");
        cateAdapter = new ArrayAdapter<>(PdfAssignmentActivity.this, R.layout.teacher_spin_title, listCategory);
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


        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        setMinDateTime();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        frmImageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);

            }
        });

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);

            }
        });



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtitle.getText().toString().isEmpty()){
                    alert(getResources().getString(R.string.Please_enter_assignment_title));
                }
                else {
                    Intent i = new Intent(PdfAssignmentActivity.this, RecipientAssignmentActivity.class);
                    i.putExtra("REQUEST_CODE", STAFF_PDFASSIGNMENT);
                    i.putExtra("FILEPATH",strPDfFilePath);
                    i.putExtra("TITLE",edtitle.getText().toString());
                    i.putExtra("CONTENT","");
                    i.putExtra("DATE",strDate);

                    startActivity(i);
                }
            }
        });


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

         lblChooseGallery.setVisibility(View.GONE);
        lnrGalleryOrCamera.setVisibility(View.GONE);
        lblGallery.setVisibility(View.GONE);
        lblCamera.setVisibility(View.GONE);
        lblChoosePdfFile.setOnClickListener(new View.OnClickListener(){
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2) {

            try {
                if (data != null && resultCode == RESULT_OK) {

                    String pdfsize = TeacherUtil_SharedPreference.getPdfsize(PdfAssignmentActivity.this);
                    long sizekb = (1024 * 1024) * Integer.parseInt(pdfsize);
                    Uri uri = data.getData();

                    File outputDir = PdfAssignmentActivity.this.getCacheDir(); // context being the Activity pointer
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
                        btnNext.setEnabled(true);
                        lblClickImage.setVisibility(View.GONE);


                        imagePathList.add(outputFile.getPath());
                        lblPdfFileName.setText(outputFile.getPath());
                        strPDfFilePath = outputFile.getPath();
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(PdfAssignmentActivity.this);
                        alert(filecontent);
                    }
                }
            }
            catch (Exception e){
                alert(getResources().getString(R.string.Please_choose_pdf_file_send));
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
    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PdfAssignmentActivity.this);


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
    private void datePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener(PdfAssignmentActivity.this)
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
