package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSelectedsectionListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherStudentsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.activity.TeacherListAllSection.SELECTED_STD_SEC_STUD_CODE;
import static com.vs.schoolmessenger.activity.TeacherPhotosScreen.IMAGE_UPLOAD_STATUS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.list_staffStdSecs;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.strNoClassWarning;


public class TeacherStaffImageCircular extends AppCompatActivity implements View.OnClickListener {

    Button ChooseRecipents, btnSend;
    ImageView ivCircular;

    RecyclerView selectedsection;
    TeacherSelectedsectionListAdapter adapter;
    LinearLayout llRecipients;

    ArrayList<TeacherSectionModel> stdSecList = new ArrayList<>();
    ArrayList<TeacherSectionModel> selectedStdSecStudList = new ArrayList<>();

    String strSelectedImgFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_send_image_staff);

//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        selectedsection = (RecyclerView) findViewById(R.id.rvselectedsectionList1);
        ImageView ivBack = (ImageView) findViewById(R.id.img1_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        strSelectedImgFilePath = getIntent().getExtras().getString("IMAGE_PATH", "");
        ivCircular = (ImageView)findViewById(R.id.staffImage_ivImage);

//        Uri imageUri = Uri.parse(strSelectedImgFilePath);
//        ivCircular.setImageURI(imageUri);

      //  Or
        Bitmap d = new BitmapDrawable(strSelectedImgFilePath).getBitmap();
        int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
        ivCircular.setImageBitmap(scaled);

        // Or  -  Error
//        try {
//            Uri imageUri = Uri.parse(strSelectedImgFilePath);
//            Bitmap myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//            myBitmap = rotateImageIfRequired(myBitmap, imageUri);
//            myBitmap = getResizedBitmap(myBitmap, 500);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            ivCircular.setImageBitmap(myBitmap);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//      **************

        btnSend = (Button) findViewById(R.id.staffImage_btnSend);
        btnSend.setOnClickListener(this);

        ChooseRecipents = (Button) findViewById(R.id.staffImage_btnChoose);
        ChooseRecipents.setOnClickListener(this);

        adapter = new TeacherSelectedsectionListAdapter(TeacherStaffImageCircular.this, selectedStdSecStudList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        selectedsection.setHasFixedSize(true);
        selectedsection.setLayoutManager(mLayoutManager);
        selectedsection.setItemAnimator(new DefaultItemAnimator());
        selectedsection.setAdapter(adapter);

        llRecipients = (LinearLayout) findViewById(R.id.staffImage_llRecipient);
        llRecipients.setVisibility(View.GONE);
        btnSend.setEnabled(false);

        stdSecListAPI();
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.staffImage_btnChoose:
                Intent inStdSec = new Intent(TeacherStaffImageCircular.this, TeacherListAllSection.class);
                inStdSec.putExtra("STD_SEC_LIST", stdSecList);
                startActivityForResult(inStdSec, SELECTED_STD_SEC_STUD_CODE);
                break;

            case R.id.staffImage_btnSend:
                sendImageCircularRetroFit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //        finish();
        backToResultActvity("BACK");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(IMAGE_UPLOAD_STATUS, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_STD_SEC_STUD_CODE) {

            String message = data.getStringExtra("MESSAGE");
            if (message.equals("OK")) {
//                showToast("Success");

                llRecipients.setVisibility(View.VISIBLE);
                btnSend.setEnabled(true);
                adapter.clearAllData();
                selectedStdSecStudList.addAll((ArrayList<TeacherSectionModel>) data.getSerializableExtra("STUDENTS"));

                // Scroll testing....
//                for (int i = 0; i < 15; i++)
//                    selectedStdSecStudList.addAll((ArrayList<TeacherSectionModel>) data.getSerializableExtra("STUDENTS"));

                adapter.notifyDataSetChanged();

            } else {
                llRecipients.setVisibility(View.GONE);
                btnSend.setEnabled(false);
//                showToast(message);
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void stdSecListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

//        TeacherSchoolsApiClient.BASE_URL = TeacherUtil_SharedPreference.getBaseUrlFromSP(TeacherStaffImageCircular.this);
        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherStaffImageCircular.this);
        final String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherStaffImageCircular.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_SubjectHandling(schoolID, staffID);
        Call<JsonArray> call = apiService.staffSubjectHandling(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("SubjectHandling:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("SubjectHandling:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0)
                    {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strSubName = jsonObject.getString("SubjectName");
                        String strSubCode = jsonObject.getString("SubjectCode");

                        if (strSubName.equals("")) {
//                            showToast(strSubCode);
//                           onBackPressed();

                            list_staffStdSecs = new ArrayList<>();
                            strNoClassWarning = strSubCode;
                        } else {
                            TeacherSectionModel sectionModel;
                            Log.d("json length", js.length() + "");
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                sectionModel = new TeacherSectionModel(false, jsonObject.getString("Class"), jsonObject.getString("Section")
                                        , jsonObject.getString("ClassSecCode"), jsonObject.getString("SubjectName")
                                        , jsonObject.getString("SubjectCode"), jsonObject.getString("NoOfStudents"), jsonObject.getString("NoOfStudents"), true);
                                stdSecList.add(sectionModel);
                            }

                            list_staffStdSecs = new ArrayList<>();
                            list_staffStdSecs.addAll(stdSecList);
                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    try {
                        JSONArray jsError = new JSONArray(response.body().toString());
                        if (jsError.length() > 0) {
                            JSONObject jsonObjectError = jsError.getJSONObject(0);

                            String status = jsonObjectError.getString("Status");
                            String msg = jsonObjectError.getString("Message");
                            showToast(msg);
                        }
                    } catch (Exception ex) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.e("SubjectHandling:Excep", ex.getMessage());
                    }
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("SubjectHandling:Failure", t.toString());
                onBackPressed();
            }
        });
    }

    private void sendImageCircularRetroFit() {

//        String tempBaseURl = "http://106.51.127.215:8089/ERP/";
//        String tempBaseURl = "http://45.113.138.248/apk/apk_controller/";
//        String tempBaseURl = "http://192.168.1.143:8089/erp/";
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(strSelectedImgFilePath);
        Log.d("FILE_Path", strSelectedImgFilePath);
//        File file = FileUtils.getFile(this, Uri.parse(recFilePath));

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

//        // add another part within the multipart request
        JsonArray jsonReqArray = constructResultJsonArray();
//        String descriptionString = "hello, this is description speaking";
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        // Or
//        RequestBody requestContent =
//                RequestBody.create(
//                        MediaType.parse("multipart/form-data"), jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffImageCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        // finally, execute the request
//        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_InsertQueReply(strCirID, strChildID, strSchoolID, strAns);
//        Log.d("BaseURL2", TeacherSchoolsApiClient.BASE_URL);

        Call<JsonArray> call = apiService.StaffwiseImage(requestBody, bodyFile);
//        Call<JsonObject> call = apiService.InsertQueReply(TeacherUtil_UrlMethods.INSERT_QUE_REPLY, strCirID, strChildID, strSchoolID, strAns, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");
                            showToast(strMsg);


                            if ((strStatus.toLowerCase()).equals("y")) {

//                                Bundle b = new Bundle();
//                                b.putString("status", "done");
//                                Intent intent = new Intent();
//                                intent.putExtras(b);
//                                setResult(RESULT_OK, intent);

//                                finish();
                                backToResultActvity("SENT");
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
                        }


                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage()+"\n"+t.toString());
                showToast(t.toString());
            }
        });
    }

