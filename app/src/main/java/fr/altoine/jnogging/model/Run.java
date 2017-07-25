package fr.altoine.jnogging.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by soulierantoine on 21/07/2017.
 */

@Entity(tableName = "runs")
public class Run {
    @PrimaryKey
    private int id;

    private int distance;
    private int speed;
    private Step[] steps;

    @ColumnInfo (name = "start_time")
    private Date startTime;

    @ColumnInfo (name = "time_spent_running")
    private int timeSpentRunning;


    public Run(int distance, int speed, Date startTime, int timeSpentRunning) {
        this.distance = distance;
        this.speed = speed;
        this.startTime = startTime;
        this.timeSpentRunning = timeSpentRunning;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }

    /* public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("distance", getDistance());
        map.put("speed", getSpeed());
        map.put("startTime", getStartTime());
        map.put("timeSpentRunning", getTimeSpentRunning());
        map.put("steps", getSteps());

        return map;
    } */
}
