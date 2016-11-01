package tdbouk.udacity.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import tdbouk.udacity.popularmovies.data.Movie;
import tdbouk.udacity.popularmovies.data.MovieContract;
import tdbouk.udacity.popularmovies.data.Utility;
import tdbouk.udacity.popularmovies.sync.PopularMoviesSyncAdapter;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] MOVIE_PROJECTION = new String[]{
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE
    };
    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_MOVIE_TITLE = 1;
    private static final int INDEX_MOVIE_DESCRIPTION = 2;
    private static final int INDEX_MOVIE_POSTER_PATH = 3;
    private static final int INDEX_MOVIE_RATING = 4;
    private static final int INDEX_MOVIE_RELEASE_DATE = 5;
    private final int MOVIE_LOADER = 0;
    private RecyclerView moviesGridView;
    private RecyclerView.Adapter adapter;
    private Spinner sortBySpinner;
    private int mSpinner_position = 0;
    private int mGridviewItemSelectedPosition = -1;
    private OnFragmentInteractionListener mListener;

    public MoviesFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSpinner_position == 2) {
            getMyFavoritesMovieList();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        sortBySpinner.setSelection(mSpinner_position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.spinner_sortby);
        sortBySpinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.list_preference_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortBySpinner.setAdapter(adapter);
        SpinnerInteractionListener listener = new SpinnerInteractionListener();
        sortBySpinner.setOnTouchListener(listener);
        sortBySpinner.setOnItemSelectedListener(listener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mSpinner_position = sortBySpinner.getSelectedItemPosition();
        outState.putInt("spinner_position", mSpinner_position);
        outState.putInt("gridview_position", mGridviewItemSelectedPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        moviesGridView = (RecyclerView) rootView.findViewById(R.id.movies);

        moviesGridView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new MyAdapter(getActivity());
        moviesGridView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mSpinner_position = savedInstanceState.getInt("spinner_position");
            mGridviewItemSelectedPosition = savedInstanceState.getInt("gridview_position");
        }
    }

    public void onButtonPressed(Movie movie) {
        if (mListener != null) {
            mListener.onFragmentInteraction(movie);
        }
    }

    public void setDefaultView(Movie movie) {
        if (mListener != null) {
            mListener.setDefaultMasterDetailView(movie);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getMyFavoritesMovieList() {
        Cursor c = getActivity().getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                MOVIE_PROJECTION, null, null, null);
        if (c.getCount() > 0) {
            ((MyAdapter) adapter).swapCursor(c);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // cursor returned is sometimes positioned at the last entry
        // mostly on a screen configuration change
        // reset the cursor position just to be on the safe side
        if (data.getCount() > 0) {
            data.moveToPosition(-1);
            ((MyAdapter) adapter).swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((MyAdapter) adapter).swapCursor(null);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Movie movie);

        void setDefaultMasterDetailView(Movie movie);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private Context mContext;
        private Cursor mCursor;

        public MyAdapter(Context context) {
            mContext = context;
        }

        public void swapCursor(Cursor newCursor) {
            mCursor = newCursor;
            notifyDataSetChanged();
        }

        public Cursor getCursor() {
            return mCursor;
        }

        private Movie createMovieFromCursorItem(int position) {
            mCursor.moveToPosition(position);

            String title = mCursor.getString(INDEX_MOVIE_TITLE);
            String description = mCursor.getString(INDEX_MOVIE_DESCRIPTION);
            String releaseDate = mCursor.getString(INDEX_MOVIE_RELEASE_DATE);
            String rating = mCursor.getString(INDEX_MOVIE_RATING);
            String posterPath = mCursor.getString(INDEX_MOVIE_POSTER_PATH);
            String id = mCursor.getString(INDEX_MOVIE_ID);

            final Movie movie = new Movie();
            movie.setId(id);
            movie.setPosterPath(posterPath);
            movie.setTitle(title);
            movie.setReleaseDate(releaseDate);
            movie.setRating(rating);
            movie.setOverview(description);

            return movie;
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
            final Movie movie = createMovieFromCursorItem(position);
            if (mGridviewItemSelectedPosition == -1 && position == 0)
                setDefaultView(movie);

            Glide.with(mContext)
                    .load("https://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(holder.mImageButton);

            holder.mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onButtonPressed(movie);
                    mGridviewItemSelectedPosition = position;
                }
            });
        }

        @Override
        public int getItemCount() {
            if (null == mCursor) return 0;
            return mCursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageButton mImageButton;

            public ViewHolder(View v) {
                super(v);
                mImageButton = (ImageButton) v.findViewById(R.id.item_movies_image);
            }
        }
    }

    /**
     * A custom spinner listener to differentiate and only allow user interaction events
     */
    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (userSelect) {
                Utility.setSortOrder(getActivity(), getResources().getStringArray(R.array.list_preference_entry_values)[pos]);
                if (pos == 2) {
                    getMyFavoritesMovieList();
                } else {
                    PopularMoviesSyncAdapter.syncImmediately(getActivity());
                    getLoaderManager().restartLoader(MOVIE_LOADER, null, MoviesFragment.this);
                }
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
