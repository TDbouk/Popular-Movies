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

public class TrailersDialogFragment extends DialogFragment {

    ArrayList<String> trailersList;

    static TrailersDialogFragment newInstance(ArrayList<String> bookmarksList) {
        TrailersDialogFragment f = new TrailersDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("trailers", bookmarksList);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trailersList = getArguments().getStringArrayList("trailers");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_show_trailers, container, false);
        final ListView lv_bookmarks = (ListView) v.findViewById(R.id.listview_trailers);
        final TrailersAdapter adapter = new TrailersAdapter(getActivity(), 0, trailersList);
        lv_bookmarks.setAdapter(adapter);

        return v;
    }
}
