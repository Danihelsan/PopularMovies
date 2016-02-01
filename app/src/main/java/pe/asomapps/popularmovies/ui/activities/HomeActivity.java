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
import android.widget.FrameLayout;

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

        homeFragment = HomeFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.homeContainer, homeFragment).commit();

        if (detailContainer!=null){
            detailFragment = DetailFragment.newInstance();
            loadDetail(detailFragment, null);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.detailContainer, fragment);
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
