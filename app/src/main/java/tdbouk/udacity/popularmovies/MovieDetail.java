package tdbouk.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import model.Constants;
import model.Movie;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Reference the views
        TextView title = (TextView) findViewById(R.id.movie_title);
        TextView overview = (TextView) findViewById(R.id.movie_overview);
        TextView rating = (TextView) findViewById(R.id.movie_rating);
        TextView releaseDate = (TextView) findViewById(R.id.movie_release_date);
        ImageView poster = (ImageView) findViewById(R.id.movie_poster);

        // Get Intent and set data to views
        Intent activityIntent = getIntent();
        if (activityIntent != null) {
            if (activityIntent.hasExtra(Constants.INTENT_MOVIE)) {
                Movie movie = activityIntent.getParcelableExtra(Constants.INTENT_MOVIE);
                title.setText(movie.getTitle());
                overview.setText(movie.getOverview());
                rating.setText(movie.getRating());
                releaseDate.setText(movie.getReleaseDate());
                Picasso.with(this).load("https://image.tmdb.org/t/p/w500/" + movie.getPosterPath()).into(poster);
            }
        }

    }
}
