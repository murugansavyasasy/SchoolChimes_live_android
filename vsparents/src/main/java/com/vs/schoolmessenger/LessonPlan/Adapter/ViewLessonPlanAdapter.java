package com.vs.schoolmessenger.LessonPlan.Adapter;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.EditDataList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.LessonPlan.Model.DataArrayItem;
import com.vs.schoolmessenger.LessonPlan.Model.EditDataItem;
import com.vs.schoolmessenger.LessonPlan.Model.EditLessonModel;
import com.vs.schoolmessenger.LessonPlan.Model.Response;
import com.vs.schoolmessenger.LessonPlan.Model.ViewDataItem;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.DatesListListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DatesModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ViewLessonPlanAdapter extends RecyclerView.Adapter<ViewLessonPlanAdapter.MyViewHolder> {

    private List<ViewDataItem> dateList;
    Context context;

    private PopupWindow PopupWindow;

    RelativeLayout rytParent;

    int SelectedPosition = 0;

    public List<EditDataItem> afterEditedList = new ArrayList<EditDataItem>();

    String LastIconStatus = "";


    @Override
    public ViewLessonPlanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_lesson_plan_list, parent, false);

        return new ViewLessonPlanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewLessonPlanAdapter.MyViewHolder holder, int position) {

        final ViewDataItem data = dateList.get(position);

        if (data.getStatus() == 1) {

            holder.vwStageOne.setBackgroundColor(context.getResources().getColor(R.color.card_grey));
            holder.vwStageTwo.setBackgroundColor(context.getResources().getColor(R.color.card_grey));

            holder.imgYetStart.setImageResource(R.drawable.yet_to_start_green);
            holder.imgInProgress.setImageResource(R.drawable.inprogress_grey);
            holder.imgCompleted.setImageResource(R.drawable.completed_grey);

            holder.lblYetToStart.setTextColor(context.getResources().getColor(R.color.clr_green));
            holder.lblInProgress.setTextColor(context.getResources().getColor(R.color.clr_grey));
            holder.lblCompleted.setTextColor(context.getResources().getColor(R.color.clr_grey));

        } else if (data.getStatus() == 2) {
            holder.vwStageOne.setBackgroundColor(context.getResources().getColor(R.color.clr_green));
            holder.vwStageTwo.setBackgroundColor(context.getResources().getColor(R.color.card_grey));

            holder.imgYetStart.setImageResource(R.drawable.yet_to_start_green);
            holder.imgInProgress.setImageResource(R.drawable.inprogress_green);
            holder.imgCompleted.setImageResource(R.drawable.completed_grey);

            holder.lblYetToStart.setTextColor(context.getResources().getColor(R.color.clr_green));
            holder.lblInProgress.setTextColor(context.getResources().getColor(R.color.clr_green));
            holder.lblCompleted.setTextColor(context.getResources().getColor(R.color.clr_grey));

        } else if (data.getStatus() == 3) {
            holder.vwStageOne.setBackgroundColor(context.getResources().getColor(R.color.clr_green));
            holder.vwStageTwo.setBackgroundColor(context.getResources().getColor(R.color.clr_green));

            holder.imgYetStart.setImageResource(R.drawable.yet_to_start_green);
            holder.imgInProgress.setImageResource(R.drawable.inprogress_green);
            holder.imgCompleted.setImageResource(R.drawable.completed_green);

            holder.lblYetToStart.setTextColor(context.getResources().getColor(R.color.clr_green));
            holder.lblInProgress.setTextColor(context.getResources().getColor(R.color.clr_green));
            holder.lblCompleted.setTextColor(context.getResources().getColor(R.color.clr_green));

        }

        holder.lnrViews.removeAllViews();

        List<DataArrayItem> dataArray = data.getDataArray();
        for (int i = 0; i < dataArray.size(); i++) {

            String name = dataArray.get(i).getName();
            String value = dataArray.get(i).getValue();


            if(!value.equals("")) {
                LinearLayout.LayoutParams lnrParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lnrParams.setMargins(10, 25, 10, 0);

                LinearLayout lnr = new LinearLayout(context);
                lnr.setOrientation(LinearLayout.HORIZONTAL);
                lnr.setLayoutParams(lnrParams);

                lnr.removeAllViews();


                LinearLayout.LayoutParams edParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                edParams.weight = 4.0f;

                TextView lblName = new TextView(context);
                lblName.setTextColor(context.getResources().getColor(R.color.clr_black));
                lblName.setTypeface(lblName.getTypeface(), Typeface.BOLD);

                lblName.setTextSize(13);
                lblName.setText(name);
                lblName.setLayoutParams(edParams);
                lnr.addView(lblName);


                LinearLayout.LayoutParams SymbolParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                SymbolParams.weight = 2.0f;

                TextView lblSymbol = new TextView(context);
                lblSymbol.setTextColor(context.getResources().getColor(R.color.clr_black));
                lblSymbol.setTextSize(15);
                lblSymbol.setText(":");
                lblSymbol.setLayoutParams(SymbolParams);
                lnr.addView(lblSymbol);


                TextView lblValue = new TextView(context);
                lblValue.setTextColor(context.getResources().getColor(R.color.clr_black));
                lblValue.setTextAppearance(context, R.style.textStyle);

                lblValue.setTextSize(15);
                lblValue.setText(value);
                lblValue.setLayoutParams(edParams);
                lnr.addView(lblValue);

                holder.lnrViews.addView(lnr);
            }


        }


        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SelectedPosition == position) {
                    if (EditDataList.isEmpty()) {
                        SelectedPosition = position;
                        getDataToEditCard(data, position);
                    } else {
                        openEditDataPopupWindow(data, position);
                    }

                } else {
                    SelectedPosition = position;
                    getDataToEditCard(data, position);
                }
            }
        });


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationAlert(data, position);

            }
        });


        holder.imgYetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!LastIconStatus.equals("Yet to start")) {
                    statusIconUpdate(data, holder.imgYetStart, holder.imgInProgress, holder.imgCompleted,
                            holder.lblYetToStart, holder.lblInProgress, holder.lblCompleted,
                            holder.vwStageOne, holder.vwStageTwo, "Yet to start");
                }
            }
        });

        holder.imgInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!LastIconStatus.equals("In Progress")) {
                    statusIconUpdate(data, holder.imgYetStart, holder.imgInProgress, holder.imgCompleted,
                            holder.lblYetToStart, holder.lblInProgress, holder.lblCompleted,
                            holder.vwStageOne, holder.vwStageTwo, "In Progress");
                }
            }
        });

        holder.imgCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!LastIconStatus.equals("Completed")) {
                    statusIconUpdate(data, holder.imgYetStart, holder.imgInProgress, holder.imgCompleted,
                            holder.lblYetToStart, holder.lblInProgress, holder.lblCompleted,
                            holder.vwStageOne, holder.vwStageTwo, "Completed");

                }

            }
        });
    }

    private void showConfirmationAlert(ViewDataItem data, int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure want to delete this lesson ?");
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                deletePlan(data, position);
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(context.getResources().getColor(R.color.teacher_colorPrimary));
        negativeButton.setTextColor(context.getResources().getColor(R.color.teacher_colorPrimary));
    }

    private void deletePlan(ViewDataItem data, int position) {

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
        jsonObject.addProperty("particular_id", String.valueOf(data.getParticularId()));
        jsonObject.addProperty("userId", TeacherUtil_Common.Principal_staffId);
        Log.d("jsonObject", jsonObject.toString());
        Call<Response> call = apiService.deleteLessonPlan(jsonObject);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response>
                    response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("delete_Response", json);
                        int status = response.body().getStatus();
                        String message = response.body().getMessage();

                        if (status == 1) {
                            Toast.makeText(context, message,
                                    Toast.LENGTH_SHORT).show();

//                            dateList.remove(data);
//                            notifyDataSetChanged();
                            notifyItemRemoved(position);
                        } else {
                            showAlertfinish(message);
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
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(context, "Server Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void statusIconUpdate(ViewDataItem data, ImageView imgYetStart, ImageView imgInProgress, ImageView imgCompleted, TextView lblYetToStart, TextView lblInProgress, TextView lblCompleted, View vwStageOne, View vwStageTwo, String type) {

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
        jsonObject.addProperty("particular_id", String.valueOf(data.getParticularId()));
        jsonObject.addProperty("user_id", TeacherUtil_Common.Principal_staffId);
        jsonObject.addProperty("institute_id", TeacherUtil_Common.Principal_SchoolId);
        jsonObject.addProperty("value", type);
        Log.d("jsonObject", jsonObject.toString());
        Call<Response> call = apiService.updateStatusIcon(jsonObject);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response>
                    response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("delete_Response", json);

                        int status = response.body().getStatus();
                        String message = response.body().getMessage();


                        if (status == 1) {

                            LastIconStatus = type;
                            Toast.makeText(context, message,
                                    Toast.LENGTH_SHORT).show();

                            if (type.equalsIgnoreCase("Yet to start")) {

                                vwStageOne.setBackgroundColor(context.getResources().getColor(R.color.card_grey));
                                vwStageTwo.setBackgroundColor(context.getResources().getColor(R.color.card_grey));

                                imgYetStart.setImageResource(R.drawable.yet_to_start_green);
                                imgInProgress.setImageResource(R.drawable.inprogress_grey);
                                imgCompleted.setImageResource(R.drawable.completed_grey);

                                lblYetToStart.setTextColor(context.getResources().getColor(R.color.clr_green));
                                lblInProgress.setTextColor(context.getResources().getColor(R.color.clr_grey));
                                lblCompleted.setTextColor(context.getResources().getColor(R.color.clr_grey));

                            } else if (type.equalsIgnoreCase("In Progress")) {
                                vwStageOne.setBackgroundColor(context.getResources().getColor(R.color.clr_green));
                                vwStageTwo.setBackgroundColor(context.getResources().getColor(R.color.card_grey));

                                imgYetStart.setImageResource(R.drawable.yet_to_start_green);
                                imgInProgress.setImageResource(R.drawable.inprogress_green);
                                imgCompleted.setImageResource(R.drawable.completed_grey);

                                lblYetToStart.setTextColor(context.getResources().getColor(R.color.clr_green));
                                lblInProgress.setTextColor(context.getResources().getColor(R.color.clr_green));
                                lblCompleted.setTextColor(context.getResources().getColor(R.color.clr_grey));

                            } else if (type.equalsIgnoreCase("Completed")) {
                                vwStageOne.setBackgroundColor(context.getResources().getColor(R.color.clr_green));
                                vwStageTwo.setBackgroundColor(context.getResources().getColor(R.color.clr_green));

                                imgYetStart.setImageResource(R.drawable.yet_to_start_green);
                                imgInProgress.setImageResource(R.drawable.inprogress_green);
                                imgCompleted.setImageResource(R.drawable.completed_green);

                                lblYetToStart.setTextColor(context.getResources().getColor(R.color.clr_green));
                                lblInProgress.setTextColor(context.getResources().getColor(R.color.clr_green));
                                lblCompleted.setTextColor(context.getResources().getColor(R.color.clr_green));

                            }

                        } else {
                            showAlertfinish(message);
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
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(context, "Server Connection Failed",

                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateParticularCard(ViewDataItem data, int position) {

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
        jsonObject.addProperty("particular_id", data.getParticularId());
        jsonObject.addProperty("user_id", Integer.parseInt(TeacherUtil_Common.Principal_staffId));
        jsonObject.addProperty("institute_id", Integer.parseInt(TeacherUtil_Common.Principal_SchoolId));

        JsonArray jsonArrayschoolstd = new JsonArray();
        for (int i = 0; i < EditDataList.size(); i++) {
            JsonObject jsonObjectclass = new JsonObject();
            jsonObjectclass.addProperty("field_id", EditDataList.get(i).getFieldId());
            jsonObjectclass.addProperty("value", EditDataList.get(i).getValue());
            jsonArrayschoolstd.add(jsonObjectclass);
        }

        jsonObject.add("keyValueArray", jsonArrayschoolstd);

        Log.d("jsonObject", jsonObject.toString());
        Call<Response> call = apiService.updateLessonParticularCard(jsonObject);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response>
                    response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("delete_Response", json);

                        int status = response.body().getStatus();
                        String message = response.body().getMessage();

                        if (status == 1) {
                            PopupWindow.dismiss();

                            for (int i = 0; i < EditDataList.size(); i++) {
                                EditDataItem editData = EditDataList.get(i);
                                if (editData.getName().equalsIgnoreCase("Status")) {
                                    String value = editData.getValue();

                                    int index = dateList.indexOf(data);
                                    ViewDataItem valueSet = dateList.get(index);
                                    if (value.equalsIgnoreCase("Yet to start")) {
                                        valueSet.setStatus(1);
                                    } else if (value.equalsIgnoreCase("In Progress")) {
                                        valueSet.setStatus(2);
                                    } else if (value.equalsIgnoreCase("Completed")) {
                                        valueSet.setStatus(3);
                                    }
                                    afterEditedList.addAll(EditDataList);
                                    int ind = afterEditedList.indexOf(editData);
                                    afterEditedList.remove(ind);
                                }
                            }

                            int index = dateList.indexOf(data);
                            List<DataArrayItem> dataArray = dateList.get(index).getDataArray();
                            for (int i = 0; i < dataArray.size(); i++) {
                                DataArrayItem value = dataArray.get(i);
                                value.setName(afterEditedList.get(i).getName());
                                value.setValue(afterEditedList.get(i).getValue());
                            }
                            notifyItemChanged(position);

                            Toast.makeText(context, message,
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            showAlertfinish(message);
                        }

                    } else {
                        Toast.makeText(context, "Server Response Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(context, "Server Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataToEditCard(ViewDataItem data, int position) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(context);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService =
                TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<EditLessonModel> call = apiService.getEditDataCard(String.valueOf(data.getParticularId()), TeacherUtil_Common.lesson_request_type, TeacherUtil_Common.Principal_SchoolId, TeacherUtil_Common.Principal_staffId);

        call.enqueue(new Callback<EditLessonModel>() {
            @Override
            public void onResponse(Call<EditLessonModel> call, retrofit2.Response<EditLessonModel>
                    response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("edit_Response", json);

                        int status = response.body().getStatus();
                        String message = response.body().getMessage();

                        EditDataList.clear();
                        if (status == 1) {
                            EditDataList = response.body().getData();
                            openEditDataPopupWindow(data, position);
                        } else {
                            showAlertfinish(message);
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
            public void onFailure(Call<EditLessonModel> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(context, "Server Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openEditDataPopupWindow(ViewDataItem data, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.edit_lesson_plan, null);
        PopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        PopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                PopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        LinearLayout lnrEditViews = (LinearLayout) layout.findViewById(R.id.lnrEditViews);
        RecyclerView rvEditViews = (RecyclerView) layout.findViewById(R.id.rvEditViews);
        TextView btnNotNow = (TextView) layout.findViewById(R.id.btnNotNow);
        TextView btnUpdate = (TextView) layout.findViewById(R.id.btnUpdate);
        TextView btnClose = (TextView) layout.findViewById(R.id.btnClose);


        EditViewAdapter mAdapter = new EditViewAdapter(EditDataList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvEditViews.setLayoutManager(mLayoutManager);
        rvEditViews.setItemAnimator(new DefaultItemAnimator());
        rvEditViews.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow.dismiss();

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateParticularCard(data, position);
            }
        });

    }

    private void showAlertfinish(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
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

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout lnrViews;
        public ImageView btnEdit, btnDelete;
        public ImageView imgYetStart, imgInProgress, imgCompleted;
        public TextView lblCompleted, lblInProgress, lblYetToStart;


        public View vwStageOne, vwStageTwo;

        public MyViewHolder(View view) {
            super(view);

            lnrViews = (LinearLayout) view.findViewById(R.id.lnrViews);
            btnEdit = (ImageView) view.findViewById(R.id.btnEdit);
            btnDelete = (ImageView) view.findViewById(R.id.btnDelete);

            imgYetStart = (ImageView) view.findViewById(R.id.imgYetStart);
            imgInProgress = (ImageView) view.findViewById(R.id.imgInProgress);
            imgCompleted = (ImageView) view.findViewById(R.id.imgCompleted);

            vwStageOne = (View) view.findViewById(R.id.vwStageOne);
            vwStageTwo = (View) view.findViewById(R.id.vwStageTwo);

            lblCompleted = (TextView) view.findViewById(R.id.lblCompleted);
            lblInProgress = (TextView) view.findViewById(R.id.lblInProgress);
            lblYetToStart = (TextView) view.findViewById(R.id.lblYetToStart);


        }

        public void bind(final DatesModel item, final DatesListListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public ViewLessonPlanAdapter(List<ViewDataItem> dateList, Context context, RelativeLayout rytParent) {
        this.context = context;
        this.dateList = dateList;
        this.rytParent = rytParent;
    }

    public void clearAllData() {
        int size = this.dateList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.dateList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void updateList(List<ViewDataItem> temp) {
        dateList = temp;
        notifyDataSetChanged();
    }
}
