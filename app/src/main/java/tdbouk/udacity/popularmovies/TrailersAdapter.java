package tdbouk.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toufik on 10/9/2016.
 */

public class TrailersAdapter extends ArrayAdapter<String> {

    private ArrayList<String> trailersList;

    public TrailersAdapter(Context context, int resource, ArrayList<String> list) {
        super(context, resource, list);
        this.trailersList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_trailer, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.trailer.setText(R.string.trailer + (position + 1));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Build the intent
                String url = trailersList.get(position);
                Uri link = Uri.parse("http://www.youtube.com/watch?v=" + url);
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, link);

                // Verify it resolves
                PackageManager packageManager = getContext().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(youtubeIntent, 0);
                boolean isIntentSafe = activities.size() > 0;

                // Start an activity if it's safe
                if (isIntentSafe) {
                    getContext().startActivity(youtubeIntent);
//                }

                    //TODO:: maybe add package chooser?
//                String title = "test";
//                Intent chooser = Intent.createChooser(youtubeIntent, title);
//
//                if (youtubeIntent.resolveActivity(getContext().getPackageManager()) != null) {
//                    getContext().startActivity(chooser);
                }
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
        public final TextView trailer;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageview);
            trailer = (TextView) view.findViewById(R.id.text_trailer);
        }
    }
}

