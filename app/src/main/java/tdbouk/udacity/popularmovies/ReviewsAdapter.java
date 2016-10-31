package tdbouk.udacity.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by toufik on 10/9/2016.
 */

public class ReviewsAdapter extends ArrayAdapter<String> {

    private ArrayList<String> reviewsList;

    public ReviewsAdapter(Context context, int resource, ArrayList<String> list) {
        super(context, resource, list);
        this.reviewsList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.review.setText(R.string.review + (position + 1));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build the intent
                String review = reviewsList.get(position);
                // TODO:: open activity to view review
                if (viewHolder.reviewDescription.getText().toString().isEmpty())
                    viewHolder.reviewDescription.setText(review);
                else viewHolder.reviewDescription.setText("");
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView review;
        public final TextView reviewDescription;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageview);
            review = (TextView) view.findViewById(R.id.text_review);
            reviewDescription = (TextView) view.findViewById(R.id.text_review_description);
        }
    }
}

