package com.unirest.ui.fragments.dormitory;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unirest.api.OnClickCallback;
import com.unirest.databinding.FragmentDormitoryBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.floors.FragmentFloors;

public class FragmentDormitory extends BaseFragment<FragmentDormitoryBinding> {

    public FragmentDormitory() {
        super(FragmentDormitoryBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.floors.setOnClickListener((OnClickCallback) (v, enableButton) -> {
            changeFragment(new FragmentFloors());
            enableButton.call(true);
        });
    }
}
