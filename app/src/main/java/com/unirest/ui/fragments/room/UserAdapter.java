package com.unirest.ui.fragments.room;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.User;
import com.unirest.databinding.ItemUserBinding;

public class UserAdapter extends BaseAdapter<User> {
    private static ICallback<User> userICallback;

    private static ICallback<User> phoneLongCallback;
    private static ICallback<User> emailLongCallback;

    {
        addHolder(new HolderPair(UserHolder.class, R.layout.item_user));
    }


    public static class UserHolder extends BaseHolder<User> {
        private final ItemUserBinding binding;

        public UserHolder(@NonNull View view) {
            super(view);
            binding = ItemUserBinding.bind(view);
        }

        @Override
        public void bind(User user) {
            Context context = itemView.getContext();
            binding.name.setText(user.getName());
            binding.surname.setText(user.getSurName());
            binding.balance.setText(String.format("%s: %s", context.getString(R.string.balance), user.getBalance()));
            binding.course.setText(String.format("%s: %s", context.getString(R.string.course), user.getCourse()));
            binding.phone.setText(user.getPhoneNumber());
            binding.email.setText(user.getEmail());

            binding.getRoot().setOnClickListener((OnClickCallback) (v, enableButton) -> {
                if (userICallback != null) {
                    userICallback.call(user);
                }
                enableButton.call(true);
            });

            DataNetHandler.getInstance().getUrlImageUser(user, url -> {
                Picasso.get().load(url).into(binding.image);
            });

            TooltipCompat.setTooltipText(binding.phoneImage, context.getString(R.string.call_on_phone));
            TooltipCompat.setTooltipText(binding.emailImage, context.getString(R.string.text_on_email));

            binding.phoneImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                if (phoneLongCallback != null) {
                    phoneLongCallback.call(user);
                    enableButton.call(true);
                }
            });
            binding.emailImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                if (emailLongCallback != null) {
                    emailLongCallback.call(user);
                    enableButton.call(true);
                }
            });
        }

    }

    public void setPhoneLongCallback(ICallback<User> phoneLongCallback) {
        UserAdapter.phoneLongCallback = phoneLongCallback;
    }

    public void setEmailLongCallback(ICallback<User> emailLongCallback) {
        UserAdapter.emailLongCallback = emailLongCallback;
    }

    public void setUserICallback(ICallback<User> userICallback) {
        UserAdapter.userICallback = userICallback;
    }
}

