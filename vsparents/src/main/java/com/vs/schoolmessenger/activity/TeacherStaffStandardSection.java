package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VIDEOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE_HW;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherNewSectionsListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.aws.AwsUploadingPreSigned;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSectionListListener;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherSectionsListNEW;
import com.vs.schoolmessenger.model.TeacherStandardSectionsListModel;
import com.vs.schoolmessenger.model.TeacherSubjectModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.CurrentDatePicking;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.UploadCallback;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherStaffStandardSection extends AppCompatActivity {

    Spinner spinStandard, spinSection, spinSubject;
    ArrayAdapter<String> adaStd;
    ArrayAdapter<String> adaSection;
    ArrayAdapter<String> adaSubject;
    Button btnSend, btnSelectStudents;
    LinearLayout llSubject;

    List<String> listStd = new ArrayList<>();
    List<String> listSection;
    List<String> listSectionID;
    List<TeacherSectionsListNEW> arrSectionCollections;

    ImageView genTextPopup_ToolBarIvBack;
    String strStdName, strSecName, strSecCode, strSubjectCode, strSubjectName, strTotalStudents;
    String strToWhom = "";
    String SchoolID, loginType, StaffID, strtittle, strmessage, Description, duration, filepath;
    //
    RecyclerView rvSectionList;
    List<String> listSubjectName;
    List<String> listSubjectCode;
    List<TeacherSubjectModel> arrSubjectCollections;
    List<String> listTotalStudentsInSec;
    Button btnSelectSubjects;
    ArrayList<TeacherSubjectModel> listSubjects1 = new ArrayList<TeacherSubjectModel>();
    String voicetype = "", strPDFFilepath, strVideoFilePath, VideoDescription;
    ArrayList<String> slectedImagePath = new ArrayList<String>();
    ArrayList<TeacherSubjectModel> SubjectsList = new ArrayList<TeacherSubjectModel>();
    Button btnGetSubject;
    TextView lblSubject;
    String sectionsTargetCode = "";
    String fileNameDateTime;
    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType = "";
    String uploadFilePath = "";
    int pathIndex = 0;
    String HOMEWORK_TYPE = "";
    private final ArrayList<TeacherStandardSectionsListModel> arrStandardsAndSectionsList = new ArrayList<>();
    private int iRequestCode = 0;
    private final ArrayList<TeacherSectionsListNEW> seletedSectionsList = new ArrayList<>();
    private int i_sections_count = 0;
    private final ArrayList<String> UploadedS3URlList = new ArrayList<>();
    AwsUploadingPreSigned isAwsUploadingPreSigned;
    private int totalFiles;
    private int uploadedFiles;
    private ProgressBar progressBar;
    private TextView progressText;
    LinearLayout lnrProgress;
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
        setContentView(R.layout.teacher_activity_staff_standard_section);


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        Log.d("REQUEST_CODE", String.valueOf(iRequestCode));

        strToWhom = getIntent().getExtras().getString("TO", "");
        SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
        StaffID = getIntent().getExtras().getString("STAFF_ID", "");
        strtittle = getIntent().getExtras().getString("TITTLE", "");
        strmessage = getIntent().getExtras().getString("MESSAGE", "");
        Description = getIntent().getExtras().getString("TITTLE", "");
        duration = getIntent().getExtras().getString("DURATION", "");
        filepath = getIntent().getExtras().getString("FILEPATH", "");
        llSubject = (LinearLayout) findViewById(R.id.staffStdSecSub_llSubject);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        lnrProgress = findViewById(R.id.lnrProgress);

        voicetype = getIntent().getExtras().getString("VOICE", "");
        strPDFFilepath = getIntent().getExtras().getString("FILE_PATH_PDF", "");
        strVideoFilePath = getIntent().getExtras().getString("VIDEO_FILE_PATH", "");
        VideoDescription = getIntent().getExtras().getString("VIDEO_DESCRIPTION", "");

        if (strPDFFilepath.equals("")) {
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }
        isAwsUploadingPreSigned = new AwsUploadingPreSigned();


        if (TeacherUtil_Common.listschooldetails.size() == 1) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        }

        genTextPopup_ToolBarIvBack = (ImageView) findViewById(R.id.genTextPopup_ToolBarIvBack);
        genTextPopup_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSelectStudents = (Button) findViewById(R.id.staffStdSecSub_btnToStudents);
        btnSelectStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TeacherSectionModel stdSec = new TeacherSectionModel(false, strStdName, strSecName, strSecCode, "", "", strTotalStudents, "0", false);
                Intent inStud = new Intent(TeacherStaffStandardSection.this, TeacherAttendanceStudentList.class);

                inStud.putExtra("STD_SEC", stdSec);
                inStud.putExtra("REQUEST_CODE", iRequestCode);
                inStud.putExtra("SCHOOL_ID", SchoolID);
                inStud.putExtra("SECCODE", strSecCode);
                inStud.putExtra("SUBCODE", strSubjectCode);
                inStud.putExtra("filepath", filepath);
                inStud.putExtra("duration", duration);
                inStud.putExtra("Description", Description);
                inStud.putExtra("Message", strmessage);
                inStud.putExtra("VOICE", voicetype);
                inStud.putExtra("FILE_PATH_PDF", strPDFFilepath);
                inStud.putExtra("PATH_LIST", slectedImagePath);

                startActivityForResult(inStud, iRequestCode);
            }
        });

        if (strToWhom.equals("STU")) {
            if (iRequestCode == STAFF_VOICE) {
                btnSelectStudents.setVisibility(View.VISIBLE);
            }
            if (iRequestCode == STAFF_TEXT) {
                btnSelectStudents.setVisibility(View.VISIBLE);
            }
            if (iRequestCode == STAFF_PHOTOS) {
                btnSelectStudents.setVisibility(View.VISIBLE);
            }
        }

        btnSend = (Button) findViewById(R.id.staffStdSecSub_btnSend);
        btnSelectSubjects = (Button) findViewById(R.id.btnSelectSubjects);


        if (strToWhom.equals("SEC")) {
            if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {
                btnSend.setVisibility(View.GONE);
                btnSelectSubjects.setVisibility(View.VISIBLE);

            }
        }

        if (strToWhom.equals("STU")) {
            if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {
                btnSend.setVisibility(View.GONE);
                btnSelectSubjects.setVisibility(View.VISIBLE);

            }
        }

        btnSelectSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JsonArray jsonArrayschoolstd = new JsonArray();
                for (int i = 0; i < seletedSectionsList.size(); i++) {
                    TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }

                Intent i = new Intent(TeacherStaffStandardSection.this, SubjectListScreen.class);
                i.putExtra("ExamName", strtittle);
                i.putExtra("ExamSyllabus", strmessage);
                i.putExtra("REQUEST_CODE", iRequestCode);
                i.putExtra("TO", strToWhom);
                i.putExtra("SECCODE", strSecCode);
                i.putExtra("SectionList", jsonArrayschoolstd.toString());
                i.putExtra("SectionList123", seletedSectionsList);
                i.putExtra("SubjectsList", listSubjects1);

                TeacherSectionModel stdSec = new TeacherSectionModel(false, strStdName, strSecName, strSecCode, "", "", strTotalStudents, "0", false);
                Intent inStud = new Intent(TeacherStaffStandardSection.this, TeacherAttendanceStudentList.class);
                i.putExtra("STD_SEC", stdSec);
                inStud.putExtra("STD_SEC", stdSec);
                inStud.putExtra("REQUEST_CODE", iRequestCode);
                inStud.putExtra("SCHOOL_ID", SchoolID);
                inStud.putExtra("SUBCODE", strSubjectCode);
                inStud.putExtra("filepath", filepath);
                inStud.putExtra("duration", duration);
                inStud.putExtra("Description", Description);
                inStud.putExtra("Message", strmessage);

                startActivity(i);

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (strToWhom.equals("SEC")) {
                    if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {
                        SendExamhomework();
                    }
                    if (iRequestCode == STAFF_VOICE) {
                        if (voicetype.equals("VoiceHistory")) {
                            sendVoiceFromVoiceHistory();
                        } else {
                            SendVoiceToEntireSection();
                        }
                    }
                    if (iRequestCode == STAFF_TEXT) {
                        SendSMSToEntireSection();
                    }
                    if (iRequestCode == STAFF_PHOTOS || iRequestCode == PRINCIPAL_PHOTOS) {

                        if (!strPDFFilepath.equals("")) {
                            contentType = "application/pdf";
                            slectedImagePath.clear();
                            slectedImagePath.add(strPDFFilepath);
                            UploadedS3URlList.clear();
                            showLoading();
                            isUploadAWS("pdf", ".pdf", "");
                        } else {
                            contentType = "image/png";
                            UploadedS3URlList.clear();
                            showLoading();
                            isUploadAWS("image", "IMG", "");
                        }
                    }
                    if (iRequestCode == STAFF_VOICE_HW || iRequestCode == PRINCIPAL_VOICE_HW) {

                        if (strSubjectCode != null) {
                            SendVoiceToEntireSectionHW();
                        } else {
                            showToast(getResources().getString(R.string.Please_select_subject));
                        }
                    }
                    if (iRequestCode == STAFF_TEXT_HW || iRequestCode == PRINCIPAL_TEXT_HW) {
                        if (strSubjectCode != null) {


                            if (!strPDFFilepath.equals("")) {
                                HOMEWORK_TYPE = "PDF";
                                contentType = "application/pdf";
                                slectedImagePath.clear();
                                slectedImagePath.add(strPDFFilepath);
                                UploadedS3URlList.clear();
                                showLoading();
                                isUploadAWS("pdf", ".pdf", "");
                            } else if (!filepath.equals("")) {
                                HOMEWORK_TYPE = "VOICE";
                                contentType = "audio/mp3";
                                slectedImagePath.clear();
                                slectedImagePath.add(filepath);
                                UploadedS3URlList.clear();
                                showLoading();
                                isUploadAWS("audio", ".mp3", "");
                            } else {
                                HOMEWORK_TYPE = "IMAGE";
                                contentType = "image/png";
                                UploadedS3URlList.clear();
                                showLoading();
                                isUploadAWS("image", "IMG", "");
                            }

//                            SendTextToEntireSectionHW();
                        } else {
                            showToast(getResources().getString(R.string.Please_select_subject));

                        }
                    }
                    if (iRequestCode == PRINCIPAL_VOICE) {

                        if (voicetype.equals("VoiceHistory")) {
                            sendVoiceFromVoiceHistory();
                        } else {
                            SendVoiceToEntireSection();
                        }
                    }
                    if (iRequestCode == PRINCIPAL_TEXT) {
                        SendSMSToEntireSection();
                    }

                    if (iRequestCode == PRINCIPAL_VIDEOS) {
                        sendVideosToSections("3");

                    }

                    if (iRequestCode == PRINCIPAL_MEETING_URL) {

                        if (strSubjectCode != null) {
                            sendOnlineClassToSections();
                        } else {
                            showToast(getResources().getString(R.string.Please_select_subject));

                        }
                    }

                    if (iRequestCode == STAFF_MEETING_URL) {

                        if (strSubjectCode != null) {
                            sendOnlineClassToSections();
                        } else {
                            showToast(getResources().getString(R.string.Please_select_subject));

                        }
                    }

                }
                if (strToWhom.equals("STU")) {
                    if (iRequestCode == STAFF_VOICE) {

                        if (voicetype.equals("VoiceHistory")) {
                            sendVoiceToSpecificSctionFromVoiceHistory();
                        } else {
                            SendVoiceToSpecificSection();
                        }
                    }
                    if (iRequestCode == STAFF_TEXT) {
                        SendSMSToSpecificSection();
                    }
                    if (iRequestCode == STAFF_PHOTOS) {

                        if (!strPDFFilepath.equals("")) {

                            contentType = "application/pdf";
                            slectedImagePath.clear();
                            slectedImagePath.add(strPDFFilepath);
                            UploadedS3URlList.clear();
                            showLoading();
                            isUploadAWS("pdf", ".pdf", "specific");
                        } else {

                            contentType = "image/png";
                            UploadedS3URlList.clear();
                            showLoading();
                            isUploadAWS("image", "IMG", "specific");
                        }
                    }
                    if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {

                        SendExamhomeworkSpecificsection();
                    }
                }

            }
        });


        btnSend.setEnabled(false);

        btnSelectSubjects.setEnabled(true);
        rvSectionList = (RecyclerView) findViewById(R.id.staffStdSecSub_rvSectionList);
        spinStandard = (Spinner) findViewById(R.id.staffStdSecSub_spinStandard);
        spinSection = (Spinner) findViewById(R.id.staffStdSecSub_spinSection);
        spinSubject = (Spinner) findViewById(R.id.staffStdSecSub_spinSubject);
        btnGetSubject = (Button) findViewById(R.id.btnGetSubject);
        lblSubject = (TextView) findViewById(R.id.staffStdSecSub_tv4);
        btnGetSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seletedSectionsList.size() > 0) {
                    getSubjectsApi();
                } else {
                    showToast(getResources().getString(R.string.selecte_atleast_one_section));
                }
            }
        });

        if (strToWhom.equals("SEC")) {
            rvSectionList.setVisibility(View.VISIBLE);
            spinSection.setVisibility(View.GONE);
            btnSend.setEnabled(false);
            btnSelectSubjects.setEnabled(false);
        } else {
            rvSectionList.setVisibility(View.GONE);
            spinSection.setVisibility(View.VISIBLE);
            btnSend.setEnabled(true);
            btnSelectSubjects.setEnabled(true);
        }
        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherStaffStandardSection.this);


        if (iRequestCode == PRINCIPAL_MEETING_URL || iRequestCode == STAFF_MEETING_URL || iRequestCode == PRINCIPAL_VOICE_HW || iRequestCode == PRINCIPAL_TEXT_HW || iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW || iRequestCode == STAFF_TEXT_EXAM || iRequestCode == PRINCIPAL_EXAM_TEST) {
            standardsAndSectoinsListHWAPI();

            if (iRequestCode == PRINCIPAL_MEETING_URL || iRequestCode == STAFF_MEETING_URL || iRequestCode == PRINCIPAL_VOICE_HW || iRequestCode == PRINCIPAL_TEXT_HW || iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW) {
                llSubject.setVisibility(View.VISIBLE);
            }

        } else {
            standardsAndSectoinsListAPI1();
        }


        spinStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSectionCollections = new ArrayList<>();

                arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position).getListSectionsNew());


                if (strToWhom.equals("SEC")) {
                    strStdName = listStd.get(position);
                    listSubjects1.clear();
                    seletedSectionsList.clear();
                    listSectionsForSelectedStandard();

                } else {
                    listSubjects1.clear();
                    listSection = new ArrayList<>();
                    listSectionID = new ArrayList<>();
                    listTotalStudentsInSec = new ArrayList<>();
                    strStdName = listStd.get(position);


                    arrSubjectCollections = new ArrayList<>();
                    for (int i = 0; i < arrSubjectCollections.size(); i++) {
                        TeacherSubjectModel subjectList;
                        subjectList = new TeacherSubjectModel(arrSubjectCollections.get(i).getStrSubName(), arrSubjectCollections.get(i).getStrSubCode(), false);
                        listSubjects1.add(subjectList);
                    }

                    for (int i = 0; i < arrSectionCollections.size(); i++) {
                        listSection.add(arrSectionCollections.get(i).getStrSectionName());
                        listSectionID.add(arrSectionCollections.get(i).getStrSectionCode());
                        listTotalStudentsInSec.add(arrSectionCollections.get(i).getStrTotalStudents());

                    }
                    adaSection = new ArrayAdapter<>(TeacherStaffStandardSection.this, R.layout.teacher_spin_title, listSection);
                    adaSection.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                    spinSection.setAdapter(adaSection);

                }

                if (iRequestCode == PRINCIPAL_MEETING_URL || iRequestCode == STAFF_MEETING_URL || iRequestCode == PRINCIPAL_VOICE_HW || iRequestCode == PRINCIPAL_TEXT_HW || iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW || iRequestCode == STAFF_TEXT_EXAM || iRequestCode == PRINCIPAL_EXAM_TEST) {
                    listSubjectName = new ArrayList<>();
                    listSubjectCode = new ArrayList<>();
                    arrSubjectCollections = new ArrayList<>();
                    arrSubjectCollections.addAll(arrStandardsAndSectionsList.get(position).getListSubjects());

                    for (int i = 0; i < arrSubjectCollections.size(); i++) {

                        TeacherSubjectModel subjectList;
                        subjectList = new TeacherSubjectModel(arrSubjectCollections.get(i).getStrSubName(), arrSubjectCollections.get(i).getStrSubCode(), false);
                        listSubjects1.add(subjectList);
                    }


                    adaSubject = new ArrayAdapter<>(TeacherStaffStandardSection.this, R.layout.teacher_spin_title, listSubjectName);
                    adaSubject.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                    spinSubject.setAdapter(adaSubject);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSecName = listSection.get(position);
                strSecCode = listSectionID.get(position);
                strTotalStudents = listTotalStudentsInSec.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSubjectCode = listSubjectCode.get(position);
                strSubjectName = listSubjectName.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * FILE UPLOAD TO AWS
     */

    private void isUploadAWS(String contentType, String isType, String value) {

//        totalFiles = slectedImagePath.size();
//        uploadedFiles = 0;
//        lnrProgress.setVisibility(View.VISIBLE);
//        progressBar.setMax(totalFiles);
//        progressBar.setProgress(0);
//        progressText.setText("0/" + totalFiles);

        Log.d("selectedImagePath", String.valueOf(slectedImagePath.size()));

        String currentDate = CurrentDatePicking.getCurrentDate();

        for (int i = 0; i < slectedImagePath.size(); i++) {
            AwsUploadingFile(String.valueOf(slectedImagePath.get(i)), SchoolID, contentType, isType, value);
        }
    }


    private void AwsUploadingFile(String isFilePath, String bucketPath, String isFileExtension, String filetype, String type) {
        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherStaffStandardSection.this);

        isAwsUploadingPreSigned.getPreSignedUrl(isFilePath, bucketPath, isFileExtension, this, countryID, true, false, new UploadCallback() {
            @Override
            public void onUploadSuccess(String response, String isAwsFile) {
                Log.d("Upload Success", response);
                UploadedS3URlList.add(isAwsFile);

//                if (UploadedS3URlList.size() <= slectedImagePath.size()) {
//                    uploadedFiles++;
//                    updateProgressBar();
//                }

                if (UploadedS3URlList.size() == slectedImagePath.size()) {
                    if (iRequestCode == STAFF_TEXT_HW || iRequestCode == PRINCIPAL_TEXT_HW) {
                        SendTextToEntireSectionHW();
                    } else {
                        SendMultipleImagePDFAsStaffToEntireSectionWithCloudURL(filetype, type);
                    }
                }
            }

            @Override
            public void onUploadError(String error) {
//                uploadedFiles++;
//                updateProgressBar();
                Log.e("Upload Error", error);
            }
        });
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1) {
            return fileName.substring(lastIndexOfDot + 1).toLowerCase();
        }
        return ""; // Return empty string if no extension found
    }

    private void updateProgressBar() {
        runOnUiThread(() -> {
            progressBar.setProgress(uploadedFiles);
            progressText.setText(uploadedFiles + "/" + totalFiles);
        });
    }


    private void sendOnlineClassToSections() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayOnlineClass();
        Call<JsonArray> call = apiService.sendOnlineClassToSections(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {

                                showAlertForOnline(strMsg, strStatus);
                            } else {
                                showAlertForOnline(strMsg, strStatus);

                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t);
                showToast(t.toString());
            }
        });
    }

    private void showAlertForOnline(String strMsg, final String strStatus) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherStaffStandardSection.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (strStatus.equals("1")) {

                    dialog.cancel();
                    finish();
                } else {
                    dialog.cancel();
                }


            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private JsonObject constructJsonArrayOnlineClass() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("school_id", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("staff_id", StaffID);
            jsonObjectSchoolstdgrp.addProperty("subject_id", strSubjectCode);
            jsonObjectSchoolstdgrp.addProperty("meeting_url", Constants.MeetingURL);
            jsonObjectSchoolstdgrp.addProperty("meeting_date", Constants.MeetingDate);
            jsonObjectSchoolstdgrp.addProperty("meeting_time", Constants.MeetingTime);
            jsonObjectSchoolstdgrp.addProperty("meeting_topic", Constants.Title);
            jsonObjectSchoolstdgrp.addProperty("meeting_id", Constants.MeetingID);
            jsonObjectSchoolstdgrp.addProperty("meeting_type", Constants.MeetingType);
            jsonObjectSchoolstdgrp.addProperty("meeting_description", Constants.Description);

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolstd.add(jsonObjectclass);

            }

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendMultipleImagePDFAsStaffToEntireSectionWithCloudURL(String fileType, String type) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        runOnUiThread(() -> {

            JsonObject jsonReqArray = SendEntireSectionJson(fileType, type);
            Call<JsonArray> call = apiService.SendMultipleImagePDFAsStaffToEntireSectionWithCloudURL(jsonReqArray);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call,
                                       Response<JsonArray> response) {

                    hideLoading();

                    Log.d("Upload-Code:Response", response.code() + "-" + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Upload:Body", response.body().toString());

                        try {
                            JSONArray js = new JSONArray(response.body().toString());
                            if (js.length() > 0) {
                                JSONObject jsonObject = js.getJSONObject(0);
                                String strStatus = jsonObject.getString("Status");
                                String strMsg = jsonObject.getString("Message");
                                if ((strStatus).equalsIgnoreCase("1")) {

                                    showAlert(strMsg, strStatus);
                                } else {
                                    showAlert(strMsg, strStatus);
                                }
                            } else {
                                showToast(getResources().getString(R.string.check_internet));
                            }


                        } catch (Exception e) {
                            showToast(getResources().getString(R.string.check_internet));
                            Log.d("Ex", e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    hideLoading();
                    showToast(getResources().getString(R.string.check_internet));
                    showToast(t.toString());
                }
            });
        });
    }

    private JsonObject SendEntireSectionJson(String fileType, String type) {
        String isMultiple = "";
        if (fileType.equals(".pdf")) {
            isMultiple = "0";
        } else {
            isMultiple = "1";
        }
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {


            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("isMultiple", isMultiple);
            jsonObjectSchoolstdgrp.addProperty("FileType", fileType);


            JsonArray jsonArrayschoolstd = new JsonArray();

            if (type.equals("specific")) {
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", strSecCode);
                jsonArrayschoolstd.add(jsonObjectclass);
            } else {
                for (int i = 0; i < seletedSectionsList.size(); i++) {
                    TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            JsonArray jsonArrayschoolstd1 = new JsonArray();
            for (int j = 0; j < UploadedS3URlList.size(); j++) {
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("FileName", UploadedS3URlList.get(j));
                jsonArrayschoolstd1.add(jsonObjectclass);

            }
            jsonObjectSchoolstdgrp.add("FileNameArray", jsonArrayschoolstd1);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void showLoading() {
        if (progressDialog == null) {
            // Initialize the ProgressDialog if it hasn't been created yet
            progressDialog = new ProgressDialog(this); // Replace 'this' with your Context if not in an Activity
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.Uploading));
            progressDialog.setCancelable(false);
        }

        // Show the ProgressDialog if it is not already showing
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        } else {
            Log.d("ProgressBar", "ProgressDialog is already showing");
        }
    }

    private void getSubjectsApi() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherStaffStandardSection.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherStaffStandardSection.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();


        String id = "";
        for (int i = 0; i < seletedSectionsList.size(); i++) {
            sectionsTargetCode = id + seletedSectionsList.get(i).getStrSectionCode() + "~";
            id = sectionsTargetCode;
        }
        String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);


        jsonObject.addProperty("SchoolID", SchoolID);
        jsonObject.addProperty("StaffId", StaffID);
        jsonObject.addProperty("TargetCode", targetCode);

        Log.d("Request", String.valueOf(jsonObject));

        Call<JsonArray> call = apiService.GetSubjects(jsonObject);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);


                    SubjectsList.clear();
                    listSubjectName.clear();
                    listSubjectCode.clear();

                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());

                        if (js.length() > 0) {
                            spinSubject.setVisibility(View.VISIBLE);
                            lblSubject.setVisibility(View.VISIBLE);

                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String SubjectID = jsonObject.getString("SubjectID");
                                String SubjectName = jsonObject.getString("SubjectName");
                                TeacherSubjectModel subjectList;
                                subjectList = new TeacherSubjectModel(SubjectName, SubjectID, false);
                                SubjectsList.add(subjectList);

                                listSubjectName.add(SubjectName);
                                listSubjectCode.add(SubjectID);
                            }
                            adaSubject = new ArrayAdapter<>(TeacherStaffStandardSection.this, R.layout.teacher_spin_title, listSubjectName);
                            adaSubject.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                            spinSubject.setAdapter(adaSubject);

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_records), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendVideosToSections(String type) {
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        File file = new File(strVideoFilePath);
        Log.d("FILE_Path", strVideoFilePath);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("video", file.getName(), requestFile);

        JsonObject jsonReqArray = JsonArrayFromSectionsVideos(type);
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        Call<JsonArray> call = apiService.UploadVideostoYoutube(requestBody, bodyFile);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if (strStatus.equals("1")) {
                                showAlert(strMsg, strStatus);

                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }
                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject JsonArrayFromSectionsVideos(String type) {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("VideoTitle", Description);
            jsonObjectSchoolstdgrp.addProperty("VideoDescription", VideoDescription);
            jsonObjectSchoolstdgrp.addProperty("sEntireSchool", "F");
            jsonObjectSchoolstdgrp.addProperty("type", type);


            JsonArray jsonArrayschoolSections = new JsonArray();
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolSections.add(jsonObjectclass);
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            JsonArray jsonArrayschoolstd = new JsonArray();
            JsonArray jsonArrayschoolgStudents = new JsonArray();

            jsonObjectSchoolstdgrp.add("Standards", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("Groups", jsonArrayschoolgrp);
            jsonObjectSchoolstdgrp.add("Sections", jsonArrayschoolSections);
            jsonObjectSchoolstdgrp.add("Students", jsonArrayschoolgStudents);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void sendVoiceToSpecificSctionFromVoiceHistory() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);

        JsonObject jsonReqArray = jsonArrayFromStaffVoiceHistory();


        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        //  Call<JsonArray> call = apiService.SendVoiceAsStaffToEntireSectionfromVoiceHistory(jsonReqArray);

        Call<JsonArray> call;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleVoiceAsStaffToEntireSectionfromVoiceHistory(jsonReqArray);
        } else {
            call = apiService.SendVoiceAsStaffToEntireSectionfromVoiceHistory(jsonReqArray);
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);

                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t);
                showToast(t.toString());
            }
        });
    }

    private JsonObject jsonArrayFromStaffVoiceHistory() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {


            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);
            jsonObjectSchoolstdgrp.addProperty("filepath", filepath);


            if (Util_Common.isScheduleCall) {
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < Util_Common.isSelectedDate.size(); i++) {
                    String isSelected = (Util_Common.isSelectedDate.get(i));
                    isSelectedArray.add(isSelected);
                }
                jsonObjectSchoolstdgrp.add("Dates", isSelectedArray);
                jsonObjectSchoolstdgrp.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchoolstdgrp.addProperty("EndTime", Util_Common.isEndTime);
            }

            JsonArray jsonArrayschoolstd = new JsonArray();
            JsonObject jsonObjectclass = new JsonObject();
            jsonObjectclass.addProperty("TargetCode", strSecCode);
            jsonArrayschoolstd.add(jsonObjectclass);
            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void sendVoiceFromVoiceHistory() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);

        JsonObject jsonReqArray = jsonArrayFromVoiceHistory();


        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        // Call<JsonArray> call = apiService.SendVoiceAsStaffToEntireSectionfromVoiceHistory(jsonReqArray);

        Call<JsonArray> call;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleVoiceAsStaffToEntireSectionfromVoiceHistory(jsonReqArray);
        } else {
            call = apiService.SendVoiceAsStaffToEntireSectionfromVoiceHistory(jsonReqArray);
        }
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);

                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject jsonArrayFromVoiceHistory() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);
            jsonObjectSchoolstdgrp.addProperty("filepath", filepath);

            if (Util_Common.isScheduleCall) {
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < Util_Common.isSelectedDate.size(); i++) {
                    String isSelected = (Util_Common.isSelectedDate.get(i));
                    isSelectedArray.add(isSelected);
                }
                jsonObjectSchoolstdgrp.add("Dates", isSelectedArray);
                jsonObjectSchoolstdgrp.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchoolstdgrp.addProperty("EndTime", Util_Common.isEndTime);
            }

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolstd.add(jsonObjectclass);

            }
            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void standardsAndSectoinsListAPI1() {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherStaffStandardSection.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherStaffStandardSection.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call;
        JsonObject jsonReqArray;
        if (iRequestCode == STAFF_VOICE || iRequestCode == STAFF_TEXT || iRequestCode == PRINCIPAL_VOICE ||
                iRequestCode == PRINCIPAL_TEXT || iRequestCode == GH_VOICE || iRequestCode == GH_TEXT) {
            jsonReqArray = constructJsonArrayMgtSchoolStdHW();
            call = apiService.GetStandardsAndSubjectsAsStaff(jsonReqArray);
        } else {
            jsonReqArray = constructJsonArrayMgtSchoolStdHW();
            call = apiService.GetStandardsAndSubjectsAsStaffWithoutNewOld(jsonReqArray);
        }
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StdSecList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StdSecList:Res", response.body().toString());


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        {
                            TeacherStandardSectionsListModel stdSecList;

                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                if (jsonObject.getString("StandardId").equals("0")) {
                                    showToast(getResources().getString(R.string.standard_sections_not_assigned));
                                    finish();
                                } else {
                                    stdSecList = new TeacherStandardSectionsListModel(jsonObject.getString("Standard"), jsonObject.getString("StandardId"));
                                    listStd.add(jsonObject.getString("Standard"));

                                    ArrayList<TeacherSectionsListNEW> listSections = new ArrayList<>();
                                    ArrayList<TeacherSubjectModel> listSubjects = new ArrayList<>();

                                    JSONArray jsArySections = jsonObject.getJSONArray("Sections");
                                    if (jsArySections.length() > 0) {
                                        JSONObject jObjStd;
                                        TeacherSectionsListNEW sectionsList;
                                        for (int j = 0; j < jsArySections.length(); j++) {
                                            jObjStd = jsArySections.getJSONObject(j);
                                            if (jObjStd.getString("SectionId").equals("0")) {
                                                showToast(jObjStd.getString("SectionName"));
                                                finish();

                                            } else {
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
                                                listSections.add(sectionsList);
                                            }
                                        }
                                    }


                                    JSONArray jsArySubjects = jsonObject.getJSONArray("Subjects");
                                    if (jsArySubjects.length() > 0) {
                                        JSONObject jObjSub;
                                        TeacherSubjectModel subjectList;
                                        for (int k = 0; k < jsArySubjects.length(); k++) {
                                            jObjSub = jsArySubjects.getJSONObject(k);

                                            subjectList = new TeacherSubjectModel(jObjSub.getString("SubjectName"), jObjSub.getString("SubjectId"), false);
                                            listSubjects.add(subjectList);

                                        }

                                    }

                                    stdSecList.setListSectionsNew(listSections);
                                    arrStandardsAndSectionsList.add(stdSecList);
                                }

                                adaStd = new ArrayAdapter<>(TeacherStaffStandardSection.this, R.layout.teacher_spin_title, listStd);
                                adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                                spinStandard.setAdapter(adaStd);


                            }
                        }


                    } else {
                        showToast(getResources().getString(R.string.no_records));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                    showToast(getResources().getString(R.string.no_records));
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                onBackPressed();
            }
        });
    }

    private void SendSMSToEntireSection() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArraySMS();
        Call<JsonArray> call = apiService.SendSMSAsStaffToEntireSection(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);
                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArraySMS() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Message", strmessage);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolstd.add(jsonObjectclass);

            }

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private void SendSMSToSpecificSection() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArraySMSSpecificsec();
        Call<JsonArray> call = apiService.SendSMSAsStaffToEntireSection(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {

                                showAlert(strMsg, strStatus);
                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArraySMSSpecificsec() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Message", strmessage);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            JsonArray jsonArrayschoolstd = new JsonArray();

            JsonObject jsonObjectclass = new JsonObject();
            jsonObjectclass.addProperty("TargetCode", strSecCode);
            jsonArrayschoolstd.add(jsonObjectclass);

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendVoiceToEntireSection() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayVoice();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());


        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        //  Call<JsonArray> call = apiService.SendVoiceAsStaffToEntireSection(requestBody, bodyFile);

        Call<JsonArray> call;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleAsStaffToEntireSection(requestBody, bodyFile);
        } else {
            call = apiService.SendVoiceAsStaffToEntireSection(requestBody, bodyFile);
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);

                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex_Exception", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("onFailure", t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayVoice() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {


            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolstd.add(jsonObjectclass);
            }

            if (Util_Common.isScheduleCall) {
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < Util_Common.isSelectedDate.size(); i++) {
                    String isSelected = (Util_Common.isSelectedDate.get(i));
                    isSelectedArray.add(isSelected);
                }
                jsonObjectSchoolstdgrp.add("Dates", isSelectedArray);
                jsonObjectSchoolstdgrp.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchoolstdgrp.addProperty("EndTime", Util_Common.isEndTime);
            }

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private void SendVoiceToSpecificSection() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArraySpecificVoice();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        // Call<JsonArray> call = apiService.SendVoiceAsStaffToEntireSection(requestBody, bodyFile);

        Call<JsonArray> call;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleAsStaffToEntireSection(requestBody, bodyFile);
        } else {
            call = apiService.SendVoiceAsStaffToEntireSection(requestBody, bodyFile);
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");
                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);
                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
                        }
                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArraySpecificVoice() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            JsonArray jsonArrayschoolstd = new JsonArray();
            JsonObject jsonObjectclass = new JsonObject();
            jsonObjectclass.addProperty("TargetCode", strSecCode);
            jsonArrayschoolstd.add(jsonObjectclass);


            if (Util_Common.isScheduleCall) {
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < Util_Common.isSelectedDate.size(); i++) {
                    String isSelected = (Util_Common.isSelectedDate.get(i));
                    isSelectedArray.add(isSelected);
                }
                jsonObjectSchoolstdgrp.add("Dates", isSelectedArray);
                jsonObjectSchoolstdgrp.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchoolstdgrp.addProperty("EndTime", Util_Common.isEndTime);
            }

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());


        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchoolstdgrp;
    }


    private JsonObject constructJsonArrayImageSpecific(String mutiple, String filetype) {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {


            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("isMultiple", mutiple);
            jsonObjectSchoolstdgrp.addProperty("FileType", filetype);

            JsonArray jsonArrayschoolstd = new JsonArray();
            JsonObject jsonObjectclass = new JsonObject();
            jsonObjectclass.addProperty("TargetCode", strSecCode);
            jsonArrayschoolstd.add(jsonObjectclass);


            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private void standardsAndSectoinsListHWAPI() {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherStaffStandardSection.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherStaffStandardSection.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStdHW();

        Call<JsonArray> call = apiService.GetStandardsAndSubjectsAsStaffWithoutNewOld(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StdSecList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StdSecList:Res", response.body().toString());


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        {
                            TeacherStandardSectionsListModel stdSecList;
                            Log.d("json length", String.valueOf(js.length()));

                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                if (jsonObject.getString("StandardId").equals("0")) {
                                    showToast(getResources().getString(R.string.standard_sections_not_assigned));
                                    finish();
                                } else {
                                    stdSecList = new TeacherStandardSectionsListModel(jsonObject.getString("Standard"), jsonObject.getString("StandardId"));
                                    listStd.add(jsonObject.getString("Standard"));

                                    ArrayList<TeacherSectionsListNEW> listSections = new ArrayList<>();
                                    ArrayList<TeacherSubjectModel> listSubjects = new ArrayList<>();

                                    JSONArray jsArySections = jsonObject.getJSONArray("Sections");
                                    if (jsArySections.length() > 0) {
                                        JSONObject jObjStd;
                                        TeacherSectionsListNEW sectionsList;
                                        for (int j = 0; j < jsArySections.length(); j++) {
                                            jObjStd = jsArySections.getJSONObject(j);
                                            if (jObjStd.getString("SectionId").equals("0")) {
                                                showToast(jObjStd.getString("SectionName"));
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
                                                listSections.add(sectionsList);
                                            } else {
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
                                                listSections.add(sectionsList);
                                            }
                                        }
                                    }


                                    JSONArray jsArySubjects = jsonObject.getJSONArray("Subjects");
                                    if (jsArySubjects.length() > 0) {
                                        JSONObject jObjSub;
                                        TeacherSubjectModel subjectList;
                                        for (int k = 0; k < jsArySubjects.length(); k++) {
                                            jObjSub = jsArySubjects.getJSONObject(k);

                                            subjectList = new TeacherSubjectModel(jObjSub.getString("SubjectName"), jObjSub.getString("SubjectId"), false);
                                            listSubjects.add(subjectList);

                                        }

                                    }

                                    stdSecList.setListSectionsNew(listSections);
                                    stdSecList.setListSubjects(listSubjects);
                                    arrStandardsAndSectionsList.add(stdSecList);
                                }

                                adaStd = new ArrayAdapter<>(TeacherStaffStandardSection.this, R.layout.teacher_spin_title, listStd);
                                adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                                spinStandard.setAdapter(adaStd);


                            }
                        }


                    } else {
                        showToast(getResources().getString(R.string.no_records));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                    showToast(getResources().getString(R.string.check_internet));
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("StdSecList:Failure", t.toString());
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
                backToResultActvity(message);
            }
        }
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }


    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", SchoolID);
            Log.d("schoolid", SchoolID);
            Log.d("staffstd&sec", jsonObjectSchool.toString());
            Log.d("request", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private JsonObject constructJsonArrayMgtSchoolStdHW() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", SchoolID);
            jsonObjectSchool.addProperty("StaffID", StaffID);
            jsonObjectSchool.addProperty("isAttendance", "0");
            Log.d("req", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }


    private void SendExamhomework() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolstext();
        Call<JsonArray> call = apiService.InsertExamToEntireSection(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);

                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t);
                showToast(t.toString());
            }
        });
    }

    private void showAlert(String strMsg, final String status) {
        lnrProgress.setVisibility(View.GONE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherStaffStandardSection.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {

                    dialog.cancel();
                    Intent homescreen = new Intent(TeacherStaffStandardSection.this, Teacher_AA_Test.class);
                    homescreen.putExtra("Homescreen", "1");
                    homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homescreen);
                    finish();
                } else {
                    dialog.cancel();
                }


            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private JsonObject constructJsonArrayMgtSchoolstext() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("ExamName", strtittle);
            jsonObjectSchoolstdgrp.addProperty("ExamSyllabus", strmessage);
            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);


            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolstd.add(jsonObjectclass);
            }

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private void SendExamhomeworkSpecificsection() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolstextSpecificSec();
        Call<JsonArray> call = apiService.InsertExamToEntireSection(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);
                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolstextSpecificSec() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("ExamName", strtittle);
            jsonObjectSchoolstdgrp.addProperty("ExamSyllabus", strmessage);
            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);


            JsonArray jsonArrayschoolstd = new JsonArray();
            JsonObject jsonObjectclass = new JsonObject();
            jsonObjectclass.addProperty("TargetCode", strSecCode);
            jsonArrayschoolstd.add(jsonObjectclass);

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void listSectionsForSelectedStandard() {
        i_sections_count = 0;

        for (int i = 0; i < arrSectionCollections.size(); i++) {
            arrSectionCollections.get(i).setSelectStatus(false);
        }
        TeacherNewSectionsListAdapter newSectionsListAdapter = new TeacherNewSectionsListAdapter(TeacherStaffStandardSection.this, new TeacherOnCheckSectionListListener() {
            @Override
            public void section_addSection(TeacherSectionsListNEW sectionsListNEW) {
                if ((sectionsListNEW != null) && (!seletedSectionsList.contains(sectionsListNEW))) {


                    seletedSectionsList.add(sectionsListNEW);
                    i_sections_count++;
                    enableDisableNext();
                }
            }

            @Override
            public void section_removeSection(TeacherSectionsListNEW sectionsListNEW) {
                if ((sectionsListNEW != null) && (seletedSectionsList.contains(sectionsListNEW))) {
                    seletedSectionsList.remove(sectionsListNEW);
                    i_sections_count--;
                    enableDisableNext();
                }
            }
        }, arrSectionCollections);

        rvSectionList.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherStaffStandardSection.this);
        rvSectionList.setLayoutManager(layoutManager);
        rvSectionList.addItemDecoration(new DividerItemDecoration(TeacherStaffStandardSection.this, LinearLayoutManager.VERTICAL));
        rvSectionList.setItemAnimator(new DefaultItemAnimator());
        rvSectionList.setAdapter(newSectionsListAdapter);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void enableDisableNext() {
        if (i_sections_count > 0) {
            btnSend.setEnabled(true);
            btnSelectSubjects.setEnabled(true);
        } else {
            btnSend.setEnabled(false);
            btnSelectSubjects.setEnabled(false);
        }
    }


    private void SendVoiceToEntireSectionHW() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayVoiceHW();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());


        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffStandardSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.InsertHomeWorkVoice(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);
                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayVoiceHW() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("ID", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolstd.add(jsonObjectclass);

            }

            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private void SendTextToEntireSectionHW() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStaffStandardSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        runOnUiThread(() -> {
            JsonObject jsonReqArray = constructJsonArraySMSHW();
            Call<JsonArray> call = apiService.InsertHomeWork(jsonReqArray);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call,
                                       Response<JsonArray> response) {
                    hideLoading();

                    Log.d("Upload-Code:Response", response.code() + "-" + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Upload:Body", response.body().toString());

                        try {
                            JSONArray js = new JSONArray(response.body().toString());
                            if (js.length() > 0) {
                                JSONObject jsonObject = js.getJSONObject(0);
                                String strStatus = jsonObject.getString("Status");
                                String strMsg = jsonObject.getString("Message");
                                if ((strStatus).equalsIgnoreCase("1")) {
                                    showAlert(strMsg, strStatus);
                                } else {
                                    showAlert(strMsg, strStatus);
                                }
                            } else {
                                showToast(getResources().getString(R.string.no_records));
                            }


                        } catch (Exception e) {
                            showToast(getResources().getString(R.string.check_internet));
                            Log.d("Ex", e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    hideLoading();
                    showToast(getResources().getString(R.string.check_internet));
                    Log.d("Upload error:", t.getMessage() + "\n" + t);
                    showToast(t.toString());
                }
            });
        });
    }

    private JsonObject constructJsonArraySMSHW() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            String CountrID = TeacherUtil_SharedPreference.getCountryID(TeacherStaffStandardSection.this);
            jsonObjectSchoolstdgrp.addProperty("CountryID", CountrID);
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);
            jsonObjectSchoolstdgrp.addProperty("HomeworkTopic", Description);
            jsonObjectSchoolstdgrp.addProperty("HomeworkText", strmessage);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = seletedSectionsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("ID", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolstd.add(jsonObjectclass);
            }
            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);

            JsonArray jsonFiles = new JsonArray();
            if (!HOMEWORK_TYPE.equals("")) {
                for (int i = 0; i < UploadedS3URlList.size(); i++) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("path", UploadedS3URlList.get(i));
                    jsonObjectclass.addProperty("type", HOMEWORK_TYPE);
                    jsonFiles.add(jsonObjectclass);
                }
                jsonObjectSchoolstdgrp.add("FilePath", jsonFiles);
            } else {
                jsonObjectSchoolstdgrp.add("FilePath", jsonFiles);
            }
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }
}
