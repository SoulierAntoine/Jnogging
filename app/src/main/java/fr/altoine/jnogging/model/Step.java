package fr.altoine.jnogging.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.location.DetectedActivity;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by soulierantoine on 21/07/2017.
 */

@Entity(tableName = "steps",
        foreignKeys = @ForeignKey(
                entity = Run.class,
                parentColumns = "id",
                childColumns = "run_id",
                onDelete = CASCADE
        )
)
public class Step {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "run_id")
    private int runId;

    private double longitude;
    private double latitude;
    private Date time;
    private DetectedActivity activity;

    /* public Step() {}

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Date getTime() {
        return time;
    }

    public DetectedActivity getActivity() {
        return activity;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("longitude", getLongitude());
        map.put("latitude", getLatitude());
        map.put("time", getTime());
        map.put("activity", getActivity());

        return map;
    } */
}
