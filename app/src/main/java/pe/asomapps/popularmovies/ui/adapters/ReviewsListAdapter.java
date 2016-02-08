package pe.asomapps.popularmovies.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.Review;
import pe.asomapps.popularmovies.ui.interfaces.OnLoadMoreListener;

/**
 * Created by Danihelsan
 */
public class ReviewsListAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private final int VISIBLE_THRESHOLD = 10;

    public static final int ITEM_VIEW_TYPE_HEADER = 0;
    public static final int ITEM_VIEW_TYPE_ITEM = 1;
    public static final int ITEM_VIEW_TYPE_FOOTER = 2;

    private List<T> items;
    private OnLoadMoreListener onLoadMoreListener;

    private int firstVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private boolean loading = true;

    public ReviewsListAdapter(RecyclerView recyclerView, ArrayList<T> items, OnLoadMoreListener onLoadMoreListener) {
        this.items = items;
        this.onLoadMoreListener = onLoadMoreListener;
        customRecyclerView(recyclerView);
    }

    private void customRecyclerView(final RecyclerView recyclerView) {
        final int numColumns = recyclerView.getContext().getResources().getConfiguration().orientation + 1;
        final CustomLinearLayoutManager recyclerViewManager = new CustomLinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                totalItemCount = recyclerViewManager.getItemCount();
                visibleItemCount = recyclerViewManager.getChildCount();
                firstVisibleItem = recyclerViewManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
                    // End has been reached

                    addItem(null);
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    loading = true;
                }
            }
        });

        recyclerView.setLayoutManager(recyclerViewManager);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case ITEM_VIEW_TYPE_HEADER:
                return onCreateHeaderViewHolder(parent);
            case ITEM_VIEW_TYPE_ITEM:
                return onCreateItemViewHolder(parent);
            case ITEM_VIEW_TYPE_FOOTER:
                return onCreateFooterViewHolder(parent);
            default:
                throw new IllegalStateException("Invalid type, this type ot items " + viewType + " can't be handled");
        }
    }

    private ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_header,parent,false);
        HeaderHolder holder = new HeaderHolder(view);
        holder.title.setText(view.getResources().getString(R.string.item_reviews_header));
        return new HeaderHolder(view);
    }

    private ViewHolder onCreateItemViewHolder(final ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_review,parent,false);
        return new ReviewHolder(view);
    }

    private ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_footer,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLoadMoreListener != null && !loading){
                    onLoadMoreListener.onLoadMoreItemClicked();
                }
            }
        });
        return new FooterHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch(getItemViewType(position)){
            case ITEM_VIEW_TYPE_HEADER:
                onBindHeaderViewHolder(holder,position); break;
            case ITEM_VIEW_TYPE_ITEM:
                onBindItemViewHolder(holder,position); break;
            case ITEM_VIEW_TYPE_FOOTER:
                onBindFooterViewHolder(holder,position); break;
        }
    }

    private void onBindHeaderViewHolder(ViewHolder holder, int position) {
        if (items.size()==0){
            ((HeaderHolder) holder).rootView.setVisibility(View.GONE);
        } else{
            ((HeaderHolder) holder).rootView.setVisibility(View.VISIBLE);
        }
    }

    private void onBindItemViewHolder(ViewHolder holder, int position) {
        Review item = (Review) items.get(position);
        if (item!=null){
            ReviewHolder itemHolder = (ReviewHolder) holder;
            itemHolder.name.setText(item.getAuthor());
            itemHolder.review.setText(item.getContent());
        }
    }

    private void onBindFooterViewHolder(ViewHolder holder, int position) {
        FooterHolder footerHolder = (FooterHolder) holder;
        if (!loading){
            Context context = footerHolder.rootView.getContext();
            footerHolder.text.setText(context.getString(R.string.footer_load_movies));
        } else {
            Context context = footerHolder.rootView.getContext();
            footerHolder.text.setText(context.getString(R.string.footer_loading_movies));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return ITEM_VIEW_TYPE_HEADER;
        } else if (position!=0 && items.get(position)!=null){
            return ITEM_VIEW_TYPE_ITEM;
        } else {
            return ITEM_VIEW_TYPE_FOOTER;
        }
    }
    static class HeaderHolder extends ViewHolder {
        @Nullable
        @Bind(R.id.rootView) View rootView;
        @Nullable @Bind(R.id.title) TextView title;
        public HeaderHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    static class ReviewHolder extends ViewHolder {
        @Nullable @Bind(R.id.rootView) View rootView;
        @Nullable @Bind(R.id.name) TextView name;
        @Nullable @Bind(R.id.review) TextView review;

        public ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    static class FooterHolder extends RecyclerView.ViewHolder {
        @Nullable @Bind(R.id.rootView) View rootView;
        @Nullable @Bind(R.id.loadingText) TextView text;
        public FooterHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public void resetItems(@NonNull List<T> items) {
        items.clear();
        addItems(items);
    }

    public void addItems(@NonNull List<T> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        if (!items.contains(item)) {
            items.add(item);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void removeItem(Movie item) {
        int index = items.indexOf(item);
        if (index != -1) {
            this.items.remove(index);
            notifyItemRemoved(index);
        }
    }

    public List getItems() {
        return items;
    }

}
