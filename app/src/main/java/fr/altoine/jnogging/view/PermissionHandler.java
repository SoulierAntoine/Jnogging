package fr.altoine.jnogging.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import fr.altoine.jnogging.utils.Constants;

/**
 * Created by soulierantoine on 25/07/2017.
 */

public class PermissionHandler implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Activity mCallingActivity;
    private IPermissionGrantedListener mPermissionGrantedListener;


    public PermissionHandler(Activity callingActivity, IPermissionGrantedListener permissionGrantedListener) {
        this.mCallingActivity = callingActivity;
        this.mPermissionGrantedListener = permissionGrantedListener;
    }

    public void handle(String[] permissions, int[] requestCodes) {
        if (permissions.length != requestCodes.length)
            throw new IllegalArgumentException("Permissions array and request codes array must be of same length");

        for (int i = 0; i < permissions.length; ++i) {
            if (!checkPermission(permissions[i]))
                requestPermission(permissions[i], requestCodes[i]);
        }
    }

    public boolean checkPermission(String permission) {
        return (ContextCompat.checkSelfPermission(mCallingActivity, permission) != PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(
                mCallingActivity,
                new String[]{ permission },
                requestCode
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PermissionsRequestCode.PERMISSION_ACCESS_COARSE_LOCATION:
            case Constants.PermissionsRequestCode.PERMISSION_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mPermissionGrantedListener.onPermissionGranted();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(mCallingActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            mPermissionGrantedListener.onShowRequestPermissionRationale();
                        } else {
                            mPermissionGrantedListener.onPermissionDenied();
                        }
                    }
                }
                break;
            // Add permissions as needed...
        }
    }
}
