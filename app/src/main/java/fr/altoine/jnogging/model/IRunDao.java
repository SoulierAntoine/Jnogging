package fr.altoine.jnogging.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

/**
 * Created by soulierantoine on 24/07/2017.
 */

@Dao
public interface IRunDao {
    @Query("select id, distance, speed, start_time, time_spent_running from runs order by start_time asc")
    Cursor loadAllRunsByDate();

    @Query("select * from runs " +
            "inner join steps on steps.run_id = runs.id" +
            "where id = :id")
    Run loadRunById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRuns(Run... runs);

    @Delete
    void deleteRuns(Run... runs);
}
