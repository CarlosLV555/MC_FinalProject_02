package com.example.finalproject_wjc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class ClusterFragment extends Fragment {

    private GoogleMap mMap;
    private CameraPosition lastCameraPosition;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor dbCursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
    }

    @SuppressLint("PotentialBehaviorOverride")
    private OnMapReadyCallback callback = googleMap -> {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Adjust control padding after layout is rendered
        View rootView = getView();
        if (rootView != null) {
            View textView = rootView.findViewById(R.id.text_view_1);
            View navigationBar = requireActivity().findViewById(R.id.btm_nav);

            if (textView != null && navigationBar != null) {
                textView.post(() -> {
                    int topPadding = textView.getHeight();
                    int bottomPadding = navigationBar.getHeight();
                    int additionalBottomPadding = 150;

                    mMap.setPadding(0, topPadding, 0, bottomPadding + additionalBottomPadding);
                });
            }
        }

        // Initialize ClusterManager and CustomClusterRenderer
        ClusterManager<DatabasePoint> clusterManager = new ClusterManager<>(requireContext(), mMap);
        clusterManager.setRenderer(new CustomClusterRenderer(requireContext(), mMap, clusterManager));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        try {
            dbHelper.createDataBase();
            database = dbHelper.getDataBase();

            dbCursor = database.rawQuery("SELECT * FROM MobCartoDB_table;", null);

            if (dbCursor.moveToFirst()) {
                do {
                    double lat = dbCursor.getDouble(dbCursor.getColumnIndexOrThrow("lat"));
                    double lng = dbCursor.getDouble(dbCursor.getColumnIndexOrThrow("lng"));
                    String name = dbCursor.getString(dbCursor.getColumnIndexOrThrow("name"));
                    String notes = dbCursor.getString(dbCursor.getColumnIndexOrThrow("notes"));
                    String category = dbCursor.getString(dbCursor.getColumnIndexOrThrow("category"));

                    DatabasePoint point = new DatabasePoint(lat, lng, name, notes, category);

                    // Add the point to the cluster manager
                    clusterManager.addItem(point);
                    builder.include(point.getPosition());

                } while (dbCursor.moveToNext());
            }

            clusterManager.cluster();

            if (!clusterManager.getAlgorithm().getItems().isEmpty()) {
                LatLngBounds bounds = builder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                mMap.moveCamera(cameraUpdate);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error loading map points: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (dbCursor != null && !dbCursor.isClosed()) {
                dbCursor.close();
            }
            if (database != null && database.isOpen()) {
                database.close();
            }
        }

        if (lastCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastCameraPosition));
        }

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setOnClusterItemClickListener(item -> {
            Toast.makeText(requireContext(), "Selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            return false;
        });
    };

    private class CustomClusterRenderer extends DefaultClusterRenderer<DatabasePoint> {

        public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<DatabasePoint> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(@NonNull DatabasePoint item, @NonNull MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            // Get the category from the item and set the marker color
            String category = item.getCategory(); // or use a different field if necessary
            float color = getMarkerColor(category);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));
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

    public void setLastCameraPosition(CameraPosition cameraPosition) {
        lastCameraPosition = cameraPosition;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cluster, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbCursor != null && !dbCursor.isClosed()) {
            dbCursor.close();
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
