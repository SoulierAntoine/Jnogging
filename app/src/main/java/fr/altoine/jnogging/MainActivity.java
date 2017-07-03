package fr.altoine.jnogging;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private Button mStartButton;
    private Button mRestartButton;
    private Chronometer mRunChronometer;

    private final int PENDING_ID = 434;
    private final int CHECK_USER_ACTIVITY_INTERVAL = 5000;

    private final int STATE_IDLE = 0;
    private final int STATE_WALKING = 1;
    private final int STATE_RUNNING = 2;
    private int mCurrentState = STATE_IDLE;

    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartButton = (Button) findViewById(R.id.btn_start);
        mStartButton.setOnClickListener(this);

        mRestartButton = (Button) findViewById(R.id.btn_restart);
        mRestartButton.setOnClickListener(this);

        mRunChronometer = (Chronometer) findViewById(R.id.chronometer);
        mRunChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {// (number % 10)
//                mRunChronometer.setText(mRunChronometer.getText() + ":" + String.valueOf(milliseconds));
                long miliseconds = (SystemClock.elapsedRealtime() - mRunChronometer.getBase());
                Log.v(TAG, "Time spent running : " + String.valueOf(miliseconds));
//                Log.v(TAG, "Last digit : " + String.valueOf(miliseconds % 10));
            }
        });

//        mRunChronometer.setFormat(
//                getString(R.string.chronometer_format)
//        );

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.btn_start:
                if (mCurrentState == STATE_IDLE) {
                    startRunning();
                } else {
                    stopRunning();
                }
                break;
            case R.id.btn_restart:
                mRunChronometer.stop();
                startRunning();
                break;
            default:
                break;
        }
    }

    private void stopRunning() {
        mRunChronometer.stop();

        mCurrentState = STATE_IDLE;
        mRestartButton.setVisibility(View.INVISIBLE);
        mStartButton.setText(
                getString(R.string.start_run)
        );

        Toast.makeText(this, "Time spent running : " + String.valueOf(SystemClock.elapsedRealtime() - mRunChronometer.getBase()) + "ms.", Toast.LENGTH_LONG).show();
    }

    private void startRunning() {
        mRunChronometer.setBase(SystemClock.elapsedRealtime());
        mRunChronometer.start();

        if (mRunChronometer.getVisibility() == View.INVISIBLE)
            mRunChronometer.setVisibility(View.VISIBLE);
        if (mRestartButton.getVisibility() == View.INVISIBLE)
            mRestartButton.setVisibility(View.VISIBLE);

        mCurrentState = STATE_RUNNING;
        mStartButton.setText(
                getString(R.string.stop_run)
        );
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognitionService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, PENDING_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingResult<Status> pendingResult = ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, CHECK_USER_ACTIVITY_INTERVAL, pendingIntent);

        pendingResult.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess())
                    Log.v(TAG, "API call successful !");
                if (status.isInterrupted())
                    Log.v(TAG, "API call was interrupted !");
            }
        });
    }

    /* class ActivityRecognitionTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    } */

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed");
    }

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
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
