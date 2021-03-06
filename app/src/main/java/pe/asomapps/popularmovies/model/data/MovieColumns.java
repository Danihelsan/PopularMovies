package pe.asomapps.popularmovies.model.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Danihelsan
 */
public interface MovieColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    String OVERVIEW = "overview";

    @DataType(DataType.Type.TEXT)
    String RELEASE_DATE = "releaseDate";

    @DataType(DataType.Type.INTEGER)
    String RUNTIME = "runtime"
            ;
    @DataType(DataType.Type.TEXT)
    String POSTER_PATH = "posterPath";

    @DataType(DataType.Type.TEXT)
    String BACK_DROP_PATH = "backDropPath";

    @DataType(DataType.Type.REAL)
    String POPULARITY = "popularity";

    @DataType(DataType.Type.TEXT)
    String TITLE = "title";

    @DataType(DataType.Type.REAL)
    String VOTE_AVERAGE = "voteAverage";

    @DataType(DataType.Type.INTEGER)
    String BUDGET = "budget";

    @DataType(DataType.Type.INTEGER)
    String VOTE_COUNT = "voteCount";

    @DataType(DataType.Type.TEXT)
    String ORIGINAL_TITLE = "originalTitle";

    @DataType(DataType.Type.TEXT)
    String ORIGINAL_LANGUAGE = "originalLanguage";

    @DataType(DataType.Type.INTEGER)
    String VIDEO = "video";

    @DataType(DataType.Type.INTEGER)
    String FAVORITED = "favorited";
}
