package com.unirest.ui.fragments.dormitory.edit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Washer;
import com.unirest.databinding.FragmentEditWashersBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.washers.WasherAdapter;

public class FragmentEditWashers extends BaseFragment<FragmentEditWashersBinding> {
    private final WasherAdapter adapter = new WasherAdapter();

    public FragmentEditWashers() {
        super(FragmentEditWashersBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                mainViewModel.selectedFloor.observe(getViewLifecycleOwner(), floor -> {
                    DataNetHandler.getInstance().getWashers(floor.getId(), washers -> {
                        adapter.setModeEdit(true);
                        adapter.setItems(washers);
                        adapter.setRemoveICallback(washer -> {
                            DataNetHandler.getInstance().adminUpdateWasher(washer, true, success -> {
                                if (success) {
                                    washers.remove(washer);
                                    adapter.setItems(washers);
                                }
                            });
                        });
                        binding.add.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            Washer washer = new Washer();
                            washer.setFloor(floor.getId());
                            DataNetHandler.getInstance().adminUpdateWasher(washer, false, success -> {
                                if (success) {
                                    mainViewModel.selectedFloor.postValue(floor);
                                }
                                enableButton.call(true);
                            });
                        });
                    });
                });
            });
        });
    }
}
