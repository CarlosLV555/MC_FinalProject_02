package com.example.finalproject_wjc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ListActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static DatabaseHelper dbHelper;
    private double userLat, userLng;

    // Inner class to hold point data
    private static class PointData {
        String name;
        String address;
        String category;
        String subCategory;
        String url;
        String notes;
        double lat;
        double lng;
        float distance;
        String distanceText;
        String img;

        // Constructor to initialize point data
        PointData(String name, String address, String category, String subCategory,
                  String url, String notes, double lat, double lng, float distance, String distanceText, String img) {
            this.name = name;
            this.address = address;
            this.category = category;
            this.subCategory = subCategory;
            this.url = url;
            this.notes = notes;
            this.lat = lat;
            this.lng = lng;
            this.distance = distance;
            this.distanceText = distanceText;
            this.img = img;
        }
    }

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        BottomNavigationView bottomNavigationView = findViewById(R.id.btm_nav);

        // Handle navigation between map and list views
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.list_view) {
                return true; // Already on ListActivity
            } else if (item.getItemId() == R.id.map) {
                startActivity(new Intent(this, MainActivity.class)); // Navigate to MainActivity
                overridePendingTransition(0, 0);
            }
            return true;
        });

        // Set the selected item in bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.list_view);

        // Get the user's location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLat = location.getLatitude(); // Get user's latitude
                        userLng = location.getLongitude(); // Get user's longitude
                        loadListView(); // Load the list view after retrieving location
                    }
                });

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase(); // Ensure the database is ready to use
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListView() {
        SQLiteDatabase database = dbHelper.getDataBase();
        @SuppressLint("Recycle") Cursor dbCursor = database.rawQuery("SELECT * FROM MobCartoDB_table;", null);
        int length = dbCursor.getCount();
        ListView listView = findViewById(R.id.list_view);

        // Hide the list view if there are no records in the database
        if (length == 0) {
            listView.setVisibility(View.GONE);
            return;
        } else {
            listView.setVisibility(View.VISIBLE);
        }

        // List to store all point data
        ArrayList<PointData> points = new ArrayList<>();

        dbCursor.moveToFirst();
        for (int i = 0; i < length; i++) {
            String name = dbCursor.getString(dbCursor.getColumnIndexOrThrow("name"));
            String address = dbCursor.getString(dbCursor.getColumnIndexOrThrow("address"));
            String category = dbCursor.getString(dbCursor.getColumnIndexOrThrow("category"));
            String subCategory = dbCursor.getString(dbCursor.getColumnIndexOrThrow("sub_cat"));
            String url = dbCursor.getString(dbCursor.getColumnIndexOrThrow("url"));
            String notes = dbCursor.getString(dbCursor.getColumnIndexOrThrow("notes"));
            double lat = dbCursor.getDouble(dbCursor.getColumnIndexOrThrow("lat"));
            double lng = dbCursor.getDouble(dbCursor.getColumnIndexOrThrow("lng"));
            String img = dbCursor.getString(dbCursor.getColumnIndexOrThrow("img"));

            // Calculate the distance from the user's location to the point's location
            Location pointLocation = new Location("point");
            pointLocation.setLatitude(lat);
            pointLocation.setLongitude(lng);

            Location userLocation = new Location("user");
            userLocation.setLatitude(userLat);
            userLocation.setLongitude(userLng);

            float distance = userLocation.distanceTo(pointLocation);
            @SuppressLint("DefaultLocale") String distanceText = (distance < 1000) ?
                    Math.round(distance) + " meters" :
                    String.format("%.2f", distance / 1000) + " km";

            // Add the point data to the list
            points.add(new PointData(name, address, category, subCategory, url, notes,
                    lat, lng, distance, distanceText, img));

            dbCursor.moveToNext();
        }

        // Sort the points by distance from the user
        Collections.sort(points, (p1, p2) -> Float.compare(p1.distance, p2.distance));

        // Prepare data for the adapter
        String[] db_names = new String[length];
        String[] db_category = new String[length];
        String[] distances = new String[length];

        for (int i = 0; i < length; i++) {
            db_names[i] = points.get(i).name;
            db_category[i] = points.get(i).category;
            distances[i] = points.get(i).distanceText;
        }

        // Set the adapter to display the points in the list view
        CustomAdapter adapter = new CustomAdapter(this, db_names, db_category, distances);
        listView.setAdapter(adapter);

        // Set the click listener for the list items
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            PointData selectedPoint = points.get(position);

            // Navigate to the description activity with the selected point's data
            Intent intent = new Intent(ListActivity.this, PointDescriptionActivity.class);
            intent.putExtra("name", selectedPoint.name);
            intent.putExtra("address", selectedPoint.address);
            intent.putExtra("category", selectedPoint.category);
            intent.putExtra("sub_cat", selectedPoint.subCategory);
            intent.putExtra("url", selectedPoint.url);
            intent.putExtra("notes", selectedPoint.notes);
            intent.putExtra("lat", selectedPoint.lat);
            intent.putExtra("lng", selectedPoint.lng);
            intent.putExtra("img", selectedPoint.img);

            startActivity(intent);
        });
    }
}
