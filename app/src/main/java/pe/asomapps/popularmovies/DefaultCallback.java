package pe.asomapps.popularmovies;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Danihelsan
 */
public class DefaultCallback<T> implements Callback<T> {
    @Override
    public void onResponse(Response<T> response, Retrofit retrofit) {
    }

    @Override
    public void onFailure(Throwable t) {
    }
}
