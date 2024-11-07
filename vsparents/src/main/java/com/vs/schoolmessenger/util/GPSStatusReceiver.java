package com.vs.schoolmessenger.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.vs.schoolmessenger.interfaces.GPSStatusListener;

public class GPSStatusReceiver extends BroadcastReceiver {
    private GPSStatusListener listener;
    public GPSStatusReceiver(GPSStatusListener listener) {
        this.listener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSenabled) {
                listener.onGPSStatusChanged(isGPSenabled);
            } else {
                listener.onGPSStatusChanged(isGPSenabled);
            }
        }
    }
}