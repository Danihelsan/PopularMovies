package pe.asomapps.popularmovies.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.github.florent37.glidepalette.GlidePalette;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.DefaultCallback;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.data.api.MoviesApi;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.Review;
import pe.asomapps.popularmovies.model.Video;
import pe.asomapps.popularmovies.model.responses.MovieResponse;
import pe.asomapps.popularmovies.model.responses.ReviewsResponse;
import pe.asomapps.popularmovies.ui.adapters.ReviewsListAdapter;
import pe.asomapps.popularmovies.ui.adapters.VideoListAdapter;
import pe.asomapps.popularmovies.ui.interfaces.FragmentInteractor;
import pe.asomapps.popularmovies.ui.interfaces.OnLoadMoreListener;
import pe.asomapps.popularmovies.ui.utils.Tag;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Danihelsan
 */
public class DetailFragment extends BaseFragment implements VideoListAdapter.VideoClickListener, OnLoadMoreListener{
    public static final Tag tag = Tag.DETAIL;
    private final String SAVE_MOVIE = "movie";
    private final String SAVE_NEXT_PAGE = "next_page";
    private final String SAVE_ITEM_LIST = "item_list";

    private final String FORMAT_RATING = "#.#";
    public static final String KEY_MOVIE = "movie_";
    public static final String KEY_POSTER = "poster_";
    public static final String KEY_FAVORITE = "favorite_";

