package pe.asomapps.popularmovies.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.florent37.glidepalette.GlidePalette;

import pe.asomapps.popularmovies.R;

/**
 * @author Danihelsan
 *
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ImageView image2, image1;
    TextView vibrant2, muted2, darkMuted2, lightMuted2, darkVibrant2, lightVibrant2, vibrant1, muted1, darkMuted1, lightMuted1, darkVibrant1, lightVibrant1;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        image2 = (ImageView) view.findViewById(R.id.image2);
        vibrant2 = (TextView) view.findViewById(R.id.vibrant2);
        muted2 = (TextView) view.findViewById(R.id.muted2);
        darkMuted2 = (TextView) view.findViewById(R.id.darkMuted2);
        lightMuted2 = (TextView) view.findViewById(R.id.lightMuted2);
        lightVibrant2 = (TextView) view.findViewById(R.id.lightVibrant2);
        darkVibrant2 = (TextView) view.findViewById(R.id.darkVibrant2);
        image1 = (ImageView) view.findViewById(R.id.image1);
        vibrant1 = (TextView) view.findViewById(R.id.vibrant1);
        muted1 = (TextView) view.findViewById(R.id.muted1);
        darkMuted1 = (TextView) view.findViewById(R.id.darkMuted1);
        lightMuted1 = (TextView) view.findViewById(R.id.lightMuted1);
        darkVibrant1 = (TextView) view.findViewById(R.id.darkVibrant1);
        lightVibrant1 = (TextView) view.findViewById(R.id.lightVibrant1);

        BitmapImageViewTarget target = new BitmapImageViewTarget(image1) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                super.onResourceReady(resource, glideAnimation);
                Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int defaultColor = getResources().getColor(R.color.colorPrimaryDark);
                        vibrant1.setBackgroundColor(palette.getVibrantColor(defaultColor));
                        muted1.setBackgroundColor(palette.getMutedColor(defaultColor));
                        darkVibrant1.setBackgroundColor(palette.getDarkVibrantColor(defaultColor));
                        lightVibrant1.setBackgroundColor(palette.getLightVibrantColor(defaultColor));
                        darkMuted1.setBackgroundColor(palette.getDarkMutedColor(defaultColor));
                        lightMuted1.setBackgroundColor(palette.getLightMutedColor(defaultColor));
                    }
                };
                Palette.from(resource).generate(paletteListener);
            }
        };
        String url = "http://www.imagenes-juegos.com/halo-4-imagen-i300563-i.jpg";
        Glide.with(this).load(url).asBitmap().into(target);

        Glide.with(this).load(url)
                .listener(GlidePalette.with(url)
                        .use(GlidePalette.Profile.VIBRANT)
                        .intoBackground(vibrant2)
                        .use(GlidePalette.Profile.MUTED)
                        .intoBackground(muted2)
                        .use(GlidePalette.Profile.VIBRANT_DARK)
                        .intoBackground(darkVibrant2)
                        .use(GlidePalette.Profile.VIBRANT_LIGHT)
                        .intoBackground(lightVibrant2)
                        .use(GlidePalette.Profile.MUTED_DARK)
                        .intoBackground(darkMuted2)
                        .use(GlidePalette.Profile.MUTED_LIGHT)
                        .intoBackground(lightMuted2)
                ).into(image2);
        return view;
    }
}
