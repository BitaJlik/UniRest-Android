package com.unirest.ui.fragments.rooms;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentRoomsBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.room.FragmentRoom;

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
