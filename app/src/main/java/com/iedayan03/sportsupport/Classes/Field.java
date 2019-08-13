package com.iedayan03.sportsupport.Classes;

import java.io.Serializable;

public class Field implements Serializable {

    private String place_id;
    private String field_name;
    private String address;
    private String longitude;
    private String latitude;
    private Double rating;

    public Field(String place_id, String field_name, String address, String longitude, String latitude, Double rating) {
        this.place_id = place_id;
        this.field_name = field_name;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rating = rating;
    }

    public String getPlaceId() {
        return place_id;
    }

    public String getFieldName() {
        return field_name;
    }

    public String getAddress() {
        return address;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public Double getRating() {
        return rating;
    }

    public void setPlaceId(String place_id) {
        this.place_id = place_id;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
