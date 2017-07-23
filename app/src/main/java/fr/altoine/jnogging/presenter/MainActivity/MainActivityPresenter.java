package fr.altoine.jnogging.presenter.MainActivity;

import fr.altoine.jnogging.presenter.IPresenter;
import fr.altoine.jnogging.view.MainActivity.IMainActivityView;

/**
 * Created by Antoine on 23/07/2017.
 */

public class MainActivityPresenter implements IPresenter {
    private IMainActivityView view;

    @Override
    public void onCreate() {}

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
        this.view = view;
    }


}
