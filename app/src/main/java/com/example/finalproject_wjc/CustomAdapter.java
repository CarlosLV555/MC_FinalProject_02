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

    public CustomAdapter(Context context, String[] names, String[] category, String[] distances) {
        super(context, R.layout.list_item, names);  // Change to list_item
        this.context = context;
        this.category = category;
        this.names = names;
        this.distances = distances;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);  // Change to list_item
        }

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView categoryTextView = convertView.findViewById(R.id.categoryTextView);
        TextView distanceTextView = convertView.findViewById(R.id.distanceTextView);

        nameTextView.setText(names[position]);
        categoryTextView.setText(category[position]);
        distanceTextView.setText(distances[position]);

        return convertView;
    }
}
