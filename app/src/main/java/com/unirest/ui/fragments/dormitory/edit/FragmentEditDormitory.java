package com.unirest.ui.fragments.dormitory.edit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.CookerType;
import com.unirest.databinding.FragmentEditDormitoryBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.utils.AsyncUtils;

public class FragmentEditDormitory extends BaseFragment<FragmentEditDormitoryBinding> {

    public FragmentEditDormitory() {
        super(FragmentEditDormitoryBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                DataNetHandler.getInstance().getDormitoryInfo(user.getDormitoryId(), dormitory -> {
                    binding.layoutName.getEditText().setText(dormitory.getName());
                    binding.layoutAddress.getEditText().setText(dormitory.getAddress());
                    binding.remindCheckBox.setChecked(!dormitory.isHasElevator());
                    View radioButton = binding.radioGroup.getChildAt(dormitory.getCookerType().ordinal());
                    if (radioButton instanceof MaterialRadioButton) {
                        ((MaterialRadioButton) radioButton).setChecked(true);
                    }

                    binding.save.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        dormitory.setName(binding.layoutName.getEditText().getText().toString());
                        dormitory.setAddress(binding.layoutAddress.getEditText().getText().toString());
                        dormitory.setHasElevator(!binding.remindCheckBox.isChecked());
                        for (int i = 0; i < binding.radioGroup.getChildCount(); i++) {
                            View button = binding.radioGroup.getChildAt(i);
                            if (button instanceof MaterialRadioButton) {
                                if (((MaterialRadioButton) button).isChecked()) {
                                    dormitory.setCookerType(CookerType.values()[i]);
                                    break;
                                }
                            }
                        }

                        DataNetHandler.getInstance().adminUpdateDormitory(dormitory, success -> {
                            if (success) {
                                Snackbar.make(view, R.string.success_upload, Snackbar.LENGTH_SHORT).show();
                            }
                            AsyncUtils.waitAsync(500, () -> {
                                ui(() -> {
                                    getActivityMain().getOnBackPressedDispatcher().onBackPressed();
                                });
                            });
                            enableButton.call(true);
                        });
                    });
                });
            });
        });
        // TODO: 06.06.2024 Create all editors for admins
    }
}
