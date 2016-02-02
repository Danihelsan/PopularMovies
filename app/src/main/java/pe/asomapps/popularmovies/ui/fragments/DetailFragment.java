package pe.asomapps.popularmovies.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import pe.asomapps.popularmovies.model.Video;
import pe.asomapps.popularmovies.model.responses.MovieResponse;
import pe.asomapps.popularmovies.ui.adapters.VideoListAdapter;
import pe.asomapps.popularmovies.ui.interfaces.FragmentInteractor;
import pe.asomapps.popularmovies.ui.utils.Tag;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Danihelsan
 */
public class DetailFragment extends BaseFragment implements VideoListAdapter.VideoClickListener{
    public static final Tag tag = Tag.DETAIL;
    private final String SAVE_MOVIE = "movie";

    private final String FORMAT_RATING = "#.#";
    public static final String KEY_MOVIE = "movie_";
    public static final String KEY_POSTER = "poster_";
    public static final String KEY_FAVORITE = "favorite_";

    @Bind(R.id.title) TextView title;
    @Bind(R.id.year) TextView year;
    @Bind(R.id.time) TextView time;
    @Bind(R.id.rating) TextView rating;
    @Bind(R.id.description) TextView description;

    @Bind(R.id.poster) ImageView poster;
    @Bind(R.id.favorite) ImageView favorite;
    @Bind(R.id.videosRV) RecyclerView videosRV;

    @Bind(R.id.detailLeftContainer) FrameLayout detailLeftContainer;
    @Bind(R.id.separator) View separator;

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
            if (getArguments()!=null){
                movie = getArguments().getParcelable(KEY_MOVIE);
            }
        } else {
            if (savedInstanceState.containsKey(SAVE_MOVIE)) {
                movie = savedInstanceState.getParcelable(SAVE_MOVIE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView;
        if (movie!=null){
            rootView = inflater.inflate(R.layout.fragment_detail,container,false);
            ButterKnife.bind(this,rootView);
            VideoListAdapter adapter = new VideoListAdapter(videosRV, new ArrayList<Video>(), this);
            videosRV.setAdapter(adapter);
            videosRV.setNestedScrollingEnabled(false);
        } else{
            rootView = inflater.inflate(R.layout.content_empty,container,false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (movie!=null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                poster.setTransitionName(KEY_POSTER + movie.getId());
                favorite.setTransitionName(KEY_FAVORITE + movie.getId());
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
                    .into(poster);

            boolean shouldLoadDetail = false;
            if (movie.getRuntime()==0){
                time.setText(getString(R.string.information_loading));
            } else {
                shouldLoadDetail = true;
                time.setText(getString(R.string.detail_runtime,movie.getRuntime()));
            }

            if (movie.getVideos()!=null && movie.getVideos().getResults()!=null){
                displayVideos(movie.getVideos().getResults());
            } else {
                shouldLoadDetail = true;
                separator.setVisibility(View.INVISIBLE);
            }

            if (shouldLoadDetail){
                loadMovieDetail();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVE_MOVIE,movie);
        super.onSaveInstanceState(outState);
    }

    private void loadMovieDetail() {
        Call call = apiService.loadMovieDetail(movie.getId(),MoviesApi.DETAIL_APPEND);
        call.enqueue(new CallbackLoadMovieDetail());
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

    private class CallbackLoadMovieDetail extends DefaultCallback<MovieResponse> {
        @Override
        public void onResponse(Response<MovieResponse> response, Retrofit retrofit) {
            if (isAdded()){
                int runtime = response.body().getRuntime();
                displayRuntime(runtime);

                if (response.body().getVideos()!=null){
                    List<Video> items = response.body().getVideos().getResults();
                    displayVideos(items);
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
        VideoListAdapter adapter = (VideoListAdapter)videosRV.getAdapter();
        if (items.size()==0){
            separator.setVisibility(View.GONE);
        } else{
            separator.setVisibility(View.VISIBLE);
            items.add(0,null);
            adapter.addItems(items);
        }
    }

}
