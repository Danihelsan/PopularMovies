package pe.asomapps.popularmovies.ui.utils;

import pe.asomapps.popularmovies.ui.adapters.SortOptionsAdapter;

public class SortOptionItem implements SortOptionsAdapter.Sortable{
    private CharSequence label;
    private Sort value;
    private boolean visible;

    public SortOptionItem(CharSequence label, Sort value) {
        this.label = label;
        this.value = value;
        this.visible = true;
    }

    public SortOptionItem(CharSequence label, Sort value, boolean visible) {
        this.label = label;
        this.value = value;
        this.visible = visible;
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

    @Override
    public boolean getVisible() {
        return visible;
    }

    @Override
    public void setVisibility(boolean visible) {
        this.visible = visible;
    }
}