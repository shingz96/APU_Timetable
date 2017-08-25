package com.shing.aputimetable.entity;

import java.io.Serializable;

/**
 * Created by Shing on 30-Jul-17.
 */

public class ApuClass implements Serializable {

    private String day;
    private String date;
    private String time;
    private String location;
    private String room;
    private String subject;
    private String lecturer;
    private int type; // 0 = lecture, 1 = t1, 2 = t2, 3=lab1, 4=lab2, 5=lab3

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String toString() {
        return "{Day:" + day + ", Date:" + date + ", Time:" + time + ", Room:" + room
                + ", Location:" + location + ", Subject:" + subject
                + ", Lecturer:" + lecturer + "}";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
