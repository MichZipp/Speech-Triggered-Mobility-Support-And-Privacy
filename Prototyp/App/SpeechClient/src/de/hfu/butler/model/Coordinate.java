package de.hfu.butler.model;

public class Coordinate {

    private final double lat;
    private final double lng;

    public Coordinate(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
