package pe.asomapps.popularmovies.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.View;

import javax.inject.Inject;

import pe.asomapps.popularmovies.App;
import pe.asomapps.popularmovies.data.api.MoviesApi;

/**
 * @author Danihelsan
 */
public class BaseFragment extends Fragment {

    @Inject
    MoviesApi apiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)getActivity().getApplication()).getComponent().inject(this);
    }

    protected void openNewScreen(Intent intent, View[] sharedViews) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && sharedViews.length>0) {
            Pair<View,String>[] sharedElements = new Pair[sharedViews.length];
            for (int i=0;i<sharedViews.length;i++){
                View view = sharedViews[i];
                sharedElements[i] = new Pair<>(view,view.getTransitionName());
            }
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),sharedElements);
            ActivityCompat.startActivity(getActivity(), intent, activityOptionsCompat.toBundle());
        } else{
            startActivity(intent);
        }

    }
}
