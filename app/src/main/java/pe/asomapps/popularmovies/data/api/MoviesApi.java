package pe.asomapps.popularmovies.data.api;

import pe.asomapps.popularmovies.model.responses.DiscoverMoviesResponse;
import pe.asomapps.popularmovies.model.responses.MovieResponse;
import pe.asomapps.popularmovies.model.responses.ReviewsResponse;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author Danihelsan
 */
public interface MoviesApi {
     String DETAIL_APPEND = "videos";

    @GET("discover/movie")
    Call<DiscoverMoviesResponse> loadMovies(@Query("sort_by") String sortBy, @Query("page") int page);

    @GET("movie/{id}")
    Call<MovieResponse> loadMovieDetail(@Path("id") int movieId, @Query("append_to_response") String append_to_response);

    @GET("movie/{id}/reviews")
    Call<ReviewsResponse> loadReviews(@Path("id") int movieId);


}
