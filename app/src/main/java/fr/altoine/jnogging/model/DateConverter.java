package fr.altoine.jnogging.model;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by soulierantoine on 24/07/2017.
 */

public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
