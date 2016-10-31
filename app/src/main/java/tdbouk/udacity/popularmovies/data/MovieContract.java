package tdbouk.udacity.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by toufik on 10/15/2016.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "tdbouk.udacity.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_MOVIE_TRAILER = "trailer";
    public static final String PATH_MOVIE_REVIEW = "review";
    public static final String PATH_MOVIE_FAVORITE = "favorite";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_DESCRIPTION = "movie_description";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMoviefromMovieId(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static Uri buildMovieTitle(String movieTitle) {
            return CONTENT_URI.buildUpon().appendPath(movieTitle).build();
        }

        public static String getMovieTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILER;

        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_TRAILER = "movie_trailer";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFrimUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEW;

        public static final String TABLE_NAME = "review";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_REVIEW = "movie_review";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_FAVORITE;

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_DESCRIPTION = "movie_description";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMoviefromMovieId(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}
