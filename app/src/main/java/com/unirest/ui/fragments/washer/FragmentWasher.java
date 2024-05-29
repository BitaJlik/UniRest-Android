package com.unirest.ui.fragments.washer;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.api.UniCode;
import com.unirest.data.DataLocalHandler;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.WasherRemind;
import com.unirest.databinding.FragmentWasherBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.utils.AsyncUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentWasher extends BaseFragment<FragmentWasherBinding> {
    private long washerId = -1;

    public FragmentWasher() {
        super(FragmentWasherBinding.class);
    }

    public FragmentWasher(long washerId) {
        super(FragmentWasherBinding.class);
        this.washerId = washerId;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (washerId == -1) {
            String barcode = mainViewModel.barcode.getValue();
            if (barcode != null) {
                UniCode uniCode = new UniCode(barcode);
                if (uniCode.isCodeWashing()) {
                    washerId = uniCode.getValue();
                }
            }
        }

        DataNetHandler.getInstance().getWasher(washerId, washer -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                if (user == null) return;

                if (washer != null) {
                    DateFormat fmtOut = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                    if (washer.getUser().equals(user.getId())) {
                        binding.free.setVisibility(View.VISIBLE);
                        binding.free.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle(String.format("%s?", getString(R.string.free)));
                            builder.setMessage(String.format("%s?", getString(R.string.free_washer_description)))
                                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                        DataNetHandler.getInstance().setBusyWasher(washer.getId(), user.getId(), 0, false, callback -> {
                                            enableButton.call(true);
                                            binding.free.setVisibility(View.GONE);
                                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                            changeFragment(new FragmentWasher(washerId), true);
                                        });
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                        enableButton.call(true);
                                        dialog.dismiss();
                                    });
                            builder.show();

                        });
                    }

                    if (!washer.isBusy()) {
                        binding.buttonBusy.setText(R.string.set_busy);
                        binding.buttonBusy.setEnabled(true);

                        binding.textBusy.setText(R.string.now_is_free);
                        binding.textBusy.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));

                        binding.timeBusy.setVisibility(View.GONE);
                        binding.timeElapsed.setVisibility(View.GONE);

                        binding.buttonBusy.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            binding.busyLayout.setVisibility(binding.busyLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                            enableButton.call(true);
                        });

                        binding.buttonTake.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            int minutes = (int) binding.slider.getValue();
                            DataNetHandler.getInstance().setBusyWasher(washer.getId(), user.getId(), minutes, true, success -> {
                                if (success) {
                                    Snackbar.make(v, R.string.success_busy, Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                    if (binding.remindCheckBox.isChecked()) {
                                        WasherRemind remind = new WasherRemind(washer.getId(), System.currentTimeMillis() + (long) minutes * 60 * 1000);
                                        mainViewModel.washerRemind.setValue(remind);
                                        DataLocalHandler.getInstance().saveWasherReminder();
                                    }
                                    AsyncUtils.waitAsync(1000, () -> ui(() -> {
                                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                        changeFragment(new FragmentWasher(washerId), true);
                                        binding.busyLayout.setVisibility(View.GONE);
                                    }));
                                } else {
                                    Snackbar.make(v, R.string.something_went_wrong, Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                }
                                enableButton.call(true);

                            });
                        });
                        String minuteString = getString(R.string.minute_short);
                        binding.slider.setLabelFormatter(value -> String.format("%s%s", ((int) value), minuteString));
                    } else {
                        binding.textBusy.setText(R.string.now_busy);
                        binding.textBusy.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_lite));

                        binding.timeBusy.setText(String.format("%s %s", getString(R.string.will_be_free), fmtOut.format(new Date(washer.getBusyTo()))));
                        binding.timeElapsed.setText(String.format("%s %s", getString(R.string.busy_since), fmtOut.format(new Date(washer.getLastUse()))));

                        binding.timeBusy.setVisibility(View.VISIBLE);
                        binding.timeElapsed.setVisibility(View.VISIBLE);

                        binding.buttonBusy.setText(R.string.cant_busy);
                        binding.buttonBusy.setEnabled(false);
                    }
                }
            });
        });

    }
}
