package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherEventsScreen;
import com.vs.schoolmessenger.app.LocaleHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.Constants.category_id;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXTASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.maxGeneralSMSCount;

public class MessageAssignmentActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {
    Button btnRecipients;
    ImageView imgBack;
    EditText edTitle, edMessge;
    TextView tvcount, tvtotcount, tvheader,tvDate;
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
        setContentView(R.layout.activity_message_assignment);
        imgBack = findViewById(R.id.genTextPopup_ToolBarIvBack);
        edTitle = findViewById(R.id.genText_txtTitle);
        edMessge = findViewById(R.id.genText_txtmessage);
        btnRecipients = findViewById(R.id.genText_btnmsg);
        tvcount = (TextView) findViewById(R.id.genText_msgcount);
        tvtotcount = (TextView) findViewById(R.id.genText_count);
        edTitle.addTextChangedListener(mTextEditorWatcher);
        edMessge.addTextChangedListener(mTextEditorWatcher);
        tvDate = (TextView) findViewById(R.id.lblDate);
        edTitle.setText("");
        edMessge.setText("");

        staff_category_spin = (Spinner) findViewById(R.id.staff_category_spin);
        listCategory.clear();
        listCategory.add("GENERAL");
        listCategory.add("CLASS WORK");
        listCategory.add("PROJECT");
        listCategory.add("RESEARCH PAPER");
        cateAdapter = new ArrayAdapter<>(MessageAssignmentActivity.this, R.layout.teacher_spin_title, listCategory);
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
        //edMessge.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxGeneralSMSCount)});
        edMessge.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.genText_txtmessage) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;

            }
        });

        setMinDateTime();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnRecipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edTitle.getText().toString().isEmpty()){
                    alert(getResources().getString(R.string.Please_enter_assignment_title));
                }
                else {
                    Intent i = new Intent(MessageAssignmentActivity.this, RecipientAssignmentActivity.class);
                    i.putExtra("REQUEST_CODE", STAFF_TEXTASSIGNMENT);
                    i.putExtra("FILEPATH", "");
                    i.putExtra("TITLE", edTitle.getText().toString());
                    i.putExtra("CONTENT", edMessge.getText().toString());
                    i.putExtra("DATE", strDate);
                    startActivity(i);
                }
            }
        });

    }

    private void datePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener(MessageAssignmentActivity.this)
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

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //tvcount.setText("" + (maxGeneralSMSCount - (s.length())));

        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };

    public void enableSubmitIfReady() {

        boolean isReady = edMessge.getText().toString().length() > 0;
//        boolean istitleReady = edTitle.getText().toString().length() > 0;

//        if(isReady && istitleReady){
        if(isReady){
            btnRecipients.setEnabled(true);
        }
        else{
            btnRecipients.setEnabled(false);
        }

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


//        tvTime.setText(timeFormater(minimumHour, minimumMinute));

    }
    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selDay = dayOfMonth;
        selMonth = monthOfYear;
        selYear = year;

        tvDate.setText(dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd MMM yyyy"));
        strDate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd/MM/yyyy");
    }

    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MessageAssignmentActivity.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();
    }
}
