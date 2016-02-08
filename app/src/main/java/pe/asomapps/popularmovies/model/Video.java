package pe.asomapps.popularmovies.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import pe.asomapps.popularmovies.model.data.VideoColumns;

/**
 * Created by Danihelsan
 */
public class Video implements Parcelable{
    private String id;
    private String key;
    private String name;
    private String site;

    public Video(){}

    protected Video(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
    }

    public static ContentValues getContentValue(Video video, long movieId) {
        ContentValues videoVContentValues = new ContentValues();
        videoVContentValues.put(VideoColumns.VIDEO_ID, video.getId());
        videoVContentValues.put(VideoColumns.MOVIE_ID, movieId);
        videoVContentValues.put(VideoColumns.NAME, video.getName());
        videoVContentValues.put(VideoColumns.SITE, video.getSite());
        videoVContentValues.put(VideoColumns.KEY, video.getKey());

        return videoVContentValues;
    }

    public String getVideoPath() {
        if (site != null && key != null) {
            String url = site;
            if (site.equalsIgnoreCase("youtube")) {
                url = String.format("https://www.youtube.com/watch?v=%s", key);
            }
            return url;
        }
        return null;
    }
}