package fr.altoine.jnogging.utils;

/**
 * Created by soulierantoine on 25/07/2017.
 */

public class Location {
    private double longitude;
    private double latitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
