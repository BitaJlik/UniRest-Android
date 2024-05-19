package com.unirest.ui.fragments.rooms;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentRoomsBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.cookers.FragmentCookers;
import com.unirest.ui.fragments.room.FragmentRoom;
import com.unirest.ui.fragments.washers.FragmentWashers;

public class FragmentRooms extends BaseFragment<FragmentRoomsBinding> {
    private final RoomAdapter adapter = new RoomAdapter();

    public FragmentRooms() {
        super(FragmentRoomsBinding.class);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.selectedFloor.observe(getViewLifecycleOwner(), floor -> {
                if (token == null || floor == null) return;
                DataNetHandler.getInstance().verifyAuth(token, isVerified -> {
                    if (isVerified) {

                        if (floor.getWashers().isEmpty()) {
                            binding.washers.setVisibility(View.GONE);
                        } else {
                            binding.washers.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                                changeFragment(new FragmentWashers(), true);
                                enableButton.call(true);
                            });
                        }

                        if (floor.getCookers().isEmpty()) {
                            binding.cookers.setVisibility(View.GONE);
                        } else {
                            binding.cookers.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                                changeFragment(new FragmentCookers(), true);
                                enableButton.call(true);
                            });
                        }


                        DataNetHandler.getInstance().getRooms(floor.getId(), rooms -> {
                            if (rooms.isEmpty()) return;

                            adapter.setItems(rooms);
                            adapter.setRoomCallback(room -> {
                                mainViewModel.selectedRoom.setValue(room);
                                changeFragment(new FragmentRoom(), true);
                            });

                            if (isVisible()) {
                                binding.shimmer.hideShimmer();
                                binding.shimmer.setVisibility(View.GONE);
                                binding.itemsRV.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            });
        });
    }
}
