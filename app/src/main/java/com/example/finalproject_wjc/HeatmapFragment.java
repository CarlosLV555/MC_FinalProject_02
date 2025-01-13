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

    // Pass map object from the MainActivity to this Fragment
    public void setMap(GoogleMap map) {
        mMap = map;
        applyMapStyle();
    }

    private void applyMapStyle() {
        if (mMap != null) {
            // Apply your custom style to the map
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap map) {

            int[] colors = {
                    Color.rgb(244, 242, 105),
                    Color.rgb(92, 178, 112)
            };
            float[] startPoints = {
                    0.2f, 1f
            };
            Gradient gradient = new Gradient(colors, startPoints);



            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < MainActivity.dd_mus_gal.size(); i++) {
                builder.include(new LatLng(
                        MainActivity.dd_mus_gal.get(i).getPosition().latitude,
                        MainActivity.dd_mus_gal.get(i).getPosition().longitude)
                );
            }

            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

            List<LatLng> latLngList = new ArrayList<>();
            for (int i = 0; i < MainActivity.dd_mus_gal.size(); i++) {
                latLngList.add(new LatLng(
                        MainActivity.dd_mus_gal.get(i).getPosition().latitude,
                        MainActivity.dd_mus_gal.get(i).getPosition().longitude
                ));
            }

            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .gradient(gradient)
                    .opacity(0.8)
                    .radius(30)
                    .data(latLngList)
                    .build();
            map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        }
    };

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