package com.unirest.data.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.unirest.data.models.CookerRemind;
import com.unirest.data.models.Floor;
import com.unirest.data.models.Payment;
import com.unirest.data.models.Room;
import com.unirest.data.models.User;
import com.unirest.data.models.WasherRemind;

import java.util.regex.Pattern;

public class MainViewModel extends AndroidViewModel {

    public final MutableLiveData<User> user = new MutableLiveData<>(null);
    public final MutableLiveData<String> token = new MutableLiveData<>(null); // Main value
    public final MutableLiveData<String> barcode = new MutableLiveData<>(null);
    public final MutableLiveData<Payment> payment = new MutableLiveData<>(null);
    public final MutableLiveData<Payment> creatingPayment = new MutableLiveData<>(null);
    public final MutableLiveData<WasherRemind> washerRemind = new MutableLiveData<>(null);
    public final MutableLiveData<CookerRemind> cookerRemind = new MutableLiveData<>(null);

    public final MutableLiveData<Floor> selectedFloor = new MutableLiveData<>(null);
    public final MutableLiveData<Room> selectedRoom = new MutableLiveData<>(null);

    public MainViewModel(@NonNull Application application) {
        super(application);
    }
}
