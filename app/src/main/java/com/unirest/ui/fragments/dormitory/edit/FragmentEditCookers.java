package com.unirest.ui.fragments.dormitory.edit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Cooker;
import com.unirest.databinding.FragmentEditCookersBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.cookers.CookerAdapter;

public class FragmentEditCookers extends BaseFragment<FragmentEditCookersBinding> {
    private final CookerAdapter adapter = new CookerAdapter();

    public FragmentEditCookers() {
        super(FragmentEditCookersBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                mainViewModel.selectedFloor.observe(getViewLifecycleOwner(), floor -> {
                    DataNetHandler.getInstance().getCookers(floor.getId(), cookers -> {
                        adapter.setModeEdit(true);
                        adapter.setItems(cookers);
                        adapter.setRemoveICallback(cooker -> {
                            DataNetHandler.getInstance().adminUpdateCooker(cooker, true, success -> {
                                if (success) {
                                    cookers.remove(cooker);
                                    adapter.setItems(cookers);
                                }
                            });
                        });
                        binding.add.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            Cooker cooker = new Cooker();
                            cooker.setFloor(floor.getId());
                            DataNetHandler.getInstance().adminUpdateCooker(cooker, false, success -> {
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
