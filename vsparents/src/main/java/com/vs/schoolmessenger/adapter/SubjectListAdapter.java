package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherEventsScreen;
import com.vs.schoolmessenger.interfaces.SubjecstListener;
import com.vs.schoolmessenger.model.DatePickerFragment;
import com.vs.schoolmessenger.model.SubjectDetails;
import com.vs.schoolmessenger.model.TeacherSubjectModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.vs.schoolmessenger.activity.SubjectListScreen.SubjectDetailsList;

//import android.support.v4.app.FragmentManager;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.MyViewHolder> {

    private List<TeacherSubjectModel> lib_list;
    Context context;

    int selDay, selMonth, selYear;
    String[] session = new String[]{
            "FN",
            "AN"
    };

    int mYear, mMonth, mDay;

    Map<Integer, Integer> mSpinnerSelectedItem = new HashMap<Integer, Integer>();

    boolean checked = false;

    SubjectDetails data;
    String value;
    private SubjecstListener onCheckStudentListener;

    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ExamName, ExamDate, lblTime,mark,txtSession,txtDate;
        EditText MaxMark,txtExamSyllabus;
        Spinner SpinnerSession;
        LinearLayout rytDate;
        CheckBox Exam_check;

        Button AddDetails, btnRemove;
        ImageView imgCalendar;

        public RelativeLayout rytExams, SelectCalendar;


        public MyViewHolder(View view) {
            super(view);


            ExamName = (TextView) view.findViewById(R.id.ExamName);
            rytDate = (LinearLayout) view.findViewById(R.id.rytDate);
            SpinnerSession = (Spinner) view.findViewById(R.id.SpinnerSession);
            Exam_check = (CheckBox) view.findViewById(R.id.Exam_check);
            MaxMark = (EditText) view.findViewById(R.id.MaxMark);
            txtExamSyllabus = (EditText) view.findViewById(R.id.txtExamSyllabus);
            mark = (TextView) view.findViewById(R.id.mark);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtSession = (TextView) view.findViewById(R.id.txtSession);
            ExamDate = (TextView) view.findViewById(R.id.ExamDate);
            AddDetails = (Button) view.findViewById(R.id.AddDetails);
            btnRemove = (Button) view.findViewById(R.id.btnRemove);
            SelectCalendar = (RelativeLayout) view.findViewById(R.id.SelectCalendar);
            imgCalendar = (ImageView) view.findViewById(R.id.imgCalendar);
            lblTime = (TextView) view.findViewById(R.id.lblTime);

        }

        public void bind(TeacherSubjectModel studentsModel) {
        }
    }

    public SubjectListAdapter(Context context, SubjecstListener onCheckListener, List<TeacherSubjectModel> lib_list) {
        this.lib_list = lib_list;
        this.context = context;
        this.onCheckStudentListener = onCheckListener;

    }

    @Override
    public SubjectListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_date_list, parent, false);
        return new SubjectListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SubjectListAdapter.MyViewHolder holder, final int position) {

        holder.bind(lib_list.get(position));

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final TeacherSubjectModel exam = lib_list.get(position);

        holder.ExamName.setText(exam.getStrSubName());


        holder.Exam_check.setOnCheckedChangeListener(null);

        holder.Exam_check.setChecked(exam.getSelectedStatus());
        holder.Exam_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exam.setSelectedStatus(isChecked);
                if (isChecked) {

                    holder.mark.setText(context.getResources().getString(R.string.enter_maximum_mark));
                    holder.txtSession.setText(context.getResources().getString(R.string.select_session));
                    holder.ExamDate.setText(context.getResources().getString(R.string.select_date));
                    holder.txtDate.setVisibility(View.GONE);

                    onCheckStudentListener.student_addClass(exam);
                    holder.rytDate.setVisibility(View.VISIBLE);
                    holder.btnRemove.setVisibility(View.GONE);

                    holder.imgCalendar.setVisibility(View.VISIBLE);
                    holder.AddDetails.setVisibility(View.VISIBLE);
                    holder.MaxMark.setEnabled(true);
                    holder.MaxMark.setText("");
                    holder.rytDate.setBackgroundColor(Color.parseColor("#D8D8D8"));
                    holder.MaxMark.setBackgroundColor(Color.parseColor("#FFFFFF"));

                } else {
                    onCheckStudentListener.student_removeClass(exam);
                    holder.rytDate.setVisibility(View.GONE);

                    holder.MaxMark.setEnabled(false);
                    holder.rytDate.setBackgroundColor(Color.parseColor("#FFA07A"));
                    holder.MaxMark.setBackgroundColor(Color.parseColor("#FFA07A"));


                    for (int i = 0; i < SubjectDetailsList.size(); i++) {
                        final SubjectDetails exam1 = SubjectDetailsList.get(i);
                        String subcode = exam1.getStrSubCode();
                        if (exam.getStrSubCode() == subcode) {
                            SubjectDetailsList.remove(i);
                        }
                    }

                }

            }
        });
        holder.MaxMark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                value = holder.MaxMark.getText().toString();

                Log.d("selectedSpinne11", value);


            }
        });



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, session);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.SpinnerSession.setAdapter(dataAdapter);

        holder.SpinnerSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int selected, final long id) {
                mSpinnerSelectedItem.put(position, selected);
            }
            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });
        if (mSpinnerSelectedItem.containsKey(position)) {
            holder.SpinnerSession.setSelection(mSpinnerSelectedItem.get(position));
        }

        holder.lblTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        holder.lblTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        holder.SelectCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);


                final DatePickerDialog mDatePicker = new DatePickerDialog(context,R.style.DialogThemesender,new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("ResourceAsColor")
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yyyy"; //Change as you need
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                        holder.txtDate.setVisibility(View.VISIBLE);
                        holder.txtDate.setText(sdf.format(myCalendar.getTime()));
                        holder.ExamDate.setText("Date");

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });







        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.rytDate.setVisibility(View.GONE);
                holder.Exam_check.setChecked(false);

                for (int i = 0; i < SubjectDetailsList.size(); i++) {
                    final SubjectDetails exam1 = SubjectDetailsList.get(i);
                    String subcode = exam1.getStrSubCode();
                    if (exam.getStrSubCode() == subcode) {
                        SubjectDetailsList.remove(i);
                    }
                }

            }
        });

        holder.AddDetails.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                String mark = holder.MaxMark.getText().toString();
                if (mark.equals("") && holder.ExamDate.getText().toString().equals("Select Date")) {
                    showToast("Please fill the details");
                } else if (mark.equals("")) {
                    showToast("Please enter the mark");
                } else if (holder.ExamDate.getText().toString().equals("Select Date")) {
                    showToast("Please select the date");
                }

                else {

                    holder.mark.setText("Maximum mark");
                    holder.txtSession.setText("Session");
                    holder.ExamDate.setText("Date");

                    holder.btnRemove.setVisibility(View.VISIBLE);
                    holder.AddDetails.setVisibility(View.GONE);
                    holder.imgCalendar.setVisibility(View.GONE);
                    holder.MaxMark.setEnabled(false);
                    holder.rytDate.setBackgroundColor(Color.parseColor("#FFA07A"));
                    holder.MaxMark.setBackgroundColor(Color.parseColor("#FFA07A"));

                    String selectedSpinner = holder.SpinnerSession.getSelectedItem().toString();
                    for (int i = 0; i < SubjectDetailsList.size(); i++) {
                        final SubjectDetails exam1 = SubjectDetailsList.get(i);
                        String subcode = exam1.getStrSubCode();
                        if (exam.getStrSubCode() == subcode) {
                            SubjectDetailsList.remove(i);
                        }
                    }
                    data = new SubjectDetails(exam.getStrSubName(), exam.getStrSubCode(), holder.txtDate.getText().toString(), holder.MaxMark.getText().toString(), selectedSpinner, true,holder.txtExamSyllabus.getText().toString());
                    SubjectDetailsList.add(data);
                }

            }
        });

    }

    private void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return lib_list.size();
    }


}


