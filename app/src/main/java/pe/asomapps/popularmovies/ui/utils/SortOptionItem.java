package pe.asomapps.popularmovies.ui.utils;

import pe.asomapps.popularmovies.ui.adapters.SortOptionsAdapter;

public class SortOptionItem implements SortOptionsAdapter.Sortable{
    private CharSequence label;
    private Sort value;

    public SortOptionItem(CharSequence label, Sort value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public long getItemId() {
        return hashCode();
    }

    @Override
    public CharSequence getLabel() {
        return label;
    }

    @Override
    public Sort getValue() {
        return value;
    }
}