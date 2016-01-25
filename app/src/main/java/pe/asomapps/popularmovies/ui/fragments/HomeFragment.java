package pe.asomapps.popularmovies.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.DefaultCallback;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.data.api.MoviesApi;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.responses.DiscoverMoviesResponse;
import pe.asomapps.popularmovies.ui.OnLoadMoreListener;
import pe.asomapps.popularmovies.ui.activities.DetailActivity;
import pe.asomapps.popularmovies.ui.adapters.HomeGridAdapter;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Danihelsan
 */
public class HomeFragment extends BaseFragment implements OnLoadMoreListener, HomeGridAdapter.OnLoadMoreItemClicked, HomeGridAdapter.MovieClickListener{
    @Bind(R.id.homeGrid) RecyclerView homeGrid;

    private int nextPageToLoad = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);
        ButterKnife.bind(this,rootView);

        HomeGridAdapter adapter = new HomeGridAdapter(homeGrid, new ArrayList<Movie>(), this, this, this);
        homeGrid.setAdapter(adapter);

        loadMoreMovies();
        return rootView;
    }


    private class CallbackLoadMovies extends DefaultCallback<DiscoverMoviesResponse> {
        @Override
        public void onResponse(Response<DiscoverMoviesResponse> response, Retrofit retrofit) {
            nextPageToLoad = response.body().getPage()+1;
            HomeGridAdapter adapter = (HomeGridAdapter)homeGrid.getAdapter();
            adapter.removeItem(null);
            adapter.addItems(response.body().getResults());
        }

        @Override
        public void onFailure(Throwable t) {
            super.onFailure(t);
            HomeGridAdapter adapter = (HomeGridAdapter)homeGrid.getAdapter();
            adapter.enableLoadingMore();
        }
    }

    @Override
    public void onLoadMore() {
        loadMoreMovies();
    }

    private void loadMoreMovies() {
        Call call = apiService.loadMovies(MoviesApi.SortOption.POP_DESC.getValue(), nextPageToLoad);
        call.enqueue(new CallbackLoadMovies());

        HomeGridAdapter adapter = (HomeGridAdapter)homeGrid.getAdapter();
        adapter.setLoading(true);
    }

    @Override
    public boolean onMovieClicked(Movie movie, View[] sharedViews) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailFragment.KEY_MOVIE_CONTENT,movie);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtras(bundle);
        openNewScreen(intent, sharedViews);
        return true;
    }

    @Override
    public boolean onAddToFavoritesClicked(Movie movie) {
        return false;
    }

    @Override
    public boolean onLoadMoreItemClicked() {
        loadMoreMovies();
        return true;
    }
}
