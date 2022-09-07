package com.vs.schoolmessenger.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.RequestMeetingForParent;
import com.vs.schoolmessenger.assignment.StudentSelectAssignment;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.StaffModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class MeetingRequestADDFragment extends Fragment {
    RecyclerView recycle_paidlist;
    String school_id, child_id,StaffID="";
    Context context;
    private ArrayList<StaffModel> staffList = new ArrayList<>();


    Spinner spnStaffList;
    EditText txtComments;
    Button btnRequest;


    ArrayList<String> staffNames = new ArrayList<>();
    String selectedItemText;

    String staffName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.meeting_request_add, container, false);
        spnStaffList = (Spinner) rootView.findViewById(R.id.spnStaffList);
        btnRequest = (Button) rootView.findViewById(R.id.btnRequest);
        txtComments = (EditText) rootView.findViewById(R.id.txtComments);


        child_id = Util_SharedPreference.getChildIdFromSP(getActivity());
        school_id = Util_SharedPreference.getSchoolIdFromSP(getActivity());
        Log.e("sizee123", String.valueOf(staffList.size()));







        spnStaffList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);
                Log.d("selectedItemText", selectedItemText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });



        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comments=txtComments.getText().toString();

                if(!comments.equals("")) {
                    RequestADDApi(comments);
                }
                else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.fill_all_details), Toast.LENGTH_SHORT).show();
                }
            }
        });


        getDetails();
        return rootView;
    }

    private void RequestADDApi(String comments) {
        for (int i = 0; i < staffList.size(); i++) {
            final StaffModel nameslist = staffList.get(i);
            String name = nameslist.getStaffName();
            if (name.equals(selectedItemText)) {


                StaffID = nameslist.getStaffId();
                Log.d("schoolID", StaffID);
            }
        }
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(getActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);


        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("RequestId", "0");
        jsonObject.addProperty("StaffID", StaffID);
        jsonObject.addProperty("ChildID", child_id);
        jsonObject.addProperty("ParentComment", comments);
        jsonObject.addProperty("StaffComment", "");
        jsonObject.addProperty("SchedueDate", "");
        jsonObject.addProperty("ScheduleTime", "");
        jsonObject.addProperty("ProcessBy", child_id);
        jsonObject.addProperty("ProcessType", "add");

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
                        Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getDetails() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(getActivity());
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        child_id = Util_SharedPreference.getChildIdFromSP(getActivity());
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        String schoolID=Util_SharedPreference.getSchoolIdFromSP(getActivity());
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(getActivity());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("memberid", child_id);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("MobileNumber", MobileNumber);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonObject> call = apiService.getStaffDetails(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        staffList.clear();
                        staffNames.clear();

                        JSONObject js = new JSONObject(response.body().toString());

                        String classTeacher = js.getString("classteacher");
                        String classTeacherID = js.getString("ClassTeacherID");

                        staffNames.add("Select Staff");

                        if(!classTeacherID.equals("0")) {

                            StaffModel data = new StaffModel("", classTeacher+" -Class Teacher", classTeacherID);
                            staffList.add(data);
                            staffNames.add(classTeacher+" -Class Teacher");
                        }

                        JSONArray array = js.getJSONArray("subjectdetails");

                        if (array.length() > 0) {

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);

                                staffName = jsonObject.getString("staffname");
                                String subjectName = jsonObject.getString("subjectname");
                                String staffID = jsonObject.getString("StaffID");
                                if (!staffID.equals("0")) {

                                    StaffModel data1 = new StaffModel(subjectName, staffName, staffID);
                                    staffList.add(data1);
                                    staffNames.add(staffName);
                                }



                            }

                            if(staffList.size()==0){
                                staffNames.add(staffName);
                            }
                            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, staffNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnStaffList.setAdapter(adapter);

                            Log.d("list123", String.valueOf(staffList.size()));

                        } else {
                            showRecordsFound(getResources().getString(R.string.no_records),"");
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showRecordsFound(String subjectName, final String next) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(subjectName);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(next.equals("1")) {

                    txtComments.setText("");
                    dialog.cancel();
                    RequestMeetingForParent.next();

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
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));


    }


}