package com.unirest.api;

public interface ICallback<ReturnType> {
    void call(ReturnType returnValue);
}