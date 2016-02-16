package pe.asomapps.popularmovies.data.helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import pe.asomapps.popularmovies.App;
import pe.asomapps.popularmovies.model.Movie;
import pe.asomapps.popularmovies.model.Review;
import pe.asomapps.popularmovies.model.Video;
import pe.asomapps.popularmovies.model.data.MovieColumns;
import pe.asomapps.popularmovies.model.data.ReviewColumns;
import pe.asomapps.popularmovies.model.data.VideoColumns;
import pe.asomapps.popularmovies.model.data.AppProvider;
import pe.asomapps.popularmovies.model.responses.ReviewsResponse;
import pe.asomapps.popularmovies.model.responses.VideosResponse;

/**
 * Created by Danihelsan
 */
public class DataBaseHelper {
    @Inject
    ContentResolver resolver;

    public DataBaseHelper(App app) {
        app.getComponent().inject(this);
    }

    public List<Movie> getMoviesFromCursor(Cursor movieCursor) {
        List<Movie> movies = new ArrayList<>();
        if (movieCursor!=null){
            while (movieCursor.moveToNext()) {
                Movie movie = getMovieFromCursor(movieCursor);
                movies.add(movie);
                movie.setVideos(getVideos(movie.getId()));
                movie.setReviews(getReviews(movie.getId()));
            }
        }
        return movies;
    }

    @NonNull
    public ReviewsResponse getReviews(int movieId) {
        Cursor reviewCursor = resolver.query(AppProvider.REVIEWS.withMovieId(movieId), null, null, null, null);
        List<Review> reviews = new ArrayList<>();
        if (reviewCursor != null) {
            while (reviewCursor.moveToNext()) {
                Review review = new Review();
                review.setId(reviewCursor.getString(reviewCursor.getColumnIndex(ReviewColumns.REVIEW_ID)));
                review.setAuthor(reviewCursor.getString(reviewCursor.getColumnIndex(ReviewColumns.AUTHOR)));
                review.setContent(reviewCursor.getString(reviewCursor.getColumnIndex(ReviewColumns.CONTENT)));
                review.setUrl(reviewCursor.getString(reviewCursor.getColumnIndex(ReviewColumns.URL)));
                reviews.add(review);
            }
            reviewCursor.close();
        }

        ReviewsResponse response = new ReviewsResponse();
        response.updateResults(reviews);
        return response;
    }

    @NonNull
    public VideosResponse getVideos(int movieId) {
        Cursor videoCursor = resolver.query(AppProvider.VIDEOS.withMovieId(movieId), null, null, null, null);
        List<Video> videos = new ArrayList<>();
        if (videoCursor != null) {
            while (videoCursor.moveToNext()) {
                Video video = new Video();
                video.setId(videoCursor.getString(videoCursor.getColumnIndex(VideoColumns.VIDEO_ID)));
                video.setKey(videoCursor.getString(videoCursor.getColumnIndex(VideoColumns.KEY)));
                video.setName(videoCursor.getString(videoCursor.getColumnIndex(VideoColumns.NAME)));
                video.setSite(videoCursor.getString(videoCursor.getColumnIndex(VideoColumns.SITE)));
                videos.add(video);
            }
            videoCursor.close();
        }
        VideosResponse response = new VideosResponse();
        response.setResults(videos);
        return response;
    }

    private Movie getMovieFromCursor(Cursor movieCursor) {
        Movie movie = new Movie();
        movie.setId(movieCursor.getInt(movieCursor.getColumnIndex(MovieColumns._ID)));
        movie.setOverview(movieCursor.getString(movieCursor.getColumnIndex(MovieColumns.OVERVIEW)));
        movie.setReleaseDate(movieCursor.getString(movieCursor.getColumnIndex(MovieColumns.RELEASE_DATE)));
        movie.setPosterPath(movieCursor.getString(movieCursor.getColumnIndex(MovieColumns.POSTER_PATH)));
        movie.setBackdropPath(movieCursor.getString(movieCursor.getColumnIndex(MovieColumns.BACK_DROP_PATH)));
        movie.setRuntime(movieCursor.getInt(movieCursor.getColumnIndex(MovieColumns.RUNTIME)));
        movie.setPopularity(movieCursor.getFloat(movieCursor.getColumnIndex(MovieColumns.POPULARITY)));
        movie.setTitle(movieCursor.getString(movieCursor.getColumnIndex(MovieColumns.TITLE)));
        movie.setVoteAverage(movieCursor.getFloat(movieCursor.getColumnIndex(MovieColumns.VOTE_AVERAGE)));
        movie.setVoteCount(movieCursor.getInt(movieCursor.getColumnIndex(MovieColumns.VOTE_COUNT)));
        movie.setOriginalTitle(movieCursor.getString(movieCursor.getColumnIndex(MovieColumns.ORIGINAL_TITLE)));
        movie.setFavorited(movieCursor.getInt(movieCursor.getColumnIndex(MovieColumns.FAVORITED)) > 0);
        return movie;
    }

