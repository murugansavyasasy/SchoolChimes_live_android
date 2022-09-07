//package com.vs.schoolmessenger.radiobuton;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.vs.schoolmessenger.R;
//import com.vs.schoolmessenger.activity.ExamListScreen;
//import com.vs.schoolmessenger.adapter.ExamListAdapter;
//import com.vs.schoolmessenger.interfaces.MessengerApiInterface;
//import com.vs.schoolmessenger.model.ExamList;
//import com.vs.schoolmessenger.model.RadioModel;
//import com.vs.schoolmessenger.rest.MessengerApiClient;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
//
//public class RadioActivity extends AppCompatActivity {
//
//    String child_ID, school_ID, Exam_ID;
//
//    RecyclerView Exam_list_recycle;
//    private List<ExamList> Exam_list = new ArrayList<>();
//
//    public RadioAdapter mAdapter;
//
//    RadioModel model;
//
//    ArrayList<RadioModel> radiolist=new ArrayList<>();
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        overridePendingTransition(R.anim.enter, R.anim.exit);
//
//        setContentView(R.layout.exam_list_recycle);
//
//
//        Exam_list_recycle = (RecyclerView) findViewById(R.id.Exam_list_recycle);
//        child_ID = getIntent().getExtras().getString("CHILD_ID");
//        school_ID = getIntent().getExtras().getString("SHOOL_ID");
//
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//
//        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
//        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("EXAMS");
//        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
//
//        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//
//
//
//        model=new RadioModel("1","mango","apple","banana");
//        radiolist.add(model);
//        model=new RadioModel("2","mango","apple","banana");
//        radiolist.add(model);
//        model=new RadioModel("3","mango","apple","banana");
//        radiolist.add(model);
//
//
//
//
//
//
//        mAdapter = new RadioAdapter(radiolist, RadioActivity.this);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        Exam_list_recycle.setLayoutManager(mLayoutManager);
//        Exam_list_recycle.setItemAnimator(new DefaultItemAnimator());
//        Exam_list_recycle.setAdapter(mAdapter);
//        Exam_list_recycle.getRecycledViewPool().setMaxRecycledViews(0, 80);
//
//
//    }
//
//
//
//    private void showRecords(String name) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RadioActivity.this);
//
//        //Setting Dialog Title
//        alertDialog.setTitle("Alert");
//
//        //Setting Dialog Message
//        alertDialog.setMessage(name);
//
//        //On Pressing Setting button
//        // On pressing cancel button
//        alertDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                finish();
////                locationtrack();
//
//            }
//        });
//
//        alertDialog.show();
//    }
//
//
//}
//
