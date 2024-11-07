package com.vs.schoolmessenger.util;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.util.ArrayList;

/**
 * Created by voicesnap on 5/18/2017.
 */

public class TeacherUtil_JsonRequest {

    public static JsonObject getJsonArray_VersionCheck(String versionCode, String appID,String deviceType,String countryID) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("VersionCode", versionCode);
        jsonObject.addProperty("AppID", appID);
        jsonObject.addProperty("DeviceType", deviceType);
        jsonObject.addProperty("CountryID", countryID);

        Log.d("JsonReq:VersionCheck", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_GetCountryList(String appID) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AppID", appID);
        jsonObject.addProperty("DeviceType", "Android");
        jsonObject.addProperty("CountryCode", "");
        Log.d("JsonReq:GetCountry", jsonObject.toString());
        return jsonObject;
    }


    public static JsonObject getJsonArray_ValidateUser(String mobile,String secureId,String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber",mobile);
        jsonObject.addProperty("DeviceType","Android");
        jsonObject.addProperty("SecureID",secureId);
        jsonObject.addProperty("Password",password);
        Log.d("JsonReq:validate", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_GetStaffList(String mobileNumber, String password,String imei_number,String secureID) {
        JsonObject jsonObject = new JsonObject();
        Log.d("mobno",mobileNumber+password);
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("Password", password);
        jsonObject.addProperty("DeviceType", "Android");
        jsonObject.addProperty("IMEINumber", imei_number);
        jsonObject.addProperty("SecureID", secureID);

        Log.d("JsonReq:GetChildList", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject GetUserDetails(String mobileNumber, String password,String secureID) {
        JsonObject jsonObject = new JsonObject();
        Log.d("mobno",mobileNumber+password);
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("Password", password);
        jsonObject.addProperty("DeviceType", "Android");
        jsonObject.addProperty("SecureID", secureID);
        Log.d("req",jsonObject.toString());


        return jsonObject;
    }

    public static JsonObject getJsonArray_ChangePassword(String mobileNumber, String oldPassword, String newPassword) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("OldPassword", oldPassword);
        jsonObject.addProperty("NewPassword", newPassword);

        Log.d("JsonReq", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_ForgetPassword(String mobileNumber,String countryID) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("CountryID", countryID);

        Log.d("JsonReq:ForgetPassword", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_GetHelp(String mobileNumber, String helpText) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("HelpText", helpText);

        Log.d("JsonReq:GetHelp", jsonObject.toString());
        return jsonObject;
    }

    public static JsonArray getMsgFromMangeMent(ArrayList<TeacherSchoolsModel> schoolsList) {


        JsonArray jsArray = new JsonArray();
        for (int i = 0; i < schoolsList.size(); i++) {

            JsonObject jsonObject = new JsonObject();
            final TeacherSchoolsModel model = schoolsList.get(i);
            jsonObject.addProperty("SchoolID", model.getStrSchoolID());
            jsonObject.addProperty("staffId",model.getStrStaffID());
            jsArray.add(jsonObject);
        }

        Log.d("JsonReq:Count", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_SubjectHandling(String schoolID, String staffID) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("StaffID", staffID);
        jsArray.add(jsonObject);

        Log.d("JsonReq:SubjectHandling", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_GetStudDetail(String schoolID, String targetCode) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("TargetCode", targetCode);
        jsArray.add(jsonObject);

        Log.d("JsonReq:GetStudDetail", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_GetSchoolLists(String mobileNumber, String callerType) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("CallerType", callerType);
        jsArray.add(jsonObject);

        Log.d("JsonReq:GetSchoolLists", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_SendSmsMgtAdmin(String strSchoolID, String strStaffID, String strMsg, String callerType, String mobileNumber) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("SchoolID", strSchoolID);
        jsonObject.addProperty("StaffID", strStaffID);
        jsonObject.addProperty("Message", strMsg);
        jsonObject.addProperty("CallerType", callerType);
        jsonObject.addProperty("CallerID", mobileNumber);
        jsArray.add(jsonObject);

        Log.d("JsonReq:SendSmsMgtAdmin", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_SendSmsAdminToSchools(String strMsg, JsonArray jsonArray) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("Message", strMsg);
        jsonObject.add("School", jsonArray);
        jsArray.add(jsonObject);

        Log.d("JsonReq:SmsAdminToSch", jsArray.toString());
        return jsArray;
    }


    public static JsonArray getJsonArray_SendVoiceMgtAdmin(String strSchoolID, String strStaffID, String callerType, String mobileNumber, String duration) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("SchoolID", strSchoolID);
        jsonObject.addProperty("StaffID", strStaffID);
        jsonObject.addProperty("CallerType", callerType);
        jsonObject.addProperty("CallerID", mobileNumber);
        jsonObject.addProperty("Duration", duration);
        jsArray.add(jsonObject);

        Log.d("JsonReq:SendVoiceMgtAdm", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_GetImageMgtAdmin(String strSchoolID, String strStaffID, String callerType, String mobileNumber) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("SchoolID", strSchoolID);
        jsonObject.addProperty("StaffID", strStaffID);
        jsonObject.addProperty("CallerType", callerType);
        jsonObject.addProperty("CallerID", mobileNumber);
        jsArray.add(jsonObject);

        Log.d("JsonReq:SendImageMgtAdm", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_GetFiles(String circularDate, String childID, String schoolID, String type) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CircularDate", circularDate);
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("Type", type);
        jsArray.add(jsonObject);

        Log.d("JsonReq:GetFiles", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_ReadStatusUpdate(String id, String childID, String schoolID, String type, String date) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ID", id);
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("Type", type);
        jsonObject.addProperty("Date", date);
        jsArray.add(jsonObject);

        Log.d("JsonReq:RdStatusUpdate", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_DeviceToken(String mobileNumber, String deviceToken, String deviceType) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("DeviceToken", deviceToken);
        jsonObject.addProperty("DeviceType", deviceType);
        jsArray.add(jsonObject);

        Log.d("JsonReq:DeviceToken", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_InsertQueReply(String id, String childID, String schoolID, String replyText) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ID", id);
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("ReplyText", replyText);
        jsArray.add(jsonObject);

        Log.d("JsonReq:InsertQueReply", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_GetSecAtt(String schoolID) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolID", schoolID);
        jsArray.add(jsonObject);

        Log.d("JsonReq:GetSecAtt", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_GetSubjects(String schoolID, String strSecCode) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("SecCode", strSecCode);
        jsArray.add(jsonObject);

        Log.d("JsonReq:GetSubjects", jsArray.toString());
        return jsArray;
    }

    public static JsonArray getJsonArray_SendSMSAtt(String schoolID, String staffID, String allPresent, JsonArray jsonArray) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("StaffID", staffID);
        jsonObject.addProperty("AllPresent", allPresent);
        jsonObject.add("StuID", jsonArray);
        jsArray.add(jsonObject);

        Log.d("JsonReq:GetSecAtt", jsArray.toString());
        return jsArray;
    }

}
