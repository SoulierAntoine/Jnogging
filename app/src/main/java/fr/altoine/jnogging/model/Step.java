package fr.altoine.jnogging.model;

import com.google.android.gms.location.DetectedActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soulierantoine on 21/07/2017.
 */

public class Step {
    private double longitude;
    private double latitude;
    private Date time;
    private DetectedActivity activity;

    public Step() {}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public DetectedActivity getActivity() {
        return activity;
    }

    public void setActivity(DetectedActivity activity) {
        this.activity = activity;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("longitude", getLongitude());
        map.put("latitude", getLatitude());
        map.put("time", getTime());
        map.put("activity", getActivity());

        return map;
    }
}
