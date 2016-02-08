package pe.asomapps.popularmovies.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import pe.asomapps.popularmovies.model.data.MovieColumns;
import pe.asomapps.popularmovies.model.responses.ReviewsResponse;
import pe.asomapps.popularmovies.model.responses.VideosResponse;

/**
 * Created by Danihelsan
 */
public class Movie implements Parcelable {
    @SerializedName("poster_path")
    private String posterPath;
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    private int runtime;
    private Genre[] genres;
    private int id;
    @SerializedName("original_title")
    private String originalTitle;
    private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private float popularity;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("vote_average")
    private float voteAverage;
    private boolean favorited = false;
    private VideosResponse videos;
    private ReviewsResponse reviews;

    public Movie(){}

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public Genre[] getGenres() {
        return genres;
    }

    public void setGenres(Genre[] genres) {
        this.genres = genres;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public VideosResponse getVideos() {
        return videos;
    }

    public void setVideos(VideosResponse videos) {
        this.videos = videos;
    }

    public ReviewsResponse getReviews() {
        return reviews;
    }

    public void setReviews(ReviewsResponse reviews) {
        this.reviews = reviews;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    protected Movie(Parcel in) {
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        runtime = in.readInt();
        id = in.readInt();
        originalTitle = in.readString();
        title = in.readString();
        backdropPath = in.readString();
        popularity = in.readFloat();
        voteCount = in.readInt();
        voteAverage = in.readFloat();
        favorited = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeInt(runtime);
        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeFloat(popularity);
        dest.writeInt(voteCount);
        dest.writeFloat(voteAverage);
        dest.writeByte((byte) (favorited ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public static ContentValues getContentValue(Movie movie) {
        ContentValues movieContentValues = new ContentValues();
        movieContentValues.put(MovieColumns._ID, movie.getId());
        movieContentValues.put(MovieColumns.OVERVIEW, movie.getOverview());
        movieContentValues.put(MovieColumns.RELEASE_DATE, movie.getReleaseDate());
        movieContentValues.put(MovieColumns.RUNTIME, movie.getRuntime());
        movieContentValues.put(MovieColumns.POSTER_PATH, movie.getPosterPath());
        movieContentValues.put(MovieColumns.BACK_DROP_PATH, movie.getBackdropPath());
        movieContentValues.put(MovieColumns.POPULARITY, movie.getPopularity());
        movieContentValues.put(MovieColumns.TITLE, movie.getTitle());
        movieContentValues.put(MovieColumns.VOTE_AVERAGE, movie.getVoteAverage());
        movieContentValues.put(MovieColumns.VOTE_COUNT, movie.getVoteCount());
        movieContentValues.put(MovieColumns.ORIGINAL_TITLE, movie.getOriginalTitle());
        movieContentValues.put(MovieColumns.FAVORITED, movie.isFavorited() ? 1 : 0);
        return movieContentValues;
    }
}