package pe.asomapps.popularmovies.ui.fragments;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.DefaultCallback;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.data.api.MoviesApi;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.Video;
import pe.asomapps.popularmovies.model.responses.MovieResponse;
import pe.asomapps.popularmovies.ui.adapters.VideoListAdapter;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Danihelsan
 */
public class DetailFragment extends BaseFragment implements VideoListAdapter.VideoClickListener{
    private final String FORMAT_RATING = "\"#.#\"";
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail,container,false);
        ButterKnife.bind(this,rootView);
        movie = getArguments().getParcelable(KEY_MOVIE);

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
                .use(GlidePalette.Profile.VIBRANT_DARK).intoBackground(title).intoBackground(detailLeftContainer);
        Glide.with(getContext()).load(movie.getPosterPath()).listener(listener).into(poster);

        VideoListAdapter adapter = new VideoListAdapter(videosRV, new ArrayList<Video>(), this);
        videosRV.setAdapter(adapter);

        loadMovieDetail();
        return rootView;
    }

    private void loadMovieDetail() {
        Call call = apiService.loadMovieDetail(movie.getId(),MoviesApi.DETAIL_APPEND);
        call.enqueue(new CallbackLoadMovieDetail());
    }

    @Override
    public boolean onWatchVideoListener(Video video) {
        return false;
    }

    private class CallbackLoadMovieDetail extends DefaultCallback<MovieResponse> {
        @Override
        public void onResponse(Response<MovieResponse> response, Retrofit retrofit) {
            time.setText(getString(R.string.detail_runtime,response.body().getRuntime()));

            if (response.body().getVideos()!=null && response.body().getVideos().getResults()!=null && response.body().getVideos().getResults().size()>0){

            } else{
                separator.setVisibility(View.GONE);

            }
        }

        @Override
        public void onFailure(Throwable t) {
            super.onFailure(t);
        }
    }
}
