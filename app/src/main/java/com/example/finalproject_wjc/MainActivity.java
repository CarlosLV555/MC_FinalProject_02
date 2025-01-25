package com.example.finalproject_wjc;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String PREFS_NAME = "AppPreferences";
    private static final String KEY_FIRST_LAUNCH = "FirstLaunch";
    private GoogleMap mMap;
    private CameraPosition lastCameraPosition;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private final List<Marker> markersList = new ArrayList<>();
    private static CameraPosition lastSavedPosition = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if this is the first launch
        checkFirstLaunch();

        BottomNavigationView bottomNavigationView = findViewById(R.id.btm_nav);

        // Handle navigation button clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.map) {
                return true; // Already on MainActivity
            } else if (item.getItemId() == R.id.list_view) {
                startActivity(new Intent(this, ListActivity.class)); // Navigate to ListActivity
                overridePendingTransition(0, 0);
            }
            return true;
        });

        // Set the map item as selected by default
        bottomNavigationView.setSelectedItemId(R.id.map);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Request location permissions
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    boolean fineLocationGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false));
                    boolean coarseLocationGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false));

                    if (fineLocationGranted || coarseLocationGranted) {
                        enableMyLocation(); // Enable location services if permissions are granted
                    } else {
                        Toast.makeText(this,
                                "Location permissions are not granted.",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Show the welcome popup when the floating action button is clicked
        findViewById(R.id.fab).setOnClickListener(view -> {
            showWelcomePopup();
        });
    }

    private void checkFirstLaunch() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean(KEY_FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            // Show the welcome dialog
            showWelcomePopup();

            // Update preference to indicate the app has been launched before
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_FIRST_LAUNCH, false);
            editor.apply();
        }
    }

    private void showWelcomePopup() {
        Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.dialog_custom); // Inflate the custom layout

        // Set the close button functionality
        customDialog.findViewById(R.id.btn_close).setOnClickListener(v -> customDialog.dismiss());

        // Show the dialog
        customDialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Customize the map appearance and settings
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setPadding(0, 0, 0, 150); // Set padding to avoid content overlap

        // Load map markers from the database
        loadMarkersFromDatabase(this);

        // Retrieve intent data for setting a specific map location
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 0.0);
        double lng = intent.getDoubleExtra("lng", 0.0);
        float zoom = intent.getFloatExtra("zoom", 5.0f); // Default zoom level

        if (lat != 0.0 && lng != 0.0) {
            LatLng targetLocation = new LatLng(lat, lng);

            // Move the camera to the target location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, zoom));

            // Display the info window for the corresponding marker
            mMap.setOnMapLoadedCallback(() -> {
                for (Marker marker : markersList) {
                    if (marker.getPosition().equals(targetLocation)) {
                        marker.showInfoWindow();
                        break;
                    }
                }
            });
        } else if (lastSavedPosition != null) {
            // Restore the last saved camera position
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastSavedPosition));
        }

        // Request location permissions
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        // Save the last camera position when the camera stops moving
        mMap.setOnCameraIdleListener(() -> {
            lastCameraPosition = mMap.getCameraPosition();
            lastSavedPosition = lastCameraPosition;
        });
    }

    // Enable user location on the map
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    // Load markers from the database into the map
    private void loadMarkersFromDatabase(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            // Ensure the database is ready
            dbHelper.createDataBase();

            // Access the database
            database = dbHelper.getDataBase();
            String query = "SELECT lat, lng, name, category FROM MobCartoDB_table";
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                do {
                    double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"));
                    double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("lng"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));

                    LatLng position = new LatLng(lat, lng);
                    BitmapDescriptor icon = getMarkerIcon(category, context); // Create custom marker icon

                    // Add a marker to the map and store it in the list
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(name)
                            .icon(icon));
                    markersList.add(marker);

                    boundsBuilder.include(position);
                } while (cursor.moveToNext());

                // Adjust the map to show all markers
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

    // Generate a custom icon for markers based on category
    private BitmapDescriptor getMarkerIcon(String category, Context context) {
        int color = getMarkerColor(category, context);
        return BitmapDescriptorFactory.fromBitmap(Objects.requireNonNull(getColoredMarker(context, color)));
    }

    private Bitmap getColoredMarker(Context context, int color) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.marker_black);

        if (drawable == null) {
            // Handle drawable not found (unlikely case)
            return null;
        }
        drawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(drawable, color);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // Assign custom colors for markers based on their category
    private int getMarkerColor(String category, Context context) {
        switch (category) {
            case "Bar":
                return ContextCompat.getColor(context, R.color.bar_color);
            case "Landmark":
                return ContextCompat.getColor(context, R.color.landmark_color);
            case "Museum":
                return ContextCompat.getColor(context, R.color.museum_color);
            case "Park":
                return ContextCompat.getColor(context, R.color.park_color);
            case "Restaurant":
                return ContextCompat.getColor(context, R.color.restaurant_color);
            case "Visit Point":
                return ContextCompat.getColor(context, R.color.visit_point_color);
            default:
                return ContextCompat.getColor(context, R.color.default_color);
        }
    }
}