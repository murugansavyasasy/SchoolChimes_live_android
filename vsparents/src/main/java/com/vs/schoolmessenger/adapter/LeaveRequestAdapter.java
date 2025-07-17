package com.vs.schoolmessenger.adapter;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.LeaveRequestStaffApproveActivity;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.LeaveRequestDetails;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class LeaveRequestAdapter extends RecyclerView.Adapter<LeaveRequestAdapter.MyViewHolder> {

    private List<LeaveRequestDetails> lib_list;
    Context context;
    private PopupWindow pwindow;
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
        public TextView lblName, lblStandard, lblSection, lblAppliedOn, lblFromDate, lblToDate, lblReason, cardProfile_tv1,
                cardProfile_tv2,
                cardProfile_tv3,
                cardProfile_tv4,
                cardProfile_tv5, lblGenerateOutpass;
        public Button btnApprove, btnDecline;
        public RelativeLayout rytLayout;
        public LinearLayout lnrParent,lnrImages;
        ImageView imageView6;
        public ImageView imgAprovalStatus;
        public MyViewHolder(View view) {
            super(view);

            lblName = (TextView) view.findViewById(R.id.lblName);
            lblStandard = (TextView) view.findViewById(R.id.lblStandard);
            lblSection = (TextView) view.findViewById(R.id.lblSection);
            lblAppliedOn = (TextView) view.findViewById(R.id.lblAppliedOn);
            lblFromDate = (TextView) view.findViewById(R.id.lblFromDate);
            lblToDate = (TextView) view.findViewById(R.id.lblToDate);
            lblReason = (TextView) view.findViewById(R.id.lblReason);

            rytLayout = (RelativeLayout) view.findViewById(R.id.rytLayout);
            lnrParent = (LinearLayout) view.findViewById(R.id.lnrParent);
            lnrImages = (LinearLayout) view.findViewById(R.id.lnrImages);
            imgAprovalStatus = (ImageView) view.findViewById(R.id.imgAprovalStatus);
            imageView6 = (ImageView) view.findViewById(R.id.imageView6);

            cardProfile_tv2 = (TextView) view.findViewById(R.id.cardProfile_tv2);
            cardProfile_tv1 = (TextView) view.findViewById(R.id.cardProfile_tv1);
            cardProfile_tv3 = (TextView) view.findViewById(R.id.cardProfile_tv3);
            cardProfile_tv4 = (TextView) view.findViewById(R.id.cardProfile_tv4);
            cardProfile_tv5 = (TextView) view.findViewById(R.id.cardProfile_tv5);
            lblGenerateOutpass = (TextView) view.findViewById(R.id.lblGenerateOutpass);
        }
    }

    public LeaveRequestAdapter(List<LeaveRequestDetails> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public LeaveRequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leave_history_request, parent, false);
        return new LeaveRequestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LeaveRequestAdapter.MyViewHolder holder, final int position) {
        final LeaveRequestDetails history = lib_list.get(position);
        holder.lblName.setText(" " + history.getName());
        holder.lblStandard.setText(" " + history.getCLS());
        holder.lblSection.setText(history.getSection());
        holder.lblAppliedOn.setText(" : " + history.getLeaveAppliedOn());
        holder.lblFromDate.setText(" : " + history.getLeaveFromDate());
        holder.lblToDate.setText(" : " + history.getLeaveToDate());
        holder.lblReason.setText(history.getReason());
        if (history.getLoginType()) {
            holder.imageView6.setVisibility(View.VISIBLE);
            holder.lblGenerateOutpass.setVisibility(View.GONE);
        } else {
            if (history.getApproved().equals("1")) {
                holder.imageView6.setVisibility(View.GONE);
                holder.lblGenerateOutpass.setVisibility(View.VISIBLE);
            } else {
                holder.imageView6.setVisibility(View.GONE);
                holder.lblGenerateOutpass.setVisibility(View.GONE);
            }
        }

        if (history.getApproved().equals("0")) {
            holder.imgAprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.waiting_tick));
        } else if (history.getApproved().equals("1")) {
            holder.imgAprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.green_tick));
        } else if (history.getApproved().equals("2")) {
            holder.imgAprovalStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.wrong_tick));
        }

        holder.rytLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (history.getLoginType()) {
                    Intent requests = new Intent(context, LeaveRequestStaffApproveActivity.class);
                    requests.putExtra("history", (Serializable) history);
                    context.startActivity(requests);
                } else {
                    if (history.getApproved().equals("1")) {
                        Intent requests = new Intent(context, LeaveRequestStaffApproveActivity.class);
                        requests.putExtra("history", (Serializable) history);
                        context.startActivity(requests);
                    }
                }
            }
        });
    }


    private void historyPopup(final LeaveRequestDetails history) {


        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_leave_history, null);

        pwindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pwindow.setContentView(layout);

        TextView lblPopupName = (TextView) layout.findViewById(R.id.lblPopupName);
        TextView lblPopupStandard = (TextView) layout.findViewById(R.id.lblPopupStandard);
        TextView lblPopupSection = (TextView) layout.findViewById(R.id.lblPopupSection);
        TextView lblPopupAppliedOn = (TextView) layout.findViewById(R.id.lblPopupAppliedOn);
        TextView lblPopupFromDate = (TextView) layout.findViewById(R.id.lblPopupFromDate);
        TextView lblPopupToDate = (TextView) layout.findViewById(R.id.lblPopupToDate);
        TextView lblPopupReason = (TextView) layout.findViewById(R.id.lblPopupReason);
        ImageView btn_ok = (ImageView) layout.findViewById(R.id.btn_ok);
        TextView btnPopupApprove = (Button) layout.findViewById(R.id.btnPopupApprove);
        TextView btnPopupDecline = (Button) layout.findViewById(R.id.btnPopupDecline);
        final EditText Reason = (EditText) layout.findViewById(R.id.txtReason);

        if (history.getLoginType()) {
            if(history.getApproved().equals("0")) {
                btnPopupApprove.setVisibility(View.VISIBLE);
                btnPopupDecline.setVisibility(View.VISIBLE);
                Reason.setVisibility(View.GONE);
            }
        } else {
            btnPopupApprove.setVisibility(View.GONE);
            btnPopupDecline.setVisibility(View.GONE);
            Reason.setVisibility(View.GONE);
        }

        lblPopupName.setText("" + history.getName());
        lblPopupStandard.setText(" " + history.getCLS());
        lblPopupSection.setText(history.getSection());
        lblPopupAppliedOn.setText(" : " + history.getLeaveAppliedOn());
        lblPopupFromDate.setText(" : " + history.getLeaveFromDate());
        lblPopupToDate.setText(" : " + history.getLeaveToDate());

        lblPopupReason.setText(history.getReason());


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindow.dismiss();
            }
        });

        btnPopupApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason=Reason.getText().toString();

                leaveApproveDeclineApi("1", history.getId(), history.getUpdatedOn(),reason);

            }
        });
        btnPopupDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason=Reason.getText().toString();
                leaveApproveDeclineApi("2", history.getId(), history.getUpdatedOn(),reason);

            }
        });
    }

    private void leaveApproveDeclineApi(String approve, String id, String UpdatedOn,String reason) {

        String baseURL= TeacherUtil_SharedPreference.getBaseUrlContext(context);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

       String staffid=  TeacherUtil_Common.Principal_staffId;
        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("leaveid", id);
        jsonObject.addProperty("status", approve);
        jsonObject.addProperty("updatedby", staffid);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonObject> call = apiService.Updateleavestatus(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONObject js = new JSONObject(response.body().toString());

                        String Status = js.getString("status");
                        String mesaage = js.getString("message");

                        if (Status.equals("0")) {
                            Toast.makeText(context, mesaage, Toast.LENGTH_SHORT).show();
                            pwindow.dismiss();
                        } else if (Status.equals("1")) {
                            Toast.makeText(context, mesaage, Toast.LENGTH_SHORT).show();
                            pwindow.dismiss();
                        } else if (Status.equals("2")) {
                            Toast.makeText(context, mesaage, Toast.LENGTH_SHORT).show();
                            pwindow.dismiss();
                        }


                    } else {
                        Toast.makeText(context, R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(context, R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }

    public void updateList(List<LeaveRequestDetails> temp) {
        lib_list = temp;
        notifyDataSetChanged();
    }

}

