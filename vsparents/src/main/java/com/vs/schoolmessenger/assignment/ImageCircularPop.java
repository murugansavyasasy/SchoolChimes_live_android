package com.vs.schoolmessenger.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jsibbold.zoomage.ZoomageView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_IMAGE;

public class ImageCircularPop extends AppCompatActivity {

//    TeacherMessageModel imgMsgModel;
    TextView tvTitle, tvTime, tvStatus, tvdescription;
    String imgitem,description;
    ImageView ivImage;
    private ZoomageView demoView;
    RelativeLayout rlheader,rlbackground;



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_circular_pop_up);

        imgitem = getIntent().getExtras().getString("IMAGE_ITEM","");
        description = getIntent().getExtras().getString("DESC","");

        ImageView ivBack = (ImageView) findViewById(R.id.imagePopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvBarTitle = (TextView) findViewById(R.id.imagePopup_ToolBarTvTitle);
//        tvBarTitle.setText(imgMsgModel.getMsgDate());

        tvTitle = (TextView) findViewById(R.id.imagePopup_tvTitle);
        tvTime = (TextView) findViewById(R.id.imagePopup_tvTime);
        tvStatus = (TextView) findViewById(R.id.imagePopup_tvNew);
        ivImage = (ImageView) findViewById(R.id.imagePopup_ivImage);
        demoView = (ZoomageView) findViewById(R.id.demoView);
        tvdescription = (TextView) findViewById(R.id.imagePopup_tvdescrip);
        rlheader =  findViewById(R.id.relativeLayoutHeader);
        rlbackground =  findViewById(R.id.rlbackground);
        rlbackground.setBackgroundColor(getResources().getColor(R.color.clr_black));
        rlheader.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvdescription.setText(description);

//        if (imgMsgModel.getMsgReadStatus().equals("0"))
//            tvStatus.setVisibility(View.VISIBLE);
         tvStatus.setVisibility(View.GONE);
        if (description.equals("")) {
            tvdescription.setVisibility(View.GONE);
        } else {
            tvdescription.setVisibility(View.VISIBLE);
        }
        loadImage();


    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void loadImage() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        Glide.with(ImageCircularPop.this)
                .load(imgitem)
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
                        return false;
                    }
                })
                .into(demoView);
    }

    private void showToast(String msg) {
        Toast.makeText(ImageCircularPop.this, msg, Toast.LENGTH_SHORT).show();
    }
}
