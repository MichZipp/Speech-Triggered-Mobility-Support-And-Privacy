package de.hfu.butler.model;

import java.util.Date;

public class DataEvent {

    private final String title;
    private final Date start;
    private final Date end;
    private final String description;
    private final int calendarID;

    public DataEvent(String title, String description, Date start, Date end,int id){
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.calendarID =id;
    }

    public String getTitle() {
        return title;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getDescription() {
        return description;
    }

    public int getCalendarID() {
        return calendarID;
    }
}
