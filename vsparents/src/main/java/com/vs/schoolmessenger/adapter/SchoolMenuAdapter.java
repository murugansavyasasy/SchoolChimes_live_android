package com.vs.schoolmessenger.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.BuildConfig;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vs.schoolmessenger.LessonPlan.Activity.LessonPlanActivity;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.DailyFeeCollectionActivity;
import com.vs.schoolmessenger.activity.FeedBackDetails;
import com.vs.schoolmessenger.activity.LeaveRequestsByStaffs;
import com.vs.schoolmessenger.activity.NewProductsScreen;
import com.vs.schoolmessenger.activity.ParentQuizScreen;
import com.vs.schoolmessenger.activity.RequestMeetingForSchool;
import com.vs.schoolmessenger.activity.ScoolsList;
import com.vs.schoolmessenger.activity.SpecialOfferScreen;
import com.vs.schoolmessenger.activity.StaffDetailListActivity;
import com.vs.schoolmessenger.activity.StaffLibraryDetails;
import com.vs.schoolmessenger.activity.StudentReportActivity;
import com.vs.schoolmessenger.activity.SubjectListActivity;
import com.vs.schoolmessenger.activity.TeacherAbsenteesReport;
import com.vs.schoolmessenger.activity.TeacherAttendanceScreen;
import com.vs.schoolmessenger.activity.TeacherCallsSmsUsages;
import com.vs.schoolmessenger.activity.TeacherEmergencyVoice;
import com.vs.schoolmessenger.activity.TeacherEventsScreen;
import com.vs.schoolmessenger.activity.TeacherGeneralText;
import com.vs.schoolmessenger.activity.TeacherMeetingURLScreen;
import com.vs.schoolmessenger.activity.TeacherMessageDatesScreen;
import com.vs.schoolmessenger.activity.TeacherNoticeBoard;
import com.vs.schoolmessenger.activity.TeacherParticularsScreen;
import com.vs.schoolmessenger.activity.TeacherPhotosScreen;
import com.vs.schoolmessenger.activity.TeacherSchoolList;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.activity.TextBookActivity;
import com.vs.schoolmessenger.assignment.AssignmentActivity;
import com.vs.schoolmessenger.assignment.VideoUpload;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.util.ArrayList;
import java.util.List;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ABSENTEES;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_CHAT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_DAILY_COLLECTION;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EVENTS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_LESSON_PLAN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MESSAGESFROMMANAGEMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_SCHOOLSTRENGTH;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_STUDENT_REPORT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE_HW;

public class SchoolMenuAdapter extends ArrayAdapter {

    ArrayList<String> isPrincipalMenuNames = new ArrayList<>();
    Context context;
    String bookLink;
    RelativeLayout rytParent;
    Boolean isPermission = false;
    Boolean isCameraPermission = false;
    private PopupWindow SettingsStoragepopupWindow;
    private PopupWindow SettingsStorageCamerapopupWindow;

