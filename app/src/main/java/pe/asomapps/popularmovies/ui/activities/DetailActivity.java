package pe.asomapps.popularmovies.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.ui.fragments.DetailFragment;

/**
 * Created by Danihelsan
 */
public class DetailActivity extends AppCompatActivity {
    Fragment detailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

        if (savedInstanceState==null){
            detailFragment = new DetailFragment();
            detailFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.detailFragment,detailFragment,"detailFragment").commit();
        } else {
            detailFragment = getSupportFragmentManager().findFragmentByTag("detailFragment");
        }
    }
}