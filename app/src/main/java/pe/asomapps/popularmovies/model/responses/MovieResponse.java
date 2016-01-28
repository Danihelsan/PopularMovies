package pe.asomapps.popularmovies.model.responses;

import android.annotation.SuppressLint;
import android.os.Parcel;

import pe.asomapps.popularmovies.model.Movie;

/**
 * Created by Danihelsan
 */
@SuppressLint("ParcelCreator")
public class MovieResponse extends Movie {

    protected MovieResponse(Parcel in) {
        super(in);
    }
}
