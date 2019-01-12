package de.hfu.butler.model;

public class DataLocation {

    private Coordinate coordinate;
    private String name;

    public DataLocation(double lat, double lng){
        name="";
        coordinate = new Coordinate(lat,lng);

    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getName() {
        return name;
    }
}
