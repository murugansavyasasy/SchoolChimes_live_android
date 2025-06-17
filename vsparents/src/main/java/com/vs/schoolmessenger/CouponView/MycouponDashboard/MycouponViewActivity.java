package com.vs.schoolmessenger.CouponView.MycouponDashboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.CouponModel.TicketCouponSummary.TicketSummary;
import com.vs.schoolmessenger.CouponView.MyCouponDetails.TicketCouponViewActivity;
import com.vs.schoolmessenger.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MycouponViewActivity extends AppCompatActivity {

    private ImageView back;
    private String merchant_name;
    private String expiry_date, expiry_type;
    private String offer_to_show;
    private String how_to_use;

    ImageView copyIcon;
    private String coupon_code;
    TextView header, lblAddress, lblLocationName, lblValidUnit;
    TextView offer, description, couponCode;
    ImageView logo;
    String cover_image;
    String merchant_logo;
    ArrayList<String> isLocationDetails;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mycoupon_view);
        back = findViewById(R.id.back);

        header = findViewById(R.id.header);
        offer = findViewById(R.id.offer);
        description = findViewById(R.id.description);
        couponCode =findViewById(R.id.couponCode);
        lblAddress = findViewById(R.id.lblAddress);
        lblLocationName = findViewById(R.id.lblLocationName);
        lblValidUnit = findViewById(R.id.lblValidUnit);
        logo = findViewById(R.id.logo);
        copyIcon = findViewById(R.id.copyIcon);

        merchant_name = getIntent().getStringExtra("merchant_name");
        expiry_date = getIntent().getStringExtra("expiry_date");
        expiry_type = getIntent().getStringExtra("expiry_type");
        offer_to_show = getIntent().getStringExtra("offer_to_show");
        how_to_use = getIntent().getStringExtra("how_to_use");
        coupon_code = getIntent().getStringExtra("coupon_code");
        cover_image = getIntent().getStringExtra("cover_image");
        merchant_logo = getIntent().getStringExtra("merchant_logo");

        List<TicketSummary.Location> receivedLocations = (List<TicketSummary.Location>) getIntent().getSerializableExtra("location_list");
        if (receivedLocations != null) {
            for (TicketSummary.Location location : receivedLocations) {
                lblAddress.setText(location.getLocation_name());
                lblValidUnit.setText(expiry_type + " : " + expiry_date);
            }
        }
        header.setText(merchant_name);
        offer.setText(offer_to_show);
        description.setText(convertHtmlToBullets(how_to_use));
        couponCode.setText(coupon_code);
        lblLocationName.setText(merchant_name);

        Glide.with(MycouponViewActivity.this)
                .load(merchant_logo)
                .into(logo);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(MycouponViewActivity.this, TicketCouponViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        copyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyTextToClipboard();
            }
        });
    }

    private void copyTextToClipboard() {
        String textToCopy = couponCode.getText().toString();

        if (textToCopy.isEmpty()) {
            Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to access clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // You can get other details like city, state, etc.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getResources().getString(R.string.Address_not_found);
    }

    private String convertHtmlToBullets(String htmlContent) {
        String cleaned = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT).toString();

        String[] lines = cleaned.split("\\n|(?<=\\.)\\s*");

        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                builder.append("• ").append(line).append("\n");
            }
        }
        return builder.toString().trim();
    }
}
