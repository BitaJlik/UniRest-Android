package com.unirest.data.models;

public class WasherRemind {
    private Long washerId;
    private Long timeToRemind;

    public WasherRemind(Long washerId, Long timeToRemind) {
        this.washerId = washerId;
        this.timeToRemind = timeToRemind;
    }

    public Long getWasherId() {
        return washerId;
    }

    public void setWasherId(Long washerId) {
        this.washerId = washerId;
    }

    public Long getTimeToRemind() {
        return timeToRemind;
    }

    public void setTimeToRemind(Long timeToRemind) {
        this.timeToRemind = timeToRemind;
    }
}
