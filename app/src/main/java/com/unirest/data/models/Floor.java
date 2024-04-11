package com.unirest.data.models;

import com.unirest.api.recycler.IDiff;

import java.util.ArrayList;
import java.util.List;

public class Floor implements IDiff<Floor> {
    private Long id;
    private int number;
    private String shortName;
    private FloorSide floorSide;
    private int minRoomNumber;
    private int maxRoomNumber;

    private List<Long> rooms = new ArrayList<>();
    private List<Long> cookers = new ArrayList<>();

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public FloorSide getFloorSide() {
        return floorSide;
    }

    public void setFloorSide(FloorSide floorSide) {
        this.floorSide = floorSide;
    }

    public int getMinRoomNumber() {
        return minRoomNumber;
    }

    public void setMinRoomNumber(int minRoomNumber) {
        this.minRoomNumber = minRoomNumber;
    }

    public int getMaxRoomNumber() {
        return maxRoomNumber;
    }

    public void setMaxRoomNumber(int maxRoomNumber) {
        this.maxRoomNumber = maxRoomNumber;
    }

    public List<Long> getRooms() {
        return rooms;
    }

    public void setRooms(List<Long> rooms) {
        this.rooms = rooms;
    }

    public List<Long> getCookers() {
        return cookers;
    }

    public void setCookers(List<Long> cookers) {
        this.cookers = cookers;
    }

    public enum FloorSide {
        FULL, LEFT, RIGHT, CENTRAL, SMALL, LARGE, CUSTOM
    }

}
