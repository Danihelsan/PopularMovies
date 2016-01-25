package pe.asomapps.popularmovies.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import pe.asomapps.popularmovies.R;

/**
 * @author Danihelsan
 */
public class DetailFragment extends BaseFragment{
    public static final String KEY_MOVIE_CONTENT = "movie_content";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail,container,false);
        ButterKnife.bind(this,rootView);

        return rootView;
    }
}
