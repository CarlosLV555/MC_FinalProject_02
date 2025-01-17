package com.example.finalproject_wjc;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class DatabasePoint implements ClusterItem {
    private final LatLng position;
    private final String name;
    private final String notes;
    private final String category;

    public DatabasePoint(double lat, double lng, String name, String notes, String category) {
        this.position = new LatLng(lat, lng);
        this.name = name;
        this.notes = notes;
        this.category = category;
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

    public String getCategory() {return category;}
}