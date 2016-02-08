package pe.asomapps.popularmovies.ui.interfaces;

import android.view.View;

import pe.asomapps.popularmovies.model.Movie;

/**
 * Created by Danihelsan
 */
public interface MovieClickListener {
    boolean onMovieClicked(Movie movie, View... views);
    boolean onAddToFavoritesClicked(Movie movie);
}