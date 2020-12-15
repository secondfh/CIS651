package com.example.puppatrol;

import com.google.android.gms.maps.model.Marker;

public class PostModel{
    public String postKey;
    public String uid;
    public String description;
    public String url;
    public String date;
    public Marker m;
    public Float rating;
    public  String lat, longi;
    public PostModel(String uid, String description, String url, String date, String key, Marker _m, String lat, String longi) {
        this.uid=uid;
        this.description=description;
        this.url=url;
        this.date=date;
        this.postKey=key;
        this.m = _m;
        this.rating = rating;
        this.lat = lat;
        this.longi = longi;
    }
}
