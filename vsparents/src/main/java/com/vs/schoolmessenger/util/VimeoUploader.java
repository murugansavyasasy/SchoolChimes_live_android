package com.vs.schoolmessenger.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class VimeoUploader {

    public interface UploadCompletionListener {
        void onUploadComplete(boolean success, String iframe, String link);
    }

    public static void uploadVideo(Activity activity, String title, String description, String authToken, String videoFilePath, UploadCompletionListener listener) {

        final ProgressDialog mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!activity.isFinishing())
            mProgressDialog.show();
        createVimeoUploadURL(title, description, authToken, videoFilePath, new VimeoUploadURLListener() {
            @Override
            public void onUploadURLGenerated(String uploadLink, String iframe, String link) {
                uploadVideoToVimeo(iframe, link, uploadLink, videoFilePath, authToken, new VimeoUploadListener() {
                    @Override
                    public void onUploadComplete(boolean success, String iframe, String link) {
                        if (listener != null) {
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            listener.onUploadComplete(success, iframe, link);
                        }
                    }


                    @Override
                    public void onFailure(String errorMessage) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("VimeoUploader", "Failed to create upload URL: " + errorMessage);
                if (listener != null) {
                    listener.onUploadComplete(false, "", "");
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }
            }
        });
    }

    private static void createVimeoUploadURL(final String isTitle, final String isDescription, final String authToken, final String videoFilePath, final VimeoUploadURLListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    File videoFile = new File(videoFilePath);
                    long fileSize = videoFile.length();

                    URL url = new URL("https://api.vimeo.com/me/videos");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "Bearer " + authToken);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/vnd.vimeo.*+json;version=3.4");
                    conn.setDoOutput(true);

                    JSONObject jsonParam = new JSONObject();
                    JSONObject uploadObj = new JSONObject();
                    uploadObj.put("approach", "tus");
                    uploadObj.put("size", fileSize);
                    jsonParam.put("upload", uploadObj);

                    JSONObject privacy = new JSONObject();
                    privacy.put("view", "unlisted");

                    if (TeacherUtil_Common.isVideoDownload) {
                        privacy.put("download", true);
                    } else {
                        privacy.put("download", false);
                    }

                    jsonParam.put("privacy", privacy);

                    JSONObject jsonshare = new JSONObject();
                    jsonshare.put("share", "false");
                    JSONObject jsonembed = new JSONObject();
                    jsonembed.put("buttons", jsonshare);
                    jsonParam.put("embed", jsonembed);


                    String title = "";
                    String Desc = "";
                    if (isTitle.isEmpty()) {
                        title = "videoTitle";
                    } else {
                        title = isTitle;
                    }
                    if (isDescription.isEmpty()) {
                        Desc = "videoDesc";
                    } else {
                        Desc = isDescription;
                    }

                    jsonParam.put("name", title); // Replace with actual video name
                    jsonParam.put("description", Desc); // Replace with actual video description

                    Log.d("Vimeo_Request", jsonParam.toString());

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(jsonParam.toString());
                    out.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONObject upload = jsonResponse.getJSONObject("upload");
                        JSONObject embed = jsonResponse.getJSONObject("embed");
                        String link = jsonResponse.getString("link");

                        String uploadLink = upload.getString("upload_link");
                        String iframe = embed.getString("html");

                        listener.onUploadURLGenerated(uploadLink, iframe, link);
                    } else {
                        listener.onFailure("HTTP error code: " + responseCode);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    listener.onFailure(e.getMessage());
                }
                return null;
            }
        }.execute();
    }

    private static void uploadVideoToVimeo(final String iframe, final String link, final String uploadLink, final String videoFilePath, final String authToken, final VimeoUploadListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                FileInputStream fileInputStream = null;
                try {
                    File videoFile = new File(videoFilePath);
                    long fileSize = videoFile.length();

                    fileInputStream = new FileInputStream(videoFile);

                    URL url = new URL(uploadLink);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PATCH");
                    conn.setRequestProperty("Authorization", "Bearer " + authToken);
                    conn.setRequestProperty("Content-Type", "application/offset+octet-stream");
                    conn.setRequestProperty("Upload-Offset", "0");
                    conn.setRequestProperty("Tus-Resumable", "1.0.0");
                    conn.setDoOutput(true);

                    byte[] buffer = new byte[5 * 1024 * 1024]; // Chunk size
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        conn.getOutputStream().write(buffer, 0, bytesRead);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        listener.onUploadComplete(true, iframe, link);
                    } else if (responseCode == HttpURLConnection.HTTP_PRECON_FAILED) {
                        String serverOffset = conn.getHeaderField("Upload-Offset");
                        if (serverOffset != null) {
                            conn.setRequestProperty("Upload-Offset", serverOffset);
                            conn.getOutputStream().write(buffer, 0, bytesRead);
                        } else {
                            listener.onFailure("Failed to upload chunk: Precondition Failed");
                        }
                    } else {
                        listener.onFailure("Failed to upload chunk, status code: " + responseCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onFailure(e.getMessage());
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    private interface VimeoUploadURLListener {
        void onUploadURLGenerated(String uploadLink, String iframe, String link);

        void onFailure(String errorMessage);
    }

    private interface VimeoUploadListener {
        void onUploadComplete(boolean success, String iframe, String link);

        void onFailure(String errorMessage);
    }
}