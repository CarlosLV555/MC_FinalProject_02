package com.example.finalproject_wjc;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.clustering.ClusterManager;

public class ClusterFragment extends Fragment {

    private GoogleMap mMap;
    private CameraPosition lastCameraPosition;

    private OnMapReadyCallback callback = googleMap -> {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Adjust control padding after layout is rendered
        View rootView = getView(); // Get the fragment's root view
        if (rootView != null) {
            View textView = rootView.findViewById(R.id.text_view_1); // Use fragment's view
            View navigationBar = requireActivity().findViewById(R.id.btm_nav); // Access MainActivity's view

            if (textView != null && navigationBar != null) {
                textView.post(() -> {
                    int topPadding = textView.getHeight(); // Height of the TextView
                    int bottomPadding = navigationBar.getHeight(); // Height of the navigation bar
                    int additionalBottomPadding = 150; // Optional extra space for zoom controls

                    // Apply padding to map controls
                    mMap.setPadding(0, topPadding, 0, bottomPadding + additionalBottomPadding);
                });
            }
        }

        ClusterManager<MuseumGalleryItem> clusterManager = new ClusterManager<>(requireContext(), mMap);

        // Add points to the cluster manager and create bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < MainActivity.dd_mus_gal.size(); i++) {
            MuseumGalleryItem item = MainActivity.dd_mus_gal.get(i);
            clusterManager.addItem(item);
            builder.include(item.getPosition());
        }

        // Cluster the items
        clusterManager.cluster();

        // Set camera position based on bounds (if available)
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100); // 100 is padding
        mMap.moveCamera(cameraUpdate);

        // Restore last camera position if it exists
        if (lastCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastCameraPosition));
        }

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
    };

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
}