package com.unirest.data.models;

public class CookerRemind {
    private Long cookerId;
    private Long timeToRemind;

    public CookerRemind(Long cookerId, Long timeToRemind) {
        this.cookerId = cookerId;
        this.timeToRemind = timeToRemind;
    }

    public Long getCookerId() {
        return cookerId;
    }

    public void setCookerId(Long cookerId) {
        this.cookerId = cookerId;
    }

    public Long getTimeToRemind() {
        return timeToRemind;
    }

    public void setTimeToRemind(Long timeToRemind) {
        this.timeToRemind = timeToRemind;
    }
}
