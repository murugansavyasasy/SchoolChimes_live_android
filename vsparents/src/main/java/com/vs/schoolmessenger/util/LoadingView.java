package com.vs.schoolmessenger.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import androidx.core.content.ContextCompat;

import com.vs.schoolmessenger.R;


public class LoadingView {
//    public static LoadingView mCShowProgress;
    public static Dialog mDialog;

//    public LoadingView() {
//    }
//
//    public static LoadingView getInstance() {
//        if (mCShowProgress== null) {
//            mCShowProgress= new LoadingView();
//        }
//        return mCShowProgress;
//    }

    public static void  showProgress(Context mContext) {
        mDialog = null;
        mDialog= new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_loading);
        mDialog.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    public static void hideProgress() {
        if (mDialog!= null) {
            mDialog.dismiss();
            mDialog= null;
        }
    }
}