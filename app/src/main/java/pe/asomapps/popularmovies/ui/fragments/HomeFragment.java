package pe.asomapps.popularmovies.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.DefaultCallback;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.responses.DiscoverMoviesResponse;
import pe.asomapps.popularmovies.ui.activities.DetailActivity;
import pe.asomapps.popularmovies.ui.activities.HomeActivity;
import pe.asomapps.popularmovies.ui.adapters.HomeGridAdapter;
import pe.asomapps.popularmovies.ui.interfaces.FragmentInteractor;
import pe.asomapps.popularmovies.ui.interfaces.MovieClickListener;
import pe.asomapps.popularmovies.ui.interfaces.OnLoadMoreListener;
import pe.asomapps.popularmovies.ui.utils.Sort;
import pe.asomapps.popularmovies.ui.utils.Tag;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Danihelsan
 */
public class HomeFragment extends BaseFragment implements OnLoadMoreListener, MovieClickListener {
    public static final Tag tag = Tag.HOME;

    private static final String KEY_SORTOPTION = "sort_option";

    private final String SAVE_NEXT_PAGE = "next_page";
    private final String SAVE_ITEM_LIST = "item_list";
    private final String SAVE_SORTOPTION = KEY_SORTOPTION;

    @Bind(R.id.moviesRV) RecyclerView moviesRV;

    private ShareActionProvider shareProvider;
    private MenuItem shareItem;

    private int nextPageToLoad;

    private String currentSort;
    private List items;
    private FragmentInteractor interactor;

    public static HomeFragment newInstance(String sortOption) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SORTOPTION, sortOption);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactor = (getActivity() instanceof FragmentInteractor)? (FragmentInteractor) getActivity() :null;

        currentSort = Sort.POPULARITY.toString();
        if (getArguments()!=null && getArguments().containsKey(KEY_SORTOPTION)){
            currentSort = getArguments().getString(KEY_SORTOPTION);
        }

        if(savedInstanceState == null){
            nextPageToLoad = 1;
            items = new ArrayList();
        } else {
            nextPageToLoad = savedInstanceState.getInt(SAVE_NEXT_PAGE,1);
            if (savedInstanceState.containsKey(SAVE_ITEM_LIST)) {
                items = savedInstanceState.getParcelableArrayList(SAVE_ITEM_LIST);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);
        ButterKnife.bind(this,rootView);

        if (interactor.isTablet()){
            setHasOptionsMenu(true);
        }

        HomeGridAdapter adapter = new HomeGridAdapter(moviesRV, new ArrayList(), this, this, interactor, currentSort!=null);
        moviesRV.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (items.size()==0){
            loadMoreMovies();
        } else{
            if (moviesRV.getAdapter() instanceof HomeGridAdapter){
                HomeGridAdapter adapter = (HomeGridAdapter)moviesRV.getAdapter();
                adapter.addItems(items);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (moviesRV.getAdapter() instanceof HomeGridAdapter){
            List items = ((HomeGridAdapter)moviesRV.getAdapter()).getItems();
            outState.putParcelableArrayList(SAVE_ITEM_LIST, (ArrayList<? extends Parcelable>) items);
        }
        outState.putInt(SAVE_NEXT_PAGE,nextPageToLoad);
        outState.putString(SAVE_SORTOPTION,currentSort);
        super.onSaveInstanceState(outState);
    }

    private class CallbackLoadMovies extends DefaultCallback<DiscoverMoviesResponse> {
        @Override
        public void onResponse(Response<DiscoverMoviesResponse> response, Retrofit retrofit) {
            updateFavorites(response.body().getResults());
            nextPageToLoad = response.body().getPage()+1;
            if (moviesRV.getAdapter() instanceof HomeGridAdapter){
                HomeGridAdapter adapter = (HomeGridAdapter)moviesRV.getAdapter();
                adapter.removeItem(null);
                adapter.addItems(response.body().getResults());
            }
        }

        @Override
        public void onFailure(Throwable t) {
            super.onFailure(t);
            HomeGridAdapter adapter = (HomeGridAdapter)moviesRV.getAdapter();
            adapter.enableLoadingMore();
        }
    }

    private void updateFavorites(List<Movie> results) {
        for (Movie result: results){
            if (dbHelper.isMovieFavorited(result.getId())){
                result.setFavorited(true);
            }
        }
    }

    private void loadMoreMovies() {
        if (currentSort==null ){
            if (items.size()==0){
                loadFavoriteMovies();
            }
        } else {
            //GET SERVICE MOVIES
            Call call = apiService.loadMovies(currentSort, nextPageToLoad);
            call.enqueue(new CallbackLoadMovies());

            HomeGridAdapter adapter = (HomeGridAdapter)moviesRV.getAdapter();
            adapter.setLoading(true);
        }
    }

    private void loadFavoriteMovies() {
        if (moviesRV.getAdapter() instanceof HomeGridAdapter){
            List<Movie> movies = dbHelper.getFavoritedMovies();
            HomeGridAdapter adapter = (HomeGridAdapter)moviesRV.getAdapter();
            adapter.removeItem(null);
            adapter.addItems(movies);
        }
    }

    @Override
    public boolean onMovieClicked(int position, Movie movie, View[] sharedViews) {
        if (!interactor.isTablet()){
            Bundle bundle = new Bundle();
            bundle.putParcelable(DetailFragment.KEY_MOVIE,movie);
            bundle.putInt(DetailFragment.KEY_POSITION,position);
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtras(bundle);
            openNewScreen(intent, HomeActivity.CODE_DETAIL, sharedViews);
        } else{
            Fragment fragment = DetailFragment.newInstance(movie);
            interactor.loadDetail(null, fragment,sharedViews);
        }
        return true;
    }

    @Override
    public boolean onFavoritedClicked(int position, Movie movie) {
        boolean dbUpdated = false;
        boolean isRemoved = false;
        if (!dbHelper.isMovieFavorited(movie.getId())) {
            movie.setFavorited(true);
            if (dbHelper.insertMovieToFavorites(movie) > 0) {
                dbUpdated = true;
            }
        } else if (dbHelper.deleteMovieFavorited(movie.getId()) > 0) {
            movie.setFavorited(false);
            dbUpdated = true;
            isRemoved = true;
        }

        if (dbUpdated){
            updateFavorited(position,!isRemoved);

            if (!interactor.isTablet()){
                interactor.updateFavorited(movie);
            }
        }
        return false;
    }

    public void updateFavorited(int position, boolean favorited) {
        if (!favorited && currentSort==null){
            ((HomeGridAdapter)moviesRV.getAdapter()).removeItem(position);
        } else {
            ((Movie)((HomeGridAdapter)moviesRV.getAdapter()).getItems().get(position)).setFavorited(favorited);
            moviesRV.getAdapter().notifyItemChanged(position);
        }
        interactor.updateSpinner();
    }

    @Override
    public void onLoadMore() {
        if (currentSort!=null){
            loadMoreMovies();
        }
    }

    @Override
    public boolean onLoadMoreItemClicked() {
        loadMoreMovies();
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        shareItem = menu.findItem(R.id.shareMenu);
        shareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        super.onPrepareOptionsMenu(menu);
    }

    public void setShareIntent(Movie movie) {
        setShareIntent(movie,shareItem,shareProvider);
    }
}
