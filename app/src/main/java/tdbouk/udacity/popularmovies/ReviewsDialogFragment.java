package tdbouk.udacity.popularmovies;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by toufik on 10/4/2016.
 */

public class ReviewsDialogFragment extends DialogFragment {

    ArrayList<String> reviewsList;

    static ReviewsDialogFragment newInstance(ArrayList<String> reviewsList) {
        ReviewsDialogFragment f = new ReviewsDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("reviews", reviewsList);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviewsList = getArguments().getStringArrayList("reviews");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_show_reviews, container, false);
        final ListView lv_bookmarks = (ListView) v.findViewById(R.id.listview_reviews);
        final ReviewsAdapter adapter = new ReviewsAdapter(getActivity(), 0, reviewsList);
        lv_bookmarks.setAdapter(adapter);

        return v;
    }

}
