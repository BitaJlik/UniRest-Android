package com.unirest.ui.fragments.floors;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentFloorsBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.rooms.FragmentRooms;

public class FragmentFloors extends BaseFragment<FragmentFloorsBinding> {
    private final FloorAdapter adapter = new FloorAdapter();

    public FragmentFloors() {
        super(FragmentFloorsBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivityMain().updateUser();
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                if (token == null || user == null) return;
                DataNetHandler.getInstance().verifyAuth(token, isVerified -> {
                    if (isVerified) {
                        DataNetHandler.getInstance().getFloors(user.getDormitoryId(), floors -> {
                            adapter.setItems(floors);
                            adapter.setFloorICallback(floor -> {
                                mainViewModel.selectedFloor.setValue(floor);
                                changeFragment(new FragmentRooms(), true);
                            });
                        });
                    }
                });
            });
        });

    }
}
