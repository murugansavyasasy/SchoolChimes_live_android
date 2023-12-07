package com.vs.schoolmessenger.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.LessonPlan.Model.EditLessonModel;
import com.vs.schoolmessenger.LessonPlan.Model.LessonPlanModel;
import com.vs.schoolmessenger.LessonPlan.Model.Response;
import com.vs.schoolmessenger.LessonPlan.Model.ViewLessonPlanModel;
import com.vs.schoolmessenger.model.CertificateListModelItem;
import com.vs.schoolmessenger.model.CertificateRequestModelItem;
import com.vs.schoolmessenger.model.CertificateTypeModelItemItem;
import com.vs.schoolmessenger.model.DailyFeeCollectionModelItem;
import com.vs.schoolmessenger.model.HomeWorkModel;
import com.vs.schoolmessenger.model.NewUpdatesModel;
import com.vs.schoolmessenger.payment.Model.FeeDetailsItems;
import com.vs.schoolmessenger.util.Util_UrlMethods;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface TeacherMessengerApiInterface {

    @POST("VersionCheck")
    Call<JsonArray> VersionCheck(@Body JsonObject jsonObject);

    @POST("GetCountryList")
    Call<JsonArray> GetCountryList(@Body JsonObject jsonObject);

//    @POST("GetUserDetails")
//    Call<JsonArray> getStaffDetail(@Body JsonObject jsonObject);

    @POST("GetUserDetailsWithValidation")
    Call<JsonArray> getStaffDetail(@Body JsonObject jsonObject);

    @POST("ValidatePassword")
    Call<JsonArray> validatePassword(@Body JsonObject jsonObject);

    @POST("HelpText")
    Call<JsonArray> GetHelp(@Body JsonObject jsonObject);

    @POST("ForgetPasswordByCountryID")
    Call<JsonArray> ForgetPassword(@Body JsonObject jsonObject);

    @POST("SendSmsStaffwise")
    Call<JsonArray> SendSmsStaffwise(@Body JsonArray jsonObject);

    @POST("SubjectHandling")
    Call<JsonArray> staffSubjectHandling(@Body JsonArray jsonObject);

    @POST("GetStudDetail")
    Call<JsonArray> GetStudDetail(@Body JsonArray jsonObject);

    @POST("GetSchoolLists")
    Call<JsonArray> GetSchoolLists(@Body JsonArray jsonObject);

    @POST("GetSecAtt")
    Call<JsonArray> GetStandardsAndSectionsList(@Body JsonArray jsonObject);

    @POST("SendSMSAtt")
    Call<JsonArray> SendAttendanceReport(@Body JsonArray jsonObject);

    @POST("GetClassSubjects")
    Call<JsonArray> GetClassSubjects(@Body JsonArray jsonObject);

    @Multipart
    @POST("StaffwiseVoice")
    Call<JsonArray> StaffwiseVoice(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @Multipart
    @POST("GetImageStaff")
    Call<JsonArray> StaffwiseImage(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @POST("SendSmsMgtAdmin")
    Call<JsonArray> SendSmsMgtAdminToAllSchools(@Body JsonArray jsonObject);

    @POST("adminsendsmstoschools")
    Call<JsonArray> SendSmsAdminToSchools(@Body JsonArray jsonObject);

    @POST("SendGroupMessageSnrMgt")
    Call<JsonArray> textMgtStdGrp(@Body JsonArray jsonObject);

    @Multipart
    @POST("SendVoiceMgtAdmin")
    Call<JsonArray> SendVoiceMgtAdminToAllSchools(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @Multipart
    @POST("getVoiceAdmin")
    Call<JsonArray> SendVoiceAdminToSchools(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @Multipart
    @POST("SnrMgtStdGrp")
    Call<JsonArray> SendVoiceMgtStdGrp(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @Multipart
    @POST("GetImageMgtAdmin")
    Call<JsonArray> SendImageMgtAdminToAllSchools(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @Multipart
    @POST("GetImageAdmin")
    Call<JsonArray> SendImageAdminToSchools(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @Multipart
    @POST("GetImageSnrMgt")
    Call<JsonArray> SendImageMgtStdGrp(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @Multipart
    @POST("SendVoiceToEntireSchools")
    Call<JsonArray> SendVoiceToEntireSchools(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @POST("SendSMSToEntireSchools")
    Call<JsonArray> SendSMSToEntireSchools(@Body JsonObject jsonObject);

    @POST("ManageNoticeBoard")
    Call<JsonArray> ManageNoticeBoard(@Body JsonObject jsonObject);

    @POST("GetStandardsAndSubjectsAsStaff")
    Call<JsonArray> GetStandardsAndSubjectsAsStaff(@Body JsonObject jsonObject);


    @POST("GetStandardsAndSubjectsAsStaffWithoutNewOld")
    Call<JsonArray> GetStandardsAndSubjectsAsStaffWithoutNewOld(@Body JsonObject jsonObject);


    @POST("GetSchoolStrengthBySchoolID")
    Call<JsonArray> GetSchoolStrengthBySchoolID(@Body JsonObject jsonObject);


    @POST("GetAllStandardsAndGroups")
    Call<JsonArray> GetAllStandardsAndGroups(@Body JsonObject jsonObject);

    @POST("GetStaffGroups")
    Call<JsonArray> GetStaffGroups(@Body JsonObject jsonObject);

    @POST("GetAllStandardsAndGroups")
    Call<JsonArray> GetAllStandardsAndGroupsForVideo(@Body JsonObject jsonObject);

    @Multipart
    @POST("SendVoiceToGroupsAndStandards")
    Call<JsonArray> SendVoiceToGroupsAndStandards(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);


    @POST("SendSMSToGroupsAndStandards")
    Call<JsonArray> SendSMSToGroupsAndStandards(@Body JsonObject jsonObject);


    @POST("SendSMSAsStaffToGroups")
    Call<JsonArray> SendSMSAsStaffToGroups(@Body JsonObject jsonObject);

    @Multipart
    @POST("SendVoiceAsStaffToGroups")
    Call<JsonArray> SendVoiceAsStaffToGroups(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);


    @POST("ManageSchoolEvents")
    Call<JsonArray> ManageSchoolEvents(@Body JsonObject jsonObject);


    @POST("SendOnlineClassToGroupsAndStandards")
    Call<JsonArray> sendOnlineClassTogroupsStd(@Body JsonObject jsonObject);

    @POST("SendOnlineClassAsStaffToEntireSection")
    Call<JsonArray> sendOnlineClassToSections(@Body JsonObject jsonObject);

    @Multipart
    @POST("SendMultipleImageToGroupsAndStandards")
    Call<JsonArray> sendMultipleImagesToGroupAndStand(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part[] files);


    @Multipart
    @POST("SendImageToGroupsAndStandards")
    Call<JsonArray> SendImageToGroupsAndStandards(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @POST("GetAbsenteesCountByDate")
    Call<JsonArray> GetAbsenteesCountByDate(@Body JsonObject jsonObject);


    @POST("InsertExam")
    Call<JsonArray> InsertExam(@Body JsonObject jsonObject);

    @POST("GetStudDetailForSection")
    Call<JsonArray> GetStudDetailForSection(@Body JsonObject jsonObject);


    @Multipart
    @POST("SendVoiceAsStaffToSpecificStudents")
    Call<JsonArray> SendVoiceAsStaffToSpecificStudents(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);


    @POST("SendSMSAsStaffToSpecificStudents")
    Call<JsonArray> SendSMSAsStaffToSpecificStudents(@Body JsonObject jsonObject);

    @Multipart
    @POST("SendImageAsStaffToSpecificStudents")
    Call<JsonArray> SendImageAsStaffToSpecificStudents(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);


    @Multipart
    @POST("SendMultipleImageAsStaffToSpecificStudents")
    Call<JsonArray> sendMultipleImagesToSpecificStudents(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part[] files);


    @Multipart
    @POST("SendVoiceAsStaffToEntireSection")
    Call<JsonArray> SendVoiceAsStaffToEntireSection(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @POST("SendSMSAsStaffToEntireSection")
    Call<JsonArray> SendSMSAsStaffToEntireSection(@Body JsonObject jsonObject);

    @Multipart
    @POST("SendImageAsStaffToEntireSection")
    Call<JsonArray> SendImageAsStaffToEntireSection(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);


    @Multipart
    @POST("SendMultipleImageAsStaffToEntireSection")
    Call<JsonArray> sendMultipleImagesToEntireSection(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part[] files);


    @Multipart
    @POST("InsertHomeWorkVoice")
    Call<JsonArray> InsertHomeWorkVoice(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @POST("InsertHomeWork")
    Call<JsonArray> InsertHomeWork(@Body JsonObject jsonObject);

//    @POST("SendAbsenteesSMS")
    @POST("SendAbsenteesSMSWithSessionType")
    Call<JsonArray> SendAbsenteesSMS(@Body JsonObject jsonObject);


    @POST("InsertExamToEntireSection")
    Call<JsonArray> InsertExamToEntireSection(@Body JsonObject jsonObject);

    @POST("InsertExamToEntireSection_WithSubjectSyllabus")
    Call<JsonArray> InsertExamToEntireSection_WithSubjectSyllabus(@Body JsonObject jsonObject);

    @POST("InsertExamToSpecificStudents")
    Call<JsonArray> InsertExamToSpecificStudents(@Body JsonObject jsonObject);

    @POST("InsertExamToSpecificStudents_WithSubjectSyllabus")
    Call<JsonArray> InsertExamToSpecificStudents_WithSubjectSyllabus(@Body JsonObject jsonObject);

    @POST("ChangePassword")
    Call<JsonArray> ChangePasswordnew(@Body JsonObject jsonObject);

    @POST("ResetPasswordAfterForget")
    Call<JsonArray> ResetPasswordAfterForget(@Body JsonObject jsonObject);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);


    @GET("CheckMobileNumberforUpdatePassword")
    Call<JsonArray> CheckMobileNumberforUpdatePassword(@Query("MobileNumber") String Mobilenumber);

    @POST("CheckMobileNumberforUpdatePasswordByCountryID")
    Call<JsonArray> CheckMobileNumberforUpdatePasswordByCountryID(@Body JsonObject jsonObject);


    @POST("ValidateUser")
    Call<JsonArray> ValidateUser(@Body JsonObject jsonObject);

    @POST("ValidateOTP")
    Call<JsonArray> ValidateOTP(@Body JsonObject jsonObject);

    @POST("UpdateNewPasswordforNewUser")
    Call<JsonArray> UpdateNewPasswordforNewUser(@Body JsonObject jsonObject);


    @GET("GetFeedbackQuestions")
    Call<JsonArray> GetFeedbackQuestions();

    @POST("InsertFeedbackDetails")
    Call<JsonArray> InsertFeedbackDetails(@Body JsonObject jsonObject);

    @POST("GetMemberBookList")
    Call<JsonArray> GetMemberBookList(@Body JsonObject jsonObject);

    @POST("GetMessageCount")
    Call<JsonArray> GetMessageCount(@Body JsonObject jsonObject);


    @POST("GetMessageCount_Archive")
    Call<JsonArray> GetMessageCount_Archive(@Body JsonObject jsonObject);

    @POST("GetFilesStaff")
    Call<JsonArray> GetFilesStaff(@Body JsonObject jsonObject);

    @POST("GetFilesStaff_Archive")
    Call<JsonArray> GetFilesStaff_Archive(@Body JsonObject jsonObject);


    @POST("GetAllStaffs")
    Call<JsonArray> GetAllStaffs(@Body JsonObject jsonObject);

    @POST("GetCommonSubjectsForSections")
    Call<JsonArray> GetSubjects(@Body JsonObject jsonObject);

    @POST("InitiatePrincipalCall")
    Call<JsonArray> InitiatePrincipalCall(@Body JsonObject jsonObject);


    @POST(Util_UrlMethods.VERSION_CHECK)
    Call<JsonArray> VersionCheck(@Body JsonArray jsonObject);


    @POST(Util_UrlMethods.GET_CHILD_LIST)
    Call<JsonArray> GetChildList(@Body JsonArray jsonObject);

    @POST(Util_UrlMethods.UNREAD_MESSAGE_COUNT)
    Call<JsonArray> UnreadMessageCount(@Body JsonArray jsonObject);

    @POST(Util_UrlMethods.GET_MESSAGE_COUNT)
    Call<JsonArray> GetMessageCount(@Body JsonArray jsonObject);

    @POST(Util_UrlMethods.GET_FILES)
    Call<JsonArray> GetFiles(@Body JsonArray jsonObject);

    @POST(Util_UrlMethods.READ_STATUS_UPDATE)
    Call<JsonArray> ReadStatusUpdate(@Body JsonArray jsonObject);


    @POST(Util_UrlMethods.GET_HELP)
    Call<JsonArray> GetHelp(@Body JsonArray jsonObject);

    @POST(Util_UrlMethods.FORGET_PASSWORD)
    Call<JsonArray> ForgetPassword(@Body JsonArray jsonObject);

    @POST(Util_UrlMethods.DEVICE_TOKEN)
    Call<JsonArray> DeviceToken(@Body JsonArray jsonObject);

    @Multipart
    @POST(Util_UrlMethods.INSERT_QUE_REPLY)
    Call<JsonArray> SignUpload(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @POST("GetCountryListV4")
    Call<JsonArray> GetCountryListnew(@Body JsonObject jsonObject);

    @POST("ForgetPassword")
    Call<JsonArray> ForgetPasswordnew(@Body JsonObject jsonObject);


    @POST("DeviceToken")
    Call<JsonArray> DeviceTokennew(@Body JsonObject jsonObject);

    @POST("ReadStatusUpdate")
    Call<JsonArray> ReadStatusUpdatenew(@Body JsonObject jsonObject);

    @POST("ReadStatusUpdate_Archive")
    Call<JsonArray> ReadStatusUpdatenew_Archive(@Body JsonObject jsonObject);

    @POST("ManageParentLogin")
    Call<JsonArray> ManageParentLogin(@Body JsonObject jsonObject);

    @POST(Util_UrlMethods.GET_HELP)
    Call<JsonArray> GetHelpnew(@Body JsonObject jsonObject);

    @POST("GetEmergencyVoiceOrImageOrPDF")
    Call<JsonArray> GetEmergencyVoiceOrImageOrPDF(@Body JsonObject jsonObject);

    @POST("GetEmergencyVoiceOrImageOrPDF_Archive")
    Call<JsonArray> LoadMoreGetEmergencyVoiceOrImageOrPDF(@Body JsonObject jsonObject);

    @POST("GetFiles")
    Call<JsonArray> GetFiles(@Body JsonObject jsonObject);

    @POST("GetFiles_Archive")
    Call<JsonArray> GetFiles_Archive(@Body JsonObject jsonObject);


    @POST("GetDateWiseUnreadCount")
    Call<JsonArray> GetDateWiseUnreadCount(@Body JsonObject jsonObject);

    @POST("GetDateWiseUnreadCount_Archive")
    Call<JsonArray> LoadMoreGetDateWiseUnreadCount(@Body JsonObject jsonObject);

    @POST("GetNoticeBoard")
    Call<JsonArray> GetNoticeBoard(@Body JsonObject jsonObject);

    @POST("GetUpcomingOnlineClassRooms")
    Call<JsonObject> getOnlineClasses(@Body JsonObject jsonObject);

    @GET("GetOnlineMeetingPlatformTypes")
    Call<JsonArray> getOnlineMeetingTypes();

    @POST("GetOnlineClassRoomsByStaffID")
    Call<JsonObject> GetOnlineClassRoomsByStaffID(@Body JsonObject jsonObject);

    @POST("CancelOnlineClassRoom")
    Call<JsonArray> CancelOnlineClassRoom(@Body JsonObject jsonObject);


    @POST("GetNoticeBoard_Archive")
    Call<JsonArray> LoadMoreGetNoticeBoard(@Body JsonObject jsonObject);

    @POST("GetExamsOrTests")
    Call<JsonArray> GetExamsOrTests(@Body JsonObject jsonObject);

    @POST("GetSchoolEvents")
    Call<JsonArray> GetSchoolEvents(@Body JsonObject jsonObject);

    @POST("GetSchoolEvents_Archive")
    Call<JsonArray> LoadMoreGetSchoolEvents(@Body JsonObject jsonObject);


    @POST("InsertLeaveInformation")
    Call<JsonArray> InsertLeaveInformation(@Body JsonObject jsonObject);


    @POST("GetAbsentDatesForChild")
    Call<JsonArray> GetAbsentDatesForChild(@Body JsonObject jsonObject);

    @POST("GetAbsentDatesForChild_Archive")
    Call<JsonArray> LoadMoreGetAbsentDatesForChild(@Body JsonObject jsonObject);

    @POST("GetHomeWorkCount")
    Call<JsonArray> GetHomeWorkCount(@Body JsonObject jsonObject);

    @POST("GetHomeWork")
    Call<List<HomeWorkModel>> getHomeWorkDetails(@Body JsonObject jsonObject);

    @POST("GetHomeWork_archive")
    Call<List<HomeWorkModel>> getHomeWorkDetails_Archive(@Body JsonObject jsonObject);

    @POST("GetHomeWorkCount_Archive")
    Call<JsonArray> LoadMoreGetHomeWorkCount(@Body JsonObject jsonObject);

    @POST("GetHomeWorkFiles")
    Call<JsonArray> GetHomeWorkFiles(@Body JsonObject jsonObject);

    @POST("GetHomeWorkFiles_Archive")
    Call<JsonArray> GetHomeWorkFiles_Archive(@Body JsonObject jsonObject);

    @POST("GetOverallUnreadCount")
    Call<JsonArray> GetOverallUnreadCount(@Body JsonObject jsonObject);


    @POST("GetStudentExamList")
    Call<JsonArray> GetStudentExamList(@Body JsonObject jsonObject);

    @POST("GetStudentList")
    Call<JsonObject> GetStudentReport(@Body JsonObject jsonObject);

    @POST("daily-collection-fee-app")
    Call<List<DailyFeeCollectionModelItem>> getDailyCollection(@Body JsonObject jsonObject);


    @POST("new-update-details-app")
    Call<NewUpdatesModel> getNewUpdateDetails(@Body JsonObject jsonObject);


    @GET("lesson-plan/get_lesson_plan_staff_report_App")
    Call<LessonPlanModel> getLessonPlans(@Query("request_type") String request_type, @Query("institute_id") String institute_id, @Query("user_id") String user_id);

    @GET("lesson-plan/view_lesson_plan_for_App")
    Call<ViewLessonPlanModel> getViewLessonPlans(@Query("section_subject_id") String request_type, @Query("institute_id") String institute_id, @Query("user_id") String user_id,@Query("status_filter") String status_filter);

    @GET("lesson-plan/get_data_to_edit_staff_lessonplan_app")
    Call<EditLessonModel> getEditDataCard(@Query("particular_id") String particular_id, @Query("request_type") String request_type, @Query("institute_id") String institute_id, @Query("user_id") String user_id);

    @POST("lesson-plan/delete_lesson_plan_data_App")
    Call<Response> deleteLessonPlan(@Body JsonObject jsonObject);

    @POST("lesson-plan/update_status_for_lesson_plan_App")
    Call<Response> updateStatusIcon(@Body JsonObject jsonObject);

    @POST("lesson-plan/update_staff_lessonplan_particular_app")
    Call<Response> updateLessonParticularCard(@Body JsonObject jsonObject);


    @GET("other-req/certificate-types")
    Call<List<CertificateTypeModelItemItem>> getCertificateTypes();

    @POST("other-req/create-other-req")
    Call<List<CertificateRequestModelItem>> createCertificateRequest(@Body JsonObject jsonObject);

    @POST("parent-request-list")
    Call<List<CertificateListModelItem>> getParentCertificateList(@Body JsonObject jsonObject);


    @POST("GetStudentExamMarks")
    Call<JsonArray> GetStudentExamMarks(@Body JsonObject jsonObject);

    @POST("GetStudentInvoice")
    Call<JsonObject> GetStudentInvoice(@Body JsonObject jsonObject);

    @POST("GetStudentInvoice_App")
    Call<JsonObject> GetStudentInvoice_App(@Body JsonObject jsonObject);

    @GET("GetInvoiceById")
    Call<JsonObject> GetInvoiceById(@Query("InvoiceId") String invoice_id);

    @POST("GetStudentPendingFee")
    Call<JsonObject> GetStudentPendingFee(@Body JsonObject jsonObject);


    @POST("GetSMSHistory")
    Call<JsonArray> GetSMSHistory(@Body JsonObject jsonObject);

    @POST("GetVoiceHistory")
    Call<JsonArray> GetVoiceHistory(@Body JsonObject jsonObject);


    @POST("SendVoiceToEntireSchoolsByVoiceHistory")
    Call<JsonArray> SendVoiceToEntireSchoolsByVoiceHistory(@Body JsonObject jsonObject);

    @POST("SendVoiceAsStaffToEntireSectionfromVoiceHistory")
    Call<JsonArray> SendVoiceAsStaffToEntireSectionfromVoiceHistory(@Body JsonObject jsonObject);

    @POST("SendVoicetoSpecificStudentsfromVoiceHistory")
    Call<JsonArray> SendVoicetoSpecificStudentsfromVoiceHistory(@Body JsonObject jsonObject);

    @POST("SendVoicetoGroupsStandardsfromVoiceHistory")
    Call<JsonArray> SendVoicetoGroupsStandardsfromVoiceHistory(@Body JsonObject jsonObject);

    @POST("GetMenuDetails")
    Call<JsonArray> ChangeLanguage(@Body JsonObject jsonObject);

    @POST("get-ads")
    Call<JsonArray> getAds(@Body JsonObject jsonObject);

    @POST("ads-viewer")
    Call<JsonArray> adsviewer(@Body JsonObject jsonObject);

    @POST("GetLeaveRequests")
    Call<JsonArray> GetLeaveRequests(@Body JsonObject jsonObject);

    @POST("Updateleavestatus")
    Call<JsonObject> Updateleavestatus(@Body JsonObject jsonObject);

    @POST("ViewHolidays")
    Call<JsonArray> ViewHolidays(@Body JsonObject jsonObject);

    @POST("ViewHolidays_Archive")
    Call<JsonArray> LoadMoreViewHolidays(@Body JsonObject jsonObject);

    @POST("getStaffDetails")
    Call<JsonObject> getStaffDetails(@Body JsonObject jsonObject);


    @GET("getFAQLink")
    Call<JsonObject> getFAQLink(@Query("MemberID") String Mobilenumber, @Query("Usertype") String uertype);

    @POST("GetPaymentGatewayLink")
    Call<JsonArray> getPayment(@Body JsonObject jsonObject);

    @POST("GetMeetingRequestsforStaff")
    Call<JsonArray> GetMeetingRequestsforStaff(@Body JsonObject jsonObject);

    @POST("GetMeetingRequestsforParents")
    Call<JsonArray> GetMeetingRequestsforParents(@Body JsonObject jsonObject);

    @POST("ManageParentTeacherMeetingRequests")
    Call<JsonArray> ManageParentTeacherMeetingRequests(@Body JsonObject jsonObject);


    @Multipart
    @POST("UploadVideostoYoutube")
    Call<JsonArray> UploadVideostoYoutube(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @POST("AgreeTermsAndConditions")
    Call<JsonArray> AgreeTermsAndConditions(@Body JsonObject jsonObject);


    @POST("GetPasswordResetStatus")
    Call<JsonArray> GetPasswordResetStatus(@Body JsonObject jsonObject);

    @Multipart
    @POST("ManageAssignmentFromApp")
    Call<JsonArray> ManageAssignmentFromAppmessage(@Part("Info") RequestBody requestBody);


    @Multipart
    @POST("ManageAssignmentFromApp")
    Call<JsonArray> ManageAssignmentFromApp(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part file);

    @Multipart
    @POST("ManageAssignmentFromApp")
    Call<JsonArray> ManageAssignmentFromAppImage(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part[] file);

    @POST("ViewAllAssignmentListByStaff")
    Call<JsonArray> ViewAllAssignmentListByStaff(@Body JsonObject jsonObject);

    @POST("ViewAllAssignmentListByStaff_Archive")
    Call<JsonArray> ViewAllAssignmentListByStaff_Archive(@Body JsonObject jsonObject);

    @POST("DeleteAssignmentFromApp")
    Call<JsonObject> DeleteAssignmentFromApp(@Body JsonObject jsonObject);

    @POST("DeleteAssignmentFromApp_Archive")
    Call<JsonObject> DeleteAssignmentFromApp_Archive(@Body JsonObject jsonObject);

    @POST("ForwardAssignment")
    Call<JsonArray> ForwardAssignment(@Body JsonObject jsonObject);

    @POST("GetAssignmentMemberCount")
    Call<JsonArray> GetAssignmentMemberCount(@Body JsonObject jsonObject);

    @POST("GetAssignmentMemberCount_Archive")
    Call<JsonArray> GetAssignmentMemberCount_Archive(@Body JsonObject jsonObject);

    @POST("ViewAssignmentContent")
    Call<JsonArray> ViewAssignmentContent(@Body JsonObject jsonObject);

    @POST("ViewAssignmentContent_Archive")
    Call<JsonArray> ViewAssignmentContent_Archive(@Body JsonObject jsonObject);

    @Multipart
    @POST("SubmitAssignmentFromApp")
    Call<JsonArray> SubmitAssignmentFromApp(@Part("Info") RequestBody requestBody, @Part MultipartBody.Part[] file);

    @POST("SubmitAssignmentFromAppWithCloudURL")
    Call<JsonArray> SubmitAssignmentFromAppWithCloudURL(@Body JsonObject jsonObject);

    @POST("SubmitAssignmentFromAppWithCloudURL_Archive")
    Call<JsonArray> SubmitAssignmentFromAppWithCloudURL_Archive(@Body JsonObject jsonObject);

    @POST("ManageAssignmentFromAppWithCloudURL")
    Call<JsonArray> ManageAssignmentFromAppWithCloudURL(@Body JsonObject jsonObject);

    @POST("SendMultipleImagePDFAsStaffToSpecificStudentsWithCloudURL")
    Call<JsonArray> SendMultipleImagePDFAsStaffToSpecificStudentsWithCloudURL(@Body JsonObject jsonObject);

    @POST("SendMultipleImagePDFToEntireSchoolsWithCloudURL")
    Call<JsonArray> SendMultipleImagePDFToEntireSchoolsWithCloudURL(@Body JsonObject jsonObject);

    @POST("SendMultipleImagePDFAsStaffToEntireSectionWithCloudURL")
    Call<JsonArray> SendMultipleImagePDFAsStaffToEntireSectionWithCloudURL(@Body JsonObject jsonObject);

    @POST("SendMultipleImagePDFToGroupsAndStandardsWithCloudURL")
    Call<JsonArray> SendMultipleImagePDFToGroupsAndStandardsWithCloudURL(@Body JsonObject jsonObject);


    @POST("SendMultipleImagePDFAsStaffToGroupsWithCloudURL")
    Call<JsonArray> SendMultipleImagePDFAsStaffToGroupsWithCloudURL(@Body JsonObject jsonObject);


    @POST("SendVideoAsStaffToGroups")
    Call<JsonArray> SendVideoAsStaffToGroups(@Body JsonObject jsonObject);

    @POST("GetAssignmentForStudent")
    Call<JsonArray> GetAssignmentForStudent(@Body JsonObject jsonObject);

    @POST("GetAssignmentForStudent_Archive")
    Call<JsonArray> LoadMoreGetAssignmentForStudent(@Body JsonObject jsonObject);

    @POST("GetVideosForStudent")
    Call<JsonArray> GetVideosForStudent(@Body JsonObject jsonObject);

    @POST("GetVideosForStudent_Archive")
    Call<JsonArray> LoadMoreGetVideosForStudent(@Body JsonObject jsonObject);


    @Headers({"Content-Type: application/json",
            "Accept: application/vnd.vimeo.*+json;version=3.4"})
    @POST("/me/videos")
    Call<JsonObject> VideoUpload(@Body JsonObject jsonObject, @Header("Authorization") String head);


    @Headers({"Tus-Resumable: 1.0.0",
            "Upload-Offset: 0",
            "Content-Type: application/offset+octet-stream",
            "Accept: application/vnd.vimeo.*+json;version=3.4"})
    @PUT("upload")
    Call<ResponseBody> patchVimeoVideoMetaData(@Query("ticket_id") String ticketid,
                                               @Query("video_file_id") String videoid,
                                               @Query("signature") String signatureid,
                                               @Query("v6") String v6id,
                                               @Query("redirect_url") String redirecturl,
                                               @Body RequestBody file);

    @Headers({"Tus-Resumable: 1.0.0",
            "Upload-Offset: 0",
            "Content-Type: application/offset+octet-stream",
            "Accept: application/vnd.vimeo.*+json;version=3.4"})
    @PATCH("https://asia-files.tus.vimeo.com/files/vimeo-prod-src-tus-asia/{id}")
    Call<ResponseBody> patch(@Path("id") String id, @Body RequestBody file);

    @GET("config")
    Call<JsonObject> Videoplay();

    @GET("userinfo")
    Call<JsonObject> getLinkedInprofiles(@Header("Authorization") String BearerToken);



    @POST("SendVideoFromAppForEnitireSchool")
    Call<JsonArray> SendVideoFromAppForEnitireSchool(@Body JsonObject jsonObject);

    @POST("AppSendVideoToGroupsAndStandards")
    Call<JsonArray> AppSendVideoToGroupsAndStandards(@Body JsonObject jsonObject);

    @POST("SendVideoAsStaffToEntireSection")
    Call<JsonArray> SendVideoAsStaffToEntireSection(@Body JsonObject jsonObject);

    @POST("SendVideoAsStaffToSpecificStudents")
    Call<JsonArray> SendVideoAsStaffToSpecificStudents(@Body JsonObject jsonObject);

    @POST("GetVideoContentRestriction")
    Call<JsonArray> GetVideoContentRestriction();

    @POST("GetStaffClassesforChat")
    Call<JsonArray> subjectList(@Body JsonObject jsonObject);

    @POST("GetStudentChatScreen")
    Call<JsonArray> studentChat(@Body JsonObject jsonObject);

    @POST("StudentAskQuestion")
    Call<JsonArray> studentQuestion(@Body JsonObject jsonObject);

    @POST("GetStaffChatScreen")
    Call<JsonArray> teacherChat(@Body JsonObject jsonObject);

    @POST("AnswerStudentQuestion")
    Call<JsonArray> answerStudentQuestion(@Body JsonObject jsonObject);

    @POST("getStaffDetails")
    Call<JsonObject> staffList(@Body JsonObject jsonObject);

    @POST("GetAllStaffs")
    Call<JsonArray> staffDetailsList(@Body JsonObject jsonObject);

    @GET("institute-fee-rate/student-fee-details-app")
    Call<FeeDetailsItems> getFeeDetails(@Query("ChildID") String childid, @Query("SchoolID") String schollid);

    @POST("institute-fee-rate/student-fee-payment-app")
    Call<JsonArray> feePayment(@Body JsonObject jsonObject);

    @POST("calculate-late-fee-details")
    Call<JsonObject> lateFee(@Body JsonObject jsonObject);

    @POST("online-fee-payment-logs")
    Call<JsonArray> feePaymentLogs(@Body JsonObject jsonObject);

    @POST("{id}/transfers")
    Call<JsonObject> transferToMultipleAccout(@Path("id") String paymentID, @Header("Content-Type") String content_type, @Header("Authorization") String secretKey, @Body JsonObject jsonobject);


    @POST("{id}/refund")
    Call<JsonObject> refundAmountToCustomer(@Path("id") String paymentID, @Header("Content-Type") String content_type, @Header("Authorization") String secretKey, @Body JsonObject jsonobject);

    @POST("{id}/capture")
    Call<JsonObject> changeCapturePayment(@Path("id") String paymentID, @Header("Content-Type") String content_type, @Header("Authorization") String secretKey, @Body JsonObject jsonobject);

    @POST("studentform/uploads-student-document")
    Call<JsonArray> submitStudentDocuments(@Body JsonObject jsonObject);


    @POST("ViewAllExamByStudent")
    Call<JsonObject> examenhancement(@Body JsonObject jsonObject);


    @POST("ViewAllKEByStudent")
    Call<JsonObject> knowledgeEnhanement(@Body JsonObject jsonObject);


    @POST("GetQuestionForQuiz")
    Call<JsonObject> questionforquiz(@Body JsonObject jsonObject);

    @POST("SubmitResponseForQuiz")
    Call<JsonObject> submitquiz(@Body JsonObject jsonObject);

    @POST("ViewAllSkillByStudent")
    Call<JsonObject> GetLSRWlist(@Body JsonObject jsonObject);

    @POST("GetAttachmentForSkill")
    Call<JsonObject> GetAttachmentForSkill(@Body JsonObject jsonObject);

    @POST("SubmitResponseForSkill")
    Call<JsonArray> SubmitResponseForSkill(@Body JsonObject jsonObject);

    @POST("ViewSubmission")
    Call<JsonObject> ViewSubmission(@Body JsonObject jsonObject);

    @POST("GetProgressCardLink")
    Call<JsonObject> GetProgressCardLink(@Body JsonObject jsonObject);

    @POST("GetStudentExamMark")
    Call<JsonObject> GetStudentExamMark(@Body JsonObject jsonObject);

    @POST("get-timetable")
    Call<JsonObject> GetTimetable(@Body JsonObject jsonObject);

    @POST("student-invoice-data")
    Call<JsonObject> GetInvoiceData(@Body JsonObject jsonObject);

    @POST("invoice-details")
    Call<JsonObject> GetInvoiceDetails(@Body JsonObject jsonObject);

}

