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
    public static final int CODE_DETAIL = 10001;

    private final String SAVE_SORTOPTION = "sort_option";
    private final String SAVE_FIRSTTIME = "first_time";
    Fragment homeFragment, detailFragment;
    FrameLayout homeContainer,detailContainer;
    private Spinner spinner;
    private Sort currentSort;
    private boolean firstTime = true;

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
        if (savedInstanceState!=null){
            if (savedInstanceState.containsKey(SAVE_SORTOPTION)){
                if (savedInstanceState.getString(SAVE_SORTOPTION)!=null){
                    currentSort = Sort.fromString(savedInstanceState.getString(SAVE_SORTOPTION));
                } else{
                    currentSort = null;
                }
            }
            if (savedInstanceState.containsKey(SAVE_FIRSTTIME)){
                firstTime = savedInstanceState.getBoolean(SAVE_FIRSTTIME);
            } else {
                firstTime = true;
            }
        }

        String homeTag = HomeFragment.tag.name();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        boolean shouldCommit = false;
        if (getSupportFragmentManager().findFragmentByTag(homeTag)==null){
            homeFragment = HomeFragment.newInstance(currentSort.toString());
            fragmentTransaction.replace(R.id.homeContainer, homeFragment,homeTag);
            shouldCommit = true;
        } else{
            homeFragment = getSupportFragmentManager().findFragmentByTag(homeTag);
        }

        if (isTablet() && detailContainer!=null){
            String detailTag = DetailFragment.tag.name();
            if (getSupportFragmentManager().findFragmentByTag(homeTag)==null){
                detailFragment = DetailFragment.newInstance();
                loadDetail(fragmentTransaction, detailFragment, null);
                shouldCommit = true;
            } else{
                detailFragment = getSupportFragmentManager().findFragmentByTag(detailTag);
            }
        }

        if (shouldCommit){
            fragmentTransaction.commit();
        }

        configureToolBar();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String sortOption = currentSort!=null?currentSort.toString():null;
        outState.putString(SAVE_SORTOPTION,sortOption);
        outState.putBoolean(SAVE_FIRSTTIME,firstTime);
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

                    if (isTablet()){
                        if (firstTime){
                            Fragment fragment = DetailFragment.newInstance(null);
                            loadDetail(null, fragment,null);
                            firstTime = false;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner.setSelection(getSortSelected());

    }

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

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.homeContainer, homeFragment,homeTag);
                if(isTablet()){
                    loadDetail(fragmentTransaction, detailFragment, null);
                }
                fragmentTransaction.commit();
            }
        }

    }

    @Override
    public boolean isTablet() {
        return detailFragment!=null || detailContainer!=null;
    }

    @Override
    public boolean loadDetail(FragmentTransaction fragmentTransaction, Fragment fragment, View[] sharedViews) {
        boolean immediateCommit = fragmentTransaction==null;
        if (fragmentTransaction==null) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
        }
        fragmentTransaction.replace(R.id.detailContainer, fragment, DetailFragment.tag.name());
        if (sharedViews!=null && sharedViews.length>0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.home_detail_transition);
                fragment.setSharedElementEnterTransition(transition);
                for (int i=0;i<sharedViews.length;i++){
                    View view = sharedViews[i];
                    fragmentTransaction.addSharedElement(view,view.getTransitionName());
                }
            }
        }

        if (immediateCommit){
            fragmentTransaction.commit();
        }

        return !immediateCommit;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DETAIL) {
            if (resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                ((HomeFragment)homeFragment).updateFavorited(
                        bundle.getInt(DetailFragment.KEY_POSITION),
                        bundle.getBoolean(DetailFragment.KEY_FAVORITE));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setShareIntent(Movie movie) {
        super.setShareIntent(movie);
        if (isTablet()){
            ((HomeFragment)homeFragment).setShareIntent(movie);
        }
    }
}
