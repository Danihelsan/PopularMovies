package pe.asomapps.popularmovies.ui.interfaces;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Danihelsan
 */
public interface FragmentInteractor {
    boolean isTablet();
    boolean isLandscape();
    void loadDetail(Fragment fragment, View[] sharedViews);
}
