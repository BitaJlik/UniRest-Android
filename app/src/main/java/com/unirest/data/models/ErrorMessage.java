package com.unirest.data.models;

import android.app.Activity;

public class ErrorMessage {
    private String username;
    private String errorTitle;
    private String[] errorDescription;
    private String sdkVersion;

    public ErrorMessage() {
        this(null);
    }

    public ErrorMessage(Throwable throwable) {
        this(null, throwable);
    }

    public ErrorMessage(Activity activity, Throwable throwable) {
        if (activity != null) {
            sdkVersion = String.valueOf(activity.getApplicationInfo().targetSdkVersion);
        }
        if (throwable != null) {
            errorTitle = throwable.getMessage();
            errorDescription = new String[throwable.getStackTrace().length];
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                StackTraceElement stackTraceElement = stackTrace[i];
                errorDescription[i] = stackTraceElement.toString();
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public String[] getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String[] errorDescription) {
        this.errorDescription = errorDescription;
    }
}
