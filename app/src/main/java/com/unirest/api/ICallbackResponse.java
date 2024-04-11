package com.unirest.api;

public interface ICallbackResponse<ReturnValue, ResponseValue> {
    void call(ReturnValue returnValue, ICallback<ResponseValue> callback);
}
