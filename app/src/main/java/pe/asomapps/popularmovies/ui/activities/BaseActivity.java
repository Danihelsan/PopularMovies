package pe.asomapps.popularmovies.ui.activities;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.ui.interfaces.FragmentInteractor;

/**
 * Created by Danihelsan
 */
public abstract class BaseActivity extends AppCompatActivity implements FragmentInteractor {
    @Override
    public boolean isTablet() {
        return false;
    }

    @Override
    public boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public boolean loadDetail(FragmentTransaction fragmentTransaction, Fragment fragment, View[] sharedViews) {
        return true;
    }

    @Override
    public void updateFavorited(Movie movie) {}

    @Override
    public void updateSpinner() {}

    @Override
    public void setShareIntent(Movie movie) {
    }
}
