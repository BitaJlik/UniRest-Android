package com.unirest.ui.fragments.cookers;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentCookersBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.cooker.FragmentCooker;
import com.unirest.ui.fragments.dormitory.CookerAdapter;

public class FragmentCookers extends BaseFragment<FragmentCookersBinding> {
    private final CookerAdapter adapter = new CookerAdapter();

    public FragmentCookers() {
        super(FragmentCookersBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);

        mainViewModel.selectedFloor.observe(getViewLifecycleOwner(), floor -> {
            if (floor == null) return;

            DataNetHandler.getInstance().getCookers(floor.getId(), cookers -> {
                adapter.setItems(cookers);
                adapter.setCookerICallback(washer -> {
                    changeFragment(new FragmentCooker(washer.getId()), true);
                });
            });
        });
    }
}
