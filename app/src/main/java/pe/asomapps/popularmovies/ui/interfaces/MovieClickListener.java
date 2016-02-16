package pe.asomapps.popularmovies.ui.interfaces;

import android.view.View;

import pe.asomapps.popularmovies.model.Movie;

/**
 * Created by Danihelsan
 */
public interface MovieClickListener {
    boolean onMovieClicked(int position, Movie movie, View... views);
    boolean onFavoritedClicked(int position, Movie movie);
}