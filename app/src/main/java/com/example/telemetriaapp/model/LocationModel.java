package com.example.telemetriaapp.model;

import android.location.Location;

import java.util.Date;
import java.util.Objects;

public class LocationModel {

    int id;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    double latitude;
    double longitude;
    String user;
    Date data;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    Location location;

    public LocationModel(int id, double latitude, double longitude, String user, Date data) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUser() {
        return user;
    }

    public Date getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationModel that = (LocationModel) o;
        return id == that.id && Double.compare(that.latitude, latitude) == 0 && Double.compare(that.longitude, longitude) == 0 && Objects.equals(user, that.user) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, latitude, longitude, user, data);
    }
}
