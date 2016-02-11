package pe.asomapps.popularmovies.model.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Danihelsan
 */
@ContentProvider(authority = AppProvider.AUTHORITY, database = AppDataBase.class)
public class AppProvider {

    public static final String AUTHORITY = "pe.asomapps.popularmovies.model.data.AppProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String FAVORITED = "favorited";
        String MOVIES = "movies";
        String REVIEWS = "reviews";
        String VIDEOS = "videos";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = AppDataBase.MOVIES)
    public static class MOVIES {

        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns.VOTE_AVERAGE + " DESC")
        public static final Uri MOVIES_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.MOVIES, String.valueOf(id));
        }

        @ContentUri(
                path = Path.FAVORITED,
                type = "vnd.android.cursor.dir/favorited",
                where = MovieColumns.FAVORITED + " = 1",
                defaultSort = MovieColumns.RELEASE_DATE + " DESC")
        public static final Uri favorited = buildUri(Path.FAVORITED);
    }

    @TableEndpoint(table = AppDataBase.REVIEWS)
    public static class REVIEWS {

        @ContentUri(
                path = Path.REVIEWS,
                type = "vnd.android.cursor.dir/review",
                defaultSort = ReviewColumns._ID + " ASC")
        public static final Uri REVIEWS_URI = buildUri(Path.REVIEWS);

        @InexactContentUri(
                name = "REVIEW_ID",
                path = Path.REVIEWS + "/#",
                type = "vnd.android.cursor.item/review",
                whereColumn = ReviewColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withMovieId(long movieId) {
            return buildUri(Path.REVIEWS, String.valueOf(movieId));
        }
    }

    @TableEndpoint(table = AppDataBase.VIDEOS)
    public static class VIDEOS {

        @ContentUri(
                path = Path.VIDEOS,
                type = "vnd.android.cursor.dir/video",
                defaultSort = VideoColumns._ID + " ASC")
        public static final Uri VIDEOS_URI = buildUri(Path.VIDEOS);

        @InexactContentUri(
                name = "VIDEO_ID",
                path = Path.VIDEOS + "/#",
                type = "vnd.android.cursor.item/video",
                whereColumn = VideoColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withMovieId(long movieId) {
            return buildUri(Path.VIDEOS, String.valueOf(movieId));
        }
    }
}
