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

import java.io.IOException;

public class ListActivity extends AppCompatActivity {
    static DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLat, userLng;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get current location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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

        // Initialize database
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListView() {
        SQLiteDatabase database = dbHelper.getDataBase();

        // Query data from the database
        Cursor dbCursor = database.rawQuery("SELECT * FROM MobCartoDB_table;", null);
        int length = dbCursor.getCount();
        ListView listView = findViewById(R.id.list_view);
        TextView emptyMessage = findViewById(R.id.empty_message);

        if (length == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        } else {
            emptyMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        String[] db_names = new String[length];
        String[] distances = new String[length];
        final Cursor finalCursor = dbCursor;

        dbCursor.moveToFirst();
        for (int i = 0; i < length; i++) {
            String name = dbCursor.getString(dbCursor.getColumnIndexOrThrow("name"));
            double lat = dbCursor.getDouble(dbCursor.getColumnIndexOrThrow("lat"));
            double lng = dbCursor.getDouble(dbCursor.getColumnIndexOrThrow("lng"));

            // Calculate the distance from the user's current location
            Location pointLocation = new Location("point");
            pointLocation.setLatitude(lat);
            pointLocation.setLongitude(lng);

            Location userLocation = new Location("user");
            userLocation.setLatitude(userLat);
            userLocation.setLongitude(userLng);

            float distance = userLocation.distanceTo(pointLocation); // Distance in meters

            String distanceText = (distance < 1000) ? distance + " meters" : String.format("%.2f", distance / 1000) + " km";

            db_names[i] = name;  // Only the name in the name array
            distances[i] = distanceText;  // Distance in the distances array
            dbCursor.moveToNext();
        }
        dbCursor.moveToFirst(); // Reset cursor

        // Use CustomAdapter
        CustomAdapter adapter = new CustomAdapter(this, db_names, distances);
        listView.setAdapter(adapter);

        // Set item click listener
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            finalCursor.moveToPosition(position);

            // Get the data for the selected item
            String name = finalCursor.getString(finalCursor.getColumnIndexOrThrow("name"));
            String address = finalCursor.getString(finalCursor.getColumnIndexOrThrow("address"));
            String category = finalCursor.getString(finalCursor.getColumnIndexOrThrow("category"));
            String subCategory = finalCursor.getString(finalCursor.getColumnIndexOrThrow("sub_cat"));
            String url = finalCursor.getString(finalCursor.getColumnIndexOrThrow("url"));
            String notes = finalCursor.getString(finalCursor.getColumnIndexOrThrow("notes"));
            double lat = finalCursor.getDouble(finalCursor.getColumnIndexOrThrow("lat"));
            double lng = finalCursor.getDouble(finalCursor.getColumnIndexOrThrow("lng"));

            // Create an intent to start PointDescriptionActivity
            Intent intent = new Intent(ListActivity.this, PointDescriptionActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            intent.putExtra("category", category);
            intent.putExtra("sub_cat", subCategory); // Must match the key in PointDescriptionActivity
            intent.putExtra("url", url);
            intent.putExtra("notes", notes);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);

            // Start the PointDescriptionActivity
            startActivity(intent);
        });
    }
}
