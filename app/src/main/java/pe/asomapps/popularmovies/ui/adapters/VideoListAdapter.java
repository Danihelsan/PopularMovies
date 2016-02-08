package pe.asomapps.popularmovies.ui.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.popularmovies.R;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.Video;

/**
 * Created by Danihelsan
 */
public class VideoListAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    public static final int ITEM_VIEW_TYPE_HEADER = 0;
    public static final int ITEM_VIEW_TYPE_ITEM = 1;

    private List<T> items;

    private VideoClickListener videoClickListener;

    public VideoListAdapter(RecyclerView recyclerView, ArrayList<T> items , VideoClickListener videoClickListener) {
        this.items = items;
        this.videoClickListener = videoClickListener;

        final CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case ITEM_VIEW_TYPE_HEADER:
                return onCreateHeaderViewHolder(parent);
            case ITEM_VIEW_TYPE_ITEM:
                return onCreateItemViewHolder(parent);
            default:
                throw new IllegalStateException("Invalid type, this type ot items " + viewType + " can't be handled");
        }
    }

    private ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_header,parent,false);
        HeaderHolder holder = new HeaderHolder(view);
        holder.title.setText(view.getResources().getString(R.string.item_video_header));
        return new HeaderHolder(view);
    }

    private ViewHolder onCreateItemViewHolder(final ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_video,parent,false);

        VideoHolder holder = new VideoHolder(view);
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoClickListener!=null){
                    Video video = (Video) v.getTag();
                    videoClickListener.onWatchVideoListener(video);
                }
            }
        });
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch(getItemViewType(position)){
            case ITEM_VIEW_TYPE_HEADER:
                onBindHeaderViewHolder(holder,position); break;
            case ITEM_VIEW_TYPE_ITEM:
                onBindItemViewHolder(holder,position); break;
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
        Video video = (Video) items.get(position);
        if (video!=null){
            VideoHolder videoHolder = (VideoHolder) holder;
            videoHolder.name.setText(video.getName());
            videoHolder.play.setTag(video);
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
        } else {
            return ITEM_VIEW_TYPE_ITEM;
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        @Nullable
        @Bind(R.id.rootView) View rootView;
        @Nullable @Bind(R.id.title) TextView title;
        public HeaderHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    static class VideoHolder extends RecyclerView.ViewHolder {
        @Nullable @Bind(R.id.rootView) View rootView;
        @Nullable @Bind(R.id.play) ImageButton play;
        @Nullable @Bind(R.id.name) TextView name;

        public VideoHolder(View view) {
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

    public List<T> getItems() {
        return items;
    }

    public interface VideoClickListener{
        boolean onWatchVideoListener(Video video);
    }
}
