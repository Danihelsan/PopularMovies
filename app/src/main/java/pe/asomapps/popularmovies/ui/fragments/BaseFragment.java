package pe.asomapps.popularmovies.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.ShareActionProvider;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import pe.asomapps.popularmovies.App;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.data.api.MoviesApi;
import pe.asomapps.popularmovies.data.helper.DataBaseHelper;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.Video;

/**
 * @author Danihelsan
 */
public class BaseFragment extends Fragment {

    @Inject
    MoviesApi apiService;

    @Inject
    DataBaseHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)getActivity().getApplication()).getComponent().inject(this);
    }

    protected void openNewScreen(Intent intent, int code, View[] sharedViews) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && sharedViews.length>0) {
            Pair<View,String>[] sharedElements = new Pair[sharedViews.length];
            for (int i=0;i<sharedViews.length;i++){
                View view = sharedViews[i];
                sharedElements[i] = new Pair<>(view,view.getTransitionName());
            }
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),sharedElements);

            if (code>0){
                ActivityCompat.startActivityForResult(getActivity(), intent, code ,activityOptionsCompat.toBundle());
            } else {
                ActivityCompat.startActivity(getActivity(), intent, activityOptionsCompat.toBundle());
            }
        } else{
            if (code>0){
                startActivityForResult(intent,code);
            } else {
                startActivity(intent);
            }

        }

    }

    protected void setShareIntent(Movie movie, MenuItem item, ShareActionProvider shareProvider) {
        if (item!=null){
            if (movie.getVideos()!=null && movie.getVideos().getResults()!=null && movie.getVideos().getResults().isEmpty()){
                item.setVisible(false);
            } else {
                item.setVisible(true);
            }
        }
        if (!movie.getVideos().getResults().isEmpty()){
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
