package com.unirest.ui.fragments.notifications;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.api.ICallback;
import com.unirest.api.ICallbackResponse;
import com.unirest.api.IReload;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Notification;
import com.unirest.data.models.User;
import com.unirest.databinding.FragmentNotificationsBinding;
import com.unirest.ui.common.BaseFragment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class FragmentNotifications extends BaseFragment<FragmentNotificationsBinding> {
    private final NotificationAdapter adapter = new NotificationAdapter();

    public FragmentNotifications() {
        super(FragmentNotificationsBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.rvItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvItems.setAdapter(adapter);

        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            DataNetHandler.getInstance().verifyAuth(token, isVerified -> {
                if (isVerified) {
                    mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                        DataNetHandler.getInstance().getNotifications(user.getId(), notifications -> {
                            adapter.setCallbackUserResponse((unused, callback) -> callback.call(user));
                            adapter.setItems(notifications);
                        });
                    });
                }
            });
        });

    }
}
