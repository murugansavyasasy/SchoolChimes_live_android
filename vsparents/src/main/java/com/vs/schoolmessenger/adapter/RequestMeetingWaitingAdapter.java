package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.StudentSelectAssignment;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.RequestMeetingForParentModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class RequestMeetingWaitingAdapter extends RecyclerView.Adapter<RequestMeetingWaitingAdapter.MyViewHolder> {


    private List<RequestMeetingForParentModel> lib_list;
    Context context;
    private PopupWindow pwindow;
    int mYear, mMonth, mDay;



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

        public TextView lblApprovalStatus, lblStaffName,lblRequestDate,lblRequestedFor,lblStaffComments,lblStaffCommentsLabel,
                lblSheduledTime,lblClassName;
        public ImageView imgApprovalStatus;
        public LinearLayout lnrAprovalStatus,lnrStaffName, lnrRequestDate, lnrRequestedFor, lnrStaffComments, lnrDateTime,
                lnrClass,lnrApproveAndReject;
        public Button btnApprove,btnReject;




        public MyViewHolder(View view) {
            super(view);

            lblApprovalStatus = (TextView) view.findViewById(R.id.lblApprovalStatus);
            lblStaffName = (TextView) view.findViewById(R.id.lblStaffName);
            lblRequestDate = (TextView) view.findViewById(R.id.lblRequestDate);
            lblRequestedFor = (TextView) view.findViewById(R.id.lblRequestedFor);
            lblStaffComments = (TextView) view.findViewById(R.id.lblStaffComments);
            lblStaffCommentsLabel = (TextView) view.findViewById(R.id.lblStaffCommentsLabel);
            lblSheduledTime = (TextView) view.findViewById(R.id.lblSheduledTime);
            lblClassName = (TextView) view.findViewById(R.id.lblClassName);


            lnrAprovalStatus = (LinearLayout) view.findViewById(R.id.lnrAprovalStatus);
            lnrStaffName = (LinearLayout) view.findViewById(R.id.lnrStaffName);
            lnrRequestDate = (LinearLayout) view.findViewById(R.id.lnrRequestDate);
            lnrRequestedFor = (LinearLayout) view.findViewById(R.id.lnrRequestedFor);
            lnrStaffComments = (LinearLayout) view.findViewById(R.id.lnrStaffComments);
            lnrDateTime = (LinearLayout) view.findViewById(R.id.lnrDateTime);
            lnrClass = (LinearLayout) view.findViewById(R.id.lnrClass);
            lnrApproveAndReject = (LinearLayout) view.findViewById(R.id.lnrApproveAndReject);

            btnApprove = (Button) view.findViewById(R.id.btnApprove);
            btnReject = (Button) view.findViewById(R.id.btnReject);

            imgApprovalStatus = (ImageView) view.findViewById(R.id.imgApprovalStatus);




        }
    }

    public RequestMeetingWaitingAdapter(List<RequestMeetingForParentModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public RequestMeetingWaitingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_meeting_school_list, parent, false);
        return new RequestMeetingWaitingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RequestMeetingWaitingAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));

        final RequestMeetingForParentModel history = lib_list.get(position);


        if (history.getApprovalStatus().equals("0")) {


            holder.lnrApproveAndReject.setVisibility(View.VISIBLE);

            holder.lblStaffName.setText(": "+history.getStaffName());
            holder.lblRequestDate.setText(": "+history.getRequestedON());
            holder.lblRequestedFor.setText(": "+history.getParentComment());
            holder.lblStaffComments.setText(": "+history.getStaffComment());
            holder.lblSheduledTime.setText(": "+history.getScheduleDate()+"/"+history.getScheduleTime());
            holder.lblClassName.setText(": "+history.getCLS());

            holder.lnrStaffComments.setVisibility(View.GONE);
            holder.lnrDateTime.setVisibility(View.GONE);

            holder.lnrAprovalStatus.setBackgroundColor(Color.parseColor("#B9B9B9"));
            holder.imgApprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.waiting_tick));

            holder.lblApprovalStatus.setText(R.string.mt_waiting_for);
            holder.lblApprovalStatus.setTextColor(Color.parseColor("#000000"));

        } else if (history.getApprovalStatus().equals("1")) {

            holder.lblStaffName.setText(history.getStaffName());
            holder.lblRequestDate.setText(history.getRequestedON());
            holder.lblRequestedFor.setText(history.getParentComment());
            holder.lblStaffComments.setText(history.getStaffComment());
            holder.lblSheduledTime.setText(history.getScheduleDate()+"/"+history.getScheduleTime());
            holder.lblClassName.setText(history.getCLS());

            holder.lnrAprovalStatus.setBackgroundColor(Color.parseColor("#24BB59"));
            holder.imgApprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.green_tick));
            holder.lblApprovalStatus.setText(R.string.mt_approved);

        } else if (history.getApprovalStatus().equals("2")) {

            holder.lblStaffName.setText(history.getStaffName());
            holder.lblRequestDate.setText(history.getRequestedON());
            holder.lblRequestedFor.setText(history.getParentComment());
            holder.lblStaffComments.setText(history.getStaffComment());
            holder.lblSheduledTime.setText(history.getScheduleDate()+"/"+history.getScheduleTime());
            holder.lblClassName.setText(history.getCLS());

            holder.lblRequestDate.setVisibility(View.GONE);
            holder.lnrDateTime.setVisibility(View.GONE);

            holder.lblStaffCommentsLabel.setText(R.string.mt_rejected_reason);

            holder.imgApprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.wrong_tick));
            holder.lnrAprovalStatus.setBackgroundColor(Color.parseColor("#E34545"));

            holder.lblApprovalStatus.setText(R.string.mt_rejected);

        }

        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showApprovePopup(history);
                pwindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showRejectPopup(history);
                pwindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);


            }
        });




    }

    private void showRejectPopup(final RequestMeetingForParentModel history) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.request_meeting_reject_popup, null);

        pwindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pwindow.setContentView(layout);

        final EditText txtComments = (EditText) layout.findViewById(R.id.txtComments);
        Button btnReject = (Button) layout.findViewById(R.id.btnReject);
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindow.dismiss();
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comments=txtComments.getText().toString();
                String reqID=history.getRequestId();
                if(!comments.equals("")){
                    meetingApproveRejectApi(comments,"","",reqID,"decline");
                }
                else {
                    Toast.makeText(context, "Please enter the reason", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showApprovePopup(final RequestMeetingForParentModel history) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.request_meeting_approve_popup, null);

        pwindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pwindow.setContentView(layout);

        final TextView lblDate = (TextView) layout.findViewById(R.id.lblDate);
        final TextView lblTime = (TextView) layout.findViewById(R.id.lblTime);
        LinearLayout lnrDate = (LinearLayout) layout.findViewById(R.id.lnrDate);
        LinearLayout lnrTime = (LinearLayout) layout.findViewById(R.id.lnrTime);
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);

        final EditText txtComments = (EditText) layout.findViewById(R.id.txtComments);
        Button btnApprove = (Button) layout.findViewById(R.id.btnApprove);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindow.dismiss();
            }
        });

        lnrDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                // To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("ResourceAsColor")
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yyyy"; //Change as you need
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);


                        lblDate.setText(sdf.format(myCalendar.getTime()));


                        // holder.ExamDate.setText(sdf.format(myCalendar.getTime()));

                        Log.d("date", sdf.format(myCalendar.getTime()));

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);

                mDatePicker.show();

            }
        });

        lnrTime.setOnClickListener(new View.OnClickListener() {
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
                        lblTime.setText(selectedHour + ":" + selectedMinute);

                        Log.d("Time", selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comments=txtComments.getText().toString();
                String date=lblDate.getText().toString();
                String time=lblTime.getText().toString();
                String reqID=history.getRequestId();

                if(!comments.equals("")&&!date.equals("")&&!time.equals("")){
                    meetingApproveRejectApi(comments,date,time,reqID,"approve");
                }
                else {
                    Toast.makeText(context, "Please fill all the details", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void meetingApproveRejectApi(String comments,String date,String time,String reqid,String processtype) {
        String baseURL= TeacherUtil_SharedPreference.getBaseUrlContext(context);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        String staffid=  TeacherUtil_Common.Principal_staffId;

        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("RequestId", reqid);
        jsonObject.addProperty("StaffComment", comments);
        jsonObject.addProperty("SchedueDate", date);
        jsonObject.addProperty("ScheduleTime", time);
        jsonObject.addProperty("ProcessBy", staffid);
        jsonObject.addProperty("ProcessType", processtype);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.ManageParentTeacherMeetingRequests(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);

                            String Status = jsonObject.getString("Status");
                            String mesaage = jsonObject.getString("Message");

                            if (Status.equals("1")) {
                                showRecordsFound(mesaage,"1");
                            } else {
                                showRecordsFound(mesaage,"");
                            }
                        }


                    } else {
                        //showToast("Server Response Failed");
                        Toast.makeText(context, R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                // showToast("Server Connection Failed");
                Toast.makeText(context, R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showRecordsFound(String mesaage, final String s) {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(mesaage);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(s.equals("1")) {
                    dialog.cancel();
                    pwindow.dismiss();
                }
                else {
                    dialog.cancel();

                }

                    }
        });

        AlertDialog dialog = alertDialog.create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(context.getResources().getColor(R.color.teacher_colorPrimary));


    }


    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}

