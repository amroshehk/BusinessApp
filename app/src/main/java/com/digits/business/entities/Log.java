package com.digits.business.entities;


public class Log {

    public static final int MISSED = 0;
    public static final int RECEIVED = 1;
    public static final int CALLED = 2;

    private int id;
    private int type;

    private long timestamp;
    private long duration;

    private String phone_no;
    private String email;
    private String name;


    public Log() {
    }

    public Log(int id, String email, long timestamp, int type, String phone_no, long duration, String name) {
        this.id = id;
        this.email = email;
        this.timestamp = timestamp;
        this.type = type;
        this.phone_no = phone_no;
        this.duration = duration;
        this.name = name;
    }
    public Log( String email, long timestamp, int type, String phone_no, long duration, String name) {
        this.email = email;
        this.timestamp = timestamp;
        this.type = type;
        this.phone_no = phone_no;
        this.duration = duration;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
