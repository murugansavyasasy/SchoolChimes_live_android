package com.vs.schoolmessenger.util;

import java.io.File;

public class VideoUtils {
    public static String getVideoSize(String videoPath) {
        File videoFile = new File(videoPath);

        if (videoFile.exists()) {
            double fileSizeInBytes = videoFile.length();
            double fileSizeInMB = fileSizeInBytes / (1024 * 1024); // Convert bytes to MB

            return fileSizeInMB + " MB"; // Return the size as a string
        } else {
            return "File not found";
        }
    }
}