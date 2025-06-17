package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.ViewPunchHistoryListener;
import com.vs.schoolmessenger.model.StaffAttendanceBiometricReportRes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceReportsAdapter extends RecyclerView.Adapter<AttendanceReportsAdapter.MyViewHolder> {
    private List<StaffAttendanceBiometricReportRes.BiometriStaffReportData> lib_list;
    Context context;
    String Type;
    private final ViewPunchHistoryListener listener;


    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void updateList(List<StaffAttendanceBiometricReportRes.BiometriStaffReportData> temp) {
        lib_list = temp;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblMonth, lblDate, lblDay, lblName, lblCheckInTime, lblStatus, lblWorkingHours, lblStaffName, lblCheckoutTime;
        public ImageView imgPunchHistory;
        public RelativeLayout rytParentCard;

        public MyViewHolder(View view) {
            super(view);

            lblMonth = (TextView) view.findViewById(R.id.lblMonth);
            lblDate = (TextView) view.findViewById(R.id.lblDate);
            lblDay = (TextView) view.findViewById(R.id.lblDay);
            lblName = (TextView) view.findViewById(R.id.lblName);
            lblStaffName = (TextView) view.findViewById(R.id.lblStaffName);
            lblCheckInTime = (TextView) view.findViewById(R.id.lblCheckInTime);
            lblCheckoutTime = (TextView) view.findViewById(R.id.lblCheckoutTime);
            lblStatus = (TextView) view.findViewById(R.id.lblStatus);
            lblWorkingHours = (TextView) view.findViewById(R.id.lblWorkingHours);
            imgPunchHistory = (ImageView) view.findViewById(R.id.imgPunchHistory);
            rytParentCard = (RelativeLayout) view.findViewById(R.id.rytParentCard);
        }
    }

    public AttendanceReportsAdapter(List<StaffAttendanceBiometricReportRes.BiometriStaffReportData> lib_list, Context context, String type, ViewPunchHistoryListener listener) {
        this.lib_list = lib_list;
        this.context = context;
        this.Type = type;
        this.listener = listener;

    }

    @Override
    public AttendanceReportsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_report_list_item, parent, false);
        return new AttendanceReportsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AttendanceReportsAdapter.MyViewHolder holder, final int position) {

        final StaffAttendanceBiometricReportRes.BiometriStaffReportData holiday = lib_list.get(position);

        holder.lblMonth.setTypeface(null, Typeface.BOLD);
        holder.lblDate.setTypeface(null, Typeface.BOLD);
        holder.lblName.setTypeface(null, Typeface.BOLD);

        if (Type.equals("StaffWise")) {
            holder.lblStaffName.setText(holiday.getStaffName());
            holder.lblStaffName.setTypeface(null, Typeface.BOLD);
            holder.lblStaffName.setVisibility(View.VISIBLE);
        } else {
            holder.lblStaffName.setVisibility(View.GONE);

        }

        extractDateComponents(holiday.getDate(), holder.lblMonth, holder.lblDate, holder.lblDay);
        holder.lblName.setText(holiday.getAttendance_type());
        holder.lblStatus.setText(holiday.getLeave_type());
        holder.lblWorkingHours.setText("Working Hours - " + holiday.getWorking_hours());

        if (!holiday.getIn_time().equals("")) {
            holder.lblCheckInTime.setText("First in - " + holiday.getIn_time());
            holder.lblCheckInTime.setVisibility(View.VISIBLE);
        } else {
            holder.lblCheckInTime.setVisibility(View.GONE);
        }

        if (!holiday.getOut_time().equals("")) {
            holder.lblCheckoutTime.setText("Last out - " + holiday.getOut_time());
            holder.lblCheckoutTime.setVisibility(View.VISIBLE);
        } else {
            holder.lblCheckoutTime.setVisibility(View.GONE);
        }

        if (holiday.getLeave_type().equals("Present")) {
            holder.lblStatus.setBackgroundResource(R.drawable.btn_green);
        } else {
            holder.lblStatus.setBackgroundResource(R.drawable.btn_red);
        }


        holder.rytParentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holiday);
            }
        });
    }

    public void extractDateComponents(String inputDate, TextView lblMonth, TextView lblDate, TextView lblDay) {
        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            // Parse the date string into a Date object
            Date date = dateFormat.parse(inputDate);

            // Create a Calendar object to extract the day, month, and year
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Extract day, month, and year
            int day = calendar.get(Calendar.DAY_OF_WEEK); // Day of the week (e.g., Sunday = 1, Monday = 2, ...)
            int dateOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // Day of the month
            int month = calendar.get(Calendar.MONTH) + 1; // Month (0-based, so add 1)
            int year = calendar.get(Calendar.YEAR); // Year

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

            // Get the day name and month name
            String dayName = dayFormat.format(date);
            String monthName = monthFormat.format(date);

            // Output the values
            System.out.println("Day of the Week: " + day);
            System.out.println("Date of the Month: " + dateOfMonth);
            System.out.println("Month: " + month);
            System.out.println("Year: " + year);

            lblDay.setText(String.valueOf(dayName));
            lblMonth.setText(String.valueOf(monthName));
            lblDate.setText(String.valueOf(dateOfMonth));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return lib_list.size();
    }
}
