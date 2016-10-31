package tdbouk.udacity.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import tdbouk.udacity.popularmovies.BuildConfig;
import tdbouk.udacity.popularmovies.R;
import tdbouk.udacity.popularmovies.data.Constants;
import tdbouk.udacity.popularmovies.data.MovieContract;
import tdbouk.udacity.popularmovies.data.Utility;

public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the movie db, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;


    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {
            String sortBy = Utility.getSortBy(getContext());
            if (!sortBy.equals(getContext().getResources().getStringArray(R.array.list_preference_entry_values)[2])) {

                getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                getContext().getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI, null, null);
                getContext().getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI, null, null);

                URL moviesUrl = generateTheMobieDbUrl(sortBy);
                String moviesJsonStr = openConnectionToUrl(moviesUrl);
                if (moviesJsonStr != null)
                    getMoviesDataFromJson(moviesJsonStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String openConnectionToUrl(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {
            // Create the request to MovieDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();
//            getMoviesDataFromJson(moviesJsonStr);
            return moviesJsonStr;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    return null;
                }
            }
        }
    }

    private URL generateTheMobieDbUrl(String sort) {

        String sortBy = sort;
        String apiKey = BuildConfig.MOVIE_DB_API_KEY;

        try {
            Uri builtUri = Uri.parse(Constants.MOVIE_DB_BASE_URL).buildUpon()
                    .appendPath(sortBy)
                    .appendQueryParameter(Constants.APIKEY_PARAM, apiKey).build();

            URL url = new URL(builtUri.toString());

            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private URL generateMovieTrailerUrl(String id) {

        String movieId = id;
        String apiKey = BuildConfig.MOVIE_DB_API_KEY;

        try {
            Uri builtUri = Uri.parse(Constants.MOVIE_DB_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(Constants.MOVIE_TRAILERS)
                    .appendQueryParameter(Constants.APIKEY_PARAM, apiKey).build();

            URL url = new URL(builtUri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private URL generateMovieReviewUrl(String id) {

        String movieId = id;
        String apiKey = BuildConfig.MOVIE_DB_API_KEY;

        try {
            Uri builtUri = Uri.parse(Constants.MOVIE_DB_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(Constants.MOVIE_REVIEWS)
                    .appendQueryParameter(Constants.APIKEY_PARAM, apiKey).build();

            URL url = new URL(builtUri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int addMovieTrailersToDb(String movieId) {
        try {
            URL movieTrailerUrl = generateMovieTrailerUrl(movieId);
            String trailerJsonStr = openConnectionToUrl(movieTrailerUrl);

            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailersArray = trailerJson.getJSONArray(Constants.MOVIE_LIST);
            Vector<ContentValues> trailersCvVector = new Vector<ContentValues>(trailersArray.length());

            for (int j = 0; j < trailersArray.length(); j++) {

                JSONObject trailerJasonObject = trailersArray.getJSONObject(j);
                String key = trailerJasonObject.getString(Constants.MOVIE_YOUTUBE_KEY);
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_TRAILER, key);
                trailersCvVector.add(trailerValues);
            }

            if (trailersCvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[trailersCvVector.size()];
                trailersCvVector.toArray(cvArray);
                return getContext().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    private int addMovieReviewToDb(String movieId) {
        try {
            URL movieReviewUrl = generateMovieReviewUrl(movieId);
            String reviewJsonStr = openConnectionToUrl(movieReviewUrl);

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(Constants.MOVIE_LIST);
            Vector<ContentValues> reviewsCvVector = new Vector<ContentValues>(reviewArray.length());

            for (int j = 0; j < reviewArray.length(); j++) {

                JSONObject reviewJasonObject = reviewArray.getJSONObject(j);
                String content = reviewJasonObject.getString(Constants.MOVIE_REVIEW);
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_REVIEW, content);
                reviewsCvVector.add(reviewValues);
            }

            if (reviewsCvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[reviewsCvVector.size()];
                reviewsCvVector.toArray(cvArray);
                return getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    private void getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        try {
            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = movieJson.getJSONArray(Constants.MOVIE_LIST);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieJasonObject = moviesArray.getJSONObject(i);

                String id = movieJasonObject.getString(Constants.MOVIE_ID);
                String overview = movieJasonObject.getString(Constants.MOVIE_OVERVIEW);
                String poster = movieJasonObject.getString(Constants.MOVIE_POSTER_PATH);
                String rating = movieJasonObject.getString(Constants.MOVIE_VOTE_AVERAGE);
                String releaseDate = movieJasonObject.getString(Constants.MOVIE_RELEASE_DATE);
                String title = movieJasonObject.getString(Constants.MOVIE_ORIGINAL_TITLE);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, poster);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, rating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);

                cVVector.add(movieValues);

                addMovieTrailersToDb(id);
                addMovieReviewToDb(id);
            }

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopularMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}