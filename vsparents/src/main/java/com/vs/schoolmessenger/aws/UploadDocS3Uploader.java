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

public class UploadDocS3Uploader {
    private static final String TAG = "S3Uploader";

    private Context context;
    private TransferUtility transferUtility;
    public UploadDocS3UploadInterface s3UploadInterface;

    public UploadDocS3Uploader(Context context) {
        this.context = context;
        transferUtility = UploadDocAmazonUtil.getTransferUtility(context);

    }

    public void initUpload(String filePath,String contenttype,String fileNameDateTime,String instituteID,Boolean ifProfile) {
        File file = new File(filePath);
        ObjectMetadata myObjectMetadata = new ObjectMetadata();
        //myObjectMetadata.setContentType("image/png");
        myObjectMetadata.setContentType(contenttype);
        String mediaUrl = file.getName();
        Log.d("mediaUrl",mediaUrl);

        TransferObserver observer = null;

        if(ifProfile) {
            observer = transferUtility.upload(AWSUploadDocKeys.BUCKET_NAME, "student_photos" + "/" + instituteID + "/" + fileNameDateTime + "_" + mediaUrl,
                    file, CannedAccessControlList.PublicRead);
        }
        else {
            observer = transferUtility.upload(AWSUploadDocKeys.BUCKET_NAME_SCHOOL_DOCS, "student-documents" + "/" + instituteID + "/" + fileNameDateTime + "_" + mediaUrl,
                    file, CannedAccessControlList.PublicRead);
        }
        observer.setTransferListener(new UploadDocUploadListener());
    }


    private class UploadDocUploadListener implements TransferListener {

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            s3UploadInterface.onUploadError(e.toString());
            s3UploadInterface.onUploadError("Error");
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);
            if (newState == TransferState.COMPLETED) {
                s3UploadInterface.onUploadSuccess("Success");
            }
        }
    }

    public void setOns3UploadDone(UploadDocS3UploadInterface s3UploadInterface) {
        this.s3UploadInterface = s3UploadInterface;
    }

    public interface UploadDocS3UploadInterface {
        void onUploadSuccess(String response);

        void onUploadError(String response);

    }
}
