package pe.asomapps.popularmovies.data.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * @author Danihelsan
 *
 * Using the following information:
 * Custom Glide module - https://github.com/bumptech/glide/wiki/Configuration
 * Custom image size to download - https://github.com/bumptech/glide/wiki/Downloading-custom-sizes-with-Glide
 * Custom https://github.com/google/iosched/blob/master/doc/IMAGES.md
 */
public class MovieGlideSetup implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(String.class, InputStream.class, new PosterUrlLoader.PosterUrlFactory());
    }
}