//    private void sendStaffImageAPI() {
//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//
////        TeacherSchoolsApiClient.BASE_URL = TeacherUtil_SharedPreference.getBaseUrlFromSP(TeacherStaffImageCircular.this);
//
//        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
//        JsonArray jsonReqArray = constructResultJsonArray();
//        Call<JsonArray> call = apiService.SendSmsStaffwise(jsonReqArray);
//        call.enqueue(new Callback<JsonArray>() {
//
//            @Override
//            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
//
//                Log.d("ImageStaff:Code", response.code() + " - " + response.toString());
//                if (response.code() == 200 || response.code() == 201)
//                    Log.d("ImageStaff:Res", response.body().toString());
//
//                try {
//                    JSONArray js = new JSONArray(response.body().toString());
//                    if (js.length() > 0) {
//                        JSONObject jsonObject = js.getJSONObject(0);
//                        String strStatus = jsonObject.getString("Status");
//                        String strMsg = jsonObject.getString("Message");
//                        showToast(strMsg);
//
//                        if ((strStatus.toLowerCase()).equals("y")) {
////                            finish();
//                            backToResultActvity("SENT");
//                        }
//
//                    } else {
//                        showToast("Server Response Failed. Try again");
//                    }
//
//                } catch (Exception e) {
//                    try {
//                        JSONArray jsError = new JSONArray(response.body().toString());
//                        if (jsError.length() > 0) {
//                            JSONObject jsonObjectError = jsError.getJSONObject(0);
//
////                                String status = jsonObjectError.getString("Status");
//                            String msg = jsonObjectError.getString("Message");
//                            showToast(msg);
//                        }
//                    } catch (Exception ex) {
//                        showToast("Server Response Failed");
//                        Log.e("ImageStaff:Excep", ex.getMessage());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonArray> call, Throwable t) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
//                showToast("Error! Try Again");
//                Log.d("ImageStaff:Failure", t.toString());
//            }
//        });
//    }

    private JsonArray constructResultJsonArray() {
        JsonArray jsonArraySchool = new JsonArray();

        try {
            String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherStaffImageCircular.this);
            String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherStaffImageCircular.this);


            JsonObject jsonObjectSchool = new JsonObject();
            jsonObjectSchool.addProperty("SchoolID", schoolID);
            jsonObjectSchool.addProperty("StaffID", staffID);
