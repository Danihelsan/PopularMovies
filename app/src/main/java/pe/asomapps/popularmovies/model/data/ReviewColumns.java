package pe.asomapps.popularmovies.model.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Danihelsan
 */
public interface ReviewColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    String REVIEW_ID = "reviewId";

    @DataType(DataType.Type.INTEGER)
    String MOVIE_ID = "movieId";

    @DataType(DataType.Type.TEXT)
    String AUTHOR = "author";

    @DataType(DataType.Type.TEXT)
    String CONTENT = "content";

    @DataType(DataType.Type.TEXT)
    String URL = "url";
}
