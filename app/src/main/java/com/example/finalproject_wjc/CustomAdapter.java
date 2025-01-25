package com.example.finalproject_wjc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] names;
    private final String[] category;
    private final String[] distances;

    // Constructor to initialize the adapter with data
    public CustomAdapter(Context context, String[] names, String[] category, String[] distances) {
        super(context, R.layout.list_item, names);  // Use the custom list item layout
        this.context = context;
        this.category = category;
        this.names = names;
        this.distances = distances;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if the view can be reused; if not, inflate a new one
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);  // Inflate the custom layout
        }

        // Find and initialize the TextViews from the layout
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView categoryTextView = convertView.findViewById(R.id.categoryTextView);
        TextView distanceTextView = convertView.findViewById(R.id.distanceTextView);

        // Set the text for each TextView based on the current position in the list
        nameTextView.setText(names[position]); // Set the name of the point
        categoryTextView.setText(category[position]); // Set the category of the point
        distanceTextView.setText(distances[position]); // Set the distance from the user

        return convertView; // Return the fully populated view
    }
}