//            jsonObjectSchool.addProperty("Message", strMsg);

            JsonArray jsonArrayStdSections = new JsonArray();
            for (int i = 0; i < selectedStdSecStudList.size(); i++) {

//                if (selectedStdSecStudList.get(i).isSelectStatus())
                {
                    JsonObject jsonObjectStdSections = new JsonObject();
                    jsonObjectStdSections.addProperty("SecCode", selectedStdSecStudList.get(i).getStdSecCode());
                    jsonObjectStdSections.addProperty("SubCode", selectedStdSecStudList.get(i).getSubjectCode());
//                    jsonObjectStdSections.addProperty("MessageToAll", selectedStdSecStudList.get(i).isAllStudentsSelected());
                    String strMsgToAll = "T";
                    if (!selectedStdSecStudList.get(i).isAllStudentsSelected())
                        strMsgToAll = "F";
                    jsonObjectStdSections.addProperty("MsgToAll", strMsgToAll);

                    JsonArray jsonArrayStudents = new JsonArray();

                    if (!selectedStdSecStudList.get(i).isAllStudentsSelected()) {
                        ArrayList<TeacherStudentsModel> seleStudents = new ArrayList<>();
                        seleStudents.addAll(selectedStdSecStudList.get(i).getStudentsList());

                        for (int j = 0; j < seleStudents.size(); j++) {
                            if (seleStudents.get(j).isSelectStatus()) {
                                JsonObject jsonObjectStudents = new JsonObject();
                                jsonObjectStudents.addProperty("StudentID", seleStudents.get(j).getStudentID());
                                jsonArrayStudents.add(jsonObjectStudents);
                            }
                        }

                    }

                    jsonObjectStdSections.add("Student", jsonArrayStudents);
                    jsonArrayStdSections.add(jsonObjectStdSections);
                }
            }
            jsonObjectSchool.add("TargetCode", jsonArrayStdSections);
            jsonArraySchool.add(jsonObjectSchool);

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }
}
