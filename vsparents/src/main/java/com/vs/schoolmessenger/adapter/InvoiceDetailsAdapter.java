package com.vs.schoolmessenger.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.InVoiceDetailsModel;
import com.vs.schoolmessenger.payment.PdfWebView;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONObject;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class InvoiceDetailsAdapter extends RecyclerView.Adapter<InvoiceDetailsAdapter.MyViewHolder> {

    private List<InVoiceDetailsModel> dateList;
    Context context;
    String child_id,schoolid;
    @Override
    public InvoiceDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invoice_adapter_list, parent, false);

        return new InvoiceDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InvoiceDetailsAdapter.MyViewHolder holder, int position) {

        final InVoiceDetailsModel data = dateList.get(position);
        child_id = Util_SharedPreference.getChildIdFromSP(context);
        schoolid = Util_SharedPreference.getSchoolIdFromSP(context);
        holder.lblInvoiceAmount.setText(data.getInvoice_amount());
        holder.lblInvoiceDate.setText(data.getInvoice_date());
        holder.lblInvoiceNumber.setText(data.getInvoice_number());

        holder.btnViewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewInvoicePDF(data.getInvoice_ID());

            }
        });
    }
    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblInvoiceDate,lblInvoiceAmount,lblInvoiceNumber;
        public ConstraintLayout LayoutOverall;
        public Button  btnViewFile;
        public MyViewHolder(View view) {
            super(view);

            lblInvoiceDate = (TextView) view.findViewById(R.id.lblInvoiceDate);
            lblInvoiceAmount = (TextView) view.findViewById(R.id.lblInvoiceAmount);
            lblInvoiceNumber = (TextView) view.findViewById(R.id.lblInvoiceNumber);
            btnViewFile = (Button) view.findViewById(R.id.btnViewFile);
        }
    }

    public InvoiceDetailsAdapter(Context context, List<InVoiceDetailsModel> dateList) {
        this.context = context;
        this.dateList = dateList;
    }


    private void ViewInvoicePDF(String invoiceId) {

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
        jsonObject.addProperty("InvoiceId",invoiceId);

        Log.d("jsonObject", jsonObject.toString());
        Log.d("InvoiceId", invoiceId);
        Call<JsonObject> call = apiService.GetInvoiceDetails(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject>
                    response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("InvoicePDF:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            String status = jsonObject.getString("Status");
                            String message = jsonObject.getString("Message");
                            if (status.equals("1")) {
                                String invoicePdf = jsonObject.getString("data");
                                if(!invoicePdf.equals("")){
                                    Intent receipt = new Intent(context, PdfWebView.class);
                                    receipt.putExtra("URL",invoicePdf);
                                    context.startActivity(receipt);

//                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//                                            Uri.parse(invoicePdf));
//                                    context.startActivity(browserIntent);
                                }
                                else{
                                    showAlertfinish(message);
                                }

                            } else {
                                showAlertfinish(message);
                            }
                        } catch (Exception e) {
                            Log.e("InvoicePDF:Exception", e.getMessage());
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
