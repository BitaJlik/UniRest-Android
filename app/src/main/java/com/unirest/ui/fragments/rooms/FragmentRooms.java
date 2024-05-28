package com.unirest.ui.fragments.rooms;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.User;
import com.unirest.databinding.FragmentRoomsBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.cookers.FragmentCookers;
import com.unirest.ui.fragments.room.FragmentRoom;
import com.unirest.ui.fragments.washer.FragmentWasher;
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
                        boolean userOnFloor;
                        User value = mainViewModel.user.getValue();
                        if (value != null) {
                            userOnFloor = value.getRoom().getFloorId().equals(floor.getId());
                        } else {
                            userOnFloor = false;
                        }

                        if (floor.getWashers().isEmpty()) {
                            binding.washers.setVisibility(View.GONE);
                        } else {
                            binding.washers.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                                if (userOnFloor) {
                                    changeFragment(new FragmentWashers(), true);
                                    enableButton.call(true);
                                } else {
                                    showFloorDialog(enableButton, continueButton -> {
                                        if(continueButton){
                                            changeFragment(new FragmentWashers(), true);
                                            enableButton.call(true);
                                        }
                                    });
                                }
                            });
                        }

                        if (floor.getCookers().isEmpty()) {
                            binding.cookers.setVisibility(View.GONE);
                        } else {
                            binding.cookers.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                                if (userOnFloor) {
                                    changeFragment(new FragmentCookers(), true);
                                    enableButton.call(true);
                                } else {
                                    showFloorDialog(enableButton, continueButton -> {
                                        if(continueButton){
                                            changeFragment(new FragmentCookers(), true);
                                            enableButton.call(true);
                                        }
                                    });
                                }
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

    public void showFloorDialog(ICallback<Boolean> enableButton, ICallback<Boolean> continueButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(String.format("%s?", getString(R.string.confirm)));
        builder.setMessage(String.format("%s?", getString(R.string.dialog_floor_not_user)))
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    continueButton.call(true);
                    enableButton.call(true);
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    continueButton.call(false);
                    enableButton.call(true);
                    dialog.dismiss();
                });
        builder.show();
    }
}
