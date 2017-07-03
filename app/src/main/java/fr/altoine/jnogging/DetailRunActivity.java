package fr.altoine.jnogging;

import android.Manifest;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import fr.altoine.jnogging.utils.JnoggingUtilities;

public class DetailRunActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionExplanationDialogFragment.PermissionExplanationDialogFragmentListener,
        OnMapReadyCallback {

    private final static String TAG = DetailRunActivity.class.getSimpleName();
    // private static boolean mMapReady = false;
    private static GoogleMap mMap;
    private final static int PERMISSION_ACCESS_FINE_LOCATION = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_run);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.f_map);
        mapFragment.getMapAsync(this);

        handlePermission();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // TODO: display two markers (start and arrival) at end of run
        // TODO: use google directions api to draw an itinerary
        // mMapReady = true;
        mMap = googleMap;
        mMap.setMinZoomPreference(11.0f);
        mMap.setMaxZoomPreference(13.0f);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng paris = new LatLng(48.8534, 2.3488);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(paris));
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.v(TAG, "Clicked");
    }


    private void handlePermission() {
        if (!JnoggingUtilities.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestFineLocationPermission();
        }
    }


    private void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                PERMISSION_ACCESS_FINE_LOCATION
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        try {
                            mMap.setMyLocationEnabled(true);
                        } catch (SecurityException e) {
                            // Something went awfully wrong
                            e.printStackTrace();
                        }
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            DialogFragment explanationDialog = new PermissionExplanationDialogFragment();
                            explanationDialog.show(getFragmentManager(), "tag");
                        }
                        // TODO: disable google map after a run in else statement
                    }
                }
                break;
            default:
                break;
        }
    }
}
