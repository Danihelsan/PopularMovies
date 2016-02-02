package pe.asomapps.popularmovies.ui.activities;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;

import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.ui.fragments.DetailFragment;
import pe.asomapps.popularmovies.ui.fragments.HomeFragment;
import pe.asomapps.popularmovies.ui.interfaces.FragmentInteractor;

public class HomeActivity extends AppCompatActivity implements FragmentInteractor{
    Fragment homeFragment, detailFragment;
    FrameLayout homeContainer,detailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeContainer = (FrameLayout)findViewById(R.id.homeContainer);
        detailContainer = (FrameLayout)findViewById(R.id.detailContainer);

        String homeTag = HomeFragment.tag.name();
        if (getSupportFragmentManager().findFragmentByTag(homeTag)==null){
            homeFragment = HomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.homeContainer, homeFragment,homeTag).commit();
        } else{
            homeFragment = getSupportFragmentManager().findFragmentByTag(homeTag);
        }

        if (detailContainer!=null){
            String detailTag = DetailFragment.tag.name();
            if (getSupportFragmentManager().findFragmentByTag(homeTag)==null){
                detailFragment = DetailFragment.newInstance();
                loadDetail(detailFragment, null);
            } else{
                detailFragment = getSupportFragmentManager().findFragmentByTag(detailTag);
            }
        }

        configureToolBar();

    }

    private void configureToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Spinner spinner = (Spinner) toolbar.findViewById(R.id.sort_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                onOptionSelected(spinner.getAdapter().getMode(position));
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) { }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isTablet() {
        return detailFragment!=null || detailContainer!=null;
    }

    @Override
    public boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void loadDetail(Fragment fragment, View[] sharedViews) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.detailContainer, fragment, DetailFragment.tag.name());
        if (sharedViews!=null && sharedViews.length>0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.home_detail_transition);
                fragment.setSharedElementEnterTransition(transition);
                for (int i=0;i<sharedViews.length;i++){
                    View view = sharedViews[i];
                    transaction.addSharedElement(view,view.getTransitionName());
                }
            }
        }
        transaction.commit();
    }
}
