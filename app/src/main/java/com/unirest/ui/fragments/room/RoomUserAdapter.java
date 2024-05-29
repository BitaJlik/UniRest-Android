package com.unirest.ui.fragments.room;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;

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

public class RoomUserAdapter extends BaseAdapter<User> {
    private static ICallback<User> userICallback;

    private static ICallback<User> addUserToNotifyCallback;
    private static ICallback<User> callToMeCallbackNow;
    private static ICallback<User> phoneLongCallback;
    private static ICallback<User> emailLongCallback;

    private static boolean isAdminPermission = false;

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
            binding.phone.setText(String.format("+%s", PhoneNumberUtils.formatNumber(user.getPhoneNumber(), Locale.getDefault().getCountry())));
            binding.email.setText(user.getEmail());

            if (userICallback != null) {
                binding.getRoot().setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    userICallback.call(user);
                    enableButton.call(true);
                });
            }

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

            if (isAdminPermission) {
                binding.hereImage.setVisibility(View.VISIBLE);
                binding.phoneImage.setVisibility(View.VISIBLE);
                binding.emailImage.setVisibility(View.VISIBLE);

                binding.email.setVisibility(View.VISIBLE);
                binding.phone.setVisibility(View.VISIBLE);
                binding.balance.setVisibility(View.VISIBLE);
            } else {
                binding.hereImage.setVisibility(View.GONE);
                binding.phoneImage.setVisibility(View.GONE);
                binding.emailImage.setVisibility(View.GONE);

                binding.email.setVisibility(View.GONE);
                binding.phone.setVisibility(View.GONE);
                binding.balance.setVisibility(View.GONE);
            }

            TooltipCompat.setTooltipText(binding.hereImage, context.getString(R.string.call_to_me));
            TooltipCompat.setTooltipText(binding.phoneImage, context.getString(R.string.call_on_phone));
            TooltipCompat.setTooltipText(binding.emailImage, context.getString(R.string.text_on_email));

            binding.hereImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                PopupMenu popupMenu = new PopupMenu(context, binding.hereImage);
                if (callToMeCallbackNow != null) {
                    popupMenu.getMenu().add(0, 0, 0, context.getString(R.string.call_to_me_now));
                }
                if (addUserToNotifyCallback != null) {
                    popupMenu.getMenu().add(0, 1, 0, context.getString(R.string.add_to_notify));
                }
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 0) {
                        callToMeCallbackNow.call(user);
                    }
                    if (item.getItemId() == 1) {
                        addUserToNotifyCallback.call(user);
                    }
                    return true;
                });

                popupMenu.show();
                enableButton.call(true);
            });

            if (phoneLongCallback != null) {
                binding.phoneImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    phoneLongCallback.call(user);
                    enableButton.call(true);
                });
            }
            if (emailLongCallback != null) {
                binding.emailImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    emailLongCallback.call(user);
                    enableButton.call(true);
                });
            }
        }

    }

    public void setPhoneLongCallback(ICallback<User> phoneLongCallback) {
        RoomUserAdapter.phoneLongCallback = phoneLongCallback;
    }

    public void setEmailLongCallback(ICallback<User> emailLongCallback) {
        RoomUserAdapter.emailLongCallback = emailLongCallback;
    }

    public void setUserICallback(ICallback<User> userICallback) {
        RoomUserAdapter.userICallback = userICallback;
    }

    public void setAddUserToNotifyCallback(ICallback<User> addUserToNotifyCallback) {
        RoomUserAdapter.addUserToNotifyCallback = addUserToNotifyCallback;
    }

    public void setCallToMeCallbackNow(ICallback<User> callToMeCallbackNow) {
        RoomUserAdapter.callToMeCallbackNow = callToMeCallbackNow;
    }

    public void setAdminPermission(boolean adminPermission) {
        isAdminPermission = adminPermission;
    }
}

