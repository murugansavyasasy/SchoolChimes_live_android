//package com.vs.schoolmessenger.interfaces;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.vs.schoolmessenger.util.Util_UrlMethods;
//
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.GET;
//import retrofit2.http.Multipart;
//import retrofit2.http.POST;
//import retrofit2.http.Part;
//import retrofit2.http.Query;
//import retrofit2.http.Streaming;
//import retrofit2.http.Url;
//
//public interface MessengerApiInterface {
//
//    @POST(Util_UrlMethods.VERSION_CHECK)
//    Call<JsonArray> VersionCheck(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.GET_COUNTRY_LIST)
//    Call<JsonArray> GetCountryList(@Body JsonObject jsonObject);
//
//    @POST(Util_UrlMethods.GET_CHILD_LIST)
//    Call<JsonArray> GetChildList(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.UNREAD_MESSAGE_COUNT)
//    Call<JsonArray> UnreadMessageCount(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.GET_MESSAGE_COUNT)
//    Call<JsonArray> GetMessageCount(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.GET_FILES)
//    Call<JsonArray> GetFiles(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.READ_STATUS_UPDATE)
//    Call<JsonArray> ReadStatusUpdate(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.CHANGE_PASSWORD)
//    Call<JsonArray> ChangePassword(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.GET_HELP)
//    Call<JsonArray> GetHelp(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.FORGET_PASSWORD)
//    Call<JsonArray> ForgetPassword(@Body JsonArray jsonObject);
//
//    @POST(Util_UrlMethods.DEVICE_TOKEN)
//    Call<JsonArray> DeviceToken(@Body JsonArray jsonObject);
//
//    @Multipart
//    @POST(Util_UrlMethods.INSERT_QUE_REPLY)
//    Call<JsonArray> SignUpload(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);
//
//    // Downloading
//    @Streaming
//    @GET
//    Call<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);
//
//
//    //    Parent App version 4
//    @POST("GetCountryListV4")
//    Call<JsonArray> GetCountryListnew(@Body JsonObject jsonObject);
//
//    @POST("ForgetPassword")
//    Call<JsonArray> ForgetPasswordnew(@Body JsonObject jsonObject);
//
//    @POST("VersionCheck")
//    Call<JsonArray>VersionCheck(@Body JsonObject jsonObject);
//
//    @POST("DeviceToken")
//    Call<JsonArray> DeviceTokennew(@Body JsonObject jsonObject);
//
//    @POST("ChangePassword")
//    Call<JsonArray>ChangePasswordnew(@Body JsonObject jsonObject);
//
//
//    @POST("ReadStatusUpdate")
//    Call<JsonArray>ReadStatusUpdatenew(@Body JsonObject jsonObject);
//
//    @POST("ManageParentLogin")
//    Call<JsonArray> ManageParentLogin(@Body JsonObject jsonObject);
//
//    @POST(Util_UrlMethods.GET_HELP)
//    Call<JsonArray> GetHelpnew(@Body JsonObject jsonObject);
//
//    @POST("GetEmergencyVoiceOrImageOrPDF")
//    Call<JsonArray> GetEmergencyVoiceOrImageOrPDF(@Body JsonObject jsonObject);
//
//
//    @POST("GetFiles")
//    Call<JsonArray> GetFiles(@Body JsonObject jsonObject);
//
//
//    @POST("GetDateWiseUnreadCount")
//    Call<JsonArray> GetDateWiseUnreadCount(@Body JsonObject jsonObject);
//
//
//
//    @POST("GetNoticeBoard")
//    Call<JsonArray> GetNoticeBoard(@Body JsonObject jsonObject);
//
//    @POST("GetExamsOrTests")
//    Call<JsonArray> GetExamsOrTests(@Body JsonObject jsonObject);
//
//    @POST("GetSchoolEvents")
//    Call<JsonArray> GetSchoolEvents(@Body JsonObject jsonObject);
//
//
//    @POST("InsertLeaveInformation")
//    Call<JsonArray> InsertLeaveInformation(@Body JsonObject jsonObject);
//
//
//    @POST("GetAbsentDatesForChild")
//    Call<JsonArray> GetAbsentDatesForChild(@Body JsonObject jsonObject);
//
//    @POST("GetHomeWorkCount")
//    Call<JsonArray> GetHomeWorkCount(@Body JsonObject jsonObject);
//
//
//    @POST("GetHomeWorkFiles")
//    Call<JsonArray> GetHomeWorkFiles(@Body JsonObject jsonObject);
//
//    @POST("GetOverallUnreadCount")
//    Call<JsonArray> GetOverallUnreadCount(@Body JsonObject jsonObject);
//
//
//
//    @POST("GetMemberBookList")
//    Call<JsonArray> GetMemberBookList(@Body JsonObject jsonObject);
//
//    @POST("GetStudentExamList")
//    Call<JsonArray> GetStudentExamList(@Body JsonObject jsonObject);
//
//    @POST("GetStudentExamMarks")
//    Call<JsonArray> GetStudentExamMarks(@Body JsonObject jsonObject);
//
//    @POST("GetStudentInvoice")
//    Call<JsonObject> GetStudentInvoice(@Body JsonObject jsonObject);
//
//    @POST("GetStudentInvoice_App")
//    Call<JsonObject> GetStudentInvoice_App(@Body JsonObject jsonObject);
//
//    @GET("GetInvoiceById")
//    Call<JsonObject> GetInvoiceById(@Query("InvoiceId") String invoice_id);
//
//    @POST("GetStudentPendingFee")
//    Call<JsonObject> GetStudentPendingFee(@Body JsonObject jsonObject);
//}
//
