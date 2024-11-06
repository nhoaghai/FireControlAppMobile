package com.vn.firecontrolapp;

import java.io.Serializable;

public class Rooms implements Serializable {
    private String room;
    private Double highestTemp;
    private Double lowestTemp;

    public Rooms() {
    }

    public Rooms(String room, Double highestTemp, Double lowestTemp) {
        this.room = room;
        this.highestTemp = highestTemp;
        this.lowestTemp = lowestTemp;
    }

    public Rooms(Double highestTemp, Double lowestTemp) {
        this.highestTemp = highestTemp;
        this.lowestTemp = lowestTemp;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Double getHighestTemp() {
        return highestTemp;
    }

    public void setHighestTemp(Double highestTemp) {
        this.highestTemp = highestTemp;
    }

    public Double getLowestTemp() {
        return lowestTemp;
    }

    public void setLowestTemp(Double lowestTemp) {
        this.lowestTemp = lowestTemp;
    }
}
