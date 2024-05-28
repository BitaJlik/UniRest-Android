package com.unirest.ui.fragments.profile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataLocalHandler;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentProfileBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.login.FragmentLogin;
import com.unirest.ui.fragments.payment.FragmentPayment;
import com.unirest.ui.fragments.profile.notification.FragmentNotificationEditor;
import com.unirest.ui.fragments.profile.information.FragmentProfileEdit;
import com.unirest.ui.fragments.settings.FragmentSettings;

public class FragmentProfile extends BaseFragment<FragmentProfileBinding> {
    public FragmentProfile() {
        super(FragmentProfileBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            getActivityMain().updateUser();
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                DataNetHandler.getInstance().verifyAuth(token, isValid -> {
                    if (!isVisible()) return;

                    if (!isValid) {
                        changeFragment(new FragmentLogin());
                    }

                    if (user != null && user.getRole().getLevel() > 1) {
                        binding.layoutNotification.setVisibility(View.VISIBLE);
                        binding.layoutPersonal.setVisibility(View.VISIBLE);

                        binding.notificationEdit.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            changeFragment(new FragmentNotificationEditor(), true);
                            enableButton.call(true);
                        });

                        binding.personalList.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            // TODO: 26.04.2024 Personal fragment
                        });
                    }
                    else {
                        binding.layoutNotification.setVisibility(View.GONE);
                        binding.layoutPersonal.setVisibility(View.GONE);
                    }


                    binding.personalInfo.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        changeFragment(new FragmentProfileEdit(), true);
                        enableButton.call(true);
                    });

                    binding.payments.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        changeFragment(new FragmentPayment(), true);
                        enableButton.call(true);
                    });


                    binding.settings.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        changeFragment(new FragmentSettings(), true);
                        enableButton.call(true);
                    });

                    binding.logout.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle(String.format("%s?", getString(R.string.log_out)));
                        builder.setMessage(String.format("%s?", getString(R.string.are_sure_logout)))
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                    mainViewModel.token.setValue(null);
                                    DataLocalHandler.getInstance().saveAll();
                                    dialog.dismiss();
                                })
                                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                    dialog.dismiss();
                                });
                        builder.show();

                        enableButton.call(true);
                    });
                });

            });
        });

    }
}
