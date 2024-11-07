package com.vs.schoolmessenger.aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class S3Uploader {
    private static final String TAG = "S3Uploader";

    private Context context;
    private TransferUtility transferUtility;
    public S3UploadInterface s3UploadInterface;

    public S3Uploader(Context context) {
        this.context = context;
        transferUtility = AmazonUtil.getTransferUtility(context);
    }

    public void initUpload(String filePath, String contenttype, String fileNameDateTime, String instituteId, String CountryID, Boolean isCommunication) {
        File file = new File(filePath);
        ObjectMetadata myObjectMetadata = new ObjectMetadata();
        //myObjectMetadata.setContentType("image/png");
        myObjectMetadata.setContentType(contenttype);
        String mediaUrl = file.getName();
        Log.d("mediaUrl", mediaUrl);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        TransferObserver observer = null;
        if (CountryID.equals("1")) {
            if (isCommunication) {
                observer = transferUtility.upload(AWSKeys.BUCKET_NAME, "communication" + "/" + currentDate + "/" + fileNameDateTime + "_" + mediaUrl,
                        file, CannedAccessControlList.PublicRead);

            } else {
                observer = transferUtility.upload(AWSKeys.BUCKET_NAME_SCHOOL_DOCS, "lms" + "/" + instituteId + "/" + fileNameDateTime + "_" + mediaUrl,
                        file, CannedAccessControlList.PublicRead);

            }

        } else {
            if (isCommunication) {
                observer = transferUtility.upload(AWSKeys.BUCKET_NAME_BANGKOK, "communication" + "/" + currentDate + "/" + fileNameDateTime + "_" + mediaUrl,
                        file, CannedAccessControlList.PublicRead);

            } else {
                observer = transferUtility.upload(AWSKeys.BUCKET_NAME_SCHOOL_DOCS, "lms" + "/" + instituteId + "/" + fileNameDateTime + "_" + mediaUrl,
                        file, CannedAccessControlList.PublicRead);
            }

        }

//         observer = transferUtility.upload(AWSKeys.BUCKET_NAME, fileNameDateTime+"_"+mediaUrl,
//                file, CannedAccessControlList.PublicRead);

        observer.setTransferListener(new UploadListener());
    }


    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            Log.d("Errror_Ex", String.valueOf(e));

            s3UploadInterface.onUploadError(e.toString());
            s3UploadInterface.onUploadError("Error");
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
            Log.d("bytesTotal", String.valueOf(bytesTotal));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);

            Log.d("newstae", String.valueOf(newState));
            if (newState == TransferState.COMPLETED) {
                s3UploadInterface.onUploadSuccess("Success");
            }
        }
    }

    public void setOns3UploadDone(S3UploadInterface s3UploadInterface) {
        this.s3UploadInterface = s3UploadInterface;
    }

    public interface S3UploadInterface {
        void onUploadSuccess(String response);

        void onUploadError(String response);

    }
}
