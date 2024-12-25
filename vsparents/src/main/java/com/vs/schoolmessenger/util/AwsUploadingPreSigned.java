package com.vs.schoolmessenger.util;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.AttendanceSectionList;
import com.vs.schoolmessenger.aws.AWSKeys;
import com.vs.schoolmessenger.aws.AWSUploadDocKeys;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.PreSignedUrl;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;

public class AwsUploadingPreSigned {

    UploadFileToAws isUploadFileToAws = new UploadFileToAws();
    String isBucket = "";

    public void getPreSignedUrl(String isFilePathUrl, String bucketPath, String isFileExtension, Activity activity, String isCountryId, Boolean isCommunication, UploadCallback uploadCallback) {

        if (isCountryId.equals("")) {
            if (isCommunication) {
                isBucket = AWSUploadDocKeys.BUCKET_NAME;
            } else {
                isBucket = AWSUploadDocKeys.BUCKET_NAME_SCHOOL_DOCS;
            }
        } else {
            if (isCountryId.equals("1")) {
                if (isCommunication) {
                    isBucket = AWSKeys.BUCKET_NAME;
                } else {
                    isBucket = AWSKeys.BUCKET_NAME_SCHOOL_DOCS;
                }
            } else {
                if (isCommunication) {
                    isBucket = AWSKeys.BUCKET_NAME_BANGKOK;
                } else {
                    isBucket = AWSKeys.BUCKET_NAME_SCHOOL_DOCS;
                }
            }
        }

        Log.d("isBucket",isBucket);

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(activity);
        Log.d("baseURL",baseURL);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<PreSignedUrl> call = apiService.getPreSignedUrl(isBucket, isFilePathUrl, bucketPath, isFileExtension);

        call.enqueue(new Callback<PreSignedUrl>() {
            @Override
            public void onResponse(Call<PreSignedUrl> call, retrofit2.Response<PreSignedUrl> response) {
                Log.d("attendance:code-res", response.code() + " - " + response);

                if (response.isSuccessful() && response.body() != null) {
                    PreSignedUrl preSignedUrlResponse = response.body();
                    Log.d("PreSignedData", new Gson().toJson(preSignedUrlResponse));

                    if (preSignedUrlResponse.getStatus() == 1) {
                        Log.d("isSuccessFullUpload", "isSuccessFullUpload");
                        String presignedUrl = preSignedUrlResponse.getData().getPresignedUrl();
                        String isFileUrl = preSignedUrlResponse.getData().getFileUrl();
                        Log.d("presignedUrl", presignedUrl);

                        // Upload the file and get the upload response
                        isAwsUpload(presignedUrl, isFilePathUrl, isFileUrl, uploadCallback);
                    } else {
                        Log.d("isSuccessFullUpload", "isErrorUpload: " + preSignedUrlResponse.getMessage());
                        uploadCallback.onUploadError(preSignedUrlResponse.getMessage());
                    }
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    String errorMessage = response.message(); // Get the error message from the response
                    Log.e("Response Error", errorMessage != null ? errorMessage : "Unknown error occurred");
                    uploadCallback.onUploadError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PreSignedUrl> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(activity, activity.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                uploadCallback.onUploadError(t.getMessage());
            }
        });
    }

    private void isAwsUpload(String presignedUrl, String filePath, String isFileUploadUrl, UploadCallback uploadCallback) {
        final File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            Log.e("Upload Error", "File does not exist: " + filePath);
            if (uploadCallback != null) {
                uploadCallback.onUploadError("File does not exist");
            }
            return; // Exit early if the file doesn't exist
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Perform the upload
                    String response = isUploadFileToAws.uploadImage(presignedUrl, file);

                    // Check response for success or failure
                    if (response != null && response.startsWith("Upload successful")) {
                        // Notify success
                        if (uploadCallback != null) {
                            uploadCallback.onUploadSuccess(response, isFileUploadUrl);
                        }
                    } else {
                        // Notify failure
                        if (uploadCallback != null) {
                            uploadCallback.onUploadError(response != null ? response : "Unknown upload error");
                        }
                    }
                } catch (IOException e) {
                    Log.e("Upload Error", "Failed to upload: " + e.getMessage(), e); // Log stack trace
                    if (uploadCallback != null) {
                        uploadCallback.onUploadError("Failed to upload: " + e.getMessage());
                    }
                } catch (Exception e) {
                    Log.e("Upload Error", "Unexpected error: " + e.toString(), e); // Log stack trace
                    if (uploadCallback != null) {
                        uploadCallback.onUploadError("Unexpected error: " + e.toString());
                    }
                }
            }
        }).start();
    }
}
