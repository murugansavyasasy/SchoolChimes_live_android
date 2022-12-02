package com.vs.schoolmessenger.assignment;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
        import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.AlbumSelectActivity;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXTASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICEASSIGNMENT;

public class AssignmentViewAdapter extends RecyclerView.Adapter<AssignmentViewAdapter.MyViewHolder> {

    private List<AssignmentViewClass> dateList;
    Context context;
    RefreshInterface listener;
    String parent,userType,enddatealert;
    PopupWindow popupWindow;
    String strDate, strCurrentDate, timeString, strTime;//strDuration
    int selDay, selMonth, selYear;
    String selHour, selMin;
    int minimumHour, minimumMinute;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignmentviewadpt, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final AssignmentViewClass assignment = dateList.get(position);
        holder.lblTextTitle.setText(assignment.getTitle());
        holder.lblmsgtype.setText(assignment.getType());
        holder.lbltime.setText(assignment.getTime());
        holder.lbldate.setText(assignment.getDate());
        holder.lblsubject.setText(assignment.getContent());
        holder.lblenddate.setText(assignment.getEnddate());
        holder.lblSubCount.setText(assignment.getSubmittedCount());
        holder.lblTotalcount.setText(assignment.getTotalcount());
        holder.lblSent.setText(assignment.getTotalcount());
        holder.lblCategory.setText(assignment.getCategory());

