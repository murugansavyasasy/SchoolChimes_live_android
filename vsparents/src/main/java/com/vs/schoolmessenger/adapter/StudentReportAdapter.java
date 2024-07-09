package com.vs.schoolmessenger.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.StudentReportModel;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import java.util.List;

public class StudentReportAdapter extends RecyclerView.Adapter<StudentReportAdapter.MyViewHolder> {
    private List<StudentReportModel> lib_list;
    Context context;
    String child_id, schoolid;
    Boolean isPermission = false;

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

        public TextView lblStudentName, lblClassName, lblSectionName, lblAdmissionNo, lblGender, lblDOB, lblMobile,lblClassTeacher,lblFather;
        Button btnCall, btnSendSms;

        public MyViewHolder(View view) {
            super(view);
            lblStudentName = (TextView) view.findViewById(R.id.lblStudentName);
            lblClassName = (TextView) view.findViewById(R.id.lblClassName);
            lblSectionName = (TextView) view.findViewById(R.id.lblSectionName);
            lblAdmissionNo = (TextView) view.findViewById(R.id.lblAdmissionNo);
            lblGender = (TextView) view.findViewById(R.id.lblGender);
            lblDOB = (TextView) view.findViewById(R.id.lblDOB);
            lblMobile = (TextView) view.findViewById(R.id.lblMobile);
            btnCall = view.findViewById(R.id.btnCall);
            btnSendSms = view.findViewById(R.id.btnSendSms);
            lblClassTeacher = view.findViewById(R.id.lblClassTeacher);
            lblFather = view.findViewById(R.id.lblFather);

        }
    }
    public StudentReportAdapter(List<StudentReportModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }
    @Override
    public StudentReportAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_report_list, parent, false);
        return new StudentReportAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StudentReportAdapter.MyViewHolder holder, final int
            position) {
        final StudentReportModel exam = lib_list.get(position);
        child_id = Util_SharedPreference.getChildIdFromSP(context);
        schoolid = Util_SharedPreference.getSchoolIdFromSP(context);
        holder.lblStudentName.setText(exam.getStudentName());
        holder.lblClassName.setText(exam.getClassName());
        holder.lblSectionName.setText(exam.getSectionName());
        holder.lblAdmissionNo.setText(exam.getAdmissionNo());
        holder.lblGender.setText(exam.getGender());
        holder.lblDOB.setText(exam.getDob());
        holder.lblMobile.setText(exam.getPrimaryMobile());
        holder.lblClassTeacher.setText(exam.getClassTeacher());
        holder.lblFather.setText(exam.getFatherName());

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPermissionGranded(exam.getPrimaryMobile())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + exam.getPrimaryMobile()));
                    ((Activity) context).startActivity(intent);

                }

            }
        });
        holder.btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri sms_uri = Uri.parse("smsto:+"+exam.getPrimaryMobile());
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                sms_intent.putExtra("sms_body", "");
                context.startActivity(sms_intent);

            }
        });
    }

    private boolean isPermissionGranded(String number) {

        Dexter.withActivity((Activity) context)
                .withPermissions(
                        Manifest.permission.CALL_PHONE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            isPermission = true;
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + number));
                            ((Activity) context).startActivity(intent);
                        }
                        else {
                            isPermissionGranded(number);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(context, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

        return isPermission;
    }

    @Override
    public int getItemCount() {
        return lib_list.size();
    }

    public void updateList(List<StudentReportModel> temp) {
        lib_list = temp;
        notifyDataSetChanged();
    }
}