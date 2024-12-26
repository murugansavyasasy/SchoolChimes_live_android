package com.vs.schoolmessenger.aws;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.PreSignedUrl;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.CurrentDatePicking;
import com.vs.schoolmessenger.util.UploadCallback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;

public class AwsUploadingPreSigned {

    String isBucket = "";

    public void getPreSignedUrl(String isFilePathUrl, String instituteID, String isFileExtension, Activity activity, String isCountryId, Boolean isCommunication, Boolean isProfilrPage, UploadCallback uploadCallback) {


        String bucketPath = "";
        String currentDate = CurrentDatePicking.getCurrentDate();

        if (isCountryId.equals("4")) {
            if (isProfilrPage) {
                if (isCommunication) {
                    isBucket = AWSKeys.THAI_SCHOOL_PHOTOS;
                    bucketPath = instituteID;
                } else {
                    isBucket = AWSKeys.THAI_SCHOOL_DOCS;
                    bucketPath = instituteID + "/" + "profile";
                }
            } else {
                if (isCommunication) {
                    isBucket = AWSKeys.THAI_SCHOOL_CHIMES_COMMUNICATION;
                    bucketPath = currentDate + "/" + instituteID;

                } else {
                    isBucket = AWSKeys.THAI_SCHOOL_CHIMES_LMS;
                    bucketPath = instituteID + "/" + "lsrw";
                }
            }
        } else {
            if (isProfilrPage) {
                if (isCommunication) {
                    isBucket = AWSKeys.SCHOOL_PHOTOS;
                    bucketPath = instituteID;
                } else {
                    isBucket = AWSKeys.SCHOOL_DOCS;
                    bucketPath = instituteID + "/" + "profile";
                }
            } else {
                if (isCommunication) {
                    isBucket = AWSKeys.SCHOOL_CHIMES_COMMUNICATION;
                    bucketPath = currentDate + "/" + instituteID;
                } else {
                    isBucket = AWSKeys.SCHOOL_CHIMES_LMS;
                    bucketPath = instituteID + "/" + "lsrw";

                }
            }
        }

        Log.d("isBucket", isBucket);
        Log.d("isFileExtension", isFileExtension);
        File isFilePth = new File(isFilePathUrl);

        String fileExtension = getFileExtension(isFilePth.getName());
        MediaType mediaType = null;

        try {
            mediaType = getMediaType(fileExtension);
            System.out.println("MediaType: " + mediaType);
        } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
        }
//        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(activity);
        String baseURL = TeacherSchoolsApiClient.GET_PRESIGNED_BASE_URL;
        Log.d("baseURL", baseURL);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        String isFileName = getFileNameFromPath(isFilePathUrl);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<PreSignedUrl> call = apiService.getPreSignedUrl(isBucket, isFileName, bucketPath, String.valueOf(mediaType));

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

    public String getFileNameFromPath(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    private void isAwsUpload(String presignedUrl, String filePath, String isFileUploadUrl, UploadCallback uploadCallback) {

        byte[] imageData = getImageData(filePath); // Replace with the actual byte array of your image

        File isFilePth = new File(filePath);

        String fileExtension = getFileExtension(isFilePth.getName());

        MediaType mediaType = null;
        try {
            mediaType = getMediaType(fileExtension);
            System.out.println("MediaType: " + mediaType);
        } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
        }

        S3Uploader uploader = new S3Uploader();
        uploader.uploadImageToS3(presignedUrl, imageData, String.valueOf(mediaType), new S3Uploader.UploadCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d("S3Upload", message);
                uploadCallback.onUploadSuccess(message, isFileUploadUrl);
            }

            @Override
            public void onError(Exception error) {
                Log.e("S3Upload", "Error: " + error.getMessage(), error);
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

    private byte[] getImageData(String filePath) {
        File imageFile = new File(filePath);
        byte[] imageData = null;
        try {
            imageData = Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageData;
    }

    public MediaType getMediaType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return MediaType.parse("image/jpeg");
            case "png":
                return MediaType.parse("image/png");
            case "pdf":
                return MediaType.parse("application/pdf");
            case "mp3":
                return MediaType.parse("audio/mpeg");
            case "wav":
                return MediaType.parse("audio/wav");
            // Add more file types as needed
            default:
                throw new UnsupportedOperationException("Unsupported file type: " + fileExtension);
        }
    }
}
