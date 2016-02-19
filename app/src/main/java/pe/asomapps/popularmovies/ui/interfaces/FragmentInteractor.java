package pe.asomapps.popularmovies.ui.interfaces;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import pe.asomapps.popularmovies.model.Movie;

/**
 * Created by Danihelsan
 */
public interface FragmentInteractor {
    boolean isTablet();
    boolean isLandscape();
    boolean loadDetail(FragmentTransaction fragmentTransaction, Fragment fragment, View[] sharedViews);
    void updateFavorited(Movie movie);
    void updateSpinner();
    void setShareIntent(Movie movie);
}
