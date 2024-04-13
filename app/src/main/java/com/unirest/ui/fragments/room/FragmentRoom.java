package com.unirest.ui.fragments.room;

import android.content.Intent;
import android.net.Uri;
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
import com.unirest.data.models.NotificationRequest;
import com.unirest.data.models.User;
import com.unirest.data.viewmodels.NotificationViewModel;
import com.unirest.databinding.FragmentRoomBinding;
import com.unirest.ui.common.BaseFragment;

import java.util.Collections;

public class FragmentRoom extends BaseFragment<FragmentRoomBinding> {
    private final UserAdapter adapter = new UserAdapter();
    private NotificationViewModel notificationViewModel;

    public FragmentRoom() {
        super(FragmentRoomBinding.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationViewModel = initViewModel(NotificationViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.selectedRoom.observe(getViewLifecycleOwner(), room -> {
                if (token == null || room == null) return;
                binding.room.setText(String.format("%s %s", getString(R.string.room), room.getNumber()));
                DataNetHandler.getInstance().verifyAuth(token, isVerified -> {
                    if (isVerified) {
                        DataNetHandler.getInstance().getUsers(room.getId(), users -> {
                            if (users.isEmpty()) return;

                            adapter.setItems(users);

                            adapter.setEmailLongCallback(user -> {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("plain/text");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
                                startActivity(Intent.createChooser(intent, ""));
                            });

                            adapter.setPhoneLongCallback(user -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + user.getPhoneNumber()));
                                startActivity(intent);
                            });

                            adapter.setCallToMeCallbackNow(user -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                                builder.setMessage(String.format("%s?", getString(R.string.dialog_call_user_now)))
                                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                            User sender = mainViewModel.user.getValue();
                                            if (sender != null) {
                                                NotificationRequest request = NotificationRequest.fastCallRequest(Collections.singletonList(user), requireContext());
                                                DataNetHandler.getInstance().callToMe(sender.getId(), request, sent -> {
                                                    if (isVisible()) {
                                                        Snackbar.make(view, sent ? R.string.sent : R.string.error, Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                                    }
                                                });
                                            }
                                            dialog.dismiss();
                                        })
                                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                            dialog.dismiss();
                                        });
                                builder.show();
                            });

                            adapter.setAddUserToNotifyCallback(user -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                                builder.setMessage(String.format("%s?", getString(R.string.dialog_add_user_notification)))
                                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                            notificationViewModel.users.getValue().add(user);
                                            dialog.dismiss();
                                        })
                                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                            dialog.dismiss();
                                        });
                                builder.show();
                            });


                            if (isVisible()) {
                                binding.shimmer.hideShimmer();
                                binding.shimmer.setVisibility(View.GONE);
                                binding.itemsRV.setVisibility(View.VISIBLE);
                            }

                            binding.callAll.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                                User sender = mainViewModel.user.getValue();
                                if (sender != null) {
                                    NotificationRequest request = NotificationRequest.fastCallRequest(users, requireContext());
                                    DataNetHandler.getInstance().callToMe(sender.getId(), request, sent -> {
                                        if (isVisible()) {
                                            Snackbar.make(view, sent ? R.string.sent : R.string.error, Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                        }
                                    });
                                    enableButton.call(true);
                                }
                            });
                        });
                    }
                });
            });
        });
    }
}
