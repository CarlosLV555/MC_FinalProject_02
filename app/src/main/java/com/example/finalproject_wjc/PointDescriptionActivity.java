package com.example.finalproject_wjc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PointDescriptionActivity extends AppCompatActivity {
    TextView tvName, tvAddress, tvCategory, tvSubCategory, tvUrl, tvNotes;
    Button btnMap, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_description);

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvCategory = findViewById(R.id.tvCategory);
        tvSubCategory = findViewById(R.id.tvSubCategory);
        tvUrl = findViewById(R.id.tvUrl);
        tvNotes = findViewById(R.id.tvNotes);
        btnMap = findViewById(R.id.btnMap);
        btnBack = findViewById(R.id.btnBack);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            tvName.setText(intent.getStringExtra("name"));
            tvAddress.setText(intent.getStringExtra("address"));
            tvCategory.setText(intent.getStringExtra("category"));
            tvSubCategory.setText(intent.getStringExtra("sub_cat")); // Ensure key matches ListActivity
            tvUrl.setText(intent.getStringExtra("url"));
            tvNotes.setText(intent.getStringExtra("notes"));
        }

        btnMap.setOnClickListener(view -> {
            if (intent != null) {
                double latitude = intent.getDoubleExtra("lat", 0.0);
                double longitude = intent.getDoubleExtra("lng", 0.0);
                float zoomLevel = 15.0f; // Adjust zoom level as needed (e.g., 15 for city-level)

                Intent mapIntent = new Intent(PointDescriptionActivity.this, MainActivity.class);
                mapIntent.putExtra("lat", latitude);
                mapIntent.putExtra("lng", longitude);
                mapIntent.putExtra("zoom", zoomLevel);
                startActivity(mapIntent);
            }
        });

        // Button to go back to the list
        btnBack.setOnClickListener(view -> {
            finish(); // Finish the activity and return to the previous screen
        });
    }
}
