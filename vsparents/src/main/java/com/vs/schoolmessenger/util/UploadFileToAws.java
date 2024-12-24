package com.vs.schoolmessenger.util;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadFileToAws {

    // OkHttpClient instance for HTTP requests
    private final OkHttpClient client;

    public UploadFileToAws() {
        this.client = new OkHttpClient();
    }

    /**
     * Uploads an image to AWS S3 using a pre-signed URL.
     *
     * @param presignedUrl The pre-signed URL for the upload.
     * @param imageFile    The image file to upload.
     * @return The response message from the server.
     * @throws IOException If any error occurs during the upload.
     */
    public String uploadImage(String presignedUrl, File imageFile) throws Exception {
        // Validate inputs
        if (presignedUrl == null || presignedUrl.isEmpty()) {
            throw new IllegalArgumentException("Presigned URL cannot be null or empty");
        }
        if (imageFile == null || !imageFile.exists()) {
            throw new IllegalArgumentException("Image file does not exist");
        }

        String fileExtension = getFileExtension(imageFile.getName());
        MediaType mediaType;

        switch (fileExtension) {
            case "jpg":
            case "jpeg":
                mediaType = MediaType.parse("image/jpeg");
                break;
            case "png":
                mediaType = MediaType.parse("image/png");
                break;
            case "pdf":
                mediaType = MediaType.parse("application/pdf");
                break;
            case "mp3":
                mediaType = MediaType.parse("audio/mpeg");
                break;
            case "wav":
                mediaType = MediaType.parse("audio/wav");
                break;
            // Add more file types as needed
            default:
                throw new UnsupportedOperationException("Unsupported file type: " + fileExtension);
        }


        // Create a request body with the file and content type
        RequestBody requestBody = RequestBody.create(imageFile, mediaType);

        // Build the PUT request
        Request request = new Request.Builder()
                .url(presignedUrl)
                .put(requestBody)
                .build();

        // Execute the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "Upload successful: " + response.code();
            } else {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("Upload failed: " + response.code() + " - " + response.message() + " | " + errorBody);
            }
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1) {
            return fileName.substring(lastIndexOfDot + 1).toLowerCase();
        }
        return ""; // Return empty string if no extension found
    }
}
