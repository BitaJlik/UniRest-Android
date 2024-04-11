package com.unirest.ui.fragments.notifications;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.api.IReload;
import com.unirest.data.models.Notification;
import com.unirest.databinding.FragmentNotificationsBinding;
import com.unirest.ui.common.BaseFragment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class FragmentNotifications extends BaseFragment<FragmentNotificationsBinding> implements IReload {
    private final NotificationAdapter adapter = new NotificationAdapter();

    public FragmentNotifications() {
        super(FragmentNotificationsBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.rvItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvItems.setAdapter(adapter);
        Random random = new Random();
        int minDay = (int) LocalDate.of(2021, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2024, 1, 1).toEpochDay();
        ArrayList<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Notification notification = new Notification();
            notification.setContent(UUID.randomUUID().toString().replace("-", ""));
            notification.setTitle(UUID.randomUUID().toString().replace("-", ""));
            long randomDay = System.currentTimeMillis() + minDay + random.nextInt(maxDay - minDay);
            notification.setDate(randomDay);
            notifications.add(notification);
        }
        adapter.setItems(notifications);
    }


    @Override
    public void onReload() {

    }
}
