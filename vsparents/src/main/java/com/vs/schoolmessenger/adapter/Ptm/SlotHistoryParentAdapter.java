package com.vs.schoolmessenger.adapter.Ptm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.SlotsHistoryData;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class SlotHistoryParentAdapter extends RecyclerView.Adapter<SlotHistoryParentAdapter.MyViewHolder> {
    Context context;
    ArrayList<SlotsHistoryData> isAvailableDateData = new ArrayList<SlotsHistoryData>();

    Boolean isCancel = true;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_history_parentside, parent, false);
        return new MyViewHolder(itemView);
    }

    public SlotHistoryParentAdapter(Context context, ArrayList<SlotsHistoryData> isSlotsHistoryData) {
        this.context = context;
        this.isAvailableDateData = isSlotsHistoryData;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final SlotsHistoryData isSlotsHistoryData = isAvailableDateData.get(position);


        holder.lblCancelReopen.setTypeface(null, Typeface.BOLD);
        holder.lblCancelReopen.setTypeface(null, Typeface.BOLD);

        holder.lblCancelReopen.setVisibility(View.GONE);
        if (!Objects.equals(isSlotsHistoryData.getPurpose(), "")) {
            holder.lblPerpose.setTypeface(null, Typeface.BOLD);

            holder.lblPerpose.setVisibility(View.VISIBLE);
            holder.lblPerpose.setText(isSlotsHistoryData.getPurpose());
        } else {
            holder.lblPerpose.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getStatus(), "")) {
            holder.lblStatus.setVisibility(View.VISIBLE);
            holder.lblStatus.setText(isSlotsHistoryData.getStatus());
        } else {
            holder.lblStatus.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getStaff_name(), "")) {
            holder.lblName.setTypeface(null, Typeface.BOLD);

            holder.lblName.setVisibility(View.VISIBLE);
            holder.lblName.setText(isSlotsHistoryData.getStaff_name());
        } else {
            holder.lblName.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getSubject_name(), "")) {
            holder.lblSubject.setVisibility(View.VISIBLE);
            holder.lblSubject.setText(isSlotsHistoryData.getSubject_name());
        } else {
            holder.lblSubject.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getSlot_date(), "")) {
            holder.lblDate.setVisibility(View.VISIBLE);
            try {
                holder.lblDate.setText(isDateConverted(isSlotsHistoryData.getSlot_date(), isSlotsHistoryData.getSlot_time()));

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            holder.lblDate.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getEvent_link(), "")) {
            holder.lblLink.setVisibility(View.VISIBLE);
        } else {
            holder.lblLink.setVisibility(View.GONE);
        }


        holder.lblLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(isSlotsHistoryData.getEvent_link()));
                context.startActivity(intent);
            }
        });

        if (!Objects.equals(isSlotsHistoryData.getMode(), "")) {
            holder.lblEventMode.setVisibility(View.VISIBLE);
            holder.lblEventMode.setText("Mode - " + isSlotsHistoryData.getMode());
        } else {
            holder.lblEventMode.setVisibility(View.GONE);
        }

        switch (isSlotsHistoryData.getStatus()) {
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

        switch (isSlotsHistoryData.getStatus()) {
            case "Upcoming":
            case "Available":
                holder.lblCancel.setVisibility(View.VISIBLE);
                break;
            case "Cancelled":
            case "Expired":
            case "Unknown":
            case "Completed":
                holder.lblCancel.setVisibility(View.GONE);
                break;
        }

        holder.lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancel = true;
                isReasonGettingDialog(isSlotsHistoryData.getSlot_id(), holder, position);
            }
        });
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblPerpose, lblStatus, lblName, lblSubject, lblClass, lblDate, lblEventName, lblLink, lblEventMode, lblCancel, lblCancelReopen;
        RelativeLayout isHeader;

        public MyViewHolder(View view) {
            super(view);

            lblPerpose = view.findViewById(R.id.lblPerpose);
            lblStatus = view.findViewById(R.id.lblStatus);
            lblName = view.findViewById(R.id.lblName);
            lblSubject = view.findViewById(R.id.lblSubject);
            lblClass = view.findViewById(R.id.lblClass);
            lblDate = view.findViewById(R.id.lblDate);
            lblLink = view.findViewById(R.id.lblLink);
            lblEventMode = view.findViewById(R.id.lblEventMode);
            isHeader = view.findViewById(R.id.isHeader);
            lblCancel = view.findViewById(R.id.lblCancelButton);
            lblCancelReopen = view.findViewById(R.id.lblCancelReopen);

        }
    }

    @Override
    public int getItemCount() {
        return isAvailableDateData.size();
    }

    private void isReasonGettingDialog(String ids, MyViewHolder holder, int position) {
        // Inflate the custom layout/view
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.cancel_slot_reason, null);

        final EditText editText = dialogView.findViewById(R.id.editTextPopup);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Create and show the dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    String enteredText = editText.getText().toString();
                    isCancel(ids, enteredText, holder, position);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Fill the reason", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeItem(int position) {
        if (position >= 0 && position < isAvailableDateData.size()) {
            isAvailableDateData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, isAvailableDateData.size());
        }
    }


    private void isCancel(String slotId, String isReason, MyViewHolder holder, int position) {
        Profiles childItem = new Profiles();
        childItem = TeacherUtil_SharedPreference.getChildItems(context, "childItem");
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(context);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("slot_id", slotId);
        jsonObject.addProperty("student_id", childItem.getChildID());
        jsonObject.addProperty("cancelled_reason", isReason);
        jsonObject.addProperty("institute_id", childItem.getSchoolID());

        Log.d("Req", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.isCancelBookingSlotForStudent(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        removeItem(position);
                        Log.d("ResponseParentSide", response.body().toString());
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
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        String formattedStartTime = outputTimeFormat.format(startTime);
        String formattedEndTime = outputTimeFormat.format(endTime);
        String output = formattedDate + ", " + formattedStartTime + " - " + formattedEndTime;
        System.out.println(output);
        return output;
    }
}