    public long insertMovieToFavorites(Movie movie) {
        long movieId = insertMovieToFavorites(Movie.getContentValue(movie));

        if (movie.getReviews()!=null && movie.getReviews().getResults()!=null && !movie.getReviews().getResults().isEmpty()) {
            insertReviews(movie);
        }
        if (movie.getVideos()!=null && movie.getVideos().getResults()!=null && !movie.getVideos().getResults().isEmpty()) {
            insertVideos(movie);
        }
        return movieId;
    }

    private long insertMovieToFavorites(ContentValues movieContentValues) {
        Uri uri = resolver.insert(AppProvider.MOVIES.MOVIES_URI, movieContentValues);
        return ContentUris.parseId(uri);
    }

    private void insertReviews(Movie movie) {
        List<Review> reviews =  movie.getReviews().getResults();
        if (reviews.get(0)==null){
            reviews = reviews.subList(1,reviews.size());
        }
        ContentValues[] contentValues = new ContentValues[reviews.size()];
        for (int i = 0; i < reviews.size(); i++) {
            contentValues[i] = Review.getContentValue(reviews.get(i), movie.getId());
        }
        resolver.bulkInsert(AppProvider.REVIEWS.REVIEWS_URI, contentValues);
    }

    public void insertReviews(List<Review> reviews, Movie movie) {
        if (reviews.get(0)==null){
            reviews = reviews.subList(1,reviews.size());
        }
        ContentValues[] contentValues = new ContentValues[reviews.size()];
        for (int i = 0; i < reviews.size(); i++) {
            contentValues[i] = Review.getContentValue(reviews.get(i), movie.getId());
        }
        resolver.bulkInsert(AppProvider.REVIEWS.REVIEWS_URI, contentValues);
    }

    private void insertVideos(Movie movie) {
        List<Video> videos =  movie.getVideos().getResults();
        if (videos.get(0)==null){
            videos = videos.subList(1,videos.size());
        }
        ContentValues[] contentValues = new ContentValues[videos.size()];
        for (int i = 0; i < videos.size(); i++) {
            contentValues[i] = Video.getContentValue(videos.get(i), movie.getId());
        }
        resolver.bulkInsert(AppProvider.VIDEOS.VIDEOS_URI, contentValues);
    }

    public int deleteMovieFavorited(long movieId) {
        resolver.delete(AppProvider.VIDEOS.withMovieId(movieId), null, null);
        resolver.delete(AppProvider.REVIEWS.withMovieId(movieId), null, null);
        int number = resolver.delete(AppProvider.MOVIES.withId(movieId), null, null);
        return number;
    }

    public Movie getMovieFavorited(long movieId) {
        Cursor cursor = resolver.query(AppProvider.MOVIES.withId(movieId),null,null,null,null);
        if (cursor.getCount()==1){
            cursor.moveToNext();
            Movie movie = getMovieFromCursor(cursor);
            movie.setVideos(getVideos(movie.getId()));
            movie.setReviews(getReviews(movie.getId()));
            return movie;
        }
        return null;
    }

    public boolean isMovieFavorited(long movieId) {
        Cursor cursor = resolver.query(AppProvider.MOVIES.withId(movieId),null,null,null,null);
        if (cursor.getCount()==1){
            cursor.moveToNext();
            return cursor.getInt(cursor.getColumnIndex(MovieColumns.FAVORITED)) > 0;
        }
        return false;
    }

    public List<Movie> getFavoritedMovies() {
        Cursor cursor = resolver.query(AppProvider.MOVIES.favorited,null,null,null,null);
        return getMoviesFromCursor(cursor);
    }

    public boolean isMovieFavorited() {
        Cursor cursor = resolver.query(AppProvider.MOVIES.favorited,null,null,null,null);
        if (cursor.getCount()>0){
            return true;
        }
        return false;
    }
}