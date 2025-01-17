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
    private final String[] distances;

    public CustomAdapter(Context context, String[] names, String[] distances) {
        super(context, R.layout.activity_list, names); // Pass 'names' to the parent constructor
        this.context = context;
        this.names = names;
        this.distances = distances;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_list, parent, false);
        }

        // Get references to the TextViews
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView distanceTextView = convertView.findViewById(R.id.distanceTextView);

        // Set name and distance text
        nameTextView.setText(names[position]);
        distanceTextView.setText(distances[position]);

        return convertView;
    }
}
