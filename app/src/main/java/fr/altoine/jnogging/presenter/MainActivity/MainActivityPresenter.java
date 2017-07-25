package fr.altoine.jnogging.presenter.MainActivity;


import java.util.Date;

import fr.altoine.jnogging.model.IRunDao;
import fr.altoine.jnogging.model.IStepDao;
import fr.altoine.jnogging.model.JnoggingDatabase;
import fr.altoine.jnogging.model.Run;
import fr.altoine.jnogging.presenter.IPresenter;
import fr.altoine.jnogging.utils.JnoggingUtilities;
import fr.altoine.jnogging.utils.Location;
import fr.altoine.jnogging.view.MainActivity.IMainActivityView;

/**
 * Created by Antoine on 23/07/2017.
 */

public class MainActivityPresenter implements IPresenter {
    private IMainActivityView mView;
    private IRunDao mRunDao;
    private IStepDao mStepDao;
    private Location mStartingPosition;
    private Location mEndingPosition;
    private long mStartedRunTimestamp;

    // Whether the user is currently in a "run" (walking, running, resting...) or not
    private boolean mStartingRun;

    public void setDao(JnoggingDatabase db) {
        mRunDao = db.runDao();
        mStepDao = db.stepDao();
    }

    public void setPosition(double latitude, double longitude) {
        if (mStartingRun)
            mStartingPosition = new Location(latitude, longitude);
    }

    public void setEndingPosition(double latitude, double longitude) {
        this.mEndingPosition = new Location(latitude, longitude);
    }

    @Override
    public void onCreate() {
        mStartingRun = true;
    }

    @Override
    public void onStart() {}

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onStop() {}

    @Override
    public void onDestroy() {}

    public MainActivityPresenter(IMainActivityView view) {
        this.mView = view;
    }

    public void startRunning(boolean resumeRunning) {
        mStartingRun = true;
        mStartedRunTimestamp = new Date().getTime();
        mView.changeToRunning(resumeRunning);
    }

    public void stopRunning(long base) {
        mStartingRun = false;
        getTimeRan(base);
        int timeSpentRunningInSeconds = JnoggingUtilities.safeLongToInt((mStartedRunTimestamp - (new Date().getTime())) / 1000);
        int distance = JnoggingUtilities.getDistanceFromLocation(mStartingPosition, mEndingPosition);
        int speedInKilometerPerHours =  distance / (timeSpentRunningInSeconds * 60);
        Date startedTime = new Date();
             startedTime.setTime(mStartedRunTimestamp);

        if (mRunDao != null) {
            mRunDao.insertRuns(
                    new Run(distance, speedInKilometerPerHours, startedTime, timeSpentRunningInSeconds)
            );
        }

        mView.changeToStopRunning();
    }

    private long getTimeRan(long base) {
        return System.currentTimeMillis() - base;
    }
}

