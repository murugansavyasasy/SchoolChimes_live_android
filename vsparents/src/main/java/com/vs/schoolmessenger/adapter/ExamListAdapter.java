package com.vs.schoolmessenger.adapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ExamEnhancementQuestions;
import com.vs.schoolmessenger.activity.MarksListScreen;
import com.vs.schoolmessenger.activity.ViewExamMarks;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Element;
import com.vs.schoolmessenger.model.ExamList;
import com.vs.schoolmessenger.model.QuestionForQuiz;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
public class ExamListAdapter extends
        RecyclerView.Adapter<ExamListAdapter.MyViewHolder> {
    private List<ExamList> lib_list;
    Context context;
    String child_id,schoolid;

    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Exam_Name;
        public RelativeLayout rytExams;
        Button btnMarks,btnProgress;

        public MyViewHolder(View view) {
            super(view);
            Exam_Name = (TextView) view.findViewById(R.id.Exam_Name);
            rytExams = (RelativeLayout) view.findViewById(R.id.rytExams);
            btnMarks = view.findViewById(R.id.btnmarks);
            btnProgress = view.findViewById(R.id.btnProgress);

        }
    }
    public ExamListAdapter(List<ExamList> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }
    @Override
    public ExamListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exams, parent, false);
        return new ExamListAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final ExamListAdapter.MyViewHolder holder, final int
            position) {
        final ExamList exam = lib_list.get(position);
        child_id = Util_SharedPreference.getChildIdFromSP(context);
        schoolid = Util_SharedPreference.getSchoolIdFromSP(context);
        holder.Exam_Name.setText(exam.getName());

        holder.btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressApi(exam);
            }
        });
        holder.btnMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ViewExamMarks.class);
                i.putExtra("examid",exam.getId());
                context.startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return lib_list.size();
    }

    public void updateList(List<ExamList> temp) {
        lib_list = temp;
        notifyDataSetChanged();
    }
    private void progressApi(ExamList exam) {

            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(context);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService =
                TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolID",schoolid );
        jsonObject.addProperty("ChildID", child_id);
        jsonObject.addProperty("ExamID",exam.getId());

        Log.d("jsonObject", jsonObject.toString());
        Call<JsonObject> call = apiService.GetProgressCardLink(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject>
                    response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            String status = jsonObject.getString("Status");
                            String message = jsonObject.getString("Message");
                            if (status.equals("1")) {
                                String progresslink = jsonObject.getString("Data");
                                if(!progresslink.equals("")){
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(progresslink));
                                    context.startActivity(browserIntent);
                                }
                                else{
                                    showAlertfinish(message);
                                }

                            } else {
                                showAlertfinish(message);
                            }
                        } catch (Exception e) {
                            Log.e("TextMsg:Exception", e.getMessage());
                        }
                    } else {
                        Toast.makeText(context, "Server Response Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(context, "Server Connection Failed",

                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showAlertfinish(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }
}