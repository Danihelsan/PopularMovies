package pe.asomapps.popularmovies.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import pe.asomapps.popularmovies.ui.fragments.DetailFragment;
import pe.asomapps.popularmovies.ui.interfaces.FragmentInteractor;
import pe.asomapps.popularmovies.ui.interfaces.MovieClickListener;
import pe.asomapps.popularmovies.ui.interfaces.OnLoadMoreListener;

/**
 * Created by Danihelsan
 */
public class HomeGridAdapter<T> extends RecyclerView.Adapter<ViewHolder>{
    private final int VISIBLE_THRESHOLD = 10;

    public static final int ITEM_VIEW_TYPE_ITEM = 0;
    public static final int ITEM_VIEW_TYPE_FOOTER = 1;

    private List<T> items;

    private MovieClickListener movieClickListener;
    private OnLoadMoreListener onLoadMoreListener;

    private int firstVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private boolean loading = true;

    public HomeGridAdapter(RecyclerView recyclerView, List<T> items, OnLoadMoreListener onLoadMoreListener, MovieClickListener movieClickListener,FragmentInteractor interactor) {
        this.items = items;
        this.movieClickListener = movieClickListener;
        this.onLoadMoreListener = onLoadMoreListener;
        customRecyclerView(recyclerView, interactor);
    }

    private void customRecyclerView(final RecyclerView recyclerView,FragmentInteractor interactor) {
        final int numColumns = recyclerView.getContext().getResources().getConfiguration().orientation + 1;
        final GridLayoutManager recyclerViewManager = new GridLayoutManager(recyclerView.getContext(),numColumns);

        if (interactor!=null && interactor.isTablet() && !interactor.isLandscape()){
            recyclerViewManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        } else{
            recyclerViewManager.setOrientation(LinearLayoutManager.VERTICAL);
        }

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_home,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieClickListener !=null){
                    int position = ((RecyclerView)parent).getChildAdapterPosition(v);
                    Movie movie = (Movie) items.get(position);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        MovieHolder holder = new MovieHolder(v);
                        movieClickListener.onMovieClicked(movie, holder.poster, holder.favorite);
                    } else{
                        movieClickListener.onMovieClicked(movie);
                    }
                }
            }
        });

        MovieHolder holder = new MovieHolder(view);

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                Movie movie = (Movie) items.get(position);
                movieClickListener.onFavoritedClicked(position, movie);
            }
        });
        return holder;
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
            case ITEM_VIEW_TYPE_ITEM:
                onBindItemViewHolder(holder,position); break;
            case ITEM_VIEW_TYPE_FOOTER:
                onBindFooterViewHolder(holder,position); break;
        }
    }

    private void onBindItemViewHolder(ViewHolder holder, int position) {
        Movie movie = (Movie) items.get(position);
        if (movie!=null){
            MovieHolder itemHolder = (MovieHolder) holder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemHolder.poster.setTransitionName(DetailFragment.KEY_POSTER + movie.getId());
                itemHolder.favorite.setTransitionName(DetailFragment.KEY_FAVORITE + movie.getId());
            }
            String imageUrl = movie.getPosterPath();
            RequestListener listener = GlidePalette.with(imageUrl).use(GlidePalette.Profile.VIBRANT).intoBackground(itemHolder.rootView);
            Glide.with(holder.itemView.getContext()).load(imageUrl).listener(listener).placeholder(R.drawable.empty_movies).dontAnimate().into(itemHolder.poster);

            itemHolder.favorite.setTag(position);
            if (movie.isFavorited()){
                itemHolder.favorite.setBackgroundResource(R.drawable.favorite_unselector);
            } else {
                itemHolder.favorite.setBackgroundResource(R.drawable.favorite_selector);
            }
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
        @Nullable @Bind(R.id.poster) ImageView poster;
        @Nullable @Bind(R.id.favorite) ImageView favorite;
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

    public void resetItems(@NonNull List<T> items) {
        loading = true;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        previousTotal = 0;

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

    public void removeItem(T item) {
        int index = items.indexOf(item);
        if (index != -1) {
            this.items.remove(index);
            notifyItemRemoved(index);
        }
    }

    public List<T> getItems() {
        return items;
    }
}
