package com.unirest.ui.fragments.paid;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Paid;
import com.unirest.databinding.FragmentPaidBinding;
import com.unirest.ui.common.BaseFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentPaid extends BaseFragment<FragmentPaidBinding> {
    private final DateFormat format = SimpleDateFormat.getDateInstance();

    public FragmentPaid() {
        super(FragmentPaidBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                DataNetHandler.getInstance().verifyAuth(token, isValid -> {
                    if (!isVisible()) return;

                    if (isValid) {
                        Calendar calendar = Calendar.getInstance();

                        Paid paid = new Paid();
                        binding.selectDate.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            DatePickerDialog dpd = new DatePickerDialog(requireContext(), null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                            dpd.setOnDateSetListener((datePicker, i, i1, i2) -> {
                                calendar.set(i, i1, i2);
                                paid.setDate(calendar.getTimeInMillis());
                                binding.layoutDate.getEditText().setText(format.format(new Date(paid.getDate())));
                            });
                            dpd.show();
                            enableButton.call(true);
                        });

                        binding.send.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            paid.setBalance(Double.parseDouble(binding.layoutBalance.getEditText().getText().toString()));
                            paid.setDormitoryId(user.getDormitoryId());
                            DataNetHandler.getInstance().sendPaid(paid, success -> {
                                if (success) {
                                    Snackbar.make(view, R.string.success_upload, Snackbar.LENGTH_LONG).show();
                                }
                                enableButton.call(true);
                            });
                        });
                    }
                });
            });
        });
    }
}
