package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_IMAGE;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jsibbold.zoomage.ZoomageView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ImageCircularPopUpParent extends AppCompatActivity {

    MessageModel imgMsgModel;
    TextView tvTitle, tvTime, tvStatus, tvdescription;
    ImageView ivImage;
    String isNewVersion;
    Boolean is_Archive;
    private ZoomageView demoView;

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_circular_pop_up);
        imgMsgModel = (MessageModel) getIntent().getSerializableExtra("IMAGE_ITEM");
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(ImageCircularPopUpParent.this);
        is_Archive = imgMsgModel.getIs_Archive();
        ImageView ivBack = (ImageView) findViewById(R.id.imagePopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressedImages(ImageCircularPopUpParent.this, "1");
                onBackPressed();
            }
        });

        TextView tvBarTitle = (TextView) findViewById(R.id.imagePopup_ToolBarTvTitle);
        tvBarTitle.setText(imgMsgModel.getMsgDate());

        tvTitle = (TextView) findViewById(R.id.imagePopup_tvTitle);
        tvTime = (TextView) findViewById(R.id.imagePopup_tvTime);
        tvStatus = (TextView) findViewById(R.id.imagePopup_tvNew);
        ivImage = (ImageView) findViewById(R.id.imagePopup_ivImage);
        demoView = (ZoomageView) findViewById(R.id.demoView);
        tvdescription = (TextView) findViewById(R.id.imagePopup_tvdescrip);

        tvTitle.setText(imgMsgModel.getMsgTitle());
        tvTime.setText(imgMsgModel.getMsgTime());
        tvdescription.setText(imgMsgModel.getMsgdescription());

        if (imgMsgModel.getMsgReadStatus().equals("0"))
            tvStatus.setVisibility(View.VISIBLE);
        else tvStatus.setVisibility(View.GONE);
        if (imgMsgModel.getMsgdescription().equals("")) {
            tvdescription.setVisibility(View.GONE);
        } else {
            tvdescription.setVisibility(View.VISIBLE);
        }
        loadImage();
    }

    @Override
    public void onBackPressed() {
        TeacherUtil_SharedPreference.putOnBackPressedImages(ImageCircularPopUpParent.this, "1");
        finish();
    }

    private void loadImage() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();
        Glide.with(ImageCircularPopUpParent.this)
                .load(imgMsgModel.getMsgContent())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        onBackPressed();
                        showToast(getResources().getString(R.string.check_internet));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        mProgressDialog.dismiss();

                        if (imgMsgModel.getMsgReadStatus().equals("0"))
                            ChangeMsgReadStatus.changeReadStatus(ImageCircularPopUpParent.this, imgMsgModel.getMsgID(), MSG_TYPE_IMAGE, imgMsgModel.getMsgDate(), isNewVersion, is_Archive, new OnRefreshListener() {
                                @Override
                                public void onRefreshItem() {
                                    imgMsgModel.setMsgReadStatus("1");
                                    if (imgMsgModel.getMsgReadStatus().equals("0"))
                                        tvStatus.setVisibility(View.VISIBLE);
                                    else tvStatus.setVisibility(View.GONE);
                                }
                            });

                        return false;
                    }
                })
                .into(demoView);

    }

    private void showToast(String msg) {
        Toast.makeText(ImageCircularPopUpParent.this, msg, Toast.LENGTH_SHORT).show();
    }
}
