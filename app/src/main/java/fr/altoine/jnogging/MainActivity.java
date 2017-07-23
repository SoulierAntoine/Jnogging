package fr.altoine.jnogging;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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

import fr.altoine.jnogging.model.data.RunContract;
import fr.altoine.jnogging.model.data.RunDbHelper;
import fr.altoine.jnogging.utils.Constants;
import fr.altoine.jnogging.view.GoogleApiErrorDialogFragment;
import fr.altoine.jnogging.view.IActivityRecognitionListener;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        IActivityRecognitionListener,
        GoogleApiClient.OnConnectionFailedListener {


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

    // Keys for saved bundle instance
    private final String STATE_CHRONOMETER = "chronometer_key";
    private final String STATE_KEY = "state_key";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";


    // Miscellaneous ------------------------------------------------------------------------------

    private SQLiteDatabase mDb;
    private ActivityRecognitionService mActivityRecognitionService;
    private GoogleApiClient mApiClient;
    boolean mBoundToActivityRecognitionService = false;

    // Whether the user has started the chronometer or not
    private int mCurrentState;

    // Bool to track whether the app is already resolving an error cause by a fail google API connection
    private boolean mResolvingError = false;


    // Service Connection for binding to service and service callbacks ----------------------------

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

    @Override
    public void onActivityRecognized(String activity) {
        switch (activity) {
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

        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();

        // TODO: Set base produce errors on Chronometer widget.
        if (savedInstanceState != null) {
            mResolvingError = savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

            if (savedInstanceState.containsKey(STATE_CHRONOMETER)) {
                long base = savedInstanceState.getLong(STATE_CHRONOMETER, SystemClock.elapsedRealtime());
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
    protected void onResume() {
        super.onResume();
        // Google Api Clients is connected after the activity calls onStart()
        startActivityRecognition();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mBoundToActivityRecognitionService) {
            if (mActivityRecognitionService != null)
                mActivityRecognitionService.unregisterListener(this);

            unbindService(mServiceConnection);
            mBoundToActivityRecognitionService = false;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentState == STATE_RUNNING)
            outState.putLong(STATE_CHRONOMETER, getTimeRan());
        outState.putInt(STATE_KEY, mCurrentState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
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


    // App Logic ------------------------------------------------------------------------------

    private void stopRunning() {
        mRunChronometer.stop();

        mCurrentState = STATE_IDLE;
        mRestartButton.setVisibility(View.INVISIBLE);
        mStartButton.setText(getString(R.string.start_run));

        Toast.makeText(this, "ID Run : " + String.valueOf(addNewRun()) + ". Time spent running : " + String.valueOf(getTimeRan()) + "ms.", Toast.LENGTH_LONG).show();
    }


    // TODO: TMP. Get the distance and then calculate the speed
    private long addNewRun() {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

        ContentValues contentValues = new ContentValues();
        contentValues.put(RunContract.RunsEntry.COLUMN_DISTANCE, 5);
        contentValues.put(RunContract.RunsEntry.COLUMN_START_TIME, dateFormat.format(new Date()));
        contentValues.put(RunContract.RunsEntry.COLUMN_TIME_SPENT_RUNNING, ((getTimeRan() / 1000) / 60));
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


    public void startActivityRecognition() {
        if (mApiClient.isConnected()) {
            final int PENDING_ID = 434;

            Intent activityRecognitionIntent = new Intent(this, ActivityRecognitionService.class);

            try {
                bindService(activityRecognitionIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            PendingIntent pendingIntent = PendingIntent.getService(this, PENDING_ID, activityRecognitionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingResult<Status> pendingResult = ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, Constants.CHECK_USER_ACTIVITY_INTERVAL, pendingIntent);
            pendingResult.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.v(TAG, "Google API call successful !");
                        if (mNoInternetErrorTextView.getVisibility() == View.VISIBLE)
                            mNoInternetErrorTextView.setVisibility(View.INVISIBLE);
                    }

                    if (status.isInterrupted()) {
                        Log.e(TAG, "Google API call was interrupted !");
                        mCurrentActivityImage.setImageDrawable(
                                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thinking, null)
                        );
                        mNoInternetErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


    // Handle Google Api error --------------------------------------------------------------------

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed");

        // Already attempting to resolve an error.
        if (mResolvingError) {
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                // Will try to resolve the error
                connectionResult.startResolutionForResult(this, Constants.Keys.REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mApiClient.connect();
            }
        } else {
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode) {
        Bundle args = new Bundle();
        args.putInt(Constants.Keys.GOOGLE_API_DIALOG_ERROR, errorCode);

        GoogleApiErrorDialogFragment dialogFragment = new GoogleApiErrorDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "error");
    }

    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /**
     * Called after the user dismisses the GoogleApiErrorDialogFragment
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.Keys.REQUEST_RESOLVE_ERROR:
                mResolvingError = false;
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
                        mApiClient.connect();
                    }
                }
            break;
        }
    }


    // Handle Menu -------------------------------------------------------------------------------

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
                // TODO: Implement settings action.
                Toast.makeText(this, "TODO: implement settings action", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_help:
                // TODO: Implement help action.
                Toast.makeText(this, "TODO: implement help action", Toast.LENGTH_SHORT).show();
                // showHelp();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
