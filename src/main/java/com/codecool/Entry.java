package com.codecool;

public class Entry {
    private int id;
    private String message;
    private String name;
    private String date;

    public Entry(int id, String message,String name,String date){
        this.id = id;
        this.message = message;
        this.name = name;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
