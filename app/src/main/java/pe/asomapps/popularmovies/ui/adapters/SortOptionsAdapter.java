package pe.asomapps.popularmovies.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pe.asomapps.popularmovies.R;

/**
 * @author Danihelsan
 */
public class SortOptionsAdapter extends BaseAdapter{
    private final int resLayoutId = R.layout.item_sortoptions_spinner;
    private final int resLayoutDropdownId = R.layout.item_sortoptions_dropdown_spinner;

    private List<? extends Sortable> items;

    public SortOptionsAdapter(List<? extends Sortable> items){
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

    public interface Sortable {
        long getItemId();
        CharSequence getLabel();
        Object getValue();
    }

}
