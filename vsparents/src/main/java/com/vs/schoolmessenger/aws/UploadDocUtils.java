package com.vs.schoolmessenger.aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import java.io.File;
import java.net.URL;

public class UploadDocUtils {

    /**
     * Method to generate a presignedurl for the image
     * @param applicationContext context
     * @param path image path
     * @return presignedurl
     */
    public static String generates3ShareUrl(Context applicationContext, String path, String fileNameDateTime,String instituteID,Boolean ifProfile) {

        File f = new File(path);
        AmazonS3 s3client = UploadDocAmazonUtil.getS3Client(applicationContext);

        ResponseHeaderOverrides overrideHeader = new ResponseHeaderOverrides();

        String mediaUrl = f.getName();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = null;

        if(ifProfile) {

            generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(AWSUploadDocKeys.BUCKET_NAME, "student_photos"  + "/" + instituteID +  "/" + fileNameDateTime+"_"+mediaUrl);
        }
        else {

            generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(AWSUploadDocKeys.BUCKET_NAME_SCHOOL_DOCS, "student-documents"  + "/" + instituteID +  "/" + fileNameDateTime+"_"+mediaUrl);
        }


        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
        generatePresignedUrlRequest.setResponseHeaders(overrideHeader);

        URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
        String fileUrl = url.toString().substring(0, url.toString().indexOf('?'));
        Log.e("s", fileUrl);
        return fileUrl;
    }
}
