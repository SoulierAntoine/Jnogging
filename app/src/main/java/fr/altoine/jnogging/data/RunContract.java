package fr.altoine.jnogging.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by soulierantoine on 04/07/2017.
 */

public class RunContract {

    /**
     * Android Uri :
     * content:// - Content Provider Prefix
     * fr.altoine.jnogging - Content Authority
     * /runs - Specific Data
     */

    public static final String AUTHORITY = "fr.altoine.jnogging";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_RUNS = "runs";
    public static final String PATH_STEPS = "steps";

    public static class RunsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RUNS).build();

        public static final String TABLE_NAME = "runs";

        public static final String COLUMN_DISTANCE = "time";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
    }


    public static class StepsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS).build();

        public static final String TABLE_NAME = "steps";
        public static final String FOREIGN_KEY_RUN = "fk_run_id";

        public static final String COLUMN_ID_RUN = "id_run";
        public static final String COLUMN_LONG = "long";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_ACTIVITY = "activity";
    }
}
