package fr.altoine.jnogging.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fr.altoine.jnogging.data.RunContract.RunsEntry;
import fr.altoine.jnogging.data.RunContract.StepsEntry;

/**
 * Created by soulierantoine on 04/07/2017.
 */

public class RunDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jnoggingDb.db";
    private static final int VERSION = 1;

    public RunDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_RUNS = "CREATE TABLE " + RunsEntry.TABLE_NAME + " (" +
                RunsEntry._ID                            + " INTEGER PRIMARY KEY, " +
                RunsEntry.COLUMN_DISTANCE                + " INTEGER NOT NULL, " +
                RunsEntry.COLUMN_SPEED                   + " INTEGER NOT NULL, " +
                RunsEntry.COLUMN_START_TIME              + " DATETIME NOT NULL, " +
                RunsEntry.COLUMN_TIME_SPENT_RUNNING      + " INTEGER NOT NULL);";


        final String CREATE_TABLE_STEPS = "CREATE TABLE " + StepsEntry.TABLE_NAME + " (" +
                StepsEntry._ID                            + " INTEGER PRIMARY KEY, " +
                StepsEntry.COLUMN_ID_RUN                  + " INTEGER NOT NULL, " +
                StepsEntry.COLUMN_ACTIVITY                + " VARCHAR(50) NOT NULL, " +
                StepsEntry.COLUMN_TIME                    + " DATETIME NOT NULL, " +
                StepsEntry.COLUMN_LAT                     + " INTEGER NOT NULL, " +
                StepsEntry.COLUMN_LONG                    + " INTEGER NOT NULL, " +

                "KEY " + StepsEntry.FOREIGN_KEY_RUN + " (" + StepsEntry.COLUMN_ID_RUN + "), " +
                "CONSTRAINT " + StepsEntry.FOREIGN_KEY_RUN + " FOREIGN KEY (" + StepsEntry.COLUMN_ID_RUN + ") " +
                "REFERENCES " + RunsEntry.TABLE_NAME + " (" + RunsEntry._ID + ");";


        db.execSQL(CREATE_TABLE_RUNS + CREATE_TABLE_STEPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StepsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RunsEntry.TABLE_NAME);

        onCreate(db);
    }
}
