package fr.altoine.jnogging;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.altoine.jnogging.data.RunContract;
import fr.altoine.jnogging.data.RunDbHelper;
import fr.altoine.jnogging.utils.Constants;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityRecognitionListener {


    // UI Components ------------------------------------------------------------------------------

    private Button mStartButton;
    private Button mRestartButton;
    private Chronometer mRunChronometer;
    private ImageView mCurrentActivityImage;
    private TextView mNoInternetErrorTextView;


    // Constants ----------------------------------------------------------------------------------

    private final int STATE_IDLE = 0;
    private final int STATE_RUNNING = 1;

    private final String TAG = MainActivity.class.getSimpleName();
    private final String CHRONOMETER_KEY = "chronometer_key";
    private final String STATE_KEY = "state_key";


    // Miscellaneous ------------------------------------------------------------------------------

    private int mCurrentState;
    private SQLiteDatabase mDb;
    ActivityRecognitionService mActivityRecognitionService;
    private GoogleApiClient mApiClient;
    boolean mBoundToActivityRecognitionService = false;

    /**
     * Interface the system uses to monitor connection to a service
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ActivityRecognitionService.LocalBinder binder = (ActivityRecognitionService.LocalBinder) service;
            mActivityRecognitionService = binder.getService();
            mActivityRecognitionService.registerListener(MainActivity.this);
            mBoundToActivityRecognitionService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundToActivityRecognitionService = false;
        }
    };

    /**
     * Called by the service whenever the activity of the user has been determined
     * @param activity the current activity of the user (e.g: still, walking...)
     */
    @Override
    public void onActivityRecognized(String activity) {
        switch (activity) {
//            case "on_foot":
            case "walking":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentActivityImage.setImageDrawable(
                                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_walking, null)
                        );
                    }
                });
                break;
            case "running":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentActivityImage.setImageDrawable(
                                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_running, null)
                        );
                    }
                });
                break;
            case "still":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentActivityImage.setImageDrawable(
                                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_standing, null)
                        );
                    }
                });
                break;
            case "unknown":
            default:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentActivityImage.setImageDrawable(
                                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thinking, null)
                        );
                    }
                });
                break;
        }
    }

    // Activity lifecycle -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartButton = (Button) findViewById(R.id.btn_start);
        mStartButton.setOnClickListener(this);

        mRestartButton = (Button) findViewById(R.id.btn_restart);
        mRestartButton.setOnClickListener(this);

        mRunChronometer = (Chronometer) findViewById(R.id.chronometer);

        mCurrentActivityImage = (ImageView) findViewById(R.id.img_current_activity);

        mNoInternetErrorTextView = (TextView) findViewById(R.id.tv_no_internet);

        mDb = new RunDbHelper(this).getWritableDatabase();

        // TODO: set base produce errors on Chronometer widget
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CHRONOMETER_KEY)) {
                long base = savedInstanceState.getLong(CHRONOMETER_KEY, SystemClock.elapsedRealtime());
                mRunChronometer.setBase(base);
            }

            if (savedInstanceState.containsKey(STATE_KEY)) {
                mCurrentState = savedInstanceState.getInt(STATE_KEY, STATE_IDLE);
                if (mCurrentState == STATE_RUNNING)
                    startRunning(true);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mApiClient != null)
            mApiClient.disconnect();

        if (mBoundToActivityRecognitionService) {
            if (mActivityRecognitionService != null)
                mActivityRecognitionService.unregisterListener(MainActivity.this);

            unbindService(mServiceConnection);
            mBoundToActivityRecognitionService = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentState == STATE_RUNNING)
            outState.putLong(CHRONOMETER_KEY, getTimeRan());
        outState.putInt(STATE_KEY, mCurrentState);
    }


    /**
     * Handle user click
     * @param v view was clicked
     */
    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.btn_start:
                if (mCurrentState == STATE_IDLE) {
                    startRunning(false);
                } else {
                    stopRunning();
                }
                break;
            case R.id.btn_restart:
                mRunChronometer.stop();
                startRunning(false);
                break;
            default:
                break;
        }
    }

    private void stopRunning() {
        mRunChronometer.stop();

        mCurrentState = STATE_IDLE;
        mRestartButton.setVisibility(View.INVISIBLE);
        mStartButton.setText(getString(R.string.start_run));

        Toast.makeText(this, "Time spent running : " + String.valueOf(getTimeRan()) + "ms.", Toast.LENGTH_LONG).show();
//        Toast.makeText(this, "ID Run : " + String.valueOf(addNewRun()) + ". Time spent running : " + String.valueOf(getTimeRan()) + "ms.", Toast.LENGTH_LONG).show();
    }


    // TODO: tmp
    private long addNewRun() {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

        ContentValues contentValues = new ContentValues();
        contentValues.put(RunContract.RunsEntry.COLUMN_DISTANCE, 5);
        contentValues.put(RunContract.RunsEntry.COLUMN_START_TIME, dateFormat.format(new Date()));
        contentValues.put(RunContract.RunsEntry.COLUMN_TIME_SPENT_RUNNING, 30);
        contentValues.put(RunContract.RunsEntry.COLUMN_SPEED, 10);
        return mDb.insert(RunContract.RunsEntry.TABLE_NAME, null, contentValues);
    }


    private void startRunning(boolean resumeRunning) {
        if (!resumeRunning)
            mRunChronometer.setBase(SystemClock.elapsedRealtime());
        mRunChronometer.start();

        if (mRestartButton.getVisibility() == View.INVISIBLE)
            mRestartButton.setVisibility(View.VISIBLE);

        mCurrentState = STATE_RUNNING;
        mStartButton.setText(getString(R.string.stop_run));
    }


    private long getTimeRan() {
        return SystemClock.elapsedRealtime() - mRunChronometer.getBase();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        final int PENDING_ID = 434;

        Intent activityRecognitionIntent = new Intent(this, ActivityRecognitionService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, PENDING_ID, activityRecognitionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingResult<Status> pendingResult = ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, Constants.CHECK_USER_ACTIVITY_INTERVAL, pendingIntent);

        try {
            bindService(activityRecognitionIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        pendingResult.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Log.v(TAG, "Google API call successful !");
                    if (mNoInternetErrorTextView.getVisibility() == View.VISIBLE)
                    mNoInternetErrorTextView.setVisibility(View.INVISIBLE);
                }

                if (status.isInterrupted()) {
                    Log.v(TAG, "Google API call was interrupted !");
                    mCurrentActivityImage.setImageDrawable(
                            ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thinking, null)
                    );
                    mNoInternetErrorTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection suspended");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { Log.e(TAG, "Connection failed"); }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.run, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_history:
                Intent launchHistoryActivity = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(launchHistoryActivity);
                break;
            case R.id.action_settings:
                // TODO: implement settings action
                Toast.makeText(this, "TODO: implement settings action", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_help:
                // TODO: implement help action
                Toast.makeText(this, "TODO: implement help action", Toast.LENGTH_SHORT).show();
                // showHelp();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /* @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_first_run_key), false);

        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_first_run_key), Boolean.TRUE);
            edit.commit();
            showHelp();
        }
    }

    public void showHelp() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(mainAct, REQUEST_CODE);
    }

    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(
                context.getString(R.string.slide_1_african_story_books),
                context.getString(R.string.slide_1_african_story_books_subtitle),
                R.color.slide_1,
                R.drawable.tut_page_1_front,
                R.drawable.tut_page_1_background
        );

        TutorialItem tutorialItem2 = new TutorialItem(
                context.getString(R.string.slide_1_african_story_books),
                context.getString(R.string.slide_1_african_story_books_subtitle),
                R.color.slide_1, R.drawable.gif_drawable, true
        );

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);

        return tutorialItems;
    } */
}
