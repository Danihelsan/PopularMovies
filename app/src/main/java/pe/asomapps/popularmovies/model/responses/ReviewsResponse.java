package pe.asomapps.popularmovies.model.responses;

import java.util.List;

import pe.asomapps.popularmovies.model.Review;

/**
 * Created by Danihelsan
 */
public class ReviewsResponse extends PagedResponse<Review> {
    public void updateResults(List<Review> reviews) {
        this.setResults(reviews);
        this.setTotalResults(reviews.size());
    }
}
