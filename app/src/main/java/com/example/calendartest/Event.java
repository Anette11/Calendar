package com.example.calendartest;

public class Event {
    private int id;
    private String eventTitle;
    private String eventDescription;
    private String eventDate;
    private int eventDateInt;

    public Event() {
    }

    public Event(String eventDate, String eventTitle, String eventDescription, int eventDateInt) {
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDateInt = eventDateInt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getEventDateInt() {
        return eventDateInt;
    }

    public void setEventDateInt(int eventDateInt) {
        this.eventDateInt = eventDateInt;
    }
}