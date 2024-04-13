package com.unirest.ui.fragments.profile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unirest.api.OnClickCallback;
import com.unirest.data.DataLocalHandler;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentProfileBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.login.FragmentLogin;
import com.unirest.ui.fragments.profile.information.FragmentProfileEdit;
import com.unirest.ui.fragments.settings.FragmentSettings;

public class FragmentProfile extends BaseFragment<FragmentProfileBinding> {
    public FragmentProfile() {
        super(FragmentProfileBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            DataNetHandler.getInstance().verifyAuth(token, isValid -> {

                if (!isValid) {
                    changeFragment(new FragmentLogin());
                }

                binding.personalInfo.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    changeFragment(new FragmentProfileEdit(), true);
                    enableButton.call(true);
                });

                binding.settings.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    changeFragment(new FragmentSettings(), true);
                    enableButton.call(true);
                });

                binding.logout.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    mainViewModel.token.setValue(null);
                    DataLocalHandler.getInstance().saveAll();
                    enableButton.call(true);
                });
            });
        });

    }
}
