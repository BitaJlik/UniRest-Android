package com.unirest.ui.fragments.room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.R;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentRoomBinding;
import com.unirest.ui.common.BaseFragment;

public class FragmentRoom extends BaseFragment<FragmentRoomBinding> {
    private final UserAdapter adapter = new UserAdapter();

    public FragmentRoom() {
        super(FragmentRoomBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivityMain().updateUser();
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.selectedRoom.observe(getViewLifecycleOwner(), room -> {
                if (token == null || room == null) return;
                binding.room.setText(String.format("%s %s", getString(R.string.room), room.getNumber()));
                DataNetHandler.getInstance().verifyAuth(token, isVerified -> {
                    if (isVerified) {
                        DataNetHandler.getInstance().getUsers(room.getId(), users -> {
                            adapter.setItems(users);
                            adapter.setUserICallback(user -> {
                                // TODO: 07.04.2024 Fragment User Info
                            });
                            adapter.setEmailLongCallback(user -> {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("plain/text");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
                                startActivity(Intent.createChooser(intent, ""));
                            });
                            adapter.setPhoneLongCallback(user -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + user.getPhoneNumber()));
                                startActivity(intent);
                            });
                        });
                    }
                });
            });
        });
    }
}
