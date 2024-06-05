package com.unirest.ui.fragments.room.add;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentRoomAddUserBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.room.RoomUserAdapter;

public class FragmentRoomAddUser extends BaseFragment<FragmentRoomAddUserBinding> {
    private final RoomAddUserAdapter adapter = new RoomAddUserAdapter();
    private volatile boolean isSearching = false;

    public FragmentRoomAddUser() {
        super(FragmentRoomAddUserBinding.class);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);

        mainViewModel.selectedRoom.observe(getViewLifecycleOwner(), room -> {
            adapter.setUserICallback(user -> {
                DataNetHandler.getInstance().addUserToRoom(user.getId(), room.getId(), success -> {
                    if (success) {
                        getActivityMain().getOnBackPressedDispatcher().onBackPressed();
                    }
                });
            });
        });

        binding.search.setOnClickListener((OnClickCallback) (v, enableButton) -> {
            isSearching = true;
            binding.itemsRV.setVisibility(View.INVISIBLE);
            binding.progress.setVisibility(View.VISIBLE);
            String keyWord = getText(binding.editText.getText());
            DataNetHandler.getInstance().searchUsersForRoom(binding.checkbox.isChecked(), keyWord, users -> {
                if (users != null) {
                    binding.itemsRV.setVisibility(View.VISIBLE);
                    binding.progress.setVisibility(View.GONE);
                    adapter.setItems(users);
                }
                isSearching = false;
                enableButton.call(true);
            });
        });

        binding.checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!isSearching) {
                isSearching = true;
                binding.itemsRV.setVisibility(View.INVISIBLE);

                binding.progress.setVisibility(View.VISIBLE);
                String keyWord = getText(binding.editText.getText());
                DataNetHandler.getInstance().searchUsersForRoom(binding.checkbox.isChecked(), keyWord, users -> {
                    if (users != null) {
                        binding.itemsRV.setVisibility(View.VISIBLE);
                        binding.progress.setVisibility(View.GONE);
                        adapter.setItems(users);
                    }
                    isSearching = false;
                });
            }
        });
    }

    private String getText(Editable editable) {
        if (editable == null) {
            return "";
        } else {
            return editable.toString();
        }
    }
}
