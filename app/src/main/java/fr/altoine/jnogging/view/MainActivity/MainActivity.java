package fr.altoine.jnogging.view.MainActivity;

import android.Manifest;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import fr.altoine.jnogging.R;
import fr.altoine.jnogging.model.JnoggingDatabase;
import fr.altoine.jnogging.presenter.ActivityRecognitionService;
import fr.altoine.jnogging.presenter.MainActivity.MainActivityPresenter;
import fr.altoine.jnogging.utils.Constants;
import fr.altoine.jnogging.view.GoogleApiErrorDialogFragment;
import fr.altoine.jnogging.view.HistoryActivity.HistoryActivity;
import fr.altoine.jnogging.view.IActivityRecognitionListener;
import fr.altoine.jnogging.view.IPermissionGrantedListener;
import fr.altoine.jnogging.view.PermissionExplanationDialogFragment;
import fr.altoine.jnogging.view.PermissionHandler;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        IActivityRecognitionListener,
        GoogleApiClient.OnConnectionFailedListener,
        IPermissionGrantedListener,
        IMainActivityView {



    // UI Components ------------------------------------------------------------------------------

    private Button mStartButton;
    private Button mStopButton;
    private Chronometer mRunChronometer;
    private ImageView mCurrentActivityImage;



    // Constants ----------------------------------------------------------------------------------

    private final String TAG = MainActivity.class.getSimpleName();

    // Keys for saved bundle instance
    private final String CHRONOMETER_STATE_KEY = "chronometer_key";
    private final String IS_RUNNING_STATE_KEY = "state_key";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";



    // Miscellaneous ------------------------------------------------------------------------------

    private MainActivityPresenter presenter = new MainActivityPresenter(this);

    private ActivityRecognitionService mActivityRecognitionService;
    private GoogleApiClient mApiClient;
    private boolean mBoundToActivityRecognitionService = false;

    private PermissionHandler mPermissionHandler = new PermissionHandler(this, this);

    // Whether the user has started the chronometer or not
    private boolean IS_CHRONOMETER_COUNTING;

    // Bool to track whether the app is already resolving a google API connection fail error or not
    private boolean mResolvingError = false;



    // Service Connection for binding to service --------------------------------------------------

    /**
     * Interface used by the system to monitor connection to a service
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



    // Activity Recognition listener --------------------------------------------------------------

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



    // Location listener --------------------------------------------------------------------------

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // TODO: now considering that a run is a start and an end, and that the start begins
            // when the chronometer is counting from 0 and end when the user click on stop
            if (location.getAccuracy() >= 68.0) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                presenter.setPosition(latitude, longitude);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };



    // Permission listener ------------------------------------------------------------------------

    @Override
    public void onPermissionGranted() {
        if (mPermissionHandler.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                mPermissionHandler.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }

    @Override
    public void onPermissionDenied() {}

    @Override
    public void onShowRequestPermissionRationale() {
        Bundle permissionRationaleDialog = new Bundle();
        permissionRationaleDialog.putString("title", "Foo");
        permissionRationaleDialog.putString("message", "Bar");
        permissionRationaleDialog.putString("positiveButton", "FooBar");

        // TODO: use setArguments instead
        DialogFragment explanationDialog = PermissionExplanationDialogFragment.newInstance(permissionRationaleDialog);
        explanationDialog.show(this.getFragmentManager(), "tag");
    }



    // Activity lifecycle -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IS_CHRONOMETER_COUNTING = false;
        mStartButton = (Button) findViewById(R.id.btn_start);
        mStartButton.setOnClickListener(this);

        mStopButton = (Button) findViewById(R.id.btn_stop);
        mStopButton.setOnClickListener(this);

        mRunChronometer = (Chronometer) findViewById(R.id.chronometer);

        mCurrentActivityImage = (ImageView) findViewById(R.id.img_current_activity);
        // MainActivityPresenter needs the database to get the DAO interfaces
//        presenter.setDao(JnoggingDatabase.getInstance(getBaseContext()));

        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();

        mPermissionHandler.handle(
                new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                new int[] {
                        Constants.PermissionsRequestCode.PERMISSION_ACCESS_FINE_LOCATION,
                        Constants.PermissionsRequestCode.PERMISSION_ACCESS_COARSE_LOCATION
                }
        );

        // TODO: Set base produce errors on Chronometer widget.
        if (savedInstanceState != null) {
            mResolvingError = savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

            if (savedInstanceState.containsKey(CHRONOMETER_STATE_KEY)) {
                long base = savedInstanceState.getLong(CHRONOMETER_STATE_KEY, SystemClock.elapsedRealtime());
                mRunChronometer.setBase(base);
            }

            if (savedInstanceState.containsKey(IS_RUNNING_STATE_KEY)) {
                IS_CHRONOMETER_COUNTING = savedInstanceState.getBoolean(IS_RUNNING_STATE_KEY, false);
                if (IS_CHRONOMETER_COUNTING)
                    presenter.startRunning(true);
            }
        } else {
            mStopButton.setEnabled(false);
            mStopButton.setAlpha(.5f);
        }

        presenter.onCreate();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Google Api Clients is connected after the activity calls onStart()
        startActivityRecognition();

        presenter.onResume();
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

        presenter.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        JnoggingDatabase.destroyInstance();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* if (mCurrentState == STATE_RUNNING)
            outState.putLong(CHRONOMETER_STATE_KEY, getTimeRan()); */
        outState.putBoolean(IS_RUNNING_STATE_KEY, IS_CHRONOMETER_COUNTING);
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
                if (!IS_CHRONOMETER_COUNTING) {
                    presenter.startRunning(false);
//                    startRunning(false);
                } else {
                    if (mPermissionHandler.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                            mPermissionHandler.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location endingLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        presenter.setEndingPosition(endingLocation.getLatitude(), endingLocation.getLongitude());
                    }
                    presenter.stopRunning(mRunChronometer.getBase());
//                    stopRunning();
                }
                break;
            case R.id.btn_stop:
                mRunChronometer.stop();
                presenter.stopRunning(mRunChronometer.getBase());
//                stopRunning();
                break;
            default:
                break;
        }
    }


    // App Logic ------------------------------------------------------------------------------

    @Override
    public void changeToStopRunning() {
        mRunChronometer.stop();

        mStopButton.setEnabled(false);
        mStopButton.setAlpha(.5f);
        mStartButton.setText(getString(R.string.start_run));

        IS_CHRONOMETER_COUNTING = false;
    }

    @Override
    public void changeToRunning(boolean resumeRunning) {
        if (!resumeRunning)
            mRunChronometer.setBase(SystemClock.elapsedRealtime());
        mRunChronometer.start();

        mStopButton.setEnabled(true);
        mStopButton.setAlpha(1);

        mStartButton.setText(getString(R.string.pause_run));
        IS_CHRONOMETER_COUNTING = true;
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
                        /* if (mNoInternetErrorTextView.getVisibility() == View.VISIBLE)
                            mNoInternetErrorTextView.setVisibility(View.INVISIBLE); */
                    }

                    if (status.isInterrupted()) {
                        Log.e(TAG, "Google API call was interrupted !");
                        mCurrentActivityImage.setImageDrawable(
                                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thinking, null)
                        );
                        // mNoInternetErrorTextView.setVisibility(View.VISIBLE);
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
