package com.ritorno.geoblocks;

class Poke_Location {
    private double latitude;
    private double longitude;
    private String name;
    private int nft;

    public Poke_Location(double latitude, double longitude, String name, int nft) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.nft = nft;
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
    public int getNft() {
        return nft;
    }
}