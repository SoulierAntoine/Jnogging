package fr.altoine.jnogging.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soulierantoine on 21/07/2017.
 */

public class Run {
    private int distance;
    private int speed;
    private Date startTime;
    private Date timeSpentRunning;
    private Step[] steps;

    public Run() {}

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getTimeSpentRunning() {
        return timeSpentRunning;
    }

    public void setTimeSpentRunning(Date timeSpentRunning) {
        this.timeSpentRunning = timeSpentRunning;
    }

    public Step[] getSteps() {
        return steps;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("distance", getDistance());
        map.put("speed", getSpeed());
        map.put("startTime", getStartTime());
        map.put("timeSpentRunning", getTimeSpentRunning());
        map.put("steps", getSteps());

        return map;
    }
}
