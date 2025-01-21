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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListActivity extends AppCompatActivity {
    static DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLat, userLng;

    // New class to hold point data
    private class PointData {
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

        PointData(String name, String address, String category, String subCategory,
                  String url, String notes, double lat, double lng, float distance, String distanceText) {
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
        }
    }

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        BottomNavigationView bottomNavigationView = findViewById(R.id.btm_nav);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.list_view) {
                return true;
            } else if (item.getItemId() == R.id.map) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.list_view);

        ListView listView = findViewById(R.id.list_view);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userLat = location.getLatitude();
                            userLng = location.getLongitude();
                            loadListView();
                        }
                    }
                });

        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListView() {
        SQLiteDatabase database = dbHelper.getDataBase();
        Cursor dbCursor = database.rawQuery("SELECT * FROM MobCartoDB_table;", null);
        int length = dbCursor.getCount();
        ListView listView = findViewById(R.id.list_view);

        if (length == 0) {
            listView.setVisibility(View.GONE);
            return;
        } else {
            listView.setVisibility(View.VISIBLE);
        }

        // Create a list to hold all point data
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

            Location pointLocation = new Location("point");
            pointLocation.setLatitude(lat);
            pointLocation.setLongitude(lng);

            Location userLocation = new Location("user");
            userLocation.setLatitude(userLat);
            userLocation.setLongitude(userLng);

            float distance = userLocation.distanceTo(pointLocation);
            String distanceText = (distance < 1000) ?
                    Math.round(distance) + " meters" :
                    String.format("%.2f", distance / 1000) + " km";

            points.add(new PointData(name, address, category, subCategory, url, notes,
                    lat, lng, distance, distanceText));

            dbCursor.moveToNext();
        }

        // Sort points by distance
        Collections.sort(points, new Comparator<PointData>() {
            @Override
            public int compare(PointData p1, PointData p2) {
                return Float.compare(p1.distance, p2.distance);
            }
        });

        // Create arrays for the adapter
        String[] db_names = new String[length];
        String[] distances = new String[length];

        for (int i = 0; i < length; i++) {
            db_names[i] = points.get(i).name;
            distances[i] = points.get(i).distanceText;
        }

        CustomAdapter adapter = new CustomAdapter(this, db_names, distances);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            PointData selectedPoint = points.get(position);

            Intent intent = new Intent(ListActivity.this, PointDescriptionActivity.class);
            intent.putExtra("name", selectedPoint.name);
            intent.putExtra("address", selectedPoint.address);
            intent.putExtra("category", selectedPoint.category);
            intent.putExtra("sub_cat", selectedPoint.subCategory);
            intent.putExtra("url", selectedPoint.url);
            intent.putExtra("notes", selectedPoint.notes);
            intent.putExtra("lat", selectedPoint.lat);
            intent.putExtra("lng", selectedPoint.lng);

            startActivity(intent);
        });
    }
}