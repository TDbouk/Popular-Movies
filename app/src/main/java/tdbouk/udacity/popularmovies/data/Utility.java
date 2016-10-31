package tdbouk.udacity.popularmovies.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import tdbouk.udacity.popularmovies.DetailActivity;
import tdbouk.udacity.popularmovies.R;

/**
 * Created by toufik on 10/22/2016.
 */

public class Utility {

    public static String getSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.PREFS_SORT_BY, context.getResources().getStringArray(R.array.list_preference_entry_values)[0]);
    }

    public static void setSortOrder(Context context, String sortOrder) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(Constants.PREFS_SORT_BY, sortOrder).apply();
    }

    public static Intent makeMovieDetailIntent(Context context, Movie movie) {
        Intent movieIntent = new Intent(context, DetailActivity.class);
        movieIntent.putExtra(Constants.INTENT_MOVIE, movie);
        return movieIntent;
    }
}
