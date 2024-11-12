package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_ONLINECLASS;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.model.OnlineClassModel;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class OnlineClassPopup extends AppCompatActivity {

    OnlineClassModel textMsgModel;
    TextView lblURL, tvMsgContent, tvdescription;
    String isNewVersion;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_class_popup);

        textMsgModel = (OnlineClassModel) getIntent().getSerializableExtra("TEXT_ITEM");

        ImageView ivBack = (ImageView) findViewById(R.id.textPopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(OnlineClassPopup.this);


        TextView tvBarTitle = (TextView) findViewById(R.id.textPopup_ToolBarTvTitle);
        tvBarTitle.setText(textMsgModel.getMeetingdate());

        tvMsgContent = (TextView) findViewById(R.id.textPopup_tvTitle);
        tvdescription = (TextView) findViewById(R.id.textPopup_tvdescrip);
        lblURL = (TextView) findViewById(R.id.lblURL);
        registerForContextMenu(tvdescription);

        tvdescription.setText(textMsgModel.getDescription());
        tvMsgContent.setText(textMsgModel.getTopic());
        lblURL.setText(textMsgModel.getUrl());


        if (textMsgModel.getDescription().equals("")) {
            tvdescription.setVisibility(View.GONE);
        } else {
            tvdescription.setVisibility(View.VISIBLE);
        }

        if (textMsgModel.getIs_app_viewed().equals("0")) {
            textMsgModel.setIs_app_viewed("1");

            ChangeMsgReadStatus.changeReadStatus(OnlineClassPopup.this, textMsgModel.getMessage_id(), MSG_TYPE_ONLINECLASS, textMsgModel.getMeetingdate(), isNewVersion, false, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {
                    textMsgModel.setIs_app_viewed("1");

                }
            });
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(), 0, "Copy");
        TextView textView = (TextView) v;
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        manager.setPrimaryClip(clipData);
    }

    @Override
    public void onBackPressed() {
        TeacherUtil_SharedPreference.putOnBackPressed(OnlineClassPopup.this, "1");
        finish();
    }


    private void showToast(String msg) {
        Toast.makeText(OnlineClassPopup.this, msg, Toast.LENGTH_SHORT).show();
    }


}
