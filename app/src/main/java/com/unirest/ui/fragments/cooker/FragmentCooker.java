package com.unirest.ui.fragments.cooker;

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
import com.unirest.data.models.CookerRemind;
import com.unirest.databinding.FragmentCookerBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.utils.AsyncUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentCooker extends BaseFragment<FragmentCookerBinding> {
    private long cookerId = -1;

    public FragmentCooker() {
        super(FragmentCookerBinding.class);
    }

    public FragmentCooker(long cookerId) {
        super(FragmentCookerBinding.class);
        this.cookerId = cookerId;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (cookerId == -1) {
            String barcode = mainViewModel.barcode.getValue();
            if (barcode != null) {
                UniCode uniCode = new UniCode(barcode);
                if (uniCode.isCodeCooker()) {
                    cookerId = uniCode.getValue();
                }
            }
        }

        DataNetHandler.getInstance().getCooker(cookerId, cooker -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                if (user == null) return;

                if (cooker != null) {
                    DateFormat fmtOut = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                    if (cooker.getUser().equals(user.getId())) {
                        binding.free.setVisibility(View.VISIBLE);
                        binding.free.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle(String.format("%s?", getString(R.string.free)));
                            builder.setMessage(String.format("%s?", getString(R.string.free_cooker_description)))
                                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                        DataNetHandler.getInstance().setBusyCooker(cooker.getId(), user.getId(), 0, false, callback -> {
                                            enableButton.call(true);
                                            binding.free.setVisibility(View.GONE);
                                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                            changeFragment(new FragmentCooker(cookerId), true);
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

                    if (!cooker.isBusy()) {
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
                            DataNetHandler.getInstance().setBusyCooker(cooker.getId(), user.getId(), minutes, true, success -> {
                                if (success) {
                                    Snackbar.make(v, R.string.success_busy, Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                    if (binding.remindCheckBox.isChecked()) {
                                        CookerRemind remind = new CookerRemind(cooker.getId(), System.currentTimeMillis() + (long) minutes * 60 * 1000);
                                        mainViewModel.cookerRemind.setValue(remind);
                                        DataLocalHandler.getInstance().saveCookerReminder();
                                    }
                                    AsyncUtils.waitAsync(1000, () -> ui(() -> {
                                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                        changeFragment(new FragmentCooker(cookerId), true);
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

                        binding.timeBusy.setText(String.format("%s %s", getString(R.string.will_be_free), fmtOut.format(new Date(cooker.getBusyTo()))));
                        binding.timeElapsed.setText(String.format("%s %s", getString(R.string.busy_since), fmtOut.format(new Date(cooker.getLastUse()))));

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
