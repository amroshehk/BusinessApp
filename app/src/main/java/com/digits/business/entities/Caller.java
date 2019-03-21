package com.digits.business.entities;

public class Caller {
    private  int id;
    private  String name;
    private  String number;
    private  long timestamp;
    private String type;

    public Caller() {
    }

    public Caller(int id, String name, String number, long timestamp, String type) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.timestamp = timestamp;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
