package tdbouk.udacity.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;

import model.Constants;
import model.Movie;

public class MainActivity extends AppCompatActivity {

    private RecyclerView moviesGridView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private Spinner sortBySpinner;
    private ProgressDialog progressDialog;
    private ArrayList<Movie> moviesList;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current gridlayout position
        int firstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        savedInstanceState.putInt("first_visible_item_position", firstVisiblePosition);
        savedInstanceState.putInt("saved_spinner_item_position", sortBySpinner.getSelectedItemPosition());
        savedInstanceState.putParcelableArrayList("saved_list_of_movies", moviesList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the saved gridlayout position
        int firstVisiblePosition = savedInstanceState.getInt("first_visible_item_position");
        int savedSpinnerPosition = savedInstanceState.getInt("saved_spinner_item_position");
        sortBySpinner.setSelection(savedSpinnerPosition);
        sortBySpinner.setTag(savedSpinnerPosition);
        moviesList = savedInstanceState.getParcelableArrayList("saved_list_of_movies");
        if (moviesList.size() > 0) {
            adapter = new MyAdapter(moviesList, MainActivity.this);
            moviesGridView.setAdapter(adapter);
            layoutManager.scrollToPosition(firstVisiblePosition);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        moviesGridView = (RecyclerView) findViewById(R.id.movies);
        sortBySpinner = (Spinner) findViewById(R.id.sort_by_spinner);

        // allow fetching movies when the app first starts
        sortBySpinner.setSelection(0);
        sortBySpinner.setTag(1);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // don't fetch movies on rotation
                if ((int) sortBySpinner.getTag() != i) {
                    fetchMovies(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        layoutManager = new GridLayoutManager(this, 4);
        moviesGridView.setLayoutManager(layoutManager);

    }

    /**
     * Fetch list of movies from the movie db api
     *
     * @param sortOrder sort order type (0: popular, 1: highest-rated)
     */
    private void fetchMovies(int sortOrder) {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            new FetchPopularMoviesTask().execute(getResources().getStringArray(R.array.list_preference_entry_values)[sortOrder]);
        else
            Toast.makeText(this, "Internet Connection is Required.", Toast.LENGTH_SHORT).show();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<Movie> movies;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageButton mImageButton;

            public ViewHolder(View v) {
                super(v);
                mImageButton = (ImageButton) v.findViewById(R.id.item_movies_image);
            }
        }

        public MyAdapter(ArrayList<Movie> movies, Context context) {
            this.movies = movies;
            this.context = context;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_movies, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {

            Picasso.with(context).load("https://image.tmdb.org/t/p/w185/" + movies.get(position)
                    .getPosterPath()).into(holder.mImageButton);

            holder.mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (movies.get(position) != null)
                        startActivity(makeMovieDetailIntent(movies.get(position)));
                }
            });
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        private Intent makeMovieDetailIntent(Movie movie) {
            Intent movieIntent = new Intent(context, MovieDetail.class);
            movieIntent.putExtra(Constants.INTENT_MOVIE, movie);
            return movieIntent;
        }
    }

    class FetchPopularMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        String sortBy;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Loading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);

            if (movies != null) {
                moviesList = movies;
                adapter = new MyAdapter(moviesList, MainActivity.this);
                moviesGridView.setAdapter(adapter);
            }
            progressDialog.dismiss();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {
            if (strings.length == 0)
                return null;

            sortBy = strings[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {

                URL url = generateTheMobieDbUrl(sortBy);

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
                return getMovies(moviesJsonStr);


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }
            return null;
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

        private ArrayList<Movie> getMovies(String moviesJsonStr)
                throws JSONException {

            ArrayList<Movie> movies;
            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = movieJson.getJSONArray(Constants.MOVIE_LIST);

            movies = new ArrayList<>(moviesArray.length());
            Movie movie = null;

            for (int i = 0; i < moviesArray.length(); i++) {

                movie = new Movie();

                JSONObject movieJasonObject = moviesArray.getJSONObject(i);

                movie.setOverview(movieJasonObject.getString(Constants.MOVIE_OVERVIEW));
                movie.setPosterPath(movieJasonObject.getString(Constants.MOVIE_POSTER_PATH));
                movie.setRating(movieJasonObject.getString(Constants.MOVIE_VOTE_AVERAGE));
                movie.setReleaseDate(movieJasonObject.getString(Constants.MOVIE_RELEASE_DATE));
                movie.setTitle(movieJasonObject.getString(Constants.MOVIE_ORIGINAL_TITLE));

                movies.add(movie);
            }
            return movies;
        }
    }
}