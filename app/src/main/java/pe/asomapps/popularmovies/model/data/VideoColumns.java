package pe.asomapps.popularmovies.model.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Danihelsan
 */
public interface VideoColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    String VIDEO_ID = "videoId";

    @DataType(DataType.Type.INTEGER)
    String MOVIE_ID = "movieId";

    @DataType(DataType.Type.TEXT)
    String SITE = "site";

    @DataType(DataType.Type.TEXT)
    String NAME = "name";

    @DataType(DataType.Type.TEXT)
    String KEY = "key";
}
