package com.unirest.ui.fragments.profile.notification;

import android.view.View;

import androidx.annotation.NonNull;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.User;
import com.unirest.databinding.ItemUserNotificationBinding;

public class NotificationUserAdapter extends BaseAdapter<User> {
    private static ICallback<User> removeCallback;

    {
        addHolder(new HolderPair(NotificationUserHolder.class, R.layout.item_user_notification));
    }

    public static class NotificationUserHolder extends BaseHolder<User> {
        private final ItemUserNotificationBinding binding;

        public NotificationUserHolder(@NonNull View view) {
            super(view);
            binding = ItemUserNotificationBinding.bind(view);
        }

        @Override
        public void bind(User user) {
            if (removeCallback != null) {
                binding.name.setText(user.getName());
                binding.surname.setText(user.getLastName());
                binding.room.setText(String.format("%s %s", itemView.getContext().getString(R.string.room), user.getRoom().getNumber()));

                binding.removeImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    removeCallback.call(user);
                    enableButton.call(true);
                });
            }
        }
    }

    public void setRemoveCallback(ICallback<User> removeCallback) {
        NotificationUserAdapter.removeCallback = removeCallback;
    }
}
