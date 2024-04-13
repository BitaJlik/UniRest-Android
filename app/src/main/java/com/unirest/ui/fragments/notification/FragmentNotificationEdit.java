package com.unirest.ui.fragments.notification;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Notification;
import com.unirest.data.models.NotificationRequest;
import com.unirest.data.viewmodels.NotificationViewModel;
import com.unirest.databinding.FragmentNotificationEditBinding;
import com.unirest.ui.common.BaseFragment;

public class FragmentNotificationEdit extends BaseFragment<FragmentNotificationEditBinding> {
    private NotificationViewModel notificationViewModel;

    public FragmentNotificationEdit() {
        super(FragmentNotificationEditBinding.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationViewModel = initViewModel(NotificationViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
            binding.send.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                NotificationRequest request = new NotificationRequest();
                request.setUsers(notificationViewModel.users.getValue());
                Notification notification = new Notification();
                notification.setDate(System.currentTimeMillis());
                notification.setTitle(binding.layoutTitle.getEditText().toString());
                notification.setContent(binding.layoutContent.getEditText().toString());
                request.setNotificationTemplate(notification);
                DataNetHandler.getInstance().callToMe(user.getId(), request, sent -> {
                    if (sent) {
                        Snackbar.make(view, R.string.success_upload, Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    } else {
                        Snackbar.make(view, R.string.fail_upload, Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    }
                    enableButton.call(true);
                });
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.layoutTitle.getEditText().setText(notificationViewModel.title.getValue());
        binding.layoutContent.getEditText().setText(notificationViewModel.content.getValue());
    }

    @Override
    public void onPause() {
        notificationViewModel.title.setValue(binding.layoutTitle.getEditText().toString());
        notificationViewModel.content.setValue(binding.layoutContent.getEditText().toString());
        super.onPause();
    }

}
