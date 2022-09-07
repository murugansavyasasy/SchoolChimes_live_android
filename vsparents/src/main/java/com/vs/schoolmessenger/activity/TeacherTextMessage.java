package com.vs.schoolmessenger.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;


public class TeacherTextMessage extends AppCompatActivity {
    Button Nextbtn;
    EditText etMessage;
    TextView tvcount;
    String strmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_text_message);

        Nextbtn = (Button) findViewById(R.id.text_btnmsg);
        etMessage = (EditText) findViewById(R.id.txtmessage);
        tvcount = (TextView) findViewById(R.id.msgcount);
        etMessage.addTextChangedListener(mTextEditorWatcher);
        ImageView ivBack = (ImageView) findViewById(R.id.textPopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        Nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();

                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherTextMessage.this).equals(LOGIN_TYPE_TEACHER)) {
                    Intent inStaff = new Intent(TeacherTextMessage.this, TeacherStaffTextCircular.class);
                    inStaff.putExtra("MESSAGE", strmessage);
                    startActivityForResult(inStaff, 1);
                } else {
                    Intent inMgt = new Intent(TeacherTextMessage.this, TeacherMgtTextCircular.class);
                    inMgt.putExtra("MESSAGE", strmessage);
                    startActivityForResult(inMgt, 1);
                }
            }
        });


    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tvcount.setText("" + (460 - (s.length())));
        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
//                showToast("Success");
                finish();
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void enableSubmitIfReady() {

        boolean isReady = etMessage.getText().toString().length() > 0;
        Nextbtn.setEnabled(isReady);
    }

    public void validation() {
        strmessage = etMessage.getText().toString().trim();
        Log.d("Message", strmessage);
    }
}
