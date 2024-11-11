package com.vs.schoolmessenger.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.vs.schoolmessenger.interfaces.LocationLatLongListener;

public class LocationHelper  {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationLatLongListener listener;

    public LocationHelper(Context context , LocationLatLongListener listener,String type) {
        this.listener = listener;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        // Create location request with high accuracy and low interval
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setWaitForAccurateLocation(true) // Only wait for accurate location
                .setMinUpdateIntervalMillis(5000) // Minimum interval between updates (5 seconds)
                .setMaxUpdateDelayMillis(0)       // No delay between updates
                .setMaxUpdates(1)                 // Get only one fresh update
                .build();

        // Create a location callback to receive location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // Use the fresh location here
                        listener.onLocationReturn(location.getLatitude(),location.getLongitude(),type);
                        Log.d("Location", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
                    }
                }
                // Stop location updates to get only one fresh result
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };
    }

    public void getFreshLocation(Context context) {
        // Request a fresh location update
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}