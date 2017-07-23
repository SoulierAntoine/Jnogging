package fr.altoine.jnogging.presenter;

/**
 * Created by Antoine on 23/07/2017.
 */

public interface IPresenter {
    void onCreate();
    void onStart();
    void onResume();
    void onPause();
    void onStop();
    void onDestroy();
}
