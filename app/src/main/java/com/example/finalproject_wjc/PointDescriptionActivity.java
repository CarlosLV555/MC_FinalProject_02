package com.example.finalproject_wjc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class PointDescriptionActivity extends AppCompatActivity {
    TextView tvName, tvAddress, tvCategory, tvUrl, tvNotes;
    ImageView ivImage;
    Button btnMap, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_description);

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        //tvCategory = findViewById(R.id.tvCategory);
        tvUrl = findViewById(R.id.tvUrl);
        tvNotes = findViewById(R.id.tvNotes);
        ivImage = findViewById(R.id.ivImage);
        btnMap = findViewById(R.id.btnMap);
        btnBack = findViewById(R.id.btnBack);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            tvName.setText(intent.getStringExtra("name"));
            tvAddress.setText(intent.getStringExtra("address"));
            //tvCategory.setText(intent.getStringExtra("category"));
            tvUrl.setText(intent.getStringExtra("url"));
            tvNotes.setText(intent.getStringExtra("notes"));

            // Load image from URL
            String imageUrl = intent.getStringExtra("img");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .fitCenter()  // or .centerCrop() depending on your preference
                        .into(ivImage);
            }
        }

        // Open URL in browser
        tvUrl.setOnClickListener(view -> {
            String url = tvUrl.getText().toString();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url; // Add a default scheme if missing
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
            startActivity(browserIntent);
        });

        btnMap.setOnClickListener(view -> {
            if (intent != null) {
                double lat = intent.getDoubleExtra("lat", 0.0);
                double lng = intent.getDoubleExtra("lng", 0.0);
                float zoomLevel = 15.0f; // Adjust zoom level as needed (e.g., 15 for city-level)

                Intent mapIntent = new Intent(PointDescriptionActivity.this, MainActivity.class);
                mapIntent.putExtra("lat", lat);
                mapIntent.putExtra("lng", lng);
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