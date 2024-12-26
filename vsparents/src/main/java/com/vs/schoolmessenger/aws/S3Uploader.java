package com.vs.schoolmessenger.aws;

import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class S3Uploader {


    public interface UploadCallback {
        void onSuccess(String message);
        void onError(Exception error);
    }

    public void uploadImageToS3(String presignedUrl, byte[] imageData, String contentType, UploadCallback callback) {
        new Thread(() -> {
            try {
                Log.d("contentTypeeee",contentType);
                URL url = new URL(presignedUrl);

                // Open a connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", contentType);

                // Write the image data to the output stream
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(imageData);
                outputStream.close();

                // Check the response code
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    callback.onSuccess("Image uploaded successfully!");
                } else {
                    callback.onError(new Exception("Upload failed with status code: " + responseCode));
                }

                connection.disconnect();

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

}
