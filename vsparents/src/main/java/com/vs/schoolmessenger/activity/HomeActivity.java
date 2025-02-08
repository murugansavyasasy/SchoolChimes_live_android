package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Constants.updates;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.ChildMenuAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.UpdatesListener;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.NewUpdatesData;
import com.vs.schoolmessenger.model.NewUpdatesModel;
import com.vs.schoolmessenger.model.ParentMenuModel;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String CONTACTS_PERMISSION = android.Manifest.permission.READ_CONTACTS;
    private static final int OTHER_VIEW_ID = R.id.rytHome;
    public static Profiles childItem = new Profiles();
    public static int i = 0;
    static ArrayList<String> isParentMenuNames = new ArrayList<>();
    public Handler handler = new Handler();
    String strChildName, child_ID;
    TextView tv_schoolname, tvSchoolAddress, aHome_tvRegionalSchoolName;
    ImageView aHome_nivSchoolLogo;
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    String BookLink;
    RelativeLayout rytParent;
    TextView scrollingtext;
    LinearLayout lnrScroll;
    GridView idGridMenus;
    String isBookEnabled;
    ArrayList<ParentMenuModel> menuList = new ArrayList<ParentMenuModel>();
    String count;
    Slider slider;
    ImageView adImage;
    int Contact_Count = 0;
    int exist_Count = 0;
    String contact_alert_title = "", contact_alert_Content = "", contact_display_name = "", contact_numbers = "", contact_button = "";
    String[] contacts;
    String Display_Name = "";
    AdView mAdView;
    String redirect_url = "";
    String image_url = "";
    String title = "";
    String content = "";
    List<NewUpdatesData> newUpdatesDataList = new ArrayList<NewUpdatesData>();
    int initial_pos = 0;
    PopupWindow updatespopupWindow;
    PopupWindow updatesManualPopup;
    ChildMenuAdapter myAdapter;
    private ArrayList<Profiles> childList = new ArrayList<>();
    private PopupWindow popupWindow;
    private PopupWindow SettingspopupWindow;
    ProgressBar isProgressBar;

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);
        isProgressBar = (ProgressBar) findViewById(R.id.isProgressBar);
        isProgressBar.setVisibility(View.VISIBLE);

        String home = getIntent().getExtras().getString("HomeScreen", "");
        if (!home.equals("1")) {
            childItem = (Profiles) getIntent().getSerializableExtra("Profiles");
            TeacherUtil_SharedPreference.putChildItems(HomeActivity.this, childItem, "childItem");
        }

        childItem = TeacherUtil_SharedPreference.getChildItems(HomeActivity.this, "childItem");
        Util_SharedPreference.putSelecedChildInfoToSP(HomeActivity.this, childItem.getChildID(), childItem.getChildName(), childItem.getSchoolID(),
                childItem.getSchoolName(), childItem.getSchoolNameRegional(), childItem.getSchoolAddress(), childItem.getSchoolThumbnailImgUrl(), childItem.getStandard(), childItem.getSection());
        strChildName = childItem.getChildName();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText(strChildName);
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkAndRequestPermission();

        Slider.init(new PicassoImageLoadingService(HomeActivity.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);

        idGridMenus = (GridView) findViewById(R.id.idGridMenus);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);
        scrollingtext = (TextView) findViewById(R.id.scrollingtext);
        lnrScroll = (LinearLayout) findViewById(R.id.lnrScroll);

        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);

        mAdView = findViewById(R.id.adView);

        aHome_nivSchoolLogo = (ImageView) findViewById(R.id.aHome_nivSchoolLogo);
        String url = childItem.getSchoolThumbnailImgUrl();

        try {
            if (!url.equals("")) {
                Glide.with(this).load(url).centerCrop().into(aHome_nivSchoolLogo);
            }
        } catch (Exception e) {
            showToast(e.getMessage());
        }


        isBookEnabled = childItem.getBookEnable();
        BookLink = childItem.getBookLink();
        child_ID = Util_SharedPreference.getChildIdFromSP(HomeActivity.this);
        tv_schoolname = (TextView) findViewById(R.id.aHome_tvSchoolName);
        aHome_tvRegionalSchoolName = (TextView) findViewById(R.id.aHome_tvRegionalSchoolName);
        String regionalSchoolName = Util_SharedPreference.getRegionalSchoolnameFromSP(HomeActivity.this);

        aHome_tvRegionalSchoolName.setText(regionalSchoolName);
        if (!regionalSchoolName.equals("") && regionalSchoolName != null && !regionalSchoolName.equals("null")) {
            aHome_tvRegionalSchoolName.setVisibility(View.VISIBLE);
        } else {
            aHome_tvRegionalSchoolName.setVisibility(View.GONE);
        }
        tv_schoolname.setText(Util_SharedPreference.getSchoolnameFromSP(HomeActivity.this));
        tvSchoolAddress = (TextView) findViewById(R.id.aHome_tvSchoolAddress);
        tvSchoolAddress.setText(Util_SharedPreference.getSchooladdressFromSP(HomeActivity.this));

    }

    private void getNewUpdates(boolean dailyCheck) {
//        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
//        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(HomeActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        String Role = TeacherUtil_SharedPreference.getRole(HomeActivity.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("staff_role", "parent");
        jsonObject.addProperty("member_id", childItem.getChildID());
        jsonObject.addProperty("instituteid", childItem.getSchoolID());
        Log.d("jsonObjectReq", jsonObject.toString());
        Call<NewUpdatesModel> call = apiService.getNewUpdateDetails(jsonObject);

        call.enqueue(new Callback<NewUpdatesModel>() {
            @Override
            public void onResponse(Call<NewUpdatesModel> call, retrofit2.Response<NewUpdatesModel> response) {
                try {

                    Log.d("daily:code-res", response.code() + " - " + response);
                    newUpdatesDataList.clear();
                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("updates_Response", json);
                        int status = response.body().getStatus();
                        String message = response.body().getMessage();

                        if (status == 1) {
                            newUpdatesDataList = response.body().getData();
                            if (newUpdatesDataList.size() > 0) {

                                String name = menuList.get(0).getMenu_name();
                                if (!name.equals(updates)) {
                                    ParentMenuModel data = new ParentMenuModel(updates, "0");
                                    menuList.add(0, data);
                                    myAdapter.notifyDataSetChanged();
                                    idGridMenus.setAnimation(null);

                                }

                                String pos = TeacherUtil_SharedPreference.getLastVisibleUpdatesPosition(HomeActivity.this);
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = df.format(c.getTime());
                                SharedPreferences sharedPrefs = getSharedPreferences("DisplayTimePref", 0);
                                SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                                prefsEditor.putString("displayedTime", formattedDate).commit();
                                prefsEditor.apply();

                                Log.d("initial_pos1", pos);

                                if (dailyCheck) {
                                    initial_pos = 0;

                                    if (updatesManualPopup == null) {
                                        manualUpdatesPopup();
                                    } else if (!updatesManualPopup.isShowing()) {
                                        manualUpdatesPopup();
                                    }

                                } else {
                                    if (newUpdatesDataList.size() - 1 > Integer.parseInt(pos)) {
                                        newUpdatesPoup();
                                    } else {
                                        String date = sharedPrefs.getString("displayedTime", "");
                                        if (!date.equals(formattedDate)) {
                                            TeacherUtil_SharedPreference.putLastVisibleUpdatesPosition(HomeActivity.this, String.valueOf(0));
                                            newUpdatesPoup();

                                        }
                                    }
                                }


                            }
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NewUpdatesModel> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void manualUpdatesPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.new_updates_popup, null);
        updatesManualPopup = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        updatesManualPopup.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                updatesManualPopup.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        ImageView img = (ImageView) layout.findViewById(R.id.img);

        TextView lblTitle = (TextView) layout.findViewById(R.id.lblTitle);
        TextView lblContent = (TextView) layout.findViewById(R.id.lblContent);
        TextView lblSkip = (TextView) layout.findViewById(R.id.lblSkip);
        TextView lblNext = (TextView) layout.findViewById(R.id.lblNext);
        TextView lblPrevious = (TextView) layout.findViewById(R.id.lblPrevious);
        TextView lblClose = (TextView) layout.findViewById(R.id.lblClose);

        LinearLayout lnrParent = (LinearLayout) layout.findViewById(R.id.lnrParent);
        LinearLayout lnrContent = (LinearLayout) layout.findViewById(R.id.lnrContent);

        Typeface roboto_bold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");

        lblTitle.setTypeface(roboto_bold);
        lblSkip.setTypeface(roboto_bold);
        lblNext.setTypeface(roboto_bold);
        lblPrevious.setTypeface(roboto_bold);
        lblContent.setTypeface(roboto_regular);


        Animation slide_in_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter);
        Animation slide_out_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right);

        title = newUpdatesDataList.get(initial_pos).getUpdate_name();
        content = newUpdatesDataList.get(initial_pos).getUpdate_description();
        image_url = newUpdatesDataList.get(initial_pos).getDownloadable_image();
        redirect_url = newUpdatesDataList.get(initial_pos).getRedirect_link();

        lblTitle.setText(title);
        lblContent.setText(content);
        Glide.with(HomeActivity.this)
                .load(image_url)
                .into(img);


        if (newUpdatesDataList.size() == 1) {
            lblNext.setVisibility(View.GONE);
            lblClose.setVisibility(View.VISIBLE);

        }


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initial_pos = 0;
                updatesManualPopup.dismiss();
            }
        });
        lblClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initial_pos = 0;
                updatesManualPopup.dismiss();
            }
        });


        lblSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatesManualPopup.dismiss();
            }
        });

        lblNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblPrevious.setVisibility(View.VISIBLE);
                initial_pos = initial_pos + 1;
                if (initial_pos == newUpdatesDataList.size() - 1) {
                    lblNext.setVisibility(View.GONE);
                    lblClose.setVisibility(View.VISIBLE);
                }

                title = newUpdatesDataList.get(initial_pos).getUpdate_name();
                content = newUpdatesDataList.get(initial_pos).getUpdate_description();
                image_url = newUpdatesDataList.get(initial_pos).getDownloadable_image();
                redirect_url = newUpdatesDataList.get(initial_pos).getRedirect_link();

                lblTitle.setText(title);
                lblContent.setText(content);
                Glide.with(HomeActivity.this)
                        .load(image_url)
                        .into(img);

                img.startAnimation(slide_in_anim);
                lblContent.startAnimation(slide_in_anim);


            }
        });

        lblPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (initial_pos != 0) {
                    initial_pos = initial_pos - 1;
                }

                if (initial_pos == 0) {
                    lblPrevious.setVisibility(View.GONE);
                }

                if (initial_pos != newUpdatesDataList.size() - 1) {
                    lblNext.setVisibility(View.VISIBLE);
                    lblClose.setVisibility(View.GONE);

                }

                title = newUpdatesDataList.get(initial_pos).getUpdate_name();
                content = newUpdatesDataList.get(initial_pos).getUpdate_description();
                image_url = newUpdatesDataList.get(initial_pos).getDownloadable_image();
                redirect_url = newUpdatesDataList.get(initial_pos).getRedirect_link();

                lblTitle.setText(title);
                lblContent.setText(content);
                Glide.with(HomeActivity.this)
                        .load(image_url)
                        .into(img);

                img.startAnimation(slide_out_anim);
                lblContent.startAnimation(slide_out_anim);


            }
        });

        lnrContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatesManualPopup.dismiss();
                if (!redirect_url.equals("")) {
                    Intent receipt = new Intent(HomeActivity.this, NewUpdateWebView.class);
                    receipt.putExtra("URL", redirect_url);
                    receipt.putExtra("tittle", title);
                    startActivity(receipt);
                }
            }
        });

    }

    private void newUpdatesPoup() {
        String pos = TeacherUtil_SharedPreference.getLastVisibleUpdatesPosition(HomeActivity.this);
        initial_pos = Integer.parseInt(pos);
        Log.d("initial_pos2", String.valueOf(initial_pos));
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.new_updates_popup, null);
        updatespopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        updatespopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                updatespopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        ImageView img = (ImageView) layout.findViewById(R.id.img);

        TextView lblTitle = (TextView) layout.findViewById(R.id.lblTitle);
        TextView lblContent = (TextView) layout.findViewById(R.id.lblContent);
        TextView lblSkip = (TextView) layout.findViewById(R.id.lblSkip);
        TextView lblNext = (TextView) layout.findViewById(R.id.lblNext);
        TextView lblPrevious = (TextView) layout.findViewById(R.id.lblPrevious);
        TextView lblClose = (TextView) layout.findViewById(R.id.lblClose);
        LinearLayout lnrParent = (LinearLayout) layout.findViewById(R.id.lnrParent);
        LinearLayout lnrContent = (LinearLayout) layout.findViewById(R.id.lnrContent);

        Typeface roboto_bold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");

        lblTitle.setTypeface(roboto_bold);
        lblSkip.setTypeface(roboto_bold);
        lblNext.setTypeface(roboto_bold);
        lblPrevious.setTypeface(roboto_bold);
        lblContent.setTypeface(roboto_regular);


        Animation slide_in_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter);
        Animation slide_out_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right);

        title = newUpdatesDataList.get(initial_pos).getUpdate_name();
        content = newUpdatesDataList.get(initial_pos).getUpdate_description();
        image_url = newUpdatesDataList.get(initial_pos).getDownloadable_image();
        redirect_url = newUpdatesDataList.get(initial_pos).getRedirect_link();

        lblTitle.setText(title);
        lblContent.setText(content);
        Glide.with(HomeActivity.this)
                .load(image_url)
                .into(img);

        if (newUpdatesDataList.size() == 1) {
            lblNext.setVisibility(View.GONE);
            lblClose.setVisibility(View.VISIBLE);

        }


        if (initial_pos == newUpdatesDataList.size() - 1) {
            lblNext.setVisibility(View.GONE);
            lblClose.setVisibility(View.VISIBLE);

        }

        if (initial_pos == 0) {
            lblPrevious.setVisibility(View.GONE);
        }

        if (initial_pos != newUpdatesDataList.size() - 1) {
            lblNext.setVisibility(View.VISIBLE);
            lblClose.setVisibility(View.GONE);

        }
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initial_pos = 0;
                updatespopupWindow.dismiss();
            }
        });
        lblClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initial_pos = 0;
                updatespopupWindow.dismiss();
            }
        });

        lblSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pos = TeacherUtil_SharedPreference.getLastVisibleUpdatesPosition(HomeActivity.this);
                TeacherUtil_SharedPreference.putLastVisibleUpdatesPosition(HomeActivity.this, String.valueOf(Integer.parseInt(pos) + 1));
                updatespopupWindow.dismiss();
            }
        });

        lblNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblPrevious.setVisibility(View.VISIBLE);

                String pos = TeacherUtil_SharedPreference.getLastVisibleUpdatesPosition(HomeActivity.this);
                initial_pos = Integer.parseInt(pos) + 1;

                if (initial_pos == newUpdatesDataList.size() - 1) {
                    lblNext.setVisibility(View.GONE);
                    lblClose.setVisibility(View.VISIBLE);
                }

                title = newUpdatesDataList.get(initial_pos).getUpdate_name();
                content = newUpdatesDataList.get(initial_pos).getUpdate_description();
                image_url = newUpdatesDataList.get(initial_pos).getDownloadable_image();
                redirect_url = newUpdatesDataList.get(initial_pos).getRedirect_link();
                TeacherUtil_SharedPreference.putLastVisibleUpdatesPosition(HomeActivity.this, String.valueOf(initial_pos));

                lblTitle.setText(title);
                lblContent.setText(content);
                Glide.with(HomeActivity.this)
                        .load(image_url)
                        .into(img);

                img.startAnimation(slide_in_anim);
                lblContent.startAnimation(slide_in_anim);
            }
        });

        lblPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pos = TeacherUtil_SharedPreference.getLastVisibleUpdatesPosition(HomeActivity.this);
                initial_pos = Integer.parseInt(pos);

                if (initial_pos != 0) {
                    initial_pos = Integer.parseInt(pos) - 1;
                }

                if (initial_pos == 0) {
                    lblPrevious.setVisibility(View.GONE);
                }

                if (initial_pos != newUpdatesDataList.size() - 1) {
                    lblNext.setVisibility(View.VISIBLE);
                    lblClose.setVisibility(View.GONE);
                }

                title = newUpdatesDataList.get(initial_pos).getUpdate_name();
                content = newUpdatesDataList.get(initial_pos).getUpdate_description();
                image_url = newUpdatesDataList.get(initial_pos).getDownloadable_image();
                redirect_url = newUpdatesDataList.get(initial_pos).getRedirect_link();
                TeacherUtil_SharedPreference.putLastVisibleUpdatesPosition(HomeActivity.this, String.valueOf(initial_pos));

                lblTitle.setText(title);
                lblContent.setText(content);
                Glide.with(HomeActivity.this)
                        .load(image_url)
                        .into(img);

                img.startAnimation(slide_out_anim);
                lblContent.startAnimation(slide_out_anim);


            }
        });

        lnrContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatespopupWindow.dismiss();
                if (!redirect_url.equals("")) {
                    Intent receipt = new Intent(HomeActivity.this, NewUpdateWebView.class);
                    receipt.putExtra("URL", redirect_url);
                    receipt.putExtra("tittle", title);
                    startActivity(receipt);
                }
            }
        });
    }

    private void getContactPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        } else {
            checkIfContactsExist();
            Log.d("Granted", "Granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                settingsContactPermission();
            }
        }
    }

    private void settingsContactPermission() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.settings_app_permission, null);
        SettingspopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        SettingspopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                SettingspopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        TextView lblNotNow = (TextView) layout.findViewById(R.id.lblNotNow);
        TextView lblSettings = (TextView) layout.findViewById(R.id.lblSettings);
        lblNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingspopupWindow.dismiss();
                Constants.Menu_ID = "101";
