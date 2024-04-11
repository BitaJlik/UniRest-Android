package com.unirest.ui.fragments.notifications;


import android.view.View;

import androidx.annotation.NonNull;

import com.unirest.R;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.Notification;
import com.unirest.databinding.ItemNotificationBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NotificationAdapter extends BaseAdapter<Notification> {

    {
        addHolder(new HolderPair(NotificationHolder.class, R.layout.item_notification));
    }

    public static class NotificationHolder extends BaseHolder<Notification> {
        private final ItemNotificationBinding binding;

        public NotificationHolder(@NonNull View view) {
            super(view);
            binding = ItemNotificationBinding.bind(view);
        }

        @Override
        public void bind(Notification notification) {
            binding.headerTitle.setText(notification.getTitle());
            binding.content.setText(notification.getContent());
            binding.date.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(notification.getDate()));
        }
    }

}