    private ShareActionProvider shareProvider;
    private MenuItem shareItem;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.year) TextView year;
    @Bind(R.id.time) TextView time;
    @Bind(R.id.rating) TextView rating;
    @Bind(R.id.description) TextView description;

    @Bind(R.id.poster) ImageView poster;
    @Bind(R.id.videosRV) RecyclerView videosRV;
    @Bind(R.id.reviewsRV) RecyclerView reviewsRV;

    @Bind(R.id.detailLeftContainer) FrameLayout detailLeftContainer;
    @Bind(R.id.separator) View separator;

    private FloatingActionButton favoriteAction;

    private int nextPageToLoad;
    private List items;

    private Movie movie;
    private FragmentInteractor interactor;

    public static DetailFragment newInstance() {
        Bundle args = new Bundle();

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DetailFragment newInstance(Movie movie) {
        DetailFragment fragment = newInstance();
        fragment.getArguments().putParcelable(KEY_MOVIE,movie);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactor = (getActivity() instanceof FragmentInteractor)? (FragmentInteractor) getActivity() :null;

        if(savedInstanceState == null){
            nextPageToLoad = 1;
            items = new ArrayList();
            if (getArguments()!=null && getArguments().containsKey(KEY_MOVIE)){
                movie = getArguments().getParcelable(KEY_MOVIE);
            }
        } else {
            nextPageToLoad = savedInstanceState.getInt(SAVE_NEXT_PAGE,1);
            if (savedInstanceState.containsKey(SAVE_ITEM_LIST)) {
                items = savedInstanceState.getParcelableArrayList(SAVE_ITEM_LIST);
            }
            if (savedInstanceState.containsKey(SAVE_MOVIE)) {
                movie = savedInstanceState.getParcelable(SAVE_MOVIE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView;
        if (!interactor.isTablet()){
            setHasOptionsMenu(true);
            favoriteAction = (FloatingActionButton) getActivity().findViewById(R.id.favorite);
        }

        if (movie!=null){
            rootView = inflater.inflate(R.layout.fragment_detail,container,false);
            ButterKnife.bind(this,rootView);
            VideoListAdapter adapterVideos = new VideoListAdapter(videosRV, new ArrayList<Video>(), this);
            videosRV.setAdapter(adapterVideos);
            videosRV.setNestedScrollingEnabled(false);

            ReviewsListAdapter adapterReviews = new ReviewsListAdapter(reviewsRV, new ArrayList<Video>(),this);
            reviewsRV.setAdapter(adapterReviews);
            reviewsRV.setNestedScrollingEnabled(false);

            if (!interactor.isTablet()){
                favoriteAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favoriteAction(view);
                    }
            });
            }
        } else{
            rootView = inflater.inflate(R.layout.content_empty,container,false);
            if (!interactor.isTablet()){
                favoriteAction.setVisibility(View.GONE);
            }
        }
        return rootView;
    }

    private void favoriteAction(View view) {
        if (!dbHelper.isMovieFavorited(movie.getId())) {
            if (dbHelper.insertMovieToFavorites(movie) > 0) {
                if (!interactor.isTablet()){
                    favoriteAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_color_unselector)));
                }
                movie.setFavorited(true);
                //TODO INSERT REVIEWS IN MOVIE ENTITY
                //TODO INSERT IN DB
            }
        } else {
            if (dbHelper.deleteMovieFavorited(movie.getId()) > 0) {
                if (!interactor.isTablet()){
                    favoriteAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_color_selector)));
                }
                movie.setFavorited(false);
                //TODO DELETE FROM DB
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (movie==null) return;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            poster.setTransitionName(KEY_POSTER + movie.getId());
            if (!interactor.isTablet()){
                favoriteAction.setTransitionName(KEY_FAVORITE + movie.getId());
            }
        }

        title.setText(movie.getTitle());
        year.setText(movie.getReleaseDate().substring(0,4));

        DecimalFormat formatter = new DecimalFormat(FORMAT_RATING);
        rating.setText(getString(R.string.detail_rating,formatter.format(movie.getVoteAverage())));
        description.setText(movie.getOverview());
        RequestListener listener = GlidePalette.with(movie.getPosterPath())
            .use(GlidePalette.Profile.VIBRANT_DARK)
            .intoBackground(title)
            .intoBackground(detailLeftContainer)
            .intoBackground(description);
        Glide.with(getContext()).load(movie.getPosterPath())
                .listener(listener)
                .placeholder(R.drawable.empty_movies)
                .dontAnimate()
                .into(poster);

        boolean shouldLoadDetail = false;
        if (movie.getRuntime()==0){
            time.setText(getString(R.string.information_loading));
        } else {
            shouldLoadDetail = true;
            time.setText(getString(R.string.detail_runtime,movie.getRuntime()));
        }

        if (movie.getVideos()!=null && movie.getVideos().getResults()!=null && !movie.getVideos().getResults().isEmpty()){
            displayVideos(movie.getVideos().getResults());
        } else {
            shouldLoadDetail = true;
        }

        if (movie.getReviews()!=null && movie.getReviews().getResults()!=null && !movie.getReviews().getResults().isEmpty()){
            displayReviews(movie.getReviews().getResults());
        } else {
            loadReviews();
        }

        if (shouldLoadDetail){
            loadMovieDetail();
        }

        if (!interactor.isTablet()){
            if (dbHelper.isMovieFavorited(movie.getId())) {
                favoriteAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_color_unselector)));
            } else {
                favoriteAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_color_selector)));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (reviewsRV.getAdapter() instanceof ReviewsListAdapter){
            List items = ((ReviewsListAdapter)reviewsRV.getAdapter()).getItems();
            outState.putParcelableArrayList(SAVE_ITEM_LIST, (ArrayList<? extends Parcelable>) items);
        }
        outState.putInt(SAVE_NEXT_PAGE,nextPageToLoad);
        outState.putParcelable(SAVE_MOVIE,movie);
        super.onSaveInstanceState(outState);
    }

    private void loadMovieDetail() {
        Call call = apiService.loadMovieDetail(movie.getId(),MoviesApi.DETAIL_APPEND);
        call.enqueue(new CallbackLoadMovieDetail());
    }

    private void loadReviews() {
        Call call = apiService.loadReviews(movie.getId());
        call.enqueue(new CallbackLoadReviews());

    }

    @Override
    public boolean onWatchVideoListener(Video video) {
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+video.getKey()));
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public boolean onLoadMoreItemClicked() {
        return false;
    }

    public void updateFavorited(Movie movie) {
        if (this.movie.getId() ==movie.getId()){
            if (movie.isFavorited()){

            }
        }
    }

    private class CallbackLoadMovieDetail extends DefaultCallback<MovieResponse> {
        @Override
        public void onResponse(Response<MovieResponse> response, Retrofit retrofit) {
            if (isAdded()){
                int runtime = response.body().getRuntime();
                displayRuntime(runtime);

                movie = response.body();
                if (movie.getVideos()!=null){
                    displayVideos(movie.getVideos().getResults());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            super.onFailure(t);
        }
    }

    private class CallbackLoadReviews extends DefaultCallback<ReviewsResponse> {
        @Override
        public void onResponse(Response<ReviewsResponse> response, Retrofit retrofit) {
            if (isAdded()){
                if (response.body().getResults()!=null){
                    displayReviews(response.body().getResults());
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            super.onFailure(t);
        }
    }

    private void displayRuntime(int runtime) {
        if (runtime==0){
            time.setText(getString(R.string.information_unavailable));
        } else {
            time.setText(getString(R.string.detail_runtime,runtime));
        }
    }

    private void displayVideos(List<Video> items) {
        if (items.isEmpty()){
            videosRV.setVisibility(View.GONE);
        } else{
            videosRV.setVisibility(View.VISIBLE);
            if (items.get(0)!=null){
                items.add(0,null);
            }
            setShareIntent();
            VideoListAdapter adapter = (VideoListAdapter)videosRV.getAdapter();
            adapter.addItems(items);
        }
    }

    private void displayReviews(List<Review> items) {
        ReviewsListAdapter adapter = (ReviewsListAdapter)reviewsRV.getAdapter();
        if (items.isEmpty() && items.isEmpty()){
            reviewsRV.setVisibility(View.GONE);
        } else{
            reviewsRV.setVisibility(View.VISIBLE);
            if (adapter.getItems().isEmpty()){
                items.add(0,null);
            }
            adapter.addItems(items);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        shareItem = menu.findItem(R.id.shareMenu);
        shareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        VideoListAdapter adapter = (VideoListAdapter)videosRV.getAdapter();
        if (adapter.getItems().isEmpty()){
            shareItem.setVisible(false);
        } else {
            shareItem.setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void setShareIntent() {
        if (movie.getVideos()!=null && !movie.getVideos().getResults().isEmpty()){
            Video video = movie.getVideos().getResults().get(1);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, video.getVideoPath());

            if (shareProvider != null) {
                shareProvider.setShareIntent(shareIntent);
                getActivity().invalidateOptionsMenu();
            }
        }
    }
}
