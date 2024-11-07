package com.vs.schoolmessenger.util;

import android.location.Location;

public class LocationDistanceCalculator {

    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Create a Location object for the first location
        Location location1 = new Location("Location 1");
        location1.setLatitude(lat1);
        location1.setLongitude(lon1);

        // Create a Location object for the second location
        Location location2 = new Location("Location 2");
        location2.setLatitude(lat2);
        location2.setLongitude(lon2);

        // Calculate the distance in meters
        return location1.distanceTo(location2);
    }
}
