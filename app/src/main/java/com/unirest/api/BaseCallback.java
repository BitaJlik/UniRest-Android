package com.unirest.api;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Interface created for simple handling json from body response
 */
public interface BaseCallback<T> extends Callback<T> {
    @Override
    default void onResponse(@NonNull Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            T responseBody = response.body();
            if (responseBody != null) {
                if (responseBody instanceof ResponseBody) {
                    try {
                        onJson(response, ((ResponseBody) responseBody).string());
                    } catch (IOException ignored) {
                    }
                }
            }
        } else {
            onFailure(call, new RuntimeException("Not successful " + response.code()));
        }
    }

    @Override
    default void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        Log.e("BaseCallback", call.request().url() + "  ->  " + t.getMessage());
    }

    void onJson(Response<T> response, String jsonString);
}
