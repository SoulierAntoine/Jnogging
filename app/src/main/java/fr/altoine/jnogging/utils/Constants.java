package fr.altoine.jnogging.utils;

import fr.altoine.jnogging.data.RunContract;

/**
 * Created by Antoine on 16/07/2017.
 */

public class Constants {
    private Constants() {}

    public static final String DATE_FORMAT = "YYYY-MM-DD HH:MM:SS";
    public static final long CHECK_USER_ACTIVITY_INTERVAL = 5000;

    public static final String[] RUNS_PROJECTION = {
            RunContract.RunsEntry._ID,
            RunContract.RunsEntry.COLUMN_DISTANCE,
            RunContract.RunsEntry.COLUMN_SPEED,
            RunContract.RunsEntry.COLUMN_START_TIME,
            RunContract.RunsEntry.COLUMN_TIME_SPENT_RUNNING,
    };

    public static final int INDEX_RUN_ID = 0;
    public static final int INDEX_RUN_DISTANCE = 1;
    public static final int INDEX_RUN_SPEED = 2;
    public static final int INDEX_RUN_START_TIME = 3;
    public static final int INDEX_TIME_SPENT_RUNNING = 4;
}
