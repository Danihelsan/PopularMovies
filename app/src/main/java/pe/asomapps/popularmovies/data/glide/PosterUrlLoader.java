package pe.asomapps.popularmovies.data.glide;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

/**
 * @author Danihelsan
 */
public class PosterUrlLoader extends BaseGlideUrlLoader<String> {
    private PosterUrlGenerator urlGenerator;
    public PosterUrlLoader(Context context, ModelCache<String, GlideUrl> modelCache) {
        super(context, modelCache);
    }

    @Override
    protected String getUrl(String imagePath, int width, int height) {
        if (urlGenerator==null){
            urlGenerator = new PosterUrlGenerator();
        }
        String imageUrl = urlGenerator.provideImageUrl(imagePath, width);
        return imageUrl;
    }

    public static class PosterUrlFactory implements ModelLoaderFactory<String, InputStream> {
        private final ModelCache<String, GlideUrl> modelCache = new ModelCache<>(100);

        @Override
        public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new PosterUrlLoader(context, modelCache);
        }

        @Override
        public void teardown() {
        }
    }
}
