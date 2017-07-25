package fr.altoine.jnogging.view;

/**
 * Created by soulierantoine on 25/07/2017.
 */

public interface IPermissionGrantedListener {
    void onPermissionGranted();
    void onPermissionDenied();
    void onShowRequestPermissionRationale();
}
