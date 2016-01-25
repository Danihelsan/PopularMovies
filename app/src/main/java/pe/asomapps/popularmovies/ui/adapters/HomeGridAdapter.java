package pe.asomapps.popularmovies.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.ui.OnLoadMoreListener;

/**
 * Created by Danihelsan
 */
public class HomeGridAdapter extends RecyclerView.Adapter<ViewHolder>{
    private final String PREFIX_TRANSITION_NAME = "home_detail_image_";
    private final int VISIBLE_THRESHOLD = 10;

    public static final int ITEM_VIEW_TYPE_ITEM = 0;
    public static final int ITEM_VIEW_TYPE_FOOTER = 1;

    private List<Movie> items;

    private MovieClickListener movieClickListener;
    private OnLoadMoreItemClicked onLoadMoreItemClicked;

    private int firstVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private boolean loading = true;

    public HomeGridAdapter(RecyclerView recyclerView, List<Movie> items, OnLoadMoreListener onLoadMoreListener, MovieClickListener movieClickListener, OnLoadMoreItemClicked onLoadMoreItemClicked) {
        this.items = items;
        this.movieClickListener = movieClickListener;
        this.onLoadMoreItemClicked = onLoadMoreItemClicked;
        customRecyclerView(recyclerView, onLoadMoreListener);
    }

    private void customRecyclerView(final RecyclerView recyclerView, final OnLoadMoreListener onLoadMoreListener) {
        final int numColumns = recyclerView.getContext().getResources().getConfiguration().orientation + 1;
        final GridLayoutManager recyclerViewManager = new GridLayoutManager(recyclerView.getContext(),numColumns);

        recyclerViewManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position) {
                switch(recyclerView.getAdapter().getItemViewType(position)){
                    case ITEM_VIEW_TYPE_ITEM:
                        return 1;
                    case ITEM_VIEW_TYPE_FOOTER:
                        return numColumns;
                    default: return -1;
                }
            }
        });

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
            case ITEM_VIEW_TYPE_ITEM:
                return onCreateItemViewHolder(parent);
            case ITEM_VIEW_TYPE_FOOTER:
                return onCreateFooterViewHolder(parent);
            default:
                throw new IllegalStateException("Invalid type, this type ot items " + viewType + " can't be handled");
        }
    }

    private ViewHolder onCreateItemViewHolder(final ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_item,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieClickListener !=null){
                    int position = ((RecyclerView)parent).getChildAdapterPosition(v);
                    Movie movie = items.get(position);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        MovieHolder holder = new MovieHolder(v);
                        movieClickListener.onMovieClicked(movie, holder.imageMovie);
                    } else{
                        movieClickListener.onMovieClicked(movie);
                    }
                }
            }
        });
        return new MovieHolder(view);
    }

    private ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_footer,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLoadMoreItemClicked != null && !loading){
                    onLoadMoreItemClicked.onLoadMoreItemClicked();
                }
            }
        });
        return new FooterHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch(getItemViewType(position)){
            case ITEM_VIEW_TYPE_ITEM:
                onBindItemViewHolder(holder,position); break;
            case ITEM_VIEW_TYPE_FOOTER:
                onBindFooterViewHolder(holder,position); break;
        }
    }

    private void onBindItemViewHolder(ViewHolder holder, int position) {
        Movie movieEntity = items.get(position);
        MovieHolder movieHolder = (MovieHolder) holder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            movieHolder.imageMovie.setTransitionName(PREFIX_TRANSITION_NAME + position);
        }
        String imageUrl = movieEntity.getPosterPath();
        RequestListener listener = GlidePalette.with(imageUrl).use(GlidePalette.Profile.MUTED).intoBackground(movieHolder.rootView);
        Glide.with(holder.itemView.getContext()).load(imageUrl).listener(listener).into(movieHolder.imageMovie);
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
        if (items.get(position)!=null) {
            return ITEM_VIEW_TYPE_ITEM;
        } else {
            return ITEM_VIEW_TYPE_FOOTER;
        }
    }

    public void enableLoadingMore() {
        loading = false;
        if (items.size() == 0){
            addItem(null);
            notifyItemInserted(1);
        } else{
            notifyItemChanged(items.size());
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyItemChanged(items.size());
    }

    static class MovieHolder extends RecyclerView.ViewHolder {
        @Nullable @Bind(R.id.rootView) View rootView;
        @Nullable @Bind(R.id.imageMovie) ImageView imageMovie;
        public MovieHolder(View view) {
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

    public void resetItems(@NonNull List<Movie> items) {
        loading = true;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        previousTotal = 0;

        items.clear();
        addItems(items);
    }

    public void addItems(@NonNull List<Movie> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addItem(Movie item) {
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

    public interface MovieClickListener {
        boolean onMovieClicked(Movie movie, View... views);
        boolean onAddToFavoritesClicked(Movie movie);
    }

    public interface OnLoadMoreItemClicked{
        boolean onLoadMoreItemClicked();
    }
}
