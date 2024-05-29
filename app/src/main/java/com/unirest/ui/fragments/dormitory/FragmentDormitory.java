package com.unirest.ui.fragments.dormitory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentDormitoryBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.floors.FragmentFloors;
import com.unirest.ui.fragments.moderate.FragmentModerate;
import com.unirest.ui.fragments.search.FragmentSearch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentDormitory extends BaseFragment<FragmentDormitoryBinding> {

    public FragmentDormitory() {
        super(FragmentDormitoryBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                if (user == null) return;
                DataNetHandler.getInstance().getDormitoryInfo(user.getDormitoryId(), dormitory -> {
                    if (dormitory == null) return;

                    binding.shimmer.hideShimmer();
                    binding.main.setVisibility(View.VISIBLE);
                    binding.shimmer.setVisibility(View.GONE);

                    binding.textDormitory.setText(dormitory.getName());
                    binding.textAddress.setText(dormitory.getAddress());
                    if (dormitory.getCookerType() != null) {
                        String textCooker;
                        switch (dormitory.getCookerType()) {
                            case GAS:
                                textCooker = getString(R.string.cooker_gas);
                                break;
                            case ELECTRICAL:
                                textCooker = getString(R.string.cooker_electrical);
                                break;

                            case HYBRID:
                                textCooker = getString(R.string.cooker_hybrid);
                                break;
                            case NONE:
                            default:
                                textCooker = getString(R.string.cooker_none);
                                break;
                        }
                        binding.textDormitoryCookerType.setText(String.format("%s %s", getString(R.string.cookers), textCooker));
                        String hasElevatorText;
                        if (dormitory.isHasElevator()) {
                            hasElevatorText = getString(R.string.elevator_yes);
                        } else {
                            hasElevatorText = getString(R.string.elevator_no);
                        }
                        binding.textDormitoryHasElevator.setText(hasElevatorText);

                        binding.textDormitoryTotalFloors.setText(String.format("%s: %s", getString(R.string.floors), dormitory.getFloors().size()));
                        binding.textDormitoryTotalBeds.setText(String.format("%s %s", getString(R.string.total_beds), dormitory.getTotalBeds()));
                        binding.textDormitoryTakenBeds.setText(String.format("%s %s", getString(R.string.total_taken_beds), dormitory.getTotalTakenBeds()));
                        binding.textDormitoryFreeBeds.setText(String.format("%s %s", getString(R.string.total_free_beds), dormitory.getTotalFreeBeds()));
                    }

                    if (dormitory.getCommandantInfo() != null) {
                        binding.textCommandantName.setText(String.format("%s %s", dormitory.getCommandantInfo().getName(), dormitory.getCommandantInfo().getLastName()));
                        binding.textCommandantPhone.setText(String.format("%s: +%s", getString(R.string.phone), PhoneNumberUtils.formatNumber(dormitory.getCommandantInfo().getPhone(), Locale.getDefault().getCountry())));
                        DateFormat fmtOut = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                        binding.textCommandantLastTime.setText(String.format("%s: %s", getString(R.string.last_active), fmtOut.format(new Date(dormitory.getCommandantInfo().getLastActive()))));
                    }

                    binding.floors.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        changeFragment(new FragmentFloors(),true);
                        enableButton.call(true);
                    });

                    binding.callCommandant.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + dormitory.getCommandantInfo().getPhone()));
                        startActivity(intent);
                        enableButton.call(true);
                    });

                    if (user.getRole().getLevel() > 1) {
                        binding.moderate.setVisibility(View.VISIBLE);
                        binding.search.setVisibility(View.VISIBLE);
                        binding.moderate.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            changeFragment(new FragmentModerate(), true);
                            enableButton.call(true);
                        });

                        binding.search.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            changeFragment(new FragmentSearch(),true);
                            enableButton.call(true);
                        });
                    } else {
                        binding.moderate.setVisibility(View.GONE);
                        binding.search.setVisibility(View.GONE);
                    }
                });
            });
        });


    }
}