        strDate = dateFormater(System.currentTimeMillis(), "dd/MM/yyyy");
        strCurrentDate = strDate;
        Log.d("date",strCurrentDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        selDay = cal.get(Calendar.DAY_OF_MONTH);
        selMonth = cal.get(Calendar.MONTH);// - 1;
        selYear = cal.get(Calendar.YEAR);

        minimumHour = cal.get(Calendar.HOUR_OF_DAY);
        minimumMinute = cal.get(Calendar.MINUTE);
        selHour = Integer.toString(minimumHour);
        selMin = Integer.toString(minimumMinute);


        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        try {
            Date date = sdf.parse(strCurrentDate);
            Date date1 = sdf.parse(assignment.getEnddate());

           long difference = date1.getTime() - date.getTime();
           long difftDays = difference / (24 * 60 * 60 * 1000);
            Log.i("Testing", "days" + difftDays);

            if(difftDays >=0){
                enddatealert="0";
                holder.lblenddate.setTextColor(context.getResources().getColor(R.color.clr_black));
                holder.sample.setText(enddatealert);
            }
            else{
                enddatealert="1";
                holder.lblenddate.setTextColor(context.getResources().getColor(R.color.clr_reject_red));
                holder.sample.setText(enddatealert);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        if(parent.equals("1")){
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnForward.setVisibility(View.GONE);
            holder.btnTotal.setVisibility(View.GONE);
            holder.btnSubmit.setVisibility(View.VISIBLE);
            holder.lnrSubmissionCount.setVisibility(View.VISIBLE);
            holder.lnrSent.setVisibility(View.VISIBLE);
            holder.lnrTotalCount.setVisibility(View.GONE);
            userType="parent";
            if(assignment.getSubmittedCount().equals("0")){
                holder.btnSubmission.setVisibility(View.GONE);
            }
            else{
                holder.btnSubmission.setVisibility(View.VISIBLE);
            }
        }
        else{
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnForward.setVisibility(View.VISIBLE);
            holder.btnTotal.setVisibility(View.VISIBLE);
            holder.btnSubmit.setVisibility(View.GONE);
            holder.lnrSubmissionCount.setVisibility(View.VISIBLE);
            holder.lnrTotalCount.setVisibility(View.VISIBLE);
            holder.lnrSent.setVisibility(View.GONE);
            userType="staff";
        }
        if(parent.equals("1")&& assignment.getIsAppread().equals("0")){
            holder.lblNew.setVisibility(View.VISIBLE);
        }
        else{
            holder.lblNew.setVisibility(View.GONE);
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertassign("Are you sure you want to Delete Assignment ?",assignment.getAssignmentId(),assignment.getIs_Archive());
            }

        });

        holder.btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,RecipientAssignmentActivity.class);
                i.putExtra("ID",assignment.getAssignmentId());
                context.startActivity(i);
            }
        });



        holder.btnSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parent.equals("1")) {
                    Log.d("enter","enter");
                    showFilePickPopup(assignment.getAssignmentId(), userType, assignment.getType(),assignment.getIs_Archive());
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                else{
                    Intent i= new Intent(context,StudentSubmissionListActivity.class);
                    i.putExtra("ID",assignment.getAssignmentId());
                    i.putExtra("TYPE","SUBMITTED");
                    i.putExtra("USER_TYPE",userType);
                    i.putExtra("is_Archive",assignment.getIs_Archive());
                    context.startActivity(i);
                }
            }
        });

        holder.btnTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(context,StudentSubmissionListActivity.class);
                i.putExtra("ID",assignment.getAssignmentId());
                i.putExtra("TYPE","TOTAL");
                i.putExtra("USER_TYPE",userType);
                i.putExtra("is_Archive",assignment.getIs_Archive());
                context.startActivity(i);
            }
        });

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(assignment.getType().equals("PDF")){
                    Intent i= new Intent(context,PdfListAcivity.class);
                    i.putExtra("ID",assignment.getAssignmentId());
                    i.putExtra("TYPE","0");
                    i.putExtra("USER_TYPE",userType);
                    i.putExtra("FileType","");
                    i.putExtra("isappread",assignment.getIsAppread());
                    i.putExtra("date",assignment.getDate());
                    i.putExtra("detailid",assignment.getDeTailId());
                    i.putExtra("is_Archive",assignment.getIs_Archive());
                    context.startActivity(i);
                }
                else {
                    Intent intent = new Intent(context, ViewTypeActivity.class);
                    intent.putExtra("assignment", assignment.getType());
                    intent.putExtra("ID", assignment.getAssignmentId());
                    intent.putExtra("TYPE", "0");
                    intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("FileType", "");
                    intent.putExtra("isappread", assignment.getIsAppread());
                    intent.putExtra("date", assignment.getDate());
                    intent.putExtra("detailid", assignment.getDeTailId());
                    intent.putExtra("is_Archive",assignment.getIs_Archive());
                    context.startActivity(intent);
                }
            }
        });
        holder.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(context,ParentSubmitActivity.class);
                i.putExtra("ID",assignment.getAssignmentId());
                i.putExtra("ENDDATE",holder.sample.getText().toString());
                i.putExtra("is_Archive",assignment.getIs_Archive());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public void updateList(List<AssignmentViewClass> temp) {
        dateList = temp;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sample,lblNew,lblTextTitle,lbldate,lbltime,lblmsgtype,lblsubject,lblenddate,lblSubCount,lblTotalcount,lblSent;
        public Button btnView,btnForward,btnDelete,btnSubmission,btnTotal,btnSubmit;
        LinearLayout lnrSubmissionCount,lnrTotalCount,lnrSent;
        RelativeLayout rytOverallCircular;
        TextView lblCategory;

        public MyViewHolder(View view) {
            super(view);

            lnrTotalCount = (LinearLayout) view.findViewById(R.id.lnrTotalCount);
            lnrSent = (LinearLayout) view.findViewById(R.id.lnrSentby);
            lnrSubmissionCount = (LinearLayout) view.findViewById(R.id.lnrSubmissionCount);
            lblTextTitle = (TextView) view.findViewById(R.id.lblTextTitle);
            sample = (TextView) view.findViewById(R.id.sample);
            lbldate = (TextView) view.findViewById(R.id.txt_date);
            lbltime = (TextView) view.findViewById(R.id.txt_time);
            lblmsgtype= (TextView) view.findViewById(R.id.txt_type);
            lblsubject = (TextView) view.findViewById(R.id.lblTextType);
            lblenddate = (TextView) view.findViewById(R.id.lblDate);
            lblSubCount = (TextView) view.findViewById(R.id.lblSubCount);
            lblTotalcount = (TextView) view.findViewById(R.id.lblTotalcount);
            lblSent = (TextView) view.findViewById(R.id.lblSent);
            lblNew = (TextView) view.findViewById(R.id.lblNew);
            lblCategory = (TextView) view.findViewById(R.id.lblCategory);
            btnView = (Button) view.findViewById(R.id.btnView);
            btnForward = (Button) view.findViewById(R.id.btnForward);
            btnDelete = (Button) view.findViewById(R.id.btnDelete);
            btnSubmission = (Button) view.findViewById(R.id.btnSubmission);
            btnTotal = (Button) view.findViewById(R.id.btnTotal);
            btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
            rytOverallCircular = (RelativeLayout) view.findViewById(R.id.rytOverallCircular);
        }
    }

    public AssignmentViewAdapter(Context context, List<AssignmentViewClass> dateList,RefreshInterface listener,String parent) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
        this.parent = parent;
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void DeleteApi(String id,Boolean Archive) {
        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String baseURL=TeacherUtil_SharedPreference.getBaseUrlContext(context);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumber(context);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AssignmentId", id);
        jsonObject.addProperty("ProcessBy", TeacherUtil_Common.Principal_staffId);
        jsonObject.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
        jsonObject.addProperty("MobileNumber", MobileNumber);

        Call<JsonObject> call ;
        if(Archive){
            call = apiService.DeleteAssignmentFromApp_Archive(jsonObject);
        }
        else {
            call = apiService.DeleteAssignmentFromApp(jsonObject);
        }
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                mProgressDialog.dismiss();
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    Log.d("URL", String.valueOf(response.code()));
                    Log.d("response", response.body().toString());

                    JSONObject object = new JSONObject(response.body().toString());
                    int status = object.getInt("result");
                    String message = object.getString("Message");


                    if(status==1){

                        alertdelete(message);
                    }
                    else{
                        alertdelete(message);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressDialog.dismiss();
                showToast(context.getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    public void alertassign(String title, final String id,final Boolean archive) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteApi(id,archive);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();


    }

    private void alertdelete(String msg) {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle("Alert");
        dlg.setMessage(msg);
        dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.delete();
            }
        });
        dlg.setCancelable(false);
        dlg.create();
        dlg.show();
    }

    private void alert(String msg) {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle("Alert");
        dlg.setMessage(msg);
        dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dlg.setCancelable(false);
        dlg.create();
        dlg.show();
    }
    private void showFilePickPopup(final String assignid, final String userid, final String type,final  boolean archive) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.choose_file_popup_sngpr, null);
        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(layout);
          Log.d("enterpop","enterpop");
        TextView lblChooseGallery = (TextView) layout.findViewById(R.id.lblChooseGallery);
        TextView lblChoosePdfFile = (TextView) layout.findViewById(R.id.lblChoosePdfFile);

        final LinearLayout   lnrGalleryOrCamera = (LinearLayout) layout.findViewById(R.id.lnrGalleryOrCamera);
        TextView  lblGallery = (TextView) layout. findViewById(R.id.lblGallery);
        TextView lblCamera = (TextView)  layout.findViewById(R.id.lblCamera);
         lblChooseGallery.setText("Image");
        lblChoosePdfFile.setText("Pdf");
        lblChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewTypeActivity.class);
                intent.putExtra("assignment","IMAGE");
                intent.putExtra("ID",assignid);
                intent.putExtra("TYPE","1");
                intent.putExtra("USER_TYPE",userType);
                intent.putExtra("FileType","IMAGE");
                intent.putExtra("is_Archive",archive);

                context.startActivity(intent);
                popupWindow.dismiss();
            }
        });

        lblChoosePdfFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent i= new Intent(context,PdfListAcivity.class);
                i.putExtra("ID",assignid);
                i.putExtra("TYPE","1");
                i.putExtra("USER_TYPE",userType);
                i.putExtra("FileType","PDF");
                i.putExtra("is_Archive",archive);
                context.startActivity(i);
                popupWindow.dismiss();
            }
        });

    }
    private String dateFormater(long dateInMillis, String formatString) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        String dateString = formatter.format(new Date(dateInMillis));
        Log.d("dateString", dateString);
        return dateString;
    }

}
