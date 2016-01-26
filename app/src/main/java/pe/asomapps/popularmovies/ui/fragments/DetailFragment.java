package pe.asomapps.popularmovies.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.R;

/**
 * @author Danihelsan
 */
public class DetailFragment extends BaseFragment{
    public static final String KEY_MOVIE_CONTENT = "movie_content";

    @Bind({R.id.title,R.id.year,R.id.time,R.id.rating,R.id.description})
    TextView title,year,time,rating,description;

    @Bind({R.id.poster,R.id.favorite})
    ImageView poster,favorite;

    @Bind(R.id.videosRV)
    RecyclerView videosRV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail,container,false);
        ButterKnife.bind(this,rootView);

        return rootView;
    }
}
