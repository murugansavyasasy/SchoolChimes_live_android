package com.vs.schoolmessenger.util;

public interface UploadCallback {
        void onUploadSuccess(String response,String isFileUploaded);
        void onUploadError(String error);
}
