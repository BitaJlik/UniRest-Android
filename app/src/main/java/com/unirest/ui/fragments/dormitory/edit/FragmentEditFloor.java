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
import com.unirest.data.models.Floor;
import com.unirest.databinding.FragmentEditFloorBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.utils.AsyncUtils;

public class FragmentEditFloor extends BaseFragment<FragmentEditFloorBinding> {
    public FragmentEditFloor() {
        super(FragmentEditFloorBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                mainViewModel.selectedFloor.observe(getViewLifecycleOwner(), floor -> {
                    binding.layoutName.getEditText().setText(floor.getShortName());
                    if (floor.getFloorSide() != null) {
                        View radioButton = binding.radioGroup.getChildAt(floor.getFloorSide().ordinal());
                        if (radioButton instanceof MaterialRadioButton) {
                            ((MaterialRadioButton) radioButton).setChecked(true);
                        }
                    }
                    binding.editCookers.setOnClickListener(v -> {
                        changeFragment(new FragmentEditCookers(), true);
                    });

                    binding.editWashers.setOnClickListener(v -> {
                        changeFragment(new FragmentEditWashers(), true);
                    });
                    binding.save.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        floor.setShortName(binding.layoutName.getEditText().getText().toString());
                        for (int i = 0; i < binding.radioGroup.getChildCount(); i++) {
                            View button = binding.radioGroup.getChildAt(i);
                            if (button instanceof MaterialRadioButton) {
                                if (((MaterialRadioButton) button).isChecked()) {
                                    floor.setFloorSide(Floor.FloorSide.values()[i]);
                                    break;
                                }
                            }
                        }
                        DataNetHandler.getInstance().adminUpdateFloor(floor, success -> {
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
    }
}
