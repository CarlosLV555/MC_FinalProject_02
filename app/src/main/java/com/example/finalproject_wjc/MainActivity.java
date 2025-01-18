package com.example.finalproject_wjc;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CameraPosition lastCameraPosition;
    private ActivityResultLauncher<String[]> locationPermissionRequest;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.btm_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.cluster) {
                fragment = new ClusterFragment();
            }

            if (fragment != null) {
                if (fragment instanceof ClusterFragment) {
                    ((ClusterFragment) fragment).setLastCameraPosition(lastCameraPosition);
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map, fragment)
                        .commit();
            }
            return true;
        });

        // Map initialization
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Location permission setup
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted || coarseLocationGranted) {
                        enableMyLocation();
                    } else {
                        Toast.makeText(this,
                                "Location permissions are not granted.",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // FAB Button functionality
        findViewById(R.id.fab).setOnClickListener(view -> {
            // Start ListActivity
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set map properties
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Load markers from the database
        loadMarkersFromDatabase();

        // Request location permissions
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        // Set camera idle listener to save the last position
        mMap.setOnCameraIdleListener(() -> lastCameraPosition = mMap.getCameraPosition());
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void loadMarkersFromDatabase() {
        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            database = openOrCreateDatabase("MobCarto_SQLite.db", MODE_PRIVATE, null);
            String query = "SELECT lat, lng, name, category FROM MobCartoDB_table";
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                do {
                    double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"));
                    double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("lng"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));

                    LatLng position = new LatLng(latitude, longitude);
                    float color = getMarkerColor(category);

                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(name)
                            .icon(BitmapDescriptorFactory.defaultMarker(color)));

                    boundsBuilder.include(position);
                } while (cursor.moveToNext());

                // Adjust the camera to show all markers
                LatLngBounds bounds = boundsBuilder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading markers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    private float getMarkerColor(String category) {
        switch (category) {
            case "Bar":
                return BitmapDescriptorFactory.HUE_RED;
            case "Landmark":
                return BitmapDescriptorFactory.HUE_BLUE;
            case "Museum":
                return BitmapDescriptorFactory.HUE_YELLOW;
            case "Park":
                return BitmapDescriptorFactory.HUE_GREEN;
            case "Restaurant":
                return BitmapDescriptorFactory.HUE_ORANGE;
            case "Visit Point":
                return BitmapDescriptorFactory.HUE_VIOLET;
            default:
                return BitmapDescriptorFactory.HUE_AZURE; // Default color
        }
    }
}
