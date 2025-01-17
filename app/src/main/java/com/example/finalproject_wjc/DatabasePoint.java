package com.example.finalproject_wjc;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class DatabasePoint implements ClusterItem {
    private final LatLng position;
    private final String name;
    private final String notes;

    public DatabasePoint(double lat, double lng, String name, String notes) {
        this.position = new LatLng(lat, lng);
        this.name = name;
        this.notes = notes;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return notes;
    }
}