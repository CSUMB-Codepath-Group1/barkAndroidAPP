package com.example.bark;

public class Event {
    private String organizer;
    private String description;
    private String datetime;
    private String name;

    public Event()
    {
        //no-arg constructor needed by Firebase
    }

    public Event(String name, String organizer, String description, String datetime)
    {
        this.name=name;
        this.organizer=organizer;
        this.description=description;
        this.datetime=datetime;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getDescription() {
        return description;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getName() {
        return name;
    }
}
