package com.vs.schoolmessenger.adapter;

import static com.vs.schoolmessenger.util.Constants.updates;
import static com.vs.schoolmessenger.util.Util_Common.MENU_ATTENDANCE;
import static com.vs.schoolmessenger.util.Util_Common.MENU_DOCUMENTS;
import static com.vs.schoolmessenger.util.Util_Common.MENU_EMERGENCY;
import static com.vs.schoolmessenger.util.Util_Common.MENU_HW;
import static com.vs.schoolmessenger.util.Util_Common.MENU_LEAVE_REQUEST;
import static com.vs.schoolmessenger.util.Util_Common.MENU_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.Util_Common.MENU_PHOTOS;
import static com.vs.schoolmessenger.util.Util_Common.MENU_TEXT;
import static com.vs.schoolmessenger.util.Util_Common.MENU_VOICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vs.schoolmessenger.BuildConfig;
import com.vs.schoolmessenger.CouponView.CouponDashboard.CouponMainClassActivity;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ApplyLeave;
import com.vs.schoolmessenger.activity.Attendance;
import com.vs.schoolmessenger.activity.CertificateRequestActivity;
import com.vs.schoolmessenger.activity.DatesList;
import com.vs.schoolmessenger.activity.EventsTapScreen;
import com.vs.schoolmessenger.activity.ExamCircularActivity;
import com.vs.schoolmessenger.activity.ExamListScreen;
import com.vs.schoolmessenger.activity.HomeActivity;
import com.vs.schoolmessenger.activity.ImageCircular;
import com.vs.schoolmessenger.activity.LSRWListActivity;
import com.vs.schoolmessenger.activity.MessageDatesScreen;
import com.vs.schoolmessenger.activity.OnlineClassParentScreen;
import com.vs.schoolmessenger.activity.PTM;
import com.vs.schoolmessenger.activity.ParentQuizScreen;
import com.vs.schoolmessenger.activity.PdfCircular;
import com.vs.schoolmessenger.activity.ProfileLinkScreen;
import com.vs.schoolmessenger.activity.RequestMeetingForParent;
import com.vs.schoolmessenger.activity.StaffListActivity;
import com.vs.schoolmessenger.activity.StudentLibraryDetails;
import com.vs.schoolmessenger.activity.TextBookForParent;
import com.vs.schoolmessenger.activity.TextCircular;
import com.vs.schoolmessenger.activity.TimeTableActivity;
import com.vs.schoolmessenger.activity.VideoListActivity;
import com.vs.schoolmessenger.activity.VoiceCircular;
import com.vs.schoolmessenger.assignment.ParentAssignmentListActivity;
import com.vs.schoolmessenger.interfaces.UpdatesListener;
import com.vs.schoolmessenger.model.ParentMenuModel;
import com.vs.schoolmessenger.payment.FeesTab;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;


public class ChildMenuAdapter extends ArrayAdapter {

    ArrayList<ParentMenuModel> isPrincipalMenuNames = new ArrayList<>();
    Context context;
    String bookLink;
    RelativeLayout rytParent;
    Boolean isPermission = false;
    Boolean isCameraPermission = false;
    Boolean isRecordPermission = false;

    private PopupWindow SettingsStoragepopupWindow;
    private PopupWindow SettingsStorageCamerapopupWindow;


    private final UpdatesListener listener;


