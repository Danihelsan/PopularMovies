package pe.asomapps.popularmovies.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.ui.utils.SortOptionItem;

/**
 * @author Danihelsan
 */
public class SortOptionsAdapter extends BaseAdapter{
    private final int resLayoutId = R.layout.item_sortoptions_spinner;
    private final int resLayoutDropdownId = R.layout.item_sortoptions_dropdown_spinner;

    private List<Sortable> items;
    private int selectedPosition;

    public SortOptionsAdapter(List<Sortable> items){
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Sortable getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i){
        Sortable sortable = getItem(i);
        return sortable.getItemId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(resLayoutId, viewGroup, false);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(i).getLabel());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(resLayoutDropdownId, viewGroup, false);
        }

        TextView normalTextView = (TextView) convertView.findViewById(android.R.id.text1);
        normalTextView.setText(getItem(position).getLabel());
        return convertView;
    }

    public Sortable getSortOption(int position) {
        return getItem(position);
    }

    public void updateFavoriteItem(boolean withFavorited, String optionName) {
        boolean favoriteFound = false;

        for (Sortable item : items){
            if (item.getValue()==null){
                favoriteFound = true;
                if (!withFavorited){
                    items.remove(item);
                }
                notifyDataSetChanged();
                break;
            }
        }

        if (!favoriteFound && withFavorited){
            items.add(new SortOptionItem(optionName, null));
            notifyDataSetChanged();
        }
    }

    public interface Sortable {
        long getItemId();
        CharSequence getLabel();
        Object getValue();
        boolean getVisible();
        void setVisibility(boolean visible);
    }

}
