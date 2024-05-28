package com.unirest.ui.fragments.washers;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentWashersBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.washer.FragmentWasher;

public class FragmentWashers extends BaseFragment<FragmentWashersBinding> {
    private final WasherAdapter adapter = new WasherAdapter();

    public FragmentWashers() {
        super(FragmentWashersBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);

        mainViewModel.selectedFloor.observe(getViewLifecycleOwner(), floor -> {
            if (floor == null) return;

            DataNetHandler.getInstance().getWashers(floor.getId(), washers -> {
                adapter.setItems(washers);
                adapter.setWasherICallback(washer -> {
                    changeFragment(new FragmentWasher(washer.getId()), true);
                });
            });
        });
    }

}