    public SchoolMenuAdapter(Context context, int textViewResourceId, ArrayList objects, String BookLink, RelativeLayout rytParent) {
        super(context, textViewResourceId, objects);
        isPrincipalMenuNames = objects;
        this.context = context;
        this.bookLink = BookLink;
        this.rytParent = rytParent;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.school_menu_card_item, parent, false);
        }
        TextView textView = (TextView) v.findViewById(R.id.lblMenuName);
        ImageView imgMenu = (ImageView) v.findViewById(R.id.imgMenu);
        LinearLayout lnrMenu = (LinearLayout) v.findViewById(R.id.lnrMenu);
        String MenuName = isPrincipalMenuNames.get(position);

        lnrMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOnClick(MenuName);

            }
        });

        if (isPrincipalMenuNames.get(position).contains("_0")) {
            imgMenu.setImageResource(R.drawable.c_emergency);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }

        if (isPrincipalMenuNames.get(position).contains("_1")) {
            imgMenu.setImageResource(R.drawable.c_genvoice);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_2")) {
            imgMenu.setImageResource(R.drawable.c_smstoall);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_3")) {
            imgMenu.setImageResource(R.drawable.c_notice_board);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_4")) {
            imgMenu.setImageResource(R.drawable.c_events);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_5")) {
            imgMenu.setImageResource(R.drawable.c_image);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_6")) {
            imgMenu.setImageResource(R.drawable.c_reportattn);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_7")) {
            imgMenu.setImageResource(R.drawable.c_strenght);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_8")) {
            imgMenu.setImageResource(R.drawable.teacher_f_attendance_report);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_9")) {
            imgMenu.setImageResource(R.drawable.c_texthw);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
        }
        if (isPrincipalMenuNames.get(position).contains("_10")) {
            imgMenu.setImageResource(R.drawable.c_voicehomewrk);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_11")) {
            imgMenu.setImageResource(R.drawable.c_exam);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_12")) {
            imgMenu.setImageResource(R.drawable.c_atteednace);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_13")) {
            imgMenu.setImageResource(R.drawable.teacher_managementmessage);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_14")) {
            imgMenu.setImageResource(R.drawable.c_feedback);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_15")) {
            imgMenu.setImageResource(R.drawable.library_image);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_16")) {
            imgMenu.setImageResource(R.drawable.c_concall);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_17")) {
            imgMenu.setImageResource(R.drawable.request);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }

        if (isPrincipalMenuNames.get(position).contains("_18")) {
            imgMenu.setImageResource(R.drawable.c_ebook);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }

        if (isPrincipalMenuNames.get(position).contains("_19")) {
            imgMenu.setImageResource(R.drawable.meeting);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_20")) {
            imgMenu.setImageResource(R.drawable.c_emergency);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_21")) {
            imgMenu.setImageResource(R.drawable.information);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_22")) {
            imgMenu.setImageResource(R.drawable.assignment);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_23")) {
            imgMenu.setImageResource(R.drawable.videoimg);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_24")) {
            imgMenu.setImageResource(R.drawable.c_staffetails);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_25")) {
            imgMenu.setImageResource(R.drawable.new_products);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }
        if (isPrincipalMenuNames.get(position).contains("_26")) {
            imgMenu.setImageResource(R.drawable.onlineclass);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));

        }
        if (isPrincipalMenuNames.get(position).contains("_27")) {
            imgMenu.setImageResource(R.drawable.online_quiz);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }

        if (isPrincipalMenuNames.get(position).contains("_28")) {
            imgMenu.setImageResource(R.drawable.daily_collection_report);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }

        if (isPrincipalMenuNames.get(position).contains("_29")) {
            imgMenu.setImageResource(R.drawable.student_report);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }

        if (isPrincipalMenuNames.get(position).contains("_30")) {
            imgMenu.setImageResource(R.drawable.lesson_plan);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
        }


        return v;

    }

    private void menuOnClick(String MenuName) {

        String substring = MenuName.substring(Math.max(MenuName.length() - 2, 0));
        String substring1 = MenuName.substring(Math.max(MenuName.length() - 3, 0));

        if (substring.equals("_0")) {
            isVoicePermissionGranded(MenuName);

        } else if (substring.equals("_1")) {
            isVoicePermissionGranded(MenuName);

        } else if (substring.equals("_2")) {
            goToNextScreen(MenuName);


        } else if (substring.equals("_3")) {
            goToNextScreen(MenuName);

        } else if (substring.equals("_4")) {
            goToNextScreen(MenuName);

        } else if (substring.equals("_5")) {

            isCameraPermission(MenuName);

        } else if (substring.equals("_6")) {

            goToNextScreen(MenuName);

        } else if (substring.equals("_7")) {

            goToNextScreen(MenuName);

        } else if (substring.equals("_8")) {
            goToNextScreen(MenuName);

        } else if (substring.equals("_9")) {

            goToNextScreen(MenuName);

        } else if (substring1.equals("_10")) {
            isVoicePermissionGranded(MenuName);
        } else if (substring1.equals("_11")) {

            goToNextScreen(MenuName);

        } else if (substring1.equals("_12")) {

            goToNextScreen(MenuName);

        } else if (substring1.equals("_13")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_14")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_15")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_16")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_17")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_18")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_19")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_20")) {

        } else if (substring1.equals("_21")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_22")) {
            isCameraPermission(MenuName);

        } else if (substring1.equals("_23")) {
            isVoicePermissionGranded(MenuName);

        } else if (substring1.equals("_24")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_25")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_26")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_27")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_28")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_29")) {
            goToNextScreen(MenuName);
        }

        else if (substring1.equals("_30")) {
            goToNextScreen(MenuName);
        }
    }

    private boolean isCameraPermission(String MenuName) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity((Activity) context)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.CAMERA
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                isCameraPermission = true;
                                goToNextScreen(MenuName);
                            } else {
                                settingsCameraPermission();
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
        else {
            Dexter.withActivity((Activity) context)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                isCameraPermission = true;
                                goToNextScreen(MenuName);
                            } else {
                                settingsCameraPermission();
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

        return isCameraPermission;
    }

    private void settingsCameraPermission() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.settings_camera_storage_permission, null);
        SettingsStorageCamerapopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        SettingsStorageCamerapopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                SettingsStorageCamerapopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });
        TextView lblHeader = (TextView) layout.findViewById(R.id.lblHeader);
        lblHeader.setText("To send media, allow School chimes access to your device's media,camera,photos and files. Tap Settings >Permissions, and turn Storage and camera on ");
        TextView lblNotNow = (TextView) layout.findViewById(R.id.lblNotNow);
        TextView lblSettings = (TextView) layout.findViewById(R.id.lblSettings);
        lblNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsStorageCamerapopupWindow.dismiss();
            }
        });

        lblSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsStorageCamerapopupWindow.dismiss();
                context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
            }
        });
    }

    public boolean isVoicePermissionGranded(String MenuName) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity((Activity) context)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.RECORD_AUDIO
                    )


                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                isPermission = true;
                                goToNextScreen(MenuName);
                            } else {
                                settingsStoragePermission();

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
        } else {
            Dexter.withActivity((Activity) context)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO
                    )


                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                isPermission = true;
                                goToNextScreen(MenuName);
                            } else {
                                settingsStoragePermission();

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


        return isPermission;
    }

    private void settingsStoragePermission() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.settings_storage_permission, null);
        SettingsStoragepopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        SettingsStoragepopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                SettingsStoragepopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });
        TextView lblHeader = (TextView) layout.findViewById(R.id.lblHeader);
        lblHeader.setText("To send media, allow School chimes access to your device's media,microphone and files. Tap Settings >Permissions, and turn Storage and microphone on ");
        TextView lblNotNow = (TextView) layout.findViewById(R.id.lblNotNow);
        TextView lblSettings = (TextView) layout.findViewById(R.id.lblSettings);
        lblNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsStoragepopupWindow.dismiss();
            }
        });

        lblSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsStoragepopupWindow.dismiss();
                context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
            }
        });
    }

    private void goToNextScreen(String MenuName) {

        String substring = MenuName.substring(Math.max(MenuName.length() - 2, 0));
        String substring1 = MenuName.substring(Math.max(MenuName.length() - 3, 0));

        if (substring.equals("_0")) {

            Intent inVoice = new Intent(context, TeacherEmergencyVoice.class);
            inVoice.putExtra("EMERGENCY", true);
            inVoice.putExtra("TeacherSchoolsModel", Teacher_AA_Test.schoolmodel);

            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_HEAD)) {
                inVoice.putExtra("REQUEST_CODE", GH_EMERGENCY);
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_HEAD)) {
                inVoice.putExtra("REQUEST_CODE", GH_VOICE);
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_EMERGENCY);
            }
            context.startActivity(inVoice);


        } else if (substring.equals("_1")) {

            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inVoice = new Intent(context, TeacherEmergencyVoice.class);
                    inVoice.putExtra("REQUEST_CODE", PRINCIPAL_VOICE);
                    inVoice.putExtra("EMERGENCY", false);
                    context.startActivity(inVoice);
                } else {
                    Intent inVoice = new Intent(context, TeacherSchoolList.class);
                    inVoice.putExtra("REQUEST_CODE", PRINCIPAL_VOICE);
                    inVoice.putExtra("EMERGENCY", false);
                    context.startActivity(inVoice);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inVoice = new Intent(context, TeacherEmergencyVoice.class);
                    inVoice.putExtra("REQUEST_CODE", STAFF_VOICE);
                    inVoice.putExtra("EMERGENCY", false);
                    context.startActivity(inVoice);
                } else {
                    Intent inVoice = new Intent(context, TeacherSchoolList.class);
                    inVoice.putExtra("REQUEST_CODE", STAFF_VOICE);
                    inVoice.putExtra("EMERGENCY", false);
                    context.startActivity(inVoice);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_HEAD)) {

                Intent inVoice = new Intent(context, TeacherEmergencyVoice.class);
                inVoice.putExtra("REQUEST_CODE", GH_VOICE);
                inVoice.putExtra("EMERGENCY", false);
                context.startActivity(inVoice);
            }

        } else if (substring.equals("_2")) {

            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inHomeWorkVoice = new Intent(context, TeacherGeneralText.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_TEXT);
                    context.startActivity(inHomeWorkVoice);
                } else {
                    Intent inHomeWorkVoice = new Intent(context, TeacherSchoolList.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_TEXT);
                    context.startActivity(inHomeWorkVoice);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inHomeWorkVoice = new Intent(context, TeacherGeneralText.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_TEXT);
                    context.startActivity(inHomeWorkVoice);
                } else {
                    Intent inHomeWorkVoice = new Intent(context, TeacherSchoolList.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_TEXT);
                    context.startActivity(inHomeWorkVoice);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_HEAD)) {

                Intent inHomeWorkVoice = new Intent(context, TeacherGeneralText.class);
                inHomeWorkVoice.putExtra("REQUEST_CODE", GH_TEXT);
                context.startActivity(inHomeWorkVoice);

            }


        } else if (substring.equals("_3")) {

            Intent inNoticeBoard = new Intent(context, TeacherNoticeBoard.class);
            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_HEAD))
                inNoticeBoard.putExtra("REQUEST_CODE", GH_NOTICE_BOARD);
            else inNoticeBoard.putExtra("REQUEST_CODE", PRINCIPAL_NOTICE_BOARD);
            context.startActivity(inNoticeBoard);

        } else if (substring.equals("_4")) {
            Intent inEvents = new Intent(context, TeacherEventsScreen.class);
            inEvents.putExtra("REQUEST_CODE", PRINCIPAL_EVENTS);
            context.startActivity(inEvents);

        } else if (substring.equals("_5")) {

            Intent inImg = new Intent(context, TeacherPhotosScreen.class);
            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL))
                inImg.putExtra("REQUEST_CODE", PRINCIPAL_PHOTOS);
            else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER))
                inImg.putExtra("REQUEST_CODE", STAFF_PHOTOS);
            context.startActivity(inImg);


        } else if (substring.equals("_6")) {

            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {

                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inAbs = new Intent(context, TeacherAbsenteesReport.class);
                    inAbs.putExtra("REQUEST_CODE", PRINCIPAL_ABSENTEES);
                    context.startActivity(inAbs);
                } else {
                    Intent inAbs = new Intent(context, TeacherSchoolList.class);
                    inAbs.putExtra("REQUEST_CODE", PRINCIPAL_ABSENTEES);
                    context.startActivity(inAbs);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER)) {
                Intent inAbsstaff = new Intent(context, TeacherAbsenteesReport.class);
                inAbsstaff.putExtra("REQUEST_CODE", STAFF_PHOTOS);
                context.startActivity(inAbsstaff);
            }

        } else if (substring.equals("_7")) {

            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inParti = new Intent(context, TeacherParticularsScreen.class);
                    inParti.putExtra("REQUEST_CODE", PRINCIPAL_SCHOOLSTRENGTH);
                    inParti.putExtra("SINGLESCHOOLLOGIN", true);
                    context.startActivity(inParti);
                } else {
                    Intent inParti = new Intent(context, TeacherSchoolList.class);
                    inParti.putExtra("REQUEST_CODE", PRINCIPAL_SCHOOLSTRENGTH);
                    context.startActivity(inParti);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER)) {
                Intent inParti = new Intent(context, TeacherParticularsScreen.class);
                context.startActivity(inParti);
            }

        } else if (substring.equals("_8")) {
            Intent inCallsSMS = new Intent(context, TeacherCallsSmsUsages.class);
            context.startActivity(inCallsSMS);

        } else if (substring.equals("_9")) {

            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inHwText = new Intent(context, TeacherGeneralText.class);
                    inHwText.putExtra("REQUEST_CODE", PRINCIPAL_TEXT_HW);
                    context.startActivity(inHwText);
                } else {
                    Intent inHwText = new Intent(context, TeacherSchoolList.class);
                    inHwText.putExtra("REQUEST_CODE", PRINCIPAL_TEXT_HW);
                    context.startActivity(inHwText);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER)) {
                Intent inHwText = new Intent(context, TeacherGeneralText.class);
                inHwText.putExtra("REQUEST_CODE", STAFF_TEXT_HW);
                context.startActivity(inHwText);
            }

        } else if (substring1.equals("_10")) {
            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inHomeWorkVoice = new Intent(context, TeacherEmergencyVoice.class);
                    inHomeWorkVoice.putExtra("EMERGENCY", false);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_VOICE_HW);
                    context.startActivity(inHomeWorkVoice);
                } else {
                    Intent inHomeWorkVoice = new Intent(context, TeacherSchoolList.class);
                    inHomeWorkVoice.putExtra("EMERGENCY", false);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_VOICE_HW);
                    context.startActivity(inHomeWorkVoice);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER)) {
                Intent inHomeWorkVoice = new Intent(context, TeacherEmergencyVoice.class);
                inHomeWorkVoice.putExtra("EMERGENCY", false);
                inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_VOICE_HW);
                context.startActivity(inHomeWorkVoice);
            }

        } else if (substring1.equals("_11")) {

            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inExam = new Intent(context, TeacherGeneralText.class);
                    inExam.putExtra("REQUEST_CODE", PRINCIPAL_EXAM_TEST);
                    context.startActivity(inExam);
                } else {
                    Intent inExam = new Intent(context, TeacherSchoolList.class);
                    inExam.putExtra("REQUEST_CODE", PRINCIPAL_EXAM_TEST);
                    context.startActivity(inExam);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER)) {
                Intent inExam = new Intent(context, TeacherGeneralText.class);
                inExam.putExtra("REQUEST_CODE", STAFF_TEXT_EXAM);
                context.startActivity(inExam);
            }

        } else if (substring1.equals("_12")) {

            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL) || TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_ADMIN)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inAtten = new Intent(context, TeacherAttendanceScreen.class);
                    inAtten.putExtra("REQUEST_CODE", PRINCIPAL_ATTENDANCE);
                    context.startActivity(inAtten);
                } else {
                    Intent inAtten = new Intent(context, TeacherSchoolList.class);
                    inAtten.putExtra("REQUEST_CODE", PRINCIPAL_ATTENDANCE);
                    context.startActivity(inAtten);
                }
            } else {
                Intent inPrincipal = new Intent(context, TeacherAttendanceScreen.class);
                inPrincipal.putExtra("REQUEST_CODE", STAFF_ATTENDANCE);
                inPrincipal.putExtra("SCHOOL_ID", TeacherUtil_Common.Principal_SchoolId);
                inPrincipal.putExtra("STAFF_ID", Principal_staffId);
                context.startActivity(inPrincipal);
            }

        } else if (substring1.equals("_13")) {
            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                Intent messages = new Intent(context, TeacherSchoolList.class);
                messages.putExtra("REQUEST_CODE", PRINCIPAL_MESSAGESFROMMANAGEMENT);
                context.startActivity(messages);

            } else {

                Intent messages = new Intent(context, TeacherMessageDatesScreen.class);
                messages.putExtra("TeacherSchoolsModel", Teacher_AA_Test.schoolmodel);
                context.startActivity(messages);

            }

        } else if (substring1.equals("_14")) {
            Intent feed = new Intent(context, FeedBackDetails.class);
            feed.putExtra("TeacherSchoolsModel", Teacher_AA_Test.schoolmodel);
            feed.putExtra("schools", Teacher_AA_Test.myArray);
            feed.putExtra("list", Teacher_AA_Test.schools_list);
            context.startActivity(feed);

        } else if (substring1.equals("_15")) {
            Intent library = new Intent(context, StaffLibraryDetails.class);
            library.putExtra("TeacherSchoolsModel", Teacher_AA_Test.schoolmodel);
            context.startActivity(library);

        } else if (substring1.equals("_16")) {
            Intent schoolslist = new Intent(context, ScoolsList.class);
            schoolslist.putExtra("TeacherSchoolsModel", Teacher_AA_Test.schools_list);
            schoolslist.putExtra("schools", "");
            context.startActivity(schoolslist);

        } else if (substring1.equals("_17")) {
            Intent messages = new Intent(context, LeaveRequestsByStaffs.class);
            messages.putExtra("TeacherSchoolsModel", Teacher_AA_Test.schoolmodel);
            messages.putExtra("type", "staff");
            context.startActivity(messages);

        } else if (substring1.equals("_18")) {
            Intent browse = new Intent(context, TextBookActivity.class);
            browse.putExtra("url", bookLink);
            context.startActivity(browse);

        } else if (substring1.equals("_19")) {
            Intent Request = new Intent(context, RequestMeetingForSchool.class);
            Request.putExtra("TeacherSchoolsModel", Teacher_AA_Test.schoolmodel);
            Request.putExtra("type", "staff");
            context.startActivity(Request);

        } else if (substring1.equals("_20")) {

        } else if (substring1.equals("_21")) {
            Intent offers = new Intent(context, SpecialOfferScreen.class);
            context.startActivity(offers);

        } else if (substring1.equals("_22")) {
            Intent assignment = new Intent(context, AssignmentActivity.class);
            context.startActivity(assignment);


        } else if (substring1.equals("_23")) {
            Intent videovimeo = new Intent(context, VideoUpload.class);
            context.startActivity(videovimeo);


        } else if (substring1.equals("_24")) {
            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inVoice = new Intent(context, StaffDetailListActivity.class);
                    inVoice.putExtra("REQUEST_CODE", PRINCIPAL_CHAT);
                    context.startActivity(inVoice);
                } else {
                    Intent inVoice = new Intent(context, TeacherSchoolList.class);
                    inVoice.putExtra("REQUEST_CODE", PRINCIPAL_CHAT);
                    context.startActivity(inVoice);
                }
            } else {
                Intent chat = new Intent(context, SubjectListActivity.class);
                chat.putExtra(Constants.STAFF_ID, Principal_staffId);
                chat.putExtra(Constants.COME_FROM, Constants.STAFF);
                context.startActivity(chat);
            }

        } else if (substring1.equals("_25")) {
            Intent newProduct = new Intent(context, NewProductsScreen.class);
            context.startActivity(newProduct);

        } else if (substring1.equals("_26")) {
            if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_PRINCIPAL)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inHomeWorkVoice = new Intent(context, TeacherMeetingURLScreen.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_MEETING_URL);
                    context.startActivity(inHomeWorkVoice);
                } else {
                    Intent inHomeWorkVoice = new Intent(context, TeacherSchoolList.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_MEETING_URL);
                    context.startActivity(inHomeWorkVoice);
                }
            } else if (TeacherUtil_SharedPreference.getLoginTypeContextFromSP(context).equals(LOGIN_TYPE_TEACHER)) {
                if (TeacherUtil_Common.listschooldetails.size() == 1) {
                    Intent inHomeWorkVoice = new Intent(context, TeacherMeetingURLScreen.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_MEETING_URL);
                    context.startActivity(inHomeWorkVoice);
                } else {
                    Intent inHomeWorkVoice = new Intent(context, TeacherSchoolList.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_MEETING_URL);
                    context.startActivity(inHomeWorkVoice);
                }
            }

        } else if (substring1.equals("_27")) {
            Intent online = new Intent(context, ParentQuizScreen.class);
            online.putExtra("Type", "SChool");
            context.startActivity(online);
        } else if (substring1.equals("_28")) {
            if (TeacherUtil_Common.listschooldetails.size() == 1) {
                Intent inVoice = new Intent(context, DailyFeeCollectionActivity.class);
                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_DAILY_COLLECTION);
                context.startActivity(inVoice);

            } else {
                Intent inVoice = new Intent(context, TeacherSchoolList.class);
                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_DAILY_COLLECTION);
                context.startActivity(inVoice);
            }
        } else if (substring1.equals("_29")) {
            if (TeacherUtil_Common.listschooldetails.size() == 1) {
                Intent inVoice = new Intent(context, StudentReportActivity.class);
                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_STUDENT_REPORT);
                context.startActivity(inVoice);
            } else {
                Intent inVoice = new Intent(context, TeacherSchoolList.class);
                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_STUDENT_REPORT);
                context.startActivity(inVoice);
            }

        }

        else if (substring1.equals("_30")) {
            if (TeacherUtil_Common.listschooldetails.size() == 1) {
                Intent inVoice = new Intent(context, LessonPlanActivity.class);
                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_LESSON_PLAN);
                context.startActivity(inVoice);
            } else {
                Intent inVoice = new Intent(context, TeacherSchoolList.class);
                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_LESSON_PLAN);
                context.startActivity(inVoice);
            }

        }

    }

}