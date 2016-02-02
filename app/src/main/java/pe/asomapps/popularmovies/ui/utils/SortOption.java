package pe.asomapps.popularmovies.ui.utils;

import pe.asomapps.popularmovies.R;

/**
 * Created by Danihelsan
 */
public enum SortOption{
    POP_DESC(R.string.sort_label_popularity,R.string.sort_value_popularity),
    REV_DESC(R.string.sort_label_revenue,R.string.sort_value_revenue),
    VOT_DESC(R.string.sort_label_vote,R.string.sort_value_vote);

    private int label;
    private int value;
    SortOption(int keyLabel, int keyValue) {
        this.label = label;
        this.value = value;
    }
}
