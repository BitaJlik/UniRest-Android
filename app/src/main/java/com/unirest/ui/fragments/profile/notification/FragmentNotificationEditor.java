package com.unirest.ui.fragments.profile.notification;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Notification;
import com.unirest.data.models.NotificationRequest;
import com.unirest.data.models.User;
import com.unirest.data.viewmodels.NotificationViewModel;
import com.unirest.databinding.FragmentNotificationEditorBinding;
import com.unirest.ui.common.BaseFragment;

import java.util.Collections;

public class FragmentNotificationEditor extends BaseFragment<FragmentNotificationEditorBinding> {
    private final NotificationUserAdapter adapter = new NotificationUserAdapter();
    private NotificationViewModel notificationViewModel;

    public FragmentNotificationEditor() {
        super(FragmentNotificationEditorBinding.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationViewModel = initViewModel(NotificationViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
            notificationViewModel.users.observe(getViewLifecycleOwner(), users -> {
                if (users == null || users.isEmpty()) {
                    binding.itemsText.setVisibility(View.VISIBLE);
                    binding.itemsRV.setVisibility(View.GONE);
                    return;
                }

                binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.itemsRV.setAdapter(adapter);

                adapter.setItems(users);
                adapter.setRemoveCallback(userToRemove -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle(getString(R.string.remove_from_list));
                    builder.setMessage(getString(R.string.remove_from_notification_list))
                            .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                users.remove(userToRemove);
                                adapter.setItems(users);
                                dialog.dismiss();
                            })
                            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                dialog.dismiss();
                            });
                    builder.show();
                });

                binding.send.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    NotificationRequest request = new NotificationRequest();
                    request.setUsers(users);
                    Notification notification = new Notification();
                    notification.setDate(System.currentTimeMillis());
                    notification.setTitle(binding.layoutTitle.getEditText().getText().toString());
                    notification.setContent(binding.layoutContent.getEditText().getText().toString());
                    request.setNotificationTemplate(notification);
                    DataNetHandler.getInstance().callToMe(user.getId(), request, sent -> {
                        if (sent) {
                            users.clear();
                            binding.layoutTitle.getEditText().getText().clear();
                            binding.layoutContent.getEditText().getText().clear();
                            notificationViewModel.users.postValue(users);
                            Snackbar.make(view, R.string.success_upload, Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                        } else {
                            Snackbar.make(view, R.string.fail_upload, Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                        }
                        enableButton.call(true);
                    });
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
        notificationViewModel.title.setValue(binding.layoutTitle.getEditText().getText().toString());
        notificationViewModel.content.setValue(binding.layoutContent.getEditText().getText().toString());
        super.onPause();
    }
}
