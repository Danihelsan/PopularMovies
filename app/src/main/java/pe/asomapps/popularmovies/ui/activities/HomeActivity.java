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

import java.util.ArrayList;
import java.util.List;

import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.ui.adapters.SortOptionsAdapter;
import pe.asomapps.popularmovies.ui.fragments.DetailFragment;
import pe.asomapps.popularmovies.ui.fragments.HomeFragment;
import pe.asomapps.popularmovies.ui.interfaces.FragmentInteractor;
import pe.asomapps.popularmovies.ui.utils.Sort;
import pe.asomapps.popularmovies.ui.utils.SortOptionItem;

public class HomeActivity extends AppCompatActivity implements FragmentInteractor{
    private final String SAVE_SORTOPTION = "sort_option";;
    Fragment homeFragment, detailFragment;
    FrameLayout homeContainer,detailContainer;

    private Sort currentSort;
    private int sortSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeContainer = (FrameLayout)findViewById(R.id.homeContainer);
        detailContainer = (FrameLayout)findViewById(R.id.detailContainer);

        currentSort = Sort.POPULARITY;
        if (savedInstanceState!=null && savedInstanceState.containsKey(SAVE_SORTOPTION)){
            currentSort = Sort.fromString(savedInstanceState.getString(SAVE_SORTOPTION));
        }

        String homeTag = HomeFragment.tag.name();
        if (getSupportFragmentManager().findFragmentByTag(homeTag)==null){
            homeFragment = HomeFragment.newInstance(currentSort.toString());
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVE_SORTOPTION,currentSort.toString());
        super.onSaveInstanceState(outState);
    }

    private void configureToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<SortOptionsAdapter.Sortable> items = new ArrayList<>();
        if (userSavedFavorites()) {
            items.add(new SortOptionItem(getString(R.string.sort_label_favorites), null));
        }
        items.add(new SortOptionItem(getString(R.string.sort_label_popularity), Sort.POPULARITY));
        items.add(new SortOptionItem(getString(R.string.sort_label_revenue), Sort.REVENUE));
        items.add(new SortOptionItem(getString(R.string.sort_label_vote), Sort.VOTE));

        SortOptionsAdapter adapter = new SortOptionsAdapter(items);
        Spinner spinner = (Spinner) toolbar.findViewById(R.id.sort_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                if (spinner.getAdapter() instanceof SortOptionsAdapter) {
                    onOptionSelected(((SortOptionsAdapter) spinner.getAdapter()).getSortOption(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner.setSelection(getSortSelected());

    }

    private boolean userSavedFavorites() {
        return false;
    }

    private void onOptionSelected(Object optionSelected){
        if (optionSelected instanceof SortOptionItem){
            Sort sort = ((SortOptionItem)optionSelected).getValue();
            if (sort!=currentSort){
                currentSort = sort;
                homeFragment = HomeFragment.newInstance(currentSort.toString());
                String homeTag = HomeFragment.tag.name();
                getSupportFragmentManager().beginTransaction().replace(R.id.homeContainer, homeFragment,homeTag).commit();
                if(isTablet()){
                    loadDetail(detailFragment, null);
                }
            }
        }

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

    public int getSortSelected() {
        int sortSelected = -1;
        if (currentSort!=null){
            if (currentSort == Sort.POPULARITY){
                sortSelected = 0;
            } else if (currentSort ==Sort.REVENUE){
                sortSelected = 1;
            } else if (currentSort == Sort.VOTE){
                sortSelected = 2;
            }

            if (userSavedFavorites()) {
                sortSelected += 1;
            }
        } else {
            sortSelected = 0;
        }


        return sortSelected;
    }
}
