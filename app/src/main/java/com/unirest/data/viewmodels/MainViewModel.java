package com.unirest.data.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.unirest.data.models.Floor;
import com.unirest.data.models.Room;
import com.unirest.data.models.User;

public class MainViewModel extends AndroidViewModel {

    public final MutableLiveData<User> user = new MutableLiveData<>(null);
    public final MutableLiveData<String> token = new MutableLiveData<>(null); // Main value
    public final MutableLiveData<String> barcode = new MutableLiveData<>(null);

    public final MutableLiveData<Floor> selectedFloor = new MutableLiveData<>(null);
    public final MutableLiveData<Room> selectedRoom = new MutableLiveData<>(null);

    public MainViewModel(@NonNull Application application) {
        super(application);
    }
}
