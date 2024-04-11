package com.unirest.data.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.unirest.R;

import java.util.Arrays;

public class ErrorViewModel extends AndroidViewModel {
    public final MutableLiveData<String> errorTitle = new MutableLiveData<>(null);
    public final MutableLiveData<String> errorDescription = new MutableLiveData<>(null);

    public void handleError(Throwable throwable) {
        errorTitle.postValue(throwable.getMessage());
        errorDescription.postValue(Arrays.toString(throwable.getStackTrace()));
    }

    public void handleError(String title, String description) {
        errorTitle.postValue(title);
        errorDescription.postValue(description);
    }

    public void defaultError() {
        Context context = getApplication().getApplicationContext();
        errorTitle.postValue(context.getString(R.string.error_default_title));
        errorDescription.postValue(context.getString(R.string.error_default_description));
    }

    public ErrorViewModel(@NonNull Application application) {
        super(application);
        defaultError();
    }
}
