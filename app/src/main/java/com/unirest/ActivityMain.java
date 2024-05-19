package com.unirest;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.unirest.api.IBackPressed;
import com.unirest.api.IReload;
import com.unirest.data.DataLocalHandler;
import com.unirest.data.DataNetHandler;
import com.unirest.data.viewmodels.MainViewModel;
import com.unirest.databinding.ActivityMainBinding;
import com.unirest.ui.fragments.dormitory.FragmentDormitory;
import com.unirest.ui.fragments.home.FragmentHome;
import com.unirest.ui.fragments.notifications.FragmentNotifications;
import com.unirest.ui.fragments.profile.FragmentProfile;
import com.unirest.ui.fragments.scanner.FragmentScanner;

public class ActivityMain extends AppCompatActivity {
    private boolean doubleBack;
    private boolean forceSelect;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DataLocalHandler.getInstance(this).loadAll();
        this.updateUser();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (!forceSelect) {
                // TODO: 06.05.2024 Make block dialog if server not responding
                if (item.getItemId() == R.id.home) {
                    changeFragment(new FragmentHome());
                }
                if (item.getItemId() == R.id.dormitory) {
                    changeFragment(new FragmentDormitory());
                }
                if (item.getItemId() == R.id.scanner) {
                    changeFragment(new FragmentScanner(), true);
                }
                if (item.getItemId() == R.id.notifications) {
                    changeFragment(new FragmentNotifications());
                }
                if (item.getItemId() == R.id.profile) {
                    changeFragment(new FragmentProfile());
                }
            }
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getSupportFragmentManager().getFragments().get(0);
                if (fragment instanceof IBackPressed) {
                    if (!((IBackPressed) fragment).onBackPressed()) {
                        this.setEnabled(false);
                        ActivityMain.this.getOnBackPressedDispatcher().onBackPressed();
                        this.setEnabled(true);
                    }
                } else {
                    this.setEnabled(false);
                    ActivityMain.this.getOnBackPressedDispatcher().onBackPressed();
                    this.setEnabled(true);
                }
            }
        });

        this.selectNav(R.id.home);
        this.changeFragment(new FragmentHome());
    }

    public void updateUser() {
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        String tokenValue = mainViewModel.token.getValue();
        if (tokenValue != null) {
            DataNetHandler.getInstance().getUser(tokenValue, user -> {
                if(user != null){
                    mainViewModel.user.setValue(user);
                    DataNetHandler.getInstance().updateStatus(user.getId());
                    DataLocalHandler.getInstance(this).saveUser();
                }
            });
        }
    }

    public void changeFragment(Fragment fragment) {
        changeFragment(fragment, false);
    }

    public void changeFragment(Fragment fragment, boolean addToBackStack) {
        changeFragment(fragment, addToBackStack, false);
    }

    public void changeFragment(Fragment fragment, boolean addToBackStack, boolean forceInstance) {
        doubleBack = false;
        if (!forceInstance) {
            Fragment f = binding.fragmentContainer.getFragment();
            if (f != null && f.getClass().equals(fragment.getClass())) {
                if (f instanceof IReload) {
                    ((IReload) f).onReload();
                }
                return;
            }
        }
        runOnUiThread(() -> {
            if (!addToBackStack) {
                FragmentManager fm = getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (addToBackStack) {
                transaction.addToBackStack(fragment.getClass().getSimpleName());
            }
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        });
    }

    public void selectNav(@IdRes int itemId) {
        forceSelect = true;
        binding.bottomNavigation.setSelectedItemId(itemId);
        forceSelect = false;
    }

    public void handleNavBar(boolean isShow) {
        binding.bottomNavigation.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }


}