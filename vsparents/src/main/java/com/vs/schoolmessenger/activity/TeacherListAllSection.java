package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.activity.TeacherAddTempClass.ADD_TEMP_CLASS_STATUS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.list_staffStdSecs;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.strNoClassWarning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSectionListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckStdSecListener;
import com.vs.schoolmessenger.interfaces.TeacherOnSelectedStudentsListener;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherStudentsModel;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class TeacherListAllSection extends AppCompatActivity {//} implements TeacherOnCheckStdSecListener, TeacherOnSelectedStudentsListener {

    static int SELECTED_STD_SEC_POSITION = 0;
    static int SELECTED_STD_SEC_STUD_CODE = 1;
    TextView tvSelectAll;
    boolean bSelectAll = true;
    Button btnConfirm, btnAddNewClass;
    RecyclerView rvSectionList;
    ArrayList<TeacherSectionModel> listSelectedStandards = new ArrayList<>();
    private TeacherSectionListAdapter adapter;
    private final ArrayList<TeacherSectionModel> sectionList = new ArrayList<>();
    private int i_standards_count = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_list_all_section);


        ImageView ivBack = (ImageView) findViewById(R.id.standards_toolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvSelectAll = (TextView) findViewById(R.id.standards_tvSelectUnselect);
        tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bSelectAll) {
                    for (int i = 0; i < sectionList.size(); i++) {
                        if (!sectionList.get(i).isSelectStatus()) {
                            sectionList.get(i).setSelectStatus(true);
                        }
                    }
                } else {
                    for (int i = 0; i < sectionList.size(); i++) {
                        if (sectionList.get(i).isSelectStatus()) {
                            sectionList.get(i).setSelectStatus(false);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });

        btnConfirm = (Button) findViewById(R.id.standards_btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToResultActvity("OK");
            }
        });
        btnConfirm.setEnabled(false);

        btnAddNewClass = (Button) findViewById(R.id.standards_btnAddNewClass);
        btnAddNewClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inAddNew = new Intent(TeacherListAllSection.this, TeacherAddTempClass.class);
                startActivityForResult(inAddNew, ADD_TEMP_CLASS_STATUS);
            }
        });

        rvSectionList = (RecyclerView) findViewById(R.id.standards_rvStdSecList);
        adapter = new TeacherSectionListAdapter(TeacherListAllSection.this, new TeacherOnCheckStdSecListener() {
            @Override
            public void stdSec_addClass(TeacherSectionModel stdSec) {
                if (stdSec != null) {
                    listSelectedStandards.add(stdSec);
                    i_standards_count++;
                    enableDisableNext();

                    if (i_standards_count == sectionList.size()) {
                        bSelectAll = false;
                        tvSelectAll.setText(getString(R.string.teacher_txt_unselect));
                    } else {
                        bSelectAll = true;
                        tvSelectAll.setText(getString(R.string.teacher_txt_select));
                    }
                }
            }

            @Override
            public void stdSec_removeClass(TeacherSectionModel stdSec) {
                if (stdSec != null) {
                    listSelectedStandards.remove(stdSec);
                    Log.e("UnCheck", stdSec.getStdSecCode() + "-" + stdSec.getSubject());
                    i_standards_count--;
                    enableDisableNext();

                    {
                        bSelectAll = true;
                        tvSelectAll.setText(getString(R.string.teacher_txt_select));
                    }
                }
            }
        }, new TeacherOnSelectedStudentsListener() {
            @Override
            public void stdSec_selectedClass(TeacherSectionModel stdSec) {
                if (stdSec != null) {
                    SELECTED_STD_SEC_POSITION = listSelectedStandards.indexOf(stdSec);

                    Intent inStud = new Intent(TeacherListAllSection.this, TeacherStudentList.class);
                    inStud.putExtra("STD_SEC", stdSec);
                    startActivityForResult(inStud, SELECTED_STD_SEC_POSITION);
                }
            }
        }, sectionList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvSectionList.setHasFixedSize(true);
        rvSectionList.setLayoutManager(mLayoutManager);
        rvSectionList.setItemAnimator(new DefaultItemAnimator());
        rvSectionList.setAdapter(adapter);


        if (list_staffStdSecs.size() < 1) {
            showAlert(String.valueOf(R.string.Warning), strNoClassWarning);
        }

        sectionList.addAll(list_staffStdSecs);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        returnIntent.putExtra("STUDENTS", listSelectedStandards);
        setResult(SELECTED_STD_SEC_STUD_CODE, returnIntent);
        finish();
    }

    private void enableDisableNext() {
        btnConfirm.setEnabled(i_standards_count > 0);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showAlert(String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                TeacherListAllSection.this);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.teacher_ic_close);

        alertDialog.setNeutralButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_STD_SEC_POSITION) {

            String message = data.getStringExtra("MESSAGE");
            if (message.equals("OK")) {

                ArrayList<TeacherStudentsModel> selectedStudentsList = (ArrayList<TeacherStudentsModel>) data.getSerializableExtra("STUDENTS");
                listSelectedStandards.get(SELECTED_STD_SEC_POSITION).setStudentsList(selectedStudentsList);
                listSelectedStandards.get(SELECTED_STD_SEC_POSITION).setAllStudentsSelected(false);


                int totSize = selectedStudentsList.size();
                int seleStudentsCount = 0;
                for (int i = 0; i < totSize; i++) {
                    if (selectedStudentsList.get(i).isSelectStatus())
                        seleStudentsCount++;
                }

                listSelectedStandards.get(SELECTED_STD_SEC_POSITION).setSelectedStudentsCount(String.valueOf(seleStudentsCount));
                sectionList.get(SELECTED_STD_SEC_POSITION).setSelectedStudentsCount(String.valueOf(seleStudentsCount));


            }

            adapter.notifyDataSetChanged();
        } else if (requestCode == ADD_TEMP_CLASS_STATUS) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("OK")) {
                TeacherSectionModel newSection = (TeacherSectionModel) data.getSerializableExtra("STD_SEC");

                boolean bExist = false;
                for (int i = 0; i < sectionList.size(); i++) {
                    if (((sectionList.get(i).getStdSecCode()).equals(newSection.getStdSecCode()))
                            && ((sectionList.get(i).getSubjectCode()).equals(newSection.getSubjectCode()))) {
                        bExist = true;
                        break;
                    } else {
                    }
                }

                if (!bExist) {
                    list_staffStdSecs.add(newSection);
                    sectionList.add(newSection);
                    adapter.notifyDataSetChanged();
                }

            } else {
                showToast(getResources().getString(R.string.class_not_added));
            }
        }
    }
}
