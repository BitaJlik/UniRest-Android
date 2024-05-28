package com.unirest.data.models;

import com.google.gson.annotations.SerializedName;
import com.unirest.api.recycler.IDiff;

import java.util.List;

public class Room implements IDiff<Room> {
    private Long id;
    @SerializedName(value = "number", alternate = "roomNumber")
    private int number;
    private int beds;
    private String notes;
    private List<Long> users;
    private Long floorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public String getNotes() {
        return notes;
    }

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }
}
