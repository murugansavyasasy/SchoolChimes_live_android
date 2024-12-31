package com.vs.schoolmessenger.assignment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherAttendanceScreen;
import com.vs.schoolmessenger.activity.TeacherSchoolList;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.util.List;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

public class CreateAssignment extends Fragment {

    Button btnMessage, btnVoice, btnImage, btnPdf;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_assignment, container, false);
    }

    @SuppressLint({"NewApi", "WrongConstant"})

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        btnMessage = view.findViewById(R.id.btnmessage);
        btnVoice = view.findViewById(R.id.btnvoice);
        btnImage = view.findViewById(R.id.btnImage);
        btnPdf = view.findViewById(R.id.btnPdf);
        final SharedPreferences sharedPreferences_permission = PreferenceManager.getDefaultSharedPreferences(getContext());
        final boolean permissions = sharedPreferences_permission.getBoolean("permissions", false);
        Log.d("permission", String.valueOf(permissions));


        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (listschooldetails.size() == 1) {
                        Intent inAtten = new Intent(getContext(), MessageAssignmentActivity.class);
                        startActivity(inAtten);
                    } else {
                        Intent inAtten = new Intent(getContext(), TeacherSchoolList.class);
                        inAtten.putExtra("REQUEST_CODE", PRINCIPAL_ASSIGNMENT);
                        inAtten.putExtra("Type", "Message");
                        startActivity(inAtten);
                    }

            }
        });

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listschooldetails.size() == 1) {
                    Intent inAtten = new Intent(getContext(), VoiceAssignmentActivity.class);
                    startActivity(inAtten);
                } else {
                    Intent inAtten = new Intent(getContext(), TeacherSchoolList.class);
                    inAtten.putExtra("REQUEST_CODE", PRINCIPAL_ASSIGNMENT);
                    inAtten.putExtra("Type", "Voice");
                    startActivity(inAtten);
                }



            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listschooldetails.size() == 1) {
                    Intent inAtten = new Intent(getContext(), ImageAssignmentActivity.class);
                    startActivity(inAtten);
                } else {
                    Intent inAtten = new Intent(getContext(), TeacherSchoolList.class);
                    inAtten.putExtra("REQUEST_CODE", PRINCIPAL_ASSIGNMENT);
                    inAtten.putExtra("Type", "Image");
                    startActivity(inAtten);
                }


            }
        });
        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listschooldetails.size() == 1) {
                    Intent inAtten = new Intent(getContext(), PdfAssignmentActivity.class);
                    startActivity(inAtten);
                } else {
                    Intent inAtten = new Intent(getContext(), TeacherSchoolList.class);
                    inAtten.putExtra("REQUEST_CODE", PRINCIPAL_ASSIGNMENT);
                    inAtten.putExtra("Type", "Pdf");
                    startActivity(inAtten);
                }


            }
        });

    }

    private void requestStoragePermission() {
        Dexter.withActivity(getActivity()).withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

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
                    }
                })
                .onSameThread()
                .check();
    }
}


