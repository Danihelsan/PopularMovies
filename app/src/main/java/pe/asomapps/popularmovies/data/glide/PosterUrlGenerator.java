package pe.asomapps.popularmovies.data.glide;

import pe.asomapps.popularmovies.BuildConfig;

/**
 * @author Danihelsan
 */
public class PosterUrlGenerator {
    public static final String provideImageUrl(String imagePath, int width){
        String widthPath;
        if (width <= 92)
            widthPath = "/w92";
        else if (width <= 154)
            widthPath = "/w154";
        else if (width <= 185)
            widthPath = "/w185";
        else if (width <= 342)
            widthPath = "/w342";
        else if (width <= 500)
            widthPath = "/w500";
        else if (width <= 780)
            widthPath = "/w780";
        else
            widthPath = "/original";
        return new StringBuilder().append(BuildConfig.POSTER_BASE_URL).append(widthPath).append(imagePath).toString();
    }
}
