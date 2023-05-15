package com.vs.schoolmessenger.LessonPlan.Adapter;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.EditDataList;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.vs.schoolmessenger.LessonPlan.Model.EditDataItem;
import com.vs.schoolmessenger.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EditViewAdapter extends RecyclerView.Adapter<EditViewAdapter.MyViewHolder> {

    private List<EditDataItem> dateList;
    Context context;

    String selectedSpinnerValue;

    DatePickerDialog datePickerDialog;

    @Override
    public EditViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_edit_lesson_list, parent, false);

        return new EditViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EditViewAdapter.MyViewHolder holder, int position) {

        final EditDataItem data = dateList.get(position);
        String field_type = data.getFieldType();

        holder.lblTextName.setTypeface(holder.lblTextName.getTypeface(), Typeface.BOLD);
        holder.lblDropDownName.setTypeface(holder.lblDropDownName.getTypeface(), Typeface.BOLD);
        holder.lblCalendarName.setTypeface(holder.lblCalendarName.getTypeface(), Typeface.BOLD);

        if(field_type.equals("text")){
            holder.lnrText.setVisibility(View.VISIBLE);
            holder.lnrDropDown.setVisibility(View.GONE);
            holder.lnrCalendar.setVisibility(View.GONE);

            holder.lblTextName.setText(data.getName());
            holder.txtTextValue.setText(data.getValue());

            if(data.getIsdisable() == 1){
              holder.txtTextValue.setEnabled(false);
            }
            else {
                holder.txtTextValue.setEnabled(true);
            }

            holder.txtTextValue.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {

                    String  editable_value = arg0.toString();
                    int index = EditDataList.indexOf(data);
                    EditDataItem data = EditDataList.get(index);
                    data.setName(data.getName());
                    data.setValue(editable_value);
                }
            });
        }
        else if(field_type.equals("dropdown")){
            holder.lnrText.setVisibility(View.GONE);
            holder.lnrDropDown.setVisibility(View.VISIBLE);
            holder.lnrCalendar.setVisibility(View.GONE);
            holder.lblDropDownName.setText(data.getName());

            String[] values = new String[data.getFieldData().size()+1];
            values[0] = data.getValue();
            for(int i=0; i< data.getFieldData().size();i++){
                values[i+1] = data.getFieldData().get(i).getValue();
            }

            ArrayAdapter adapter
                    = new ArrayAdapter(
                    context,
                    R.layout.text_spinner,R.id.text1,
                    values);

            holder.spinners.setAdapter(adapter);

            if(data.getIsdisable() == 0) {
                holder.spinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                        if(position != 0) {
                            selectedSpinnerValue = data.getFieldData().get(position - 1).getValue();
                        }
                        else {
                            selectedSpinnerValue = data.getValue();
                        }

                            int index = EditDataList.indexOf(data);
                            EditDataItem data = EditDataList.get(index);
                            data.setName(data.getName());
                            data.setValue(selectedSpinnerValue);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

        }
        else if(field_type.equals("datepicker")){
            holder.lnrText.setVisibility(View.GONE);
            holder.lnrDropDown.setVisibility(View.GONE);
            holder.lnrCalendar.setVisibility(View.VISIBLE);

            holder.lblCalendarName.setText(data.getName());
            holder.lblCalendarValue.setText(data.getValue());
        }


        holder.rytCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(data.getIsdisable() == 0){
                   openCalendar(holder.lblCalendarValue,data);
                }
            }
        });
    }

    private void openCalendar(TextView lblCalendarValue, EditDataItem data) {

        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(context, R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String dateString = format.format(calendar.getTime());
                        lblCalendarValue.setText(dateString);

                        int index = EditDataList.indexOf(data);
                        EditDataItem data = EditDataList.get(index);
                        data.setName(data.getName());
                        data.setValue(dateString);

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView lblTextName, lblCalendarName, lblDropDownName,lblCalendarValue;

        public RelativeLayout rytSpinner,rytCalendar;

        public EditText txtTextValue;

        public Spinner spinners;

        public LinearLayout lnrDropDown,lnrCalendar,lnrText;
        public MyViewHolder(View view) {
            super(view);

            lnrDropDown = (LinearLayout) view.findViewById(R.id.lnrDropDown);
            lnrCalendar = (LinearLayout) view.findViewById(R.id.lnrCalendar);
            lnrText = (LinearLayout) view.findViewById(R.id.lnrText);

            spinners = (Spinner) view.findViewById(R.id.spinners);

            txtTextValue = (EditText) view.findViewById(R.id.txtTextValue);

            lblTextName = (TextView) view.findViewById(R.id.lblTextName);
            lblCalendarName = (TextView) view.findViewById(R.id.lblCalendarName);
            lblDropDownName = (TextView) view.findViewById(R.id.lblDropDownName);
            lblCalendarValue = (TextView) view.findViewById(R.id.lblCalendarValue);

            rytSpinner = (RelativeLayout) view.findViewById(R.id.rytSpinner);
            rytCalendar = (RelativeLayout) view.findViewById(R.id.rytCalendar);

        }

    }

    public EditViewAdapter(List<EditDataItem> dateList, Context context) {
        this.context = context;
        this.dateList = dateList;
    }

    public void clearAllData() {
        int size = this.dateList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.dateList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void updateList(List<EditDataItem> temp) {
        dateList = temp;
        notifyDataSetChanged();
    }
}
