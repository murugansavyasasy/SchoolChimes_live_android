package com.vs.schoolmessenger.adapter.Ptm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TodaySlotsData;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class TodaySlotsStaffAdapter extends RecyclerView.Adapter<TodaySlotsStaffAdapter.MyViewHolder> {
    Context context;
    ArrayList<TodaySlotsData> todaySlotsData = new ArrayList<TodaySlotsData>();
    Boolean isCancel = true;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_history_parentside, parent, false);
        return new MyViewHolder(itemView);
    }

    public TodaySlotsStaffAdapter(Context context, ArrayList<TodaySlotsData> isSlotsHistoryData) {
        this.context = context;
        this.todaySlotsData = isSlotsHistoryData;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final TodaySlotsData isSlotsHistoryData = todaySlotsData.get(position);


        if (!Objects.equals(isSlotsHistoryData.getEventName(), "")) {
            holder.lblPerpose.setVisibility(View.VISIBLE);
            holder.lblPerpose.setTypeface(null, Typeface.BOLD);
            holder.lblPerpose.setText(isSlotsHistoryData.getStudentName());
        } else {
            holder.lblPerpose.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getSlotStatus(), "")) {
            holder.lblStatus.setVisibility(View.VISIBLE);
            holder.lblStatus.setText(isSlotsHistoryData.getSlotStatus());
        } else {
            holder.lblStatus.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getStudentName(), "")) {
            holder.lblName.setVisibility(View.VISIBLE);
            holder.lblName.setText(isSlotsHistoryData.getClassName() + " - " + isSlotsHistoryData.getSectionName());
        } else {
            holder.lblName.setVisibility(View.GONE);
        }


        if (!Objects.equals(isSlotsHistoryData.getStudentName(), "")) {
            holder.lblPersion.setVisibility(View.VISIBLE);
            holder.lblPersion.setText(isSlotsHistoryData.getEventName());
        } else {
            holder.lblPersion.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getEventDate(), "")) {
            holder.lblDate.setVisibility(View.VISIBLE);
            holder.lblDate.setText(isSlotsHistoryData.getEventDate());
            try {
                holder.lblDate.setText(isDateConverted(isSlotsHistoryData.getEventDate()) + ", " + isSlotsHistoryData.getSlotFrom() + " - " + isSlotsHistoryData.getSlotTo());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        } else {
            holder.lblDate.setVisibility(View.GONE);
        }

        if (!Objects.equals(isSlotsHistoryData.getEventLink(), "")) {
            holder.lblLink.setVisibility(View.VISIBLE);
        } else {
            holder.lblLink.setVisibility(View.GONE);
        }

        holder.lblLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(isSlotsHistoryData.getEventLink()));
                context.startActivity(intent);
            }
        });

        if (!Objects.equals(isSlotsHistoryData.getEventMode(), "")) {
            holder.lblEventMode.setVisibility(View.VISIBLE);
            holder.lblEventMode.setText(isSlotsHistoryData.getEventMode());
        } else {
            holder.lblEventMode.setVisibility(View.GONE);
        }


        if (isSlotsHistoryData.getSlotStatus().equals("Upcoming")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_blue));

            if (isSlotsHistoryData.getIsBooked().equals("1")) {
                holder.lblCancelReopen.setVisibility(View.VISIBLE);
                holder.lblCancel.setVisibility(View.VISIBLE);
            } else {
                holder.lblCancelReopen.setVisibility(View.GONE);
                holder.lblCancel.setVisibility(View.GONE);
            }
        } else if (isSlotsHistoryData.getSlotStatus().equals("Available")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_blue));

            holder.lblCancel.setVisibility(View.GONE);
            holder.lblCancelReopen.setVisibility(View.GONE);
        } else if (isSlotsHistoryData.getSlotStatus().equals("Cancelled")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_red));

            holder.lblCancel.setVisibility(View.GONE);
            holder.lblCancelReopen.setVisibility(View.GONE);
        } else if (isSlotsHistoryData.getSlotStatus().equals("Expired")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_red));

            holder.lblCancel.setVisibility(View.GONE);
            holder.lblCancelReopen.setVisibility(View.GONE);
        } else if (isSlotsHistoryData.getSlotStatus().equals("Unknown")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_red));

            holder.lblCancel.setVisibility(View.GONE);
            holder.lblCancelReopen.setVisibility(View.GONE);
        } else if (isSlotsHistoryData.getSlotStatus().equals("Completed")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.gnt_ad_green));
            holder.lblCancel.setVisibility(View.GONE);
            holder.lblCancelReopen.setVisibility(View.GONE);
        }

        if (isSlotsHistoryData.getSlotStatus().equals("Upcoming")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_blue));
        } else if (isSlotsHistoryData.getSlotStatus().equals("Cancelled") || isSlotsHistoryData.getSlotStatus().equals("Expired") || isSlotsHistoryData.getSlotStatus().equals("Unknown")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.clr_red));
        } else if (isSlotsHistoryData.getSlotStatus().equals("Completed")) {
            holder.lblStatus.setTextColor(holder.itemView.getResources().getColor(R.color.gnt_green));

        }

        holder.lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancel = true;
                isPopup(isSlotsHistoryData.getSlotId(), holder, position);
            }
        });

        holder.lblCancelReopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancel = false;
                isPopup(isSlotsHistoryData.getSlotId(), holder, position);
            }
        });
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblPerpose, lblStatus, lblName, lblSubject, lblClass, lblDate, lblEventName, lblLink, lblEventMode, lblCancel, lblCancelReopen;
        RelativeLayout isHeader;
        TextView lblPersion;

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
            lblPersion = view.findViewById(R.id.lblPersion);

        }
    }

    @Override
    public int getItemCount() {
        return todaySlotsData.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < todaySlotsData.size()) {
            todaySlotsData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, todaySlotsData.size());
        }
    }


    public void isPopup(String slotId, MyViewHolder holder, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Alert");

        if (isCancel) {
            builder.setMessage("Are you sure you want to cancel?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    isCancel(slotId, holder, position);
                }
            });
        } else {
            builder.setMessage("Are you sure you want to cancel and reopen?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    isCancelAndReopen(slotId, holder, position);
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


    private void isCancel(String slotId, MyViewHolder holder, int position) {

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
                        holder.lblCancel.setVisibility(View.GONE);
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


    private void isCancelAndReopen(String slotId, MyViewHolder holder, int position) {

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
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {

                        holder.lblCancelReopen.setVisibility(View.GONE);
                        removeItem(position);
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

    public String isDateConverted(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = dateFormat.parse(dateString);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEEE - MMM dd, yyyy", Locale.ENGLISH);
        String formattedDate = outputDateFormat.format(date);
        String output = formattedDate;
        System.out.println(output);
        return output;
    }
}