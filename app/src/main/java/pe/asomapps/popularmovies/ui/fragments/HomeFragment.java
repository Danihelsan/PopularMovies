package pe.asomapps.popularmovies.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.DefaultCallback;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.data.api.MoviesApi;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.responses.DiscoverMoviesResponse;
import pe.asomapps.popularmovies.ui.activities.DetailActivity;
import pe.asomapps.popularmovies.ui.adapters.HomeGridAdapter;
import pe.asomapps.popularmovies.ui.interfaces.FragmentInteractor;
import pe.asomapps.popularmovies.ui.interfaces.OnLoadMoreListener;
import pe.asomapps.popularmovies.ui.utils.Tag;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Danihelsan
 */
public class HomeFragment extends BaseFragment implements OnLoadMoreListener, HomeGridAdapter.OnLoadMoreItemClicked, HomeGridAdapter.MovieClickListener{
    public static final Tag tag = Tag.HOME;

    private final String SAVE_NEXT_PAGE = "next_page";
    private final String SAVE_ITEM_LIST = "item_list";

    @Bind(R.id.moviesRV) RecyclerView moviesRV;

    private int nextPageToLoad;

    private List items;
    private FragmentInteractor interactor;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactor = (getActivity() instanceof FragmentInteractor)? (FragmentInteractor) getActivity() :null;

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

        HomeGridAdapter adapter = new HomeGridAdapter(moviesRV, new ArrayList(), this, this, this, interactor);
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
        super.onSaveInstanceState(outState);
    }

    private class CallbackLoadMovies extends DefaultCallback<DiscoverMoviesResponse> {
        @Override
        public void onResponse(Response<DiscoverMoviesResponse> response, Retrofit retrofit) {
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

    @Override
    public void onLoadMore() {
        loadMoreMovies();
    }

    private void loadMoreMovies() {
        Call call = apiService.loadMovies(MoviesApi.SortOption.POP_DESC.getValue(), nextPageToLoad);
        call.enqueue(new CallbackLoadMovies());

        HomeGridAdapter adapter = (HomeGridAdapter)moviesRV.getAdapter();
        adapter.setLoading(true);
    }

    @Override
    public boolean onMovieClicked(Movie movie, View[] sharedViews) {
        if (!interactor.isTablet()){
            Bundle bundle = new Bundle();
            bundle.putParcelable(DetailFragment.KEY_MOVIE,movie);
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtras(bundle);
            openNewScreen(intent, sharedViews);
        } else{
            Fragment fragment = DetailFragment.newInstance(movie);
            interactor.loadDetail(fragment,sharedViews);
        }
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
