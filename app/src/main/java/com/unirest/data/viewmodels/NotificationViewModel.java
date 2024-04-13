package com.unirest.data.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.unirest.data.models.User;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends AndroidViewModel {
    public final MutableLiveData<String> title = new MutableLiveData<>(null);
    public final MutableLiveData<String> content = new MutableLiveData<>(null);

    public final MutableLiveData<List<User>> users = new MutableLiveData<>(new ArrayList<>());


    public NotificationViewModel(@NonNull Application application) {
        super(application);
    }
}
