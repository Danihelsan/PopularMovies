package pe.asomapps.popularmovies.ui.interfaces;

import android.support.v4.app.Fragment;
import android.view.View;

import pe.asomapps.popularmovies.model.Movie;

/**
 * Created by Danihelsan
 */
public interface FragmentInteractor {
    boolean isTablet();
    boolean isLandscape();
    void loadDetail(Fragment fragment, View[] sharedViews);
    void updateFavorited(Movie movie);
    void updateSpinner();
}
