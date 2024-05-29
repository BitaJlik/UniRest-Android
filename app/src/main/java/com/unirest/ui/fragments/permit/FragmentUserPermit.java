package com.unirest.ui.fragments.permit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;
import com.unirest.R;
import com.unirest.api.UniCode;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentProfileOpenBinding;
import com.unirest.ui.common.BaseFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentUserPermit extends BaseFragment<FragmentProfileOpenBinding> {
    private final DateFormat fmtOut = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
    private static boolean isDialogShowing = false;

    public FragmentUserPermit() {
        super(FragmentProfileOpenBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.barcode.observe(getViewLifecycleOwner(), barcode -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                if (user.getRole().getLevel() > 0) {
                    DataNetHandler.getInstance().getUserPermit(Long.parseLong(barcode.substring(1)), userPermit -> {
                        if (userPermit != null) {
                            requireActivity().runOnUiThread(() -> {
                                DataNetHandler.getInstance().getUrlImageUser(userPermit, url -> {
                                    Picasso.get().load(url).into(binding.image);
                                });
                                binding.textName.setText(userPermit.getName());
                                binding.textSurname.setText(userPermit.getLastName());
                                binding.course.setText(String.format("%s: %s", getString(R.string.course), userPermit.getCourse()));
                                binding.expire.setText(String.format("%s: %s", getString(R.string.expire), fmtOut.format(new Date(userPermit.getExpire()))));
                                binding.room.setText(String.format("%s: %s", getString(R.string.room), userPermit.getRoom()));
                                binding.barcode.setText(String.format("%s%s", UniCode.PREFIX_USER, fixNumber(userPermit.getId(), 7)));
                            });
                        }
                    });
                } else {
                    if (!isDialogShowing) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle(getString(R.string.not_permission));
                        builder.setCancelable(false);
                        builder.setMessage(getString(R.string.dialog_permit_barcode_view_permission))
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                    isDialogShowing = false;
                                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                });
                        isDialogShowing = true;
                        builder.show();
                    }
                }
            });
        });
    }


    public String fixNumber(Long number, int desiredLength) {
        String numberStr = String.valueOf(number);
        int zerosToAdd = desiredLength - numberStr.length();
        if (zerosToAdd <= 0) {
            return numberStr;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < zerosToAdd; i++) {
            result.append("0");
        }
        result.append(numberStr);
        return result.toString();
    }
}
