package com.unirest.ui.fragments.notifications;


import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.unirest.R;
import com.unirest.api.ICallbackResponse;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Notification;
import com.unirest.data.models.User;
import com.unirest.databinding.ItemNotificationBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NotificationAdapter extends BaseAdapter<Notification> {

    private static ICallbackResponse<Void, User> callbackUserResponse;

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
            Context context = itemView.getContext();
            if(callbackUserResponse != null){
                callbackUserResponse.call(null, user -> {
                    if (notification.getReceiver().equals(user.getId())) {
                        binding.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_message_in));
                    } else if (notification.getSender().equals(user.getId())) {
                        binding.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_message_out));
                    }

                    DataNetHandler.getInstance().getUserPermit(notification.getSender(), sender -> {
                        binding.author.setText(String.format("%s %s %s", context.getString(R.string.author), sender.getName(), sender.getLastName()));
                    });
                });
            }

            binding.headerTitle.setText(notification.getTitle());
            binding.content.setText(notification.getContent());
            binding.date.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(notification.getDate()));
        }
    }

    public void setCallbackUserResponse(ICallbackResponse<Void, User> callbackUserResponse) {
        NotificationAdapter.callbackUserResponse = callbackUserResponse;
    }

}
