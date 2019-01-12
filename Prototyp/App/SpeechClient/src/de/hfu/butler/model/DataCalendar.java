package de.hfu.butler.model;

public class DataCalendar {

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private final int id;
    private final String name;

    public DataCalendar(int id, String name){
        this.id = id;
        this.name = name;
    }

}
