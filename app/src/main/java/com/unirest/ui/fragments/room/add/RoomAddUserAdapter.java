package com.unirest.ui.fragments.room.add;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.User;
import com.unirest.databinding.ItemUserBinding;

import java.util.Locale;

public class RoomAddUserAdapter extends BaseAdapter<User> {

    private static ICallback<User> userICallback;


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
            if (user instanceof User.UserStub) return;

            Context context = itemView.getContext();
            binding.name.setText(user.getName());
            binding.surname.setText(user.getSurName());
            binding.balance.setText(String.format("%s: %s", context.getString(R.string.balance), user.getBalance()));
            binding.course.setText(String.format("%s: %s", context.getString(R.string.course), user.getCourse()));
            binding.phone.setText(String.format("+%s", PhoneNumberUtils.formatNumber(user.getPhoneNumber(), Locale.getDefault().getCountry())));
            binding.email.setText(user.getEmail());


            DataNetHandler.getInstance().getUrlImageUser(user, url -> {
                Picasso.get().load(url).into(binding.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        binding.image.setVisibility(View.VISIBLE);
                        binding.progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        binding.progress.setVisibility(View.GONE);
                        binding.imageError.setVisibility(View.VISIBLE);
                    }
                });
            });


            if (userICallback != null) {
                binding.getRoot().setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    userICallback.call(user);
                    enableButton.call(true);
                });
            }
            binding.adminLayout.setVisibility(View.GONE);
        }

    }

    public void setUserICallback(ICallback<User> userICallback) {
        RoomAddUserAdapter.userICallback = userICallback;
    }
}
