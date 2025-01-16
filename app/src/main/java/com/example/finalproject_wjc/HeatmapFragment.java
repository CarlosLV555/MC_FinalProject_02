package com.example.finalproject_wjc;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class HeatmapFragment extends Fragment {

    private GoogleMap mMap;
    private CameraPosition lastCameraPosition;

    private OnMapReadyCallback callback = googleMap -> {

        mMap = googleMap;

        // Apply the custom map style
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Prepare heatmap colors and gradient
        int[] colors = {
                Color.rgb(244, 242, 105), // Yellow
                Color.rgb(92, 178, 112)  // Green
        };
        float[] startPoints = {0.2f, 1f};
        Gradient gradient = new Gradient(colors, startPoints);

        // Create LatLngBounds and LatLng list for the heatmap
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> latLngList = new ArrayList<>();

        for (MuseumGalleryItem item : MainActivity.dd_mus_gal) {
            LatLng position = new LatLng(
                    item.getPosition().latitude,
                    item.getPosition().longitude
            );
            latLngList.add(position);
            builder.include(position);
        }

        // Move the camera to the last known position or fit to the bounds
        if (lastCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastCameraPosition));
        } else if (!latLngList.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }

        // Create the heatmap provider and overlay
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .gradient(gradient)
                .opacity(0.8) // Adjust opacity as needed
                .radius(30)   // Adjust radius as needed
                .data(latLngList)
                .build();

        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    };

    public void setLastCameraPosition(CameraPosition cameraPosition) {
        lastCameraPosition = cameraPosition;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_heatmap, container, false);
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