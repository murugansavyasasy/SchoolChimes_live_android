package com.vs.schoolmessenger.util;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by voicesnap on 5/18/2017.
 */

public class Util_JsonRequest {

    public static JsonArray getJsonArray_VersionCheck(String versionCode, String appID) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("VersionCode", versionCode);
        jsonObject.addProperty("AppID", appID);
        jsArray.add(jsonObject);

        Log.d("JsonReq:VersionCheck", jsArray.toString());
        return jsArray;
    }

    public static JsonObject getJsonArray_VersionChecknew(String versionCode, String appID, String type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("VersionCode", versionCode);
        jsonObject.addProperty("AppID", appID);
        jsonObject.addProperty("DeviceType", type);

        Log.d("JsonReq:VersionCheck", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_GetCountryList(String appID) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AppID", appID);

        Log.d("JsonReq:GetCountry", jsonObject.toString());
        return jsonObject;
    }

    public static JsonArray getJsonArray_GetChildList(String mobileNumber, String password) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("Password", password);
        jsonObject.addProperty("DeviceType", "Android");
        jsArray.add(jsonObject);

        Log.d("JsonReq:GetChildList", jsArray.toString());
        return jsArray;
    }


    public static JsonObject getJsonArray_GetChildListnew(String mobileNumber, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("Password", password);
        jsonObject.addProperty("DeviceType", "Android");

        Log.d("JsonReq:GetChildList", jsonObject.toString());
        return jsonObject;
    }

    public static JsonArray getJsonArray_UnreadMessageCount(String mobileNumber) {
        JsonArray jsArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsArray.add(jsonObject);

        Log.d("JsonReq:UnreadMsgCount", jsArray.toString());
        return jsArray;
    }

    public static JsonObject getJsonArray_GetMessageCount(String childID, String schoolID) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);

        Log.d("JsonReq:GetMessageCount", jsonObject.toString());
        return jsonObject;
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

    public static JsonObject getJsonArray_GetEmergencyvoice(String childID, String schoolID, String type, String MobileNumber) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("Type", type);
        jsonObject.addProperty("MobileNumber", MobileNumber);
        Log.d("JsonReq:GetFiles", jsonObject.toString());
        return jsonObject;
    }


    public static JsonObject getJsonArray_GetGeneralvoiceortext(String childID, String schoolID, String type, String date) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("Type", type);
        jsonObject.addProperty("CircularDate", date);

        Log.d("JsonReq:GetFiles", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_GetPDF(String childID, String schoolID, String type, String date) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("Type", type);
        jsonObject.addProperty("CircularDate", date);

        Log.d("JsonReq:GetFiles", jsonObject.toString());
        return jsonObject;
    }


    public static JsonObject getJsonArray_Getnoticeboard(String childID, String schoolID) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        Log.d("JsonReq:GetFiles", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_GetnoticeboardStaffSze(String childID, String schoolID) {
        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        Log.d("JsonReq:GetFiles", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getNoticeBoardStaff(String StaffId, String SchoolId) {
        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("staff_id", StaffId);
        jsonObject.addProperty("SchoolID", SchoolId);
        Log.d("JsonReq:GetFiles", jsonObject.toString());
        return jsonObject;
    }


    public static JsonObject getJsonArray_Gethomework(String childID, String date, String type, String schoolID, String mobile) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("Date", date);
        jsonObject.addProperty("Type", type);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("MobileNumber", mobile);
        Log.d("JsonReq:GetFiles", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_ReadStatusUpdate(String id, String childID, String schoolID, String type, String date) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ID", id);
        jsonObject.addProperty("Type", type);

        Log.d("JsonReq:RdStatusUpdate", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_addPoints(String mobile_number, String activity_name) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile_number", mobile_number);
        jsonObject.addProperty("activity", activity_name);
        jsonObject.addProperty("user_type", Util_Common.USER_TYPE);
        Log.d("JsonReq:addPoints", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_ChangePassword(String mobileNumber, String oldPassword, String newPassword) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("OldPassword", oldPassword);
        jsonObject.addProperty("NewPassword", newPassword);

        Log.d("JsonReq:ChangePassword", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_ForgetPassword(String mobileNumber) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobileNumber);

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

    public static JsonObject getJsonArray_DeviceToken(String mobileNumber, String deviceToken, String deviceType) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("MobileNumber", mobileNumber);
        jsonObject.addProperty("DeviceToken", deviceToken);
        jsonObject.addProperty("DeviceType", deviceType);

        Log.d("JsonReq:DeviceToken", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_InsertQueReply(String id, String childID, String schoolID, String replyText) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("ID", id);
        jsonObject.addProperty("ChildID", childID);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("ReplyText", replyText);

        Log.d("JsonReq:InsertQueReply", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJsonArray_overallUnreadCount(String childID, String schoolID) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("ChildID", childID);

        Log.d("JsonReq:InsertQueReply", jsonObject.toString());
        return jsonObject;
    }

    public static JsonObject getJson_feesDetails(String childID, String schoolID) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("ChildID", childID);

        Log.d("JsonReq:InsertQueReply", jsonObject.toString());
        return jsonObject;
    }

}
