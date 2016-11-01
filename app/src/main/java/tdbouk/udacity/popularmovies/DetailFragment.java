package tdbouk.udacity.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import tdbouk.udacity.popularmovies.data.Movie;
import tdbouk.udacity.popularmovies.data.MovieContract;

public class DetailFragment extends android.support.v4.app.Fragment {

    private TextView mTextViewTitle;
    private TextView mTextViewOverview;
    private TextView mTextViewRating;
    private TextView mTextViewReleaseDate;
    private ImageView mImageViewPoster;
    private Button mButtonFavorites;
    private Button mButtonReviews;
    private Button mButtonTrailers;
    private TrailersDialogFragment mDialogFragmentTrailers;
    private ReviewsDialogFragment mDialogFragmentReviews;
    private ArrayList<String> Trailerslist = new ArrayList<>();
    private ArrayList<String> Reviewlist = new ArrayList<>();
    private Movie mMovie;
    static final String DETAIL_MOVIE = "movie";
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mMovie != null) {
            mShareActionProvider.setShareIntent(createShareYoutubeIntent());
        }
    }

    /**
     * If a Trailer is niot fetched yet, use the movies title
     *
     * @return intent
     */
    private Intent createShareYoutubeIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        if (Trailerslist.size() > 0) {
            Uri link = Uri.parse("http://www.youtube.com/watch?v=" + Trailerslist.get(0));
            shareIntent.putExtra(Intent.EXTRA_TEXT, link.toString());
        } else
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getTitle());
        return shareIntent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mTextViewTitle = (TextView) rootView.findViewById(R.id.text_movie_title);
        mTextViewOverview = (TextView) rootView.findViewById(R.id.text_movie_overview);
        mTextViewRating = (TextView) rootView.findViewById(R.id.text_movie_rating);
        mTextViewReleaseDate = (TextView) rootView.findViewById(R.id.text_movie_release_date);
        mImageViewPoster = (ImageView) rootView.findViewById(R.id.image_movie_poster);
        mButtonFavorites = (Button) rootView.findViewById(R.id.button_movie_favorite);
        mButtonReviews = (Button) rootView.findViewById(R.id.button_movie_reviews);
        mButtonTrailers = (Button) rootView.findViewById(R.id.button_movie_trailer);

        if (arguments != null) {
            mMovie = arguments.getParcelable(DetailFragment.DETAIL_MOVIE);

            mTextViewTitle.setText(mMovie.getTitle());
            mTextViewOverview.setText(mMovie.getOverview());
            mTextViewRating.setText(mMovie.getRating() + "/10");
            mTextViewReleaseDate.setText(mMovie.getReleaseDate());
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500/" + mMovie.getPosterPath())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mImageViewPoster);

            getMovieTrailer(mMovie.getId());
            getMovieReview(mMovie.getId());

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareYoutubeIntent());
            }

            if (checkIfMovieIsInFavorites(mMovie.getId())) {
                // movie exists in favorites
                mButtonFavorites.setText(R.string.favorite_unchecked);
                mButtonFavorites.setTag("checked");
            } else {
                mButtonFavorites.setText(R.string.favorite_checked);
                mButtonFavorites.setTag("uncheked");
            }
        }
        setClickListeners();

        return rootView;
    }

    private void setClickListeners() {
        mButtonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("checked".equals(view.getTag())) {
                    deleteMovieFromFavorites(mMovie.getId());
                    view.setTag("unchecked");
                    ((Button) view).setText(R.string.favorite_checked);
                } else {
                    saveToFavorites(mMovie);
                    view.setTag("checked");
                    ((Button) view).setText(R.string.favorite_unchecked);
                }
            }
        });

        mButtonTrailers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTrailersList();
            }
        });

        mButtonReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewsList();
            }
        });
    }

    private void saveToFavorites(Movie movie) {
        if (movie != null) {
            String id = movie.getId();
            String overview = movie.getOverview();
            String poster = movie.getPosterPath();
            String rating = movie.getRating();
            String releaseDate = movie.getReleaseDate();
            String title = movie.getTitle();

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_DESCRIPTION, overview);
            movieValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_POSTER_PATH, poster);
            movieValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_RATING, rating);
            movieValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_TITLE, title);

            getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, movieValues);

            Toast.makeText(getActivity(), title + " added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTrailersList() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("trailer_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        mDialogFragmentTrailers = TrailersDialogFragment.newInstance(Trailerslist);
        mDialogFragmentTrailers.show(ft, "trailer_dialog");
    }

    private void showReviewsList() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("reviews_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        mDialogFragmentReviews = ReviewsDialogFragment.newInstance(Reviewlist);
        mDialogFragmentReviews.show(ft, "reviews_dialog");
    }

    private void getMovieTrailer(String movieId) {

        Cursor c = getActivity().getContentResolver().query(MovieContract.TrailerEntry.buildMovieId(movieId),
                new String[]{MovieContract.TrailerEntry.COLUMN_MOVIE_TRAILER}, null, null, null);
        if (c.getCount() > 0) {
            try {
                while (c.moveToNext()) {
                    Trailerslist.add(c.getString(c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_MOVIE_TRAILER)));
                }
            } finally {
                c.close();
            }
        }

    }

    private void getMovieReview(String movieId) {

        Cursor c = getActivity().getContentResolver().query(MovieContract.ReviewEntry.buildReviewId(movieId),
                new String[]{MovieContract.ReviewEntry.COLUMN_MOVIE_REVIEW}, null, null, null);

        if (c.getCount() > 0) {
            try {
                while (c.moveToNext()) {
                    Reviewlist.add(c.getString(c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_MOVIE_REVIEW)));
                }
            } finally {
                c.close();
            }
        }
    }

    private boolean checkIfMovieIsInFavorites(String movieId) {

        Cursor c = getActivity().getContentResolver().query(MovieContract.FavoriteEntry.buildMoviefromMovieId(movieId),
                new String[]{MovieContract.FavoriteEntry.COLUMN_MOVIE_ID}, null, null, null);
        try {
            if (c.getCount() > 0) {
                return true;
            } else {
                return false;
            }
        } finally {
            c.close();
        }
    }

    private void deleteMovieFromFavorites(String movieId) {
        getActivity().getContentResolver().delete(MovieContract.FavoriteEntry.buildMoviefromMovieId(movieId), null, null);
        Toast.makeText(getActivity(), mMovie.getTitle() + " removed from favorites", Toast.LENGTH_SHORT).show();
    }

}
