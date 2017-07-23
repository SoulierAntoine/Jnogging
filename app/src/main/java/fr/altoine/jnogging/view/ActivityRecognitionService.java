package fr.altoine.jnogging.view;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soulierantoine on 29/06/7517.
 */

public class ActivityRecognitionService extends IntentService {
    public ActivityRecognitionService() { super("ActivityRecognitionService"); }
    private final IBinder mBinder = new LocalBinder();
    private List<IActivityRecognitionListener> mActivityRecognitionListeners = new ArrayList<>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /**
     * Used by the client (activity or other component) to get the service
     */
    public class LocalBinder extends Binder {
        public ActivityRecognitionService getService() {
            return ActivityRecognitionService.this;
        }
    }


    /**
     * Register listener that'll be called when the user activity is recognized
     * @param listener that'll have to implement a callback method
     */
    public void registerListener(IActivityRecognitionListener listener) {
        mActivityRecognitionListeners.add(listener);
    }

    public void unregisterListener(IActivityRecognitionListener listener) {
        mActivityRecognitionListeners.remove(listener);
    }

    private void sendRecognizedActivity(String activity) {
        for (int i = 0; i < mActivityRecognitionListeners.size(); ++i) {
            mActivityRecognitionListeners.get(i).onActivityRecognized(activity);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for (DetectedActivity activity : probableActivities ) {
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.v("in_vehicle", String.valueOf(activity.getConfidence()));

                    if (activity.getConfidence() >= 75)
                        sendRecognizedActivity("in_vehicle");
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.v("on_bicycle", String.valueOf(activity.getConfidence()));

                    if (activity.getConfidence() >= 75)
                        sendRecognizedActivity("on_bicycle");
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.v("running", String.valueOf(activity.getConfidence()));

                    if (activity.getConfidence() >= 75)
                        sendRecognizedActivity("running");
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.v("still", String.valueOf(activity.getConfidence()));

                    if (activity.getConfidence() >= 75)
                        sendRecognizedActivity("still");
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.v("tilting", String.valueOf(activity.getConfidence()));

                    if (activity.getConfidence() >= 75)
                        sendRecognizedActivity("tilting");
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.v("on_foot", String.valueOf(activity.getConfidence()));

                    if (activity.getConfidence() >= 75)
                        sendRecognizedActivity("on_foot");
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.v("walking", String.valueOf(activity.getConfidence()));

                    if (activity.getConfidence() >= 75)
                        sendRecognizedActivity("walking");
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.v("unknown", String.valueOf(activity.getConfidence()));

                    if (activity.getConfidence() >= 75)
                        sendRecognizedActivity("unknown");
                    break;
                }
            }
        }
    }
}
