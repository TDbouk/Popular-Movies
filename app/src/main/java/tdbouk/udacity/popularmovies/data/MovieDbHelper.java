package tdbouk.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by toufik on 10/15/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 14;
    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                // might need to be unique
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MovieContract.TrailerEntry.COLUMN_MOVIE_TRAILER + " TEXT NOT NULL " +
                // Set up the location column as a foreign key to location table.
//                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
//                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + ") " +
                " );";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_MOVIE_REVIEW + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
