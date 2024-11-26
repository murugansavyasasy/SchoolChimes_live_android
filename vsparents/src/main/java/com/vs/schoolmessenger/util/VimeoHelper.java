package com.vs.schoolmessenger.util;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vs.schoolmessenger.activity.ParentSubmitLSRW;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VimeoHelper {

    private static final String VIMEO_API_BASE_URL = "https://api.vimeo.com/videos/";
//    private static final String ACCESS_TOKEN = "8d74d8bf6b5742d39971cc7d3ffbb51a";  // Replace with your actual access token

    // Define the callback interface
    public interface VimeoDownloadCallback {
        void onDownloadUrlRetrieved(String quality, String downloadUrl);
        void onError(String errorMessage);
    }

    public static void getVimeoDownloadUrl(String videoId,String isVimeoToken, VimeoDownloadCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Create request
        Request request = new Request.Builder()
                .url(VIMEO_API_BASE_URL + videoId)
                .addHeader("Authorization", "Bearer " + isVimeoToken)
                .build();

        // Send request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Failed to retrieve video: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

                    Log.d("jsonResponse",String.valueOf(jsonResponse));
                    // Parse JSON response to extract download links
                    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    if (jsonObject.has("download")) {
                        for (JsonElement download : jsonObject.get("download").getAsJsonArray()) {
                            String quality = download.getAsJsonObject().get("quality").getAsString();
                            String downloadUrl = download.getAsJsonObject().get("link").getAsString();

                            Log.d("downloadUrl",String.valueOf(downloadUrl));

                            // Pass download URL back to MainActivity through callback
                            callback.onDownloadUrlRetrieved(quality, downloadUrl);
                        }
                    } else {
                        callback.onError("No downloadable URLs available for this video.");
                    }
                } else {
                    callback.onError("Failed to fetch video info: " + response.message());
                }
            }
        });
    }
}