//                ShowAds.getAds(HomeActivity.this, adImage, slider, "Dashboard", mAdView);
                getMenuDetails();
            }
        });

        lblSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SettingspopupWindow.dismiss();
                    startActivity(
                            new Intent(
                                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null)
                            )
                    );
                } catch (Exception e) {
                    Log.d("saveexception", e.toString());
                }
            }
        });
    }

    private void getMenuDetails() {
      //  LoadingView.showProgress(HomeActivity.this);

//        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
//        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);


        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(HomeActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(HomeActivity.this);
        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(HomeActivity.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(HomeActivity.this, "child_list");
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "parent");
        jsonObject.addProperty("id", childItem.getChildID());
        jsonObject.addProperty("schoolid", childItem.getSchoolID());
        jsonArray.add(jsonObject);

        JsonObject jsonObjectlanguage = new JsonObject();
        jsonObjectlanguage.add("MemberData", jsonArray);
        jsonObjectlanguage.addProperty("LanguageId", "1");
        jsonObjectlanguage.addProperty("CountryID", countryId);

        Log.d("Menu_Request", jsonObjectlanguage.toString());
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.ChangeLanguage(jsonObjectlanguage);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                //  LoadingView.hideProgress();

                Log.d("GetMenuDetails:code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("GetMenuDetails:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");

                        if (status.equals("1")) {
                            String menu_name = jsonObject.getString("menu_name");

                            String[] name = menu_name.split(",");
                            isParentMenuNames.clear();
                            Collections.addAll(isParentMenuNames, name);

                            contact_alert_title = jsonObject.getString("contact_alert_title");
                            contact_alert_Content = jsonObject.getString("contact_alert_content");
                            contact_display_name = jsonObject.getString("contact_display_name");
                            contact_numbers = jsonObject.getString("contact_numbers");
                            contact_button = jsonObject.getString("contact_button_content");

                            if (!contact_numbers.equals("")) {
                                contacts = contact_numbers.split(",");
                                getContactPermission();
                            }

                            String alert_message = jsonObject.getString("alert_message");
                            if (!alert_message.equals("")) {
                                lnrScroll.setVisibility(View.VISIBLE);
                                scrollingtext.setText(alert_message);
                            } else {
                                lnrScroll.setVisibility(View.GONE);
                            }
                            UnreadCount();
                        }
                    }
                } catch (Exception e) {
                    Log.e("menu:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                //  LoadingView.hideProgress();

                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }


    private void checkIfContactsExist() {

        exist_Count = 0;
        ContentResolver contentResolver = HomeActivity.this.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID};
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
        String[] selectionArguments = {contact_display_name};

        // Query for the display name.
        try (Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null)) {
            if (cursor != null) {
                exist_Count = cursor.getCount();
                Log.d("exist_Count", String.valueOf(exist_Count));

                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String fetchedName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Log.d("Fetched Name", fetchedName);
                }
            } else {
                Log.d("Error", "Cursor is null!");
            }
        } catch (Exception e) {
            Log.e("Query Error", e.getMessage());
        }


        Contact_Count = 0;
        for (String number : contacts) {
            if (number != null) {
                Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
                String[] mPhoneNumberProjection = {
                        ContactsContract.PhoneLookup._ID,
                        ContactsContract.PhoneLookup.NUMBER,
                        ContactsContract.PhoneLookup.DISPLAY_NAME
                };

                try (Cursor cur = contentResolver.query(lookupUri, mPhoneNumberProjection, null, null, null)) {
                    if (cur != null && cur.moveToFirst()) {
                        Contact_Count++;
                    } else {
                        Log.d("No Match Found", "Number: " + number);
                        exist_Count = 0;
                    }
                } catch (Exception e) {
                    Log.e("Phone Query Error", e.getMessage());
                }
            }
        }

        Log.d("exist_Count1", String.valueOf(exist_Count));

        // Check and save contact if necessary.
        if (contacts.length != Contact_Count) {
            if (exist_Count == 0 || exist_Count < contacts.length) {
                contactSaveContent();
            }
        }
    }

