package com.vs.schoolmessenger.aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class S3Utils {

    /**
     * Method to generate a presignedurl for the image
     *
     * @param applicationContext context
     * @param path               image path
     * @return presignedurl
     */
    public static String generates3ShareUrl(Context applicationContext, String path, String fileNameDateTime, String instituteId, String CountryID, Boolean isCommunication) {

        File f = new File(path);
        AmazonS3 s3client = AmazonUtil.getS3Client(applicationContext);

        ResponseHeaderOverrides overrideHeader = new ResponseHeaderOverrides();
        String mediaUrl = f.getName();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = null;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (CountryID.equals("1")) {
            if (isCommunication) {

                generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(AWSKeys.BUCKET_NAME, "communication" + "/" + currentDate + "/" + fileNameDateTime + "_" + mediaUrl);

            } else {

                generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(AWSKeys.BUCKET_NAME_SCHOOL_DOCS, "lms" + "/" + instituteId + "/" + fileNameDateTime + "_" + mediaUrl);

            }
        } else {
            if (isCommunication) {

                generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(AWSKeys.BUCKET_NAME_BANGKOK, "communication" + "/" + currentDate + "/" + fileNameDateTime + "_" + mediaUrl);

            } else {

                generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(AWSKeys.BUCKET_NAME_SCHOOL_DOCS, "lms" + "/" + instituteId + "/" + fileNameDateTime + "_" + mediaUrl);
            }
        }

//        GeneratePresignedUrlRequest generatePresignedUrlRequest =
//                new GeneratePresignedUrlRequest(AWSKeys.BUCKET_NAME, fileNameDateTime+"_"+mediaUrl);

        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
        generatePresignedUrlRequest.setResponseHeaders(overrideHeader);

        URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
        String fileUrl = url.toString().substring(0, url.toString().indexOf('?'));
        Log.e("aws_url", fileUrl);
        return fileUrl;
    }
}
