package com.example.finalproject_wjc;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.clustering.ClusterManager;

public class ClusterFragment extends Fragment {

    private GoogleMap mMap;

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
            ClusterManager clusterManager = new
                    ClusterManager<MuseumGalleryItem>(getContext(), map);
            map.setOnCameraIdleListener(clusterManager);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for(int i = 0; i < MainActivity.dd_mus_gal.size(); i++) {
                clusterManager.addItem(MainActivity.dd_mus_gal.get(i));
                builder.include(MainActivity.dd_mus_gal.get(i).getPosition());
            }

            clusterManager.cluster();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),100));
        }
    };

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