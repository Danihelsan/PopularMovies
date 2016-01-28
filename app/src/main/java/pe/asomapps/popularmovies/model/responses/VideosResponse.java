package pe.asomapps.popularmovies.model.responses;

import java.util.List;

import pe.asomapps.popularmovies.model.Video;

/**
 * Created by Danihelsan
 */
public class VideosResponse {
    private List<Video> results;

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
