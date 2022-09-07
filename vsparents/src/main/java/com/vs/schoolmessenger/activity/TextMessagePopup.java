package com.vs.schoolmessenger.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_SMS;

public class TextMessagePopup extends AppCompatActivity {

    TeacherMessageModel textMsgModel;

    TextView tvTitle, tvTime, tvStatus, tvMsgContent,tvdescription;

    String isNewVersion;
    Boolean is_Archive;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_message_popup);

        textMsgModel = (TeacherMessageModel) getIntent().getSerializableExtra("TEXT_ITEM");

        ImageView ivBack = (ImageView) findViewById(R.id.textPopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressed(TextMessagePopup.this,"1");
                onBackPressed();
            }
        });
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TextMessagePopup.this);
        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);


        TextView tvBarTitle = (TextView) findViewById(R.id.textPopup_ToolBarTvTitle);
        tvBarTitle.setText(textMsgModel.getMsgDate());

        tvTitle = (TextView) findViewById(R.id.textPopup_tvTitle);
        tvTime = (TextView) findViewById(R.id.textPopup_tvTime);
        tvStatus = (TextView) findViewById(R.id.textPopup_tvNew);
        tvMsgContent = (TextView) findViewById(R.id.textPopup_tvMsg);


        tvdescription= (TextView) findViewById(R.id.textPopup_tvdescrip);
        registerForContextMenu(tvdescription);

        tvTitle.setText(textMsgModel.getMsgTitle());
        tvTime.setText(textMsgModel.getMsgTime());
        tvdescription.setText(textMsgModel.getMsgdescription());
        tvMsgContent.setText(textMsgModel.getMsgContent());
        if (textMsgModel.getMsgReadStatus().equals("0"))
            tvStatus.setVisibility(View.VISIBLE);
        else tvStatus.setVisibility(View.GONE);

        if(textMsgModel.getMsgdescription().equals("")){
            tvdescription.setVisibility(View.GONE);
        }
        else{
            tvdescription.setVisibility(View.VISIBLE);
        }

        if (textMsgModel.getMsgReadStatus().equals("0")) {
            textMsgModel.setMsgReadStatus("1");
            tvStatus.setVisibility(View.GONE);

            ChangeMsgReadStatus.changeReadStatus(TextMessagePopup.this, textMsgModel.getMsgID(), MSG_TYPE_SMS, textMsgModel.getMsgDate(),isNewVersion,is_Archive, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {
                    textMsgModel.setMsgReadStatus("1");
                    if (textMsgModel.getMsgReadStatus().equals("0"))
                        tvStatus.setVisibility(View.VISIBLE);
                    else tvStatus.setVisibility(View.GONE);
                }
            });
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(),0, "Copy");
//        menu.setHeaderTitle("Copy text"); //setting header title for menu
        TextView textView = (TextView) v;
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        manager.setPrimaryClip(clipData);
    }

    @Override
    public void onBackPressed() {
        TeacherUtil_SharedPreference.putOnBackPressed(TextMessagePopup.this,"1");
        finish();
    }



    private void showToast(String msg) {
        Toast.makeText(TextMessagePopup.this, msg, Toast.LENGTH_SHORT).show();
    }


}
