package fr.altoine.jnogging.utils;

import android.content.ContentValues;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import fr.altoine.jnogging.data.RunContract;

/**
 * Created by soulierantoine on 03/07/2017.
 */

public class FakeRunsData {
    private static Date[] getRandomSubsequentDate() {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        int year = rand(2014, 2016);
        int day = rand(1, calendar.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
        int hour = rand(0, calendar.getActualMaximum(GregorianCalendar.HOUR_OF_DAY));
        int minute = rand(0, calendar.getActualMaximum(GregorianCalendar.MINUTE));

        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.DAY_OF_YEAR, day);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, hour);
        calendar.set(GregorianCalendar.MINUTE, minute);
        calendar.set(GregorianCalendar.SECOND, 0);

        Date dateStart = calendar.getTime();
        int minutesRunning = rand(45, 90);

        calendar.add(GregorianCalendar.MINUTE, minutesRunning);
        Date dateEnd = calendar.getTime();

        return new Date[]{dateStart, dateEnd};
    }

    private static ContentValues createFakeData() {
        ContentValues testValues = new ContentValues();

        int minimumDistance = 5;
        int maximumDistance = 15;
        int randomDistance = rand(minimumDistance, maximumDistance);

        Date[] subsequentDates = getRandomSubsequentDate();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

        long diffBetweenDatesInMillis = (subsequentDates[1].getTime() - subsequentDates[0].getTime());
        long timeSpentRunningInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffBetweenDatesInMillis);
        float timeSpentRunningInHours = (float) timeSpentRunningInMinutes / 60;

        double speedInKilometerPerHours = 0.0;
        if (timeSpentRunningInHours != 0)
            speedInKilometerPerHours = randomDistance / timeSpentRunningInHours;

        // Round the number to 2 number after decimal point
        speedInKilometerPerHours = Math.floor(speedInKilometerPerHours * 100) / 100;


        testValues.put(RunContract.RunsEntry.COLUMN_DISTANCE, randomDistance);
        testValues.put(RunContract.RunsEntry.COLUMN_START_TIME, dateFormat.format(subsequentDates[0]));
        testValues.put(RunContract.RunsEntry.COLUMN_TIME_SPENT_RUNNING,  timeSpentRunningInMinutes);
        testValues.put(RunContract.RunsEntry.COLUMN_SPEED,  speedInKilometerPerHours);

        return testValues;
    }

    public static void insertFakeData(Context context) {
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        for (int i = 0; i < 10; ++i)
            fakeValues.add(FakeRunsData.createFakeData());

        context.getContentResolver().bulkInsert(RunContract.RunsEntry.CONTENT_URI, fakeValues.toArray(new ContentValues[10]));
    }

    public static int rand(int min, int max) {
        return (int)(Math.random() * (max - min)) + min;
    }
}
