package fr.altoine.jnogging.utils;

import android.content.ContentValues;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.altoine.jnogging.data.RunContract;

/**
 * Created by soulierantoine on 03/07/2017.
 */

public class FakeRunsData {
    public static Date[] getRandomSubsequentDate() {
        Date[] dates = {new Date(), new Date()};
        return dates;
    }

    public static ContentValues createFakeData() {
        ContentValues testValues = new ContentValues();

        int minimumDistance = 5;
        int maxmumDistance = 15;
        int randomDistance = (int)(Math.random() * (maxmumDistance - minimumDistance)) + minimumDistance;
        Date[] subsequentDates = getRandomSubsequentDate();
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.getDefault());

        testValues.put(RunContract.RunsEntry.COLUMN_DISTANCE, randomDistance);
        testValues.put(RunContract.RunsEntry.COLUMN_START_TIME, dateFormat.format(subsequentDates[0]));
        testValues.put(RunContract.RunsEntry.COLUMN_END_TIME,  dateFormat.format(subsequentDates[1]));


        return testValues;
        /* return new String[]{
                "30min 7km 14km/h",
                "60min 9km 9km/h",
                "50min 10km 12km/h",
                "90min 10km 6.6km/h",
                "40min 8km 12km/h",
                "50min 12km 14.4km/h",
                "120min 21km 10.5km/h",
        }; */
    }

    public static void insertFakeData(Context context) {
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        for (int i=0; i < 10; ++i)
            fakeValues.add(FakeRunsData.createFakeData());

        context.getContentResolver().bulkInsert(RunContract.RunsEntry.CONTENT_URI, fakeValues.toArray(new ContentValues[10]));
    }
}