//    private void checkIfContactsExist() {
//        exist_Count = 0;
//        ContentResolver contentResolver = HomeActivity.this.getContentResolver();
//        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        String[] projection = new String[]{ContactsContract.PhoneLookup._ID};
//        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
//        String[] selectionArguments = {contact_display_name};
//
//
//        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null);
//        exist_Count = cursor.getCount();
//        Log.d("exist_Count", String.valueOf(exist_Count));
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//
//            }
//        }
//
//        Contact_Count = 0;
//        for (int i = 0; i < contacts.length; i++) {
//            String number = contacts[i];
//            if (number != null) {
//                Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
//                String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
//                Cursor cur = HomeActivity.this.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
//                try {
//                    if (cur.getCount() > 0) {
//                        if (cur.moveToFirst()) {
//                            Contact_Count = Contact_Count + 1;
//                            int indexName = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//                            Display_Name = cur.getString(indexName);
//                            Log.d("Display_Name", Display_Name);
//                            if (!Display_Name.equals(contact_display_name)) {
//                                Contact_Count = Contact_Count - 1;
//                            }
//                        }else {
//                            Log.d("isComing","moveToFirst");
//                        }
//                    } else {
//                        Log.d("isComing","isComing");
//                        exist_Count = 0;
//                        Contact_Count = Contact_Count - 1;
//                    }
//                } finally {
//                    if (cur != null)
//                        cur.close();
//                }
//            }
//        }
//        Log.d("exist_Count1", String.valueOf(exist_Count));
//
//        if (contacts.length != Contact_Count) {
//            if (exist_Count == 0 || exist_Count < contacts.length) {
//                contactSaveContent();
//            }
//        }
//    }

    private void contactSaveContent() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.save_contact_alert, null);
        popupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                popupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        TextView btnSaveContact = (TextView) layout.findViewById(R.id.btnSaveContact);
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        TextView lblHeader = (TextView) layout.findViewById(R.id.lblHeader);
        TextView lblContent = (TextView) layout.findViewById(R.id.lblContent);
        lblHeader.setText(contact_alert_title);
        lblContent.setText(contact_alert_Content);
        btnSaveContact.setText(contact_button);

        btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                saveContacts();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void saveContacts() {

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.conatct_school);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ArrayList<ContentValues> data = new ArrayList<ContentValues>();
        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        ContentValues row = new ContentValues();
        row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
        row.put(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray);
        data.add(row);
        for (int i = 0; i < contacts.length; i++) {
            ContentValues row_Number = new ContentValues();
            row_Number.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row_Number.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contacts[i]);
            row_Number.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            data.add(row_Number);
        }
        intent.putExtra(ContactsContract.Intents.Insert.NAME, contact_display_name);
        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
        startActivityForResult(intent, 100);
    }

    public void deleteCache(Activity activity) {
        try {
            File dir = activity.getCacheDir();
            if (deleteDir(dir)) {
                Toast.makeText(activity, R.string.Cache_cleared, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, R.string.Cache_not_cleared, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:
                Intent homescreen = new Intent(HomeActivity.this, ChildrenScreen.class);
                homescreen.putExtra("HomeScreen", "1");
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:
                Intent faq = new Intent(HomeActivity.this, FAQScreen.class);
                startActivity(faq);
                break;

            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(HomeActivity.this, "change");
                startActivity(new Intent(HomeActivity.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:
                Util_Common.popUpMenu(HomeActivity.this, v, "1");

                break;
            default:
                break;
        }
    }



    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(HomeActivity.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];
        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        AlertDialog alertDialog;
        builder.setTitle(R.string.choose_language);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(countriesArray, 0, null);
        builder.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                final Languages model = LanguageList.get(selectedPosition);
                String ID = model.getStrLanguageID();
                String code = model.getScriptCode();
                changeLanguage(code, ID);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.pop_password_btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
    }

    private void changeLanguage(String code, String id) {
        TeacherUtil_SharedPreference.putLanguageType(HomeActivity.this, code);
        languageChangeApi(id, code);
    }

    private void languageChangeApi(String id, final String lang) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(HomeActivity.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(HomeActivity.this, "child_list");
        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(HomeActivity.this);
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        if (schools_list != null) {
            for (int i = 0; i < schools_list.size(); i++) {
                final TeacherSchoolsModel model = schools_list.get(i);
                jsonObject.addProperty("type", "staff");
                jsonObject.addProperty("id", model.getStrStaffID());
                jsonObject.addProperty("schoolid", model.getStrSchoolID());
                jsonArray.add(jsonObject);

            }
        }
        if (childList != null) {
            for (int i = 0; i < childList.size(); i++) {
                final Profiles model = childList.get(i);
                jsonObject.addProperty("type", "parent");
                jsonObject.addProperty("id", model.getChildID());
                jsonObject.addProperty("schoolid", model.getSchoolID());
                jsonArray.add(jsonObject);
            }
        }

//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        if (!this.isFinishing())
//            mProgressDialog.show();

        JsonObject jsonObjectlanguage = new JsonObject();
        jsonObjectlanguage.add("MemberData", jsonArray);
        jsonObjectlanguage.addProperty("LanguageId", id);
        jsonObjectlanguage.addProperty("CountryID", countryId);
        Log.d("Request", jsonObject.toString());
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.ChangeLanguage(jsonObjectlanguage);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
                Log.d("VersionCheck:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());
                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");

                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), HomeActivity.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), HomeActivity.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), HomeActivity.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), HomeActivity.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), HomeActivity.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), HomeActivity.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), HomeActivity.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), HomeActivity.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), HomeActivity.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), HomeActivity.this);

                        if (Integer.parseInt(status) > 0) {
                            showToast(message);

                            Locale myLocale = new Locale(lang);
                            //saveLocale(lang);
                            Locale.setDefault(myLocale);
                            Configuration config = new Configuration();
                            config.locale = myLocale;
                            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                            recreate();

                        } else {
                            showToast(message);
                        }

                    }

                } catch (Exception e) {
                    Log.e("VersionCheck:Exception", e.getMessage());

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    public void showLogoutAlert(final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(R.string.txt_menu_logout);
        alertDialog.setMessage(R.string.want_to_logut);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                TeacherUtil_SharedPreference.putInstall(activity, "1");
                TeacherUtil_SharedPreference.putOTPNum(activity, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(activity, "");
                TeacherUtil_SharedPreference.clearStaffSharedPreference(activity);
                activity.startActivity(new Intent(activity, TeacherSignInScreen.class));
                activity.finish();
            }
        });
        alertDialog.setPositiveButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        Button negativebutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        negativebutton.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isProgressBar.setVisibility(View.VISIBLE);
        if (hasPermission()) {
            Constants.Menu_ID = "101";
            ShowAds.getAds(HomeActivity.this, adImage, slider, "Dashboard", mAdView);
            getMenuDetails();
        }
    }
    @Override
    protected void onDestroy() {

        Log.d("onDestroyHome","onDestroy");
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("onPauseHome","onPause");

        if (mAdView != null) {
            mAdView.pause();  // Pause the ad
        }
        super.onPause();
    }

    private void checkAndRequestPermission() {
        // Check if the permission is granted
        if (!hasPermission()) {
            // Request the permission
            requestPermission();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SCHEDULE_EXACT_ALARM}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    private boolean hasPermission() {
        // Check if you have the necessary permission
        // Return true if the permission is granted, false otherwise
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(CONTACTS_PERMISSION) == PackageManager.PERMISSION_GRANTED;
        }
        return true;  // If targeting SDK < 23, permission is granted at installation time
    }

    private void UnreadCount() {
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(HomeActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(HomeActivity.this);
        String strChildID = Util_SharedPreference.getChildIdFromSP(HomeActivity.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(HomeActivity.this);
        Log.d("ChangePass:mob-Old-New", mobNumber + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_overallUnreadCount(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.GetOverallUnreadCount(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("overallUnreadCount:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("overallUnreadCount:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);

                        String emgvoicecount = jsonObject.getString("EMERGENCYVOICE");
                        String voicemsgcount = jsonObject.getString("VOICE");
                        String textmessagecount = jsonObject.getString("SMS");
                        String photoscount = jsonObject.getString("IMAGE");
                        String documentcount = jsonObject.getString("PDF");
                        String noticeboard = jsonObject.getString("NOTICEBOARD");
                        String examtest = jsonObject.getString("EXAM");
                        String schoolevent = jsonObject.getString("EVENTS");
                        String homework = jsonObject.getString("HOMEWORK");
                        String assignment = jsonObject.getString("ASSIGNMENT");
                        String video = jsonObject.getString("VIDEO");
                        String onlineClass = jsonObject.getString("ONLINECLASS");
                        String quiz = jsonObject.getString("QUIZEXAM");
                        String lsrw = jsonObject.getString("LSRW");
                        String exam_marks = jsonObject.getString("EXAMMARKS");

                        menuList.clear();

                        if (updatespopupWindow == null) {
                            getNewUpdates(false);
                        } else {
                            if (!updatespopupWindow.isShowing()) {
                                getNewUpdates(false);
                            }
                        }

                        for (int i = 0; i < isParentMenuNames.size(); i++) {
                            String name = isParentMenuNames.get(i);
                            String substring = name.substring(Math.max(name.length() - 2, 0));
                            String substring1 = name.substring(Math.max(name.length() - 3, 0));

                            if (substring.equals("_0")) {
                                count = emgvoicecount;
                            } else if (substring.equals("_1")) {
                                count = voicemsgcount;

                            } else if (substring.equals("_2")) {
                                count = textmessagecount;

                            } else if (substring.equals("_3")) {
                                count = homework;

                            } else if (substring.equals("_4")) {
                                count = examtest;

                            } else if (substring.equals("_5")) {
                                count = exam_marks;

                            } else if (substring.equals("_6")) {
                                count = documentcount;

                            } else if (substring.equals("_7")) {
                                count = noticeboard;

                            } else if (substring.equals("_8")) {
                                count = schoolevent;

                            } else if (substring.equals("_9")) {
                                count = "0";

                            } else if (substring1.equals("_10")) {
                                count = "0";

                            } else if (substring1.equals("_11")) {
                                count = "0";

                            } else if (substring1.equals("_12")) {
                                count = photoscount;

                            } else if (substring1.equals("_13")) {
                                count = "0";

                            } else if (substring1.equals("_14")) {
                                count = "0";

                            } else if (substring1.equals("_15")) {
                                count = "0";

                            } else if (substring1.equals("_16")) {
                                count = "0";

                            } else if (substring1.equals("_17")) {
                                count = "0";

                            } else if (substring1.equals("_18")) {
                                count = assignment;

                            } else if (substring1.equals("_19")) {
                                count = video;

                            } else if (substring1.equals("_20")) {
                                count = onlineClass;

                            } else if (substring1.equals("_21")) {
                                count = quiz;

                            } else if (substring1.equals("_22")) {
                                count = lsrw;

                            } else if (substring1.equals("_23")) {
                                count = "0";
                            } else if (substring1.equals("_24")) {
                                count = "0";
                            } else if (substring1.equals("_25")) {
                                count = "0";
                            }


                            if (!substring1.equals("_15")) {
                                menuList.add(new ParentMenuModel(name, count));
                            } else {
                                if (isBookEnabled.equals("1")) {
                                    menuList.add(new ParentMenuModel(name, count));
                                }
                            }
                        }
                        myAdapter = new ChildMenuAdapter(HomeActivity.this, R.layout.child_menu_item, menuList, BookLink, rytParent, new UpdatesListener() {
                            @Override
                            public void onMsgItemClick(String name) {
                                if (name.equals(updates)) {
                                    if (newUpdatesDataList.size() > 0) {
                                        getNewUpdates(true);
                                    }
                                }
                            }
                        });
                        // idGridMenus.setSelection(TeacherUtil_Common.scroll_to_position);
                        idGridMenus.setAdapter(myAdapter);
                        isProgressBar.setVisibility(View.GONE);
                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("UnreadCount:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
