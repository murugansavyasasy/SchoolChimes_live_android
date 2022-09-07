package com.vs.schoolmessenger.assignment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> {

    private List<Studentclass> dateList;
    Context context;
    RefreshInterface listener;
    String assignmentid;
    PopupWindow popupWindow;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_assignment_adpt, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Studentclass assignment = dateList.get(position);
        holder.lblstudent.setText(assignment.getStudentname());
        holder.lblstatus.setText(assignment.getMessage());
        holder.lblsection.setText(assignment.getStandard()+"-"+assignment.getSection());

        if(holder.lblstatus.getText().toString().equals("Non Submitted")){
            holder.btnView.setVisibility(View.GONE);
            holder.lblstatus.setTextColor(context.getResources().getColor(R.color.clr_red));
        }
        else{
            holder.btnView.setVisibility(View.VISIBLE);
        }
        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("enter","enter");
                showFilePickPopup(assignmentid, assignment.getStudentid(),"1",assignment.getIs_Archive());
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblstudent,lblstatus,lblsection;
        Button btnView;

        public MyViewHolder(View view) {
            super(view);

            lblstudent = (TextView) view.findViewById(R.id.lblstudent);
            lblstatus = (TextView) view.findViewById(R.id.lblstatus);
            lblsection = (TextView) view.findViewById(R.id.lblsection);
            btnView = (Button) view.findViewById(R.id.btnView);

        }
    }

    public StudentListAdapter(Context context, List<Studentclass> dateList,String assignmentid) {
        this.context = context;
        this.dateList = dateList;
        this.assignmentid = assignmentid;
    }
    private void showFilePickPopup(final String assignid, final String userid, final String type,final Boolean archive) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.choose_file_popup_sngpr, null);
        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(layout);
        Log.d("enterpop","enterpop");
        TextView lblChooseGallery = (TextView) layout.findViewById(R.id.lblChooseGallery);
        TextView lblChoosePdfFile = (TextView) layout.findViewById(R.id.lblChoosePdfFile);

        final LinearLayout lnrGalleryOrCamera = (LinearLayout) layout.findViewById(R.id.lnrGalleryOrCamera);
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
                intent.putExtra("USER_ID",userid);
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
                i.putExtra("USER_ID",userid);
                i.putExtra("USER_TYPE","");
                i.putExtra("FileType","PDF");
                i.putExtra("is_Archive",archive);
                context.startActivity(i);
                popupWindow.dismiss();
            }
        });

    }
}
