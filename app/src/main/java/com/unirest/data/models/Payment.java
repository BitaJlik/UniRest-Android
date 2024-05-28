package com.unirest.data.models;

import com.unirest.api.recycler.IDiff;

public class Payment implements IDiff<Payment> {
    private Long id;
    private long date;
    private double balance;
    private String checkId;

    private long moderateDate;
    private boolean moderated;
    private boolean valid;

    private Long dormitoryId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getModerateDate() {
        return moderateDate;
    }

    public void setModerateDate(long moderateDate) {
        this.moderateDate = moderateDate;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public boolean isModerated() {
        return moderated;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    public Long getDormitoryId() {
        return dormitoryId;
    }

    public void setDormitoryId(Long dormitoryId) {
        this.dormitoryId = dormitoryId;
    }

    public static class Comparators {
        public static int compareDate(Payment o1, Payment o2) {
            return Long.compare(o1.getDate(), o2.getDate());
        }

        public static int compareSum(Payment o1, Payment o2) {
            return Double.compare(o1.getBalance(), o2.getBalance());
        }

        public static int compareIfModerated(Payment o1, Payment o2) {
            return Boolean.compare(o1.isModerated(), o2.isModerated());
        }
    }

    public static class ReverseComparators {
        public static int compareDate(Payment o1, Payment o2) {
            return Long.compare(o2.getDate(), o1.getDate());
        }

        public static int compareSum(Payment o1, Payment o2) {
            return Double.compare(o2.getBalance(), o1.getBalance());
        }

        public static int compareIfModerated(Payment o1, Payment o2) {
            return Boolean.compare(o2.isModerated(), o1.isModerated());
        }
    }
}
