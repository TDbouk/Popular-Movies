package tdbouk.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import tdbouk.udacity.popularmovies.data.Constants;
import tdbouk.udacity.popularmovies.data.Movie;

public class DetailActivity extends AppCompatActivity {

    private DetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(Constants.INTENT_MOVIE)) {

                Movie movie = intent.getParcelableExtra(Constants.INTENT_MOVIE);

                Bundle arguments = new Bundle();
                arguments.putParcelable(DetailFragment.DETAIL_MOVIE, movie);

                fragment = new DetailFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, fragment)
                        .commit();
            }
        }
    }
}
