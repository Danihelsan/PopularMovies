package pe.asomapps.popularmovies.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import pe.asomapps.popularmovies.App;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.data.helper.DataBaseHelper;
import pe.asomapps.popularmovies.data.helper.PreferencesHelper;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.ui.adapters.SortOptionsAdapter;
import pe.asomapps.popularmovies.ui.fragments.DetailFragment;
import pe.asomapps.popularmovies.ui.fragments.HomeFragment;
import pe.asomapps.popularmovies.ui.utils.Sort;
import pe.asomapps.popularmovies.ui.utils.SortOptionItem;

public class HomeActivity extends BaseActivity {
    private final String SAVE_SORTOPTION = "sort_option";
    Fragment homeFragment, detailFragment;
    FrameLayout homeContainer,detailContainer;
    private Spinner spinner;
    private Sort currentSort;

    @Inject
    PreferencesHelper preferencesHelper;

    @Inject
    DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)getApplication()).getComponent().inject(this);
        installShortcut();

        setContentView(R.layout.activity_home);

        homeContainer = (FrameLayout)findViewById(R.id.homeContainer);
        detailContainer = (FrameLayout)findViewById(R.id.detailContainer);

        currentSort = Sort.POPULARITY;
        if (savedInstanceState!=null && savedInstanceState.containsKey(SAVE_SORTOPTION) ){
            if (savedInstanceState.getString(SAVE_SORTOPTION)!=null){
                currentSort = Sort.fromString(savedInstanceState.getString(SAVE_SORTOPTION));
            } else{
                currentSort = null;
            }
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
        String sortOption = currentSort!=null?currentSort.toString():null;
        outState.putString(SAVE_SORTOPTION,sortOption);
        super.onSaveInstanceState(outState);
    }

    private void configureToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        List<SortOptionsAdapter.Sortable> items = new ArrayList<>();
        items.add(new SortOptionItem(getString(R.string.sort_label_popularity), Sort.POPULARITY));
        items.add(new SortOptionItem(getString(R.string.sort_label_revenue), Sort.REVENUE));
        items.add(new SortOptionItem(getString(R.string.sort_label_vote), Sort.VOTE));
        if (userSavedFavorites()) {
            items.add(new SortOptionItem(getString(R.string.sort_label_favorites), null, false));
        }

        SortOptionsAdapter adapter = new SortOptionsAdapter(items);
        spinner = (Spinner) toolbar.findViewById(R.id.sort_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                if (spinner.getAdapter() instanceof SortOptionsAdapter) {
                    SortOptionsAdapter adapter = (SortOptionsAdapter) spinner.getAdapter();
                    onOptionSelected(adapter.getSortOption(position));

                    if (firstTime){
                        firstTime = false;
                    } else {
                        Fragment fragment = DetailFragment.newInstance(null);
                        loadDetail(fragment,null);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner.setSelection(getSortSelected());

    }

    boolean firstTime = true;

    private boolean userSavedFavorites() {
        return dbHelper.isMovieFavorited();
    }

    private void onOptionSelected(Object optionSelected){
        if (optionSelected instanceof SortOptionItem){
            Sort sort = ((SortOptionItem)optionSelected).getValue();
            if (sort!=currentSort){
                currentSort = sort;
                String sortOption = currentSort!=null? currentSort.toString():null;
                homeFragment = HomeFragment.newInstance(sortOption);
                String homeTag = HomeFragment.tag.name();
                getSupportFragmentManager().beginTransaction().replace(R.id.homeContainer, homeFragment,homeTag).commit();
                if(isTablet()){
                    loadDetail(detailFragment, null);
                }
            }
        }

    }

    @Override
    public boolean isTablet() {
        return detailFragment!=null || detailContainer!=null;
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

    @Override
    public void updateFavorited(Movie movie) {
        if (isTablet()){
            ((DetailFragment)detailFragment).updateFavorited(movie);
        }
    }

    @Override
    public void updateSpinner() {
        boolean withFavorited = dbHelper.isMovieFavorited();
        if (spinner!=null){
            ((SortOptionsAdapter)spinner.getAdapter()).updateFavoriteItem(withFavorited, getString(R.string.sort_label_favorites));
        }
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
        } else {
            sortSelected = 3;
        }

        return sortSelected;
    }

    public void installShortcut(){
        if (preferencesHelper == null || preferencesHelper.isShortcutInstalled()){
            return;
        }

        Intent shortcutIntent = new Intent();
        shortcutIntent.setClassName(this, HomeActivity.class.getName());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        this.sendBroadcast(addIntent);
        preferencesHelper.saveValue(preferencesHelper.KEY_SHORTCUT_INSTALLED,true);

    }
}
