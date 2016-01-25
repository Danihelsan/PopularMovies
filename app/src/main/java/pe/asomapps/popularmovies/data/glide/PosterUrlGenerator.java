package pe.asomapps.popularmovies.data.glide;

import pe.asomapps.popularmovies.BuildConfig;

/**
 * Created by Danihelsan
 */
public class PosterUrlGenerator {
    public String provideImageUrl(String imagePath, int width){
        String widthPath;
        if (width <= 138)
            widthPath = "/w92";
        else if (width <= 231)
            widthPath = "/w154";
        else if (width <= 278)
            widthPath = "/w185";
        else if (width <= 513)
            widthPath = "/w342";
        else if (width <= 750)
            widthPath = "/w500";
        else if (width <= 1170)
            widthPath = "/w780";
        else
            widthPath = "/original";
        return new StringBuilder().append(BuildConfig.POSTER_BASE_URL).append(widthPath).append(imagePath).toString();
    }
}