package com.unirest.api;

public class UniCode {
    public static final String PREFIX_USER = "U";
    public static final String PREFIX_COOKER = "C";
    public static final String PREFIX_WASHER = "W";
    public static final String[] PREFIXES = {PREFIX_USER, PREFIX_COOKER, PREFIX_WASHER}; // User, Cooker, Washer
    //
    private String prefix;
    private long value;
    private boolean valid = false;

    public UniCode(String barcode) {
        for (String s : PREFIXES) {
            if (barcode.startsWith(s)) {
                this.prefix = s;
                this.value = Long.parseLong(barcode.substring(1));
                this.valid = true;
                break;
            }
        }
    }

    public boolean isCodeUser() {
        return prefix.equals(PREFIXES[0]);
    }

    public boolean isCodeCooker() {
        return prefix.equals(PREFIXES[1]);
    }

    public boolean isCodeWashing() {
        return prefix.equals(PREFIXES[2]);
    }

    public boolean isValid() {
        return valid;
    }

    public String getPrefix() {
        return prefix;
    }

    public long getValue() {
        return value;
    }
}
