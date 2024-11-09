package com.vs.schoolmessenger.util;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

public class AudioUtils {
    public static long getWavFileDurationFromUrl(String url) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        long duration = -1;

        try {
            retriever.setDataSource(url, new HashMap<>());
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Long.parseLong(time);
        } catch (Exception e) {
            Log.e("AudioUtils", "Error retrieving duration", e);
        } finally {
            retriever.release();
        }

        return duration;
    }
    public static String formatDuration(long duration) {
        if (duration < 0) return "00:00"; // Handle error case

        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}