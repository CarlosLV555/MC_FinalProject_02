package com.example.finalproject_wjc;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        ListView listView = findViewById(R.id.list_view);
        TextView emptyMessage = findViewById(R.id.empty_message);

        // Example data (replace this with real data or data passed via Intent)
        List<String> data = new ArrayList<>(); // Empty list to test the "no data" message

        // Check if data is empty
        if (data.isEmpty()) {
            // Show the empty message and hide the ListView
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            // Hide the empty message and show the ListView with data
            emptyMessage.setVisibility(View.GONE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1, data);
            listView.setAdapter(adapter);
        }
    }
}
