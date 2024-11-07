package com.vs.schoolmessenger.adapter.Ptm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.SlotModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.DateFormatter;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private final List<SlotModel> slotModelList;
    Context context;
    Boolean isCancel = true;


    public SlotAdapter(Context context, List<SlotModel> slotModelList) {
        this.context = context;
        this.slotModelList = slotModelList;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_history_parentside, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        SlotModel slotModel = slotModelList.get(position);
        holder.lblPerpose.setText(slotModel.getBooked_by());
        holder.lblPerpose.setTypeface(null, Typeface.BOLD);
        holder.lblStatus.setText(slotModel.getStatus());

        holder.lblCancelButton.setTypeface(null, Typeface.BOLD);
        holder.lblCancelReopen.setTypeface(null, Typeface.BOLD);

//        if (!slotModel.getMy_class().equals("")) {
//            holder.lblClass.setVisibility(View.VISIBLE);
//            holder.lblClass.setText(slotModel.getMy_class() + " - " + slotModel.getMy_section());
//        } else {
//            holder.lblClass.setVisibility(View.GONE);
//        }

        if (!slotModel.getBooked_by().equals("")) {
            holder.lblName.setVisibility(View.VISIBLE);
            holder.lblName.setText(slotModel.getMy_class() + " - " + slotModel.getMy_section());
        } else {
            holder.lblName.setVisibility(View.GONE);
        }

        holder.lblPersion.setText(slotModel.getEvent_name());

        if (!slotModel.getProfile_url().equals("")) {
            Glide.with(context)
                    .load(slotModel.getProfile_url())
                    .into(holder.imgUser);
        }

        holder.lblDate.setText(DateFormatter.formatDate(slotModel.getDate()) + " " + slotModel.getFrom_time() + " to " + slotModel.getTo_time());
        holder.lblEventMode.setText("Mode - " + slotModel.getEvent_mode());

        switch (slotModel.getStatus()) {
            case "Upcoming":
                if (slotModel.getIs_booked() == 1 && slotModel.getIs_cancelled() == 0) {
                    holder.lblCancelReopen.setVisibility(View.VISIBLE);
                    holder.lblCancelButton.setVisibility(View.VISIBLE);
                } else {
                    holder.lblCancelReopen.setVisibility(View.GONE);
                    holder.lblCancelButton.setVisibility(View.GONE);
                }
                break;
            case "Available":
                if (slotModel.getIs_cancelled() == 0 && slotModel.getIs_booked() == 0) {
                    holder.lblCancelButton.setVisibility(View.VISIBLE);
                    holder.lblCancelReopen.setVisibility(View.GONE);
                }
                break;
            case "Cancelled":
            case "Expired":
            case "Unknown":
            case "Completed":
                holder.lblCancelButton.setVisibility(View.GONE);
                holder.lblCancelReopen.setVisibility(View.GONE);
                break;
        }

        switch (slotModel.getStatus()) {
            case "Upcoming":
            case "Available":
                holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_blue));
                break;
            case "Cancelled":
            case "Expired":
            case "Unknown":
                holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_red));
                break;
            case "Completed":
                holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.multiple_image_select_primaryDark));
                break;
        }

        holder.lblCancelReopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCancel = false;
                isPopup(slotModel.getSlot_id(), holder);
            }
        });

        holder.lblCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCancel = true;
                isPopup(slotModel.getSlot_id(), holder);
            }
        });
    }


    @Override
    public int getItemCount() {
        return slotModelList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView lblClass, lblPerpose, lblStatus, lblName, lblEventMode, lblCancelReopen, lblCancelButton, lblLink, lblDate, lblPersion, lblSubject;
        RelativeLayout isHeader;
        ImageView imgUser;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);

            lblPerpose = itemView.findViewById(R.id.lblPerpose);
            lblClass = itemView.findViewById(R.id.lblClass);
            lblStatus = itemView.findViewById(R.id.lblStatus);
            lblName = itemView.findViewById(R.id.lblName);
            lblEventMode = itemView.findViewById(R.id.lblEventMode);
            isHeader = itemView.findViewById(R.id.isHeader);
            lblCancelReopen = itemView.findViewById(R.id.lblCancelReopen);
            lblCancelButton = itemView.findViewById(R.id.lblCancelButton);
            lblLink = itemView.findViewById(R.id.lblLink);
            lblDate = itemView.findViewById(R.id.lblDate);
            lblPersion = itemView.findViewById(R.id.lblPersion);
            imgUser = itemView.findViewById(R.id.imgUser);
            lblSubject = itemView.findViewById(R.id.lblSubject);
        }
    }

    public void isPopup(String slotId, SlotViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Alert");

        if (isCancel) {
            builder.setMessage("Are you sure you want to cancel?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    isCancel(slotId, holder);
                }
            });
        } else {
            builder.setMessage("Are you sure you want to cancel and reopen?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    isCancelAndReopen(slotId, holder);
                }
            });
        }

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void isCancel(String slotId, SlotViewHolder holder) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(context);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("slot_id", slotId);
        jsonObject.addProperty("staff_id", TeacherUtil_Common.Principal_staffId);
        jsonObject.addProperty("institute_id", TeacherUtil_Common.Principal_SchoolId);

        Log.d("Req", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.isCancel_slot_staff(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        holder.lblCancelButton.setVisibility(View.GONE);
                        holder.lblStatus.setText("Cancelled");
                        holder.isHeader.setBackgroundResource(R.drawable.studentside_listing_design);
                        Log.d("Response", response.body().toString());
                    } else {
                        Toast.makeText(context, "Check your Internet connectivity", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(context, "Check your Internet connectivity", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void isCancelAndReopen(String slotId, SlotViewHolder holder) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(context);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("slot_id", slotId);
        jsonObject.addProperty("staff_id", TeacherUtil_Common.Principal_staffId);
        jsonObject.addProperty("institute_id", TeacherUtil_Common.Principal_SchoolId);

        Log.d("Req", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.isCancelAndReopen_slot_staff(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res__", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {

                        holder.lblCancelReopen.setVisibility(View.GONE);
                        holder.lblStatus.setText("Available");
                        holder.isHeader.setBackgroundResource(R.drawable.studentside_slot_upcoming);
                        Log.d("ResponseStaffSide", response.body().toString());
                    } else {
                        Toast.makeText(context, "Check your Internet connectivity", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(context, "Check your Internet connectivity", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public String isDateConverted(String dateString, String timeRange) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = dateFormat.parse(dateString);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE - MMM dd, yyyy", Locale.ENGLISH);
        String formattedDate = outputDateFormat.format(date);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        String[] times = timeRange.split(" - ");
        Date startTime = timeFormat.parse(times[0]);
        Date endTime = timeFormat.parse(times[1]);
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH.mm", Locale.ENGLISH);
        String formattedStartTime = outputTimeFormat.format(startTime);
        String formattedEndTime = outputTimeFormat.format(endTime);
        String output = formattedDate + ", " + formattedStartTime + " - " + formattedEndTime;
        System.out.println(output);
        return output;
    }
}

