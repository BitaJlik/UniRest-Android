package com.unirest.data.models;

import java.util.List;

public class Dormitory {
    private int id;
    private String name;
    private String address;
    private boolean hasElevator;
    private List<Long> floors;
    private CookerType cookerType;
    private int commandant;
    private CommandantInfo commandantInfo;

    private int totalBeds;
    private int totalTakenBeds;
    private int totalFreeBeds;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isHasElevator() {
        return hasElevator;
    }

    public void setHasElevator(boolean hasElevator) {
        this.hasElevator = hasElevator;
    }

    public List<Long> getFloors() {
        return floors;
    }

    public void setFloors(List<Long> floors) {
        this.floors = floors;
    }

    public CookerType getCookerType() {
        return cookerType;
    }

    public void setCookerType(CookerType cookerType) {
        this.cookerType = cookerType;
    }

    public int getCommandant() {
        return commandant;
    }

    public void setCommandant(int commandant) {
        this.commandant = commandant;
    }

    public CommandantInfo getCommandantInfo() {
        return commandantInfo;
    }

    public void setCommandantInfo(CommandantInfo commandantInfo) {
        this.commandantInfo = commandantInfo;
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public void setTotalBeds(int totalBeds) {
        this.totalBeds = totalBeds;
    }

    public int getTotalTakenBeds() {
        return totalTakenBeds;
    }

    public void setTotalTakenBeds(int totalTakenBeds) {
        this.totalTakenBeds = totalTakenBeds;
    }

    public int getTotalFreeBeds() {
        return totalFreeBeds;
    }

    public void setTotalFreeBeds(int totalFreeBeds) {
        this.totalFreeBeds = totalFreeBeds;
    }
}