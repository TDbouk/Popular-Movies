package tdbouk.udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_TITLE = 101;
    static final int MOVIE_ID = 102;

    static final int TRAILER = 200;

    static final int REVIEW = 300;
    static final int REVIEW_MOVIE_ID = 301;

    static final int FAVORITE = 400;
    static final int FAVORITE_MOVIE_ID = 401;


    private static final String sMovieTitleFromMovieSelection = MovieContract.MovieEntry.TABLE_NAME +
            "." + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " = ? ";

    private static final String sMovieIdFromTrailerSelection =
            MovieContract.TrailerEntry.TABLE_NAME +
                    "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sMovieIdFromReviewSelection =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sMovieIdFromFavoriteSelection =
            MovieContract.FavoriteEntry.TABLE_NAME +
                    "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ";

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_TITLE);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_TRAILER + "/*", MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_REVIEW + "/*", REVIEW_MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_FAVORITE, FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_FAVORITE + "/*", FAVORITE_MOVIE_ID);

        return matcher;
    }

    public MovieProvider() {
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_TITLE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_MOVIE_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // make delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE:
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIE_ID: {
                selectionArgs = new String[]{MovieContract.FavoriteEntry.getMovieIdFromUri(uri)};
                selection = sMovieIdFromFavoriteSelection;
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case FAVORITE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavoriteEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MOVIE_TITLE: {
                selection = sMovieTitleFromMovieSelection;
                selectionArgs = new String[]{MovieContract.MovieEntry.getMovieTitleFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MOVIE_ID: {
                selection = sMovieIdFromTrailerSelection;
                selectionArgs = new String[]{MovieContract.TrailerEntry.getMovieIdFrimUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            case REVIEW_MOVIE_ID: {
                selection = sMovieIdFromReviewSelection;
                selectionArgs = new String[]{MovieContract.ReviewEntry.getMovieIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            case FAVORITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            case FAVORITE_MOVIE_ID: {
                selection = sMovieIdFromFavoriteSelection;
                selectionArgs = new String[]{MovieContract.FavoriteEntry.getMovieIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVORITE:
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
