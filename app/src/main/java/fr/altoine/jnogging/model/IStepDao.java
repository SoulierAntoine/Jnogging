package fr.altoine.jnogging.model;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

/**
 * Created by soulierantoine on 24/07/2017.
 */

public interface IStepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSteps(Step... steps);
}
