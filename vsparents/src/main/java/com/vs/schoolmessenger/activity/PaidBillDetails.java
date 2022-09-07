package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.BillDetailsAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.BillDetails;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class PaidBillDetails extends AppCompatActivity {

    String child_ID,school_ID,amount,invoive_id,date,payment_type;

    RecyclerView paid_list_recycle;
    private ArrayList<BillDetails> bill_details = new ArrayList<>();

    TextView lblTotalAmount,lblDate,lblPaymentType;
    public BillDetailsAdapter mAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.table_layout);



        paid_list_recycle=(RecyclerView) findViewById(R.id.paid_list_recycle);
        lblTotalAmount=(TextView) findViewById(R.id.lblTotalAmount);
        lblDate=(TextView) findViewById(R.id.lblDate);
        lblPaymentType=(TextView) findViewById(R.id.lblPaymentType);

        invoive_id = getIntent().getExtras().getString("invoice_id");
        amount = getIntent().getExtras().getString("total_amount");
        date = getIntent().getExtras().getString("Date");
        payment_type = getIntent().getExtras().getString("Type");


        lblTotalAmount.setText("Rs:"+amount+"0");
        lblDate.setText(date);
        lblPaymentType.setText(payment_type);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.fee+" "+R.string.details);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        billDetails();




    mAdapter = new BillDetailsAdapter(bill_details, PaidBillDetails.this);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PaidBillDetails.this);
    paid_list_recycle.setLayoutManager(mLayoutManager);
    paid_list_recycle.setItemAnimator(new DefaultItemAnimator());
    paid_list_recycle.setAdapter(mAdapter);
    paid_list_recycle.getRecycledViewPool().setMaxRecycledViews(0, 80);


    }

    private void billDetails() {

        String baseURL= TeacherUtil_SharedPreference.getBaseUrl(PaidBillDetails.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.GetInvoiceById(invoive_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());



                        JSONObject jresponse = new JSONObject(response.body().toString());
                        JSONArray values=jresponse.getJSONArray("PaymentDetails");

                        if (values.length()>0) {

                            mAdapter.clearAllData();
                            BillDetails data;
                            int sno=0;
                            for (int i = 0; i < values.length(); i++) {
                                JSONObject jsonObject = values.getJSONObject(i);
                                String InvoiceId = jsonObject.getString("InvoiceId");
                                String FeeName = jsonObject.getString("FeeName");
                                String FeeTerm = jsonObject.getString("FeeTerm");
                                String PaidAmount = jsonObject.getString("PaidAmount");
                                String Discount = jsonObject.getString("Discount");
                                String FeeGroupId = jsonObject.getString("FeeGroupId");
                                String Disc = jsonObject.getString("Disc");
                                String LateFee = jsonObject.getString("LateFee");

                                  int serial=sno+1;
                                  sno=serial;
                                  data = new BillDetails(InvoiceId, FeeName, FeeTerm, PaidAmount, Discount, FeeGroupId, Disc, LateFee,serial);
                                  bill_details.add(data);

                            }

                            Log.e("bill_size", String.valueOf(bill_details.size()));
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_records), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }


}
