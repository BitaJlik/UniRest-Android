package com.unirest.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.unirest.data.models.CookerRemind;
import com.unirest.data.models.User;
import com.unirest.data.models.WasherRemind;
import com.unirest.data.viewmodels.MainViewModel;

@SuppressWarnings("unused")
public class DataLocalHandler {
    private final Gson gson = new Gson();

    public void loadAll() {
        loadToken();
        loadUser();
        loadWasherReminder();
        loadCookerReminder();
    }

    public void loadUser() {
        mainViewModel.user.postValue(gson.fromJson(preferences.getString("User", null), User.class));
    }

    public void loadToken() {
        mainViewModel.token.postValue(gson.fromJson(preferences.getString("Token", null), String.class));
    }

    public void loadWasherReminder() {
        mainViewModel.washerRemind.postValue(gson.fromJson(preferences.getString("WasherReminder", null), WasherRemind.class));
    }

    public void loadCookerReminder() {
        mainViewModel.cookerRemind.postValue(gson.fromJson(preferences.getString("CookerReminder", null), CookerRemind.class));
    }

    public void saveAll() {
        saveToken();
        saveUser();
        saveWasherReminder();
        saveCookerReminder();
    }

    public void saveUser() {
        edit(editor -> {
            User user = mainViewModel.user.getValue();
            if (user != null) {
                editor.putString("User", gson.toJson(user));
            }
        });
    }

    public void saveToken() {
        edit(editor -> {
            String token = mainViewModel.token.getValue();
            if (token != null) {
                String oldToken = preferences.getString("Token", null);
                if (oldToken != null) {
                    if (oldToken.equals(token)) return;
                }
                editor.putString("Token", gson.toJson(token));
            }
        });
    }

    public void saveWasherReminder() {
        edit(editor -> {
            WasherRemind reminder = mainViewModel.washerRemind.getValue();
            if (reminder != null) {
                editor.putString("WasherReminder", gson.toJson(reminder));
            }
        });
    }

    public void saveCookerReminder() {
        edit(editor -> {
            WasherRemind reminder = mainViewModel.washerRemind.getValue();
            if (reminder != null) {
                editor.putString("CookerReminder", gson.toJson(reminder));
            }
        });
    }

    // Other
    private static DataLocalHandler instance;
    private final MainViewModel mainViewModel;
    private final SharedPreferences preferences;

    private DataLocalHandler(AppCompatActivity activity) {
        preferences = activity.getSharedPreferences("Data", MODE_PRIVATE);
        mainViewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    /**
     * @param activity needs to create instance
     * @return instance to working local data
     */
    public static DataLocalHandler getInstance(AppCompatActivity activity) {
        if (instance == null && activity != null) {
            instance = new DataLocalHandler(activity);
        }
        return instance;
    }

    /**
     * Can be available after creating instance
     *
     * @return instance if created
     */
    public static DataLocalHandler getInstance() {
        if (instance == null) {
            throw new RuntimeException("Not used getInstance#AppCompatActivity method");
        }
        return instance;
    }

    // Utils
    public void clear() {
        edit(SharedPreferences.Editor::clear);
    }

    private void edit(SharedEditor editor) {
        System.out.println("DataLocalHandler editing...");
        SharedPreferences.Editor edit = preferences.edit();
        editor.edit(edit);
        edit.apply();
    }


    private interface SharedEditor {
        void edit(SharedPreferences.Editor editor);
    }
}