    public ChildMenuAdapter(Context context, int textViewResourceId, ArrayList objects, String BookLink, RelativeLayout rytParent, UpdatesListener listener) {
        super(context, textViewResourceId, objects);
        isPrincipalMenuNames = objects;
        this.context = context;
        this.bookLink = BookLink;
        this.rytParent = rytParent;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.child_menu_item, parent, false);
        }
        TextView textView = (TextView) v.findViewById(R.id.lblMenuName);
        TextView lblUnreadCount = (TextView) v.findViewById(R.id.lblUnreadCount);
        ImageView imgMenu = (ImageView) v.findViewById(R.id.imgMenu);
        LinearLayout lnrMenu = (LinearLayout) v.findViewById(R.id.lnrMenu);
        GifImageView gifImage = (GifImageView) v.findViewById(R.id.gifImage);
        ParentMenuModel model = isPrincipalMenuNames.get(position);
        String MenuName = model.getMenu_name();
        String unReadCount = model.getUnread_count();

        lnrMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_Common.scroll_to_position = position;
                listener.onMsgItemClick(model.getMenu_name());
                menuOnClick(model.getMenu_name());
            }
        });

        lblUnreadCount.setText(unReadCount);

        if (MenuName.equals(updates)) {
            gifImage.setVisibility(View.VISIBLE);
            imgMenu.setVisibility(View.GONE);
            lnrMenu.setBackgroundDrawable(null);
            textView.setText("");
            setUnReadCount(unReadCount, lblUnreadCount);
        }

        if (MenuName.contains("_0")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_emergency);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);
        }

        if (MenuName.contains("_1")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_genvoice);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_2")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_messages);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_3")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_homework);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_4")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_exam);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_5")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_esammark);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_6")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.file);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_7")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_notice_board);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_8")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_events);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_9")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_atteednace);
            textView.setText(MenuName.substring(0, MenuName.length() - 2));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_10")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.leaverequest);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_11")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_feeshand);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_12")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.image_c);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_13")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_library);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_14")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_staffetails);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_15")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.c_ebook);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_16")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.meeting);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_17")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
        }

        if (MenuName.contains("_18")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.assignment);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }

        if (MenuName.contains("_19")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.videoimg);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);

        }
        if (MenuName.contains("_20")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.onlineclass);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }
        if (MenuName.contains("_21")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.idea);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }
        if (MenuName.contains("_22")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.lsrw);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }
        if (MenuName.contains("_23")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.time_table);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }

        if (MenuName.contains("_24")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.student_profile);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }

        if (MenuName.contains("_25")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.certificate_request);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }

        if (MenuName.contains("_26")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.ptm);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }

        if (MenuName.contains("_27")) {
            gifImage.setVisibility(View.GONE);
            imgMenu.setVisibility(View.VISIBLE);
            imgMenu.setImageResource(R.drawable.gift_voucher);
            textView.setText(MenuName.substring(0, MenuName.length() - 3));
            setUnReadCount(unReadCount, lblUnreadCount);
        }

        return v;
    }

    private void setUnReadCount(String unReadCount, TextView lblUnreadCount) {
//        if (!unReadCount.equals("0")) {
//            lblUnreadCount.setVisibility(View.VISIBLE);
//        } else {
//            lblUnreadCount.setVisibility(View.INVISIBLE);
//        }
    }

    private void menuOnClick(String MenuName) {
        String substring = MenuName.substring(Math.max(MenuName.length() - 2, 0));
        String substring1 = MenuName.substring(Math.max(MenuName.length() - 3, 0));
        String menuIDSingle = MenuName.substring(Math.max(MenuName.length() - 1, 0));
        String menuIDTwo = MenuName.substring(Math.max(MenuName.length() - 2, 0));


        if (substring.equals("_0")) {
            isPermissionGranded(MenuName);
        } else if (substring.equals("_1")) {
            isPermissionGranded(MenuName);
        } else if (substring.equals("_2")) {
            goToNextScreen(MenuName);

        } else if (substring.equals("_3")) {
            isPermissionGranded(MenuName);

        } else if (substring.equals("_4")) {
            goToNextScreen(MenuName);

        } else if (substring.equals("_5")) {
            goToNextScreen(MenuName);

        } else if (substring.equals("_6")) {
            isPermissionGranded(MenuName);

        } else if (substring.equals("_7")) {
            goToNextScreen(MenuName);


        } else if (substring.equals("_8")) {
            goToNextScreen(MenuName);


        } else if (substring.equals("_9")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_10")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_11")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_12")) {
            isPermissionGranded(MenuName);
        } else if (substring1.equals("_13")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_14")) {
            goToNextScreen(MenuName);


        } else if (substring1.equals("_15")) {
            goToNextScreen(MenuName);

        } else if (substring1.equals("_16")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_17")) {
        } else if (substring1.equals("_18")) {
            isCameraPermissions(MenuName);
        } else if (substring1.equals("_19")) {
            isPermissionGranded(MenuName);
        } else if (substring1.equals("_20")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_21")) {
            isPermissionGranded(MenuName);
        } else if (substring1.equals("_22")) {
            isCameraPermissions(MenuName);
        } else if (substring1.equals("_23")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_24")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_25")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_26")) {
            goToNextScreen(MenuName);
        } else if (substring1.equals("_27")) {
            goToNextScreen(MenuName);
        }


    }


    private boolean isCameraPermissions(String MenuName) {


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            Dexter.withContext((Activity) context)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                isCameraPermission = true;
                                goToNextScreen(MenuName);
                            } else {

                                String isPermissionDeniedCount = TeacherUtil_SharedPreference.getParentStorageandCameraPermission(context);

                                if (isPermissionDeniedCount.equals("2")) {
                                    settingsCameraPermission();
                                } else if (isPermissionDeniedCount.equals("1")) {
                                    TeacherUtil_SharedPreference.putParentStorageandCameraPermission(context, "2");
                                } else {
                                    TeacherUtil_SharedPreference.putParentStorageandCameraPermission(context, "1");
                                }
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

                            //  Toast.makeText(context, "Error occurred! ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onSameThread()
                    .check();
        } else {

            Dexter.withContext((Activity) context)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                isCameraPermission = true;
                                goToNextScreen(MenuName);
                            } else {

                                String isPermissionDeniedCount = TeacherUtil_SharedPreference.getParentStorageandCameraPermission(context);

                                if (isPermissionDeniedCount.equals("2")) {
                                    settingsCameraPermission();
                                } else if (isPermissionDeniedCount.equals("1")) {
                                    TeacherUtil_SharedPreference.putParentStorageandCameraPermission(context, "2");
                                } else {
                                    TeacherUtil_SharedPreference.putParentStorageandCameraPermission(context, "1");
                                }
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

                            //  Toast.makeText(context, "Error occurred! ", Toast.LENGTH_SHORT).show();
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
        lblHeader.setText("To send and save media, allow School chimes access to your device's photos,media,camera,microphone and files. Tap Settings >Permissions, and turn Storage,Camera and microphone on ");
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


    private boolean isPermissionGranded(String MenuName) {


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext((Activity) context)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                isPermission = true;
                                goToNextScreen(MenuName);
                            } else {

                                String isPermissionDeniedCount = TeacherUtil_SharedPreference.getParentStoragePermission(context);

                                if (isPermissionDeniedCount.equals("2")) {
                                    settingsStoragePermission();
                                } else if (isPermissionDeniedCount.equals("1")) {
                                    TeacherUtil_SharedPreference.putParentStoragePermission(context, "2");
                                } else {
                                    TeacherUtil_SharedPreference.putParentStoragePermission(context, "1");
                                }
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

                            //  Toast.makeText(context, "Error occurred! ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onSameThread()
                    .check();
        } else {
            Dexter.withContext((Activity) context)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                isPermission = true;
                                goToNextScreen(MenuName);
                            } else {

                                String isPermissionDeniedCount = TeacherUtil_SharedPreference.getParentStoragePermission(context);

                                if (isPermissionDeniedCount.equals("2")) {
                                    settingsStoragePermission();
                                } else if (isPermissionDeniedCount.equals("1")) {
                                    TeacherUtil_SharedPreference.putParentStoragePermission(context, "2");
                                } else {
                                    TeacherUtil_SharedPreference.putParentStoragePermission(context, "1");
                                }
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

                            //  Toast.makeText(context, "Error occurred! ", Toast.LENGTH_SHORT).show();
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

        lblHeader.setText("To send and save media, allow School chimes access to your device's ,storage .Tap Settings >Permissions, and turn Storage and media on ");

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
        String menuIDSingle = MenuName.substring(Math.max(MenuName.length() - 1, 0));
        String menuIDTwo = MenuName.substring(Math.max(MenuName.length() - 2, 0));

        if (substring.equals("_0")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, VoiceCircular.class);
            inNext.putExtra("REQUEST_CODE", MENU_EMERGENCY);
            inNext.putExtra("HEADER", R.string.emergency);
            context.startActivity(inNext);
        } else if (substring.equals("_1")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, DatesList.class);
            inNext.putExtra("REQUEST_CODE", MENU_VOICE);
            inNext.putExtra("HEADER", R.string.recent_voice_messages);
            context.startActivity(inNext);
        } else if (substring.equals("_2")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, DatesList.class);
            inNext.putExtra("REQUEST_CODE", MENU_TEXT);
            inNext.putExtra("HEADER", R.string.recent_messages);
            context.startActivity(inNext);
        } else if (substring.equals("_3")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, MessageDatesScreen.class);
            inNext.putExtra("REQUEST_CODE", MENU_HW);
            inNext.putExtra("Profiles", HomeActivity.childItem);
            context.startActivity(inNext);
        } else if (substring.equals("_4")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, ExamCircularActivity.class);
            context.startActivity(inNext);
        } else if (substring.equals("_5")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, ExamListScreen.class);
            String stu_id = Util_SharedPreference.getChildIdFromSP(context);
            String school_id = Util_SharedPreference.getSchoolIdFromSP(context);
            inNext.putExtra("CHILD_ID", stu_id);
            inNext.putExtra("SHOOL_ID", school_id);
            context.startActivity(inNext);
        } else if (substring.equals("_6")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, PdfCircular.class);
            inNext.putExtra("REQUEST_CODE", MENU_DOCUMENTS);
            inNext.putExtra("HEADER", R.string.recent_files);
            context.startActivity(inNext);
        } else if (substring.equals("_7")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, TextCircular.class);
            inNext.putExtra("REQUEST_CODE", MENU_NOTICE_BOARD);
            inNext.putExtra("HEADER", R.string.home_notice_board);
            inNext.putExtra("Profiles", HomeActivity.childItem);
            inNext.putExtra("type","parent");

            context.startActivity(inNext);
        } else if (substring.equals("_8")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, EventsTapScreen.class);
            context.startActivity(inNext);
        } else if (substring.equals("_9")) {
            Constants.Menu_ID = menuIDSingle;
            Intent inNext = new Intent(context, Attendance.class);
            inNext.putExtra("REQUEST_CODE", MENU_ATTENDANCE);
            inNext.putExtra("HEADER", R.string.attedance);
            inNext.putExtra("Profiles", HomeActivity.childItem);
            context.startActivity(inNext);
        } else if (substring1.equals("_10")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, ApplyLeave.class);
            inNext.putExtra("REQUEST_CODE", MENU_LEAVE_REQUEST);
            inNext.putExtra("HEADER", R.string.leave + " " + R.string.requesttttt);
            context.startActivity(inNext);
        } else if (substring1.equals("_11")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, FeesTab.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_12")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, ImageCircular.class);
            inNext.putExtra("REQUEST_CODE", MENU_PHOTOS);
            inNext.putExtra("HEADER", R.string.recent_photos);
            context.startActivity(inNext);
        } else if (substring1.equals("_13")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, StudentLibraryDetails.class);
            String id = Util_SharedPreference.getChildIdFromSP(context);
            inNext.putExtra("CHILD_ID", id);
            context.startActivity(inNext);
        } else if (substring1.equals("_14")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, StaffListActivity.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_15")) {
            Constants.Menu_ID = menuIDTwo;
            Intent browse = new Intent(context, TextBookForParent.class);
            browse.putExtra("url", bookLink);
            context.startActivity(browse);
        } else if (substring1.equals("_16")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, RequestMeetingForParent.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_17")) {
        } else if (substring1.equals("_18")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, ParentAssignmentListActivity.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_19")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, VideoListActivity.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_20")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, OnlineClassParentScreen.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_21")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, ParentQuizScreen.class);
            inNext.putExtra("Type", "Parent");
            context.startActivity(inNext);
        } else if (substring1.equals("_22")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, LSRWListActivity.class);
            inNext.putExtra("Type", "Parent");
            context.startActivity(inNext);
        } else if (substring1.equals("_23")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, TimeTableActivity.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_24")) {
            Intent profile = new Intent(context, ProfileLinkScreen.class);
            context.startActivity(profile);
        } else if (substring1.equals("_25")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, CertificateRequestActivity.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_26")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, PTM.class);
            context.startActivity(inNext);
        } else if (substring1.equals("_27")) {
            Constants.Menu_ID = menuIDTwo;
            Intent inNext = new Intent(context, CouponMainClassActivity.class);
            context.startActivity(inNext);
        }
    }
}