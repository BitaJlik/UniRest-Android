package com.unirest.data.models;

import com.google.gson.annotations.SerializedName;
import com.unirest.api.recycler.IDiff;

public class Cooker implements IDiff<Cooker> {
    private Long id;

    @SerializedName("busy")
    private boolean isBusy;
    private long busyTo;
    private long lastUse;
    private Long floor;
    private Long user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public long getBusyTo() {
        return busyTo;
    }

    public void setBusyTo(long busyTo) {
        this.busyTo = busyTo;
    }

    public long getLastUse() {
        return lastUse;
    }

    public void setLastUse(long lastUse) {
        this.lastUse = lastUse;
    }

    public Long getFloor() {
        return floor;
    }

    public void setFloor(Long floor) {
        this.floor = floor;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}
