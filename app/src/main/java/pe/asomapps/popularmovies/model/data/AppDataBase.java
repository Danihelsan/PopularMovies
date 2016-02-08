package pe.asomapps.popularmovies.model.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

import pe.asomapps.popularmovies.BuildConfig;

/**
 * Created by Danihelsan
 */
@Database(version = BuildConfig.DATABASE_VERSION)
public final class AppDataBase {
    @Table(MovieColumns.class)
    public static final String MOVIES = "movies";
    @Table(VideoColumns.class)
    public static final String VIDEOS = "videos";
    @Table(ReviewColumns.class)
    public static final String REVIEWS = "reviews";
}
