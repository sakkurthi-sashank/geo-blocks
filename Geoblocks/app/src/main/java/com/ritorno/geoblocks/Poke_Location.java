package com.ritorno.geoblocks;

class Poke_Location {
    private double latitude;
    private double longitude;
    private String name;

    public Poke_Location(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }
}