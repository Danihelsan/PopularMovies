package pe.asomapps.popularmovies.data.api;

import java.util.List;

import pe.asomapps.popularmovies.model.Video;
import pe.asomapps.popularmovies.model.responses.DiscoverMoviesResponse;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author Danihelsan
 */
public interface MoviesApi {
    enum SortOption{
        POP_DESC("popularity.desc"),
        REV_DESC("revenue.desc"),
        VOT_DESC("vote_count.desc");

        private String value;
        SortOption(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @GET("discover/movie")
    Call<DiscoverMoviesResponse> loadMovies(@Query("sort_by") String sortBy, @Query("page") int page);

    @GET("movie/{id}/videos")
    Call<List<Video>> loadVideosFromMovie(@Path("id") int movieId);

    @GET("movie/{id}/reviews")
    Call<List<Video>> loadReviewsFromMovie(@Path("id") int movieId);

}
