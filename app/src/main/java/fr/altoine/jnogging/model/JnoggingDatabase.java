package fr.altoine.jnogging.model;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import fr.altoine.jnogging.utils.Constants;

/**
 * Created by soulierantoine on 24/07/2017.
 */

@TypeConverters({DateConverter.class})
public abstract class JnoggingDatabase extends RoomDatabase {
    public abstract IRunDao runDao();
    public abstract IStepDao stepDao();

    private static JnoggingDatabase db;

    public static JnoggingDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(
                    context.getApplicationContext(),
                    JnoggingDatabase.class,
                    Constants.DATABASE_NAME)
                    .build();
        }

        return db;
    }

    public static void destroyInstance() {
        db = null;
    }
}
