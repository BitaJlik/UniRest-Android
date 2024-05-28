package com.unirest.ui.fragments.moderate;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentModerateBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.moderate.payment.FragmentModeratePayment;
import com.unirest.ui.fragments.moderate.request.FragmentModerateRequest;

public class FragmentModerate extends BaseFragment<FragmentModerateBinding> {

    public FragmentModerate() {
        super(FragmentModerateBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                DataNetHandler.getInstance().verifyAuth(token, isValid -> {
                    DataNetHandler.getInstance().getDormitoryInfo(user.getDormitoryId(), dormitory -> {
                        binding.text.setText(String.format("%s", dormitory.getName()));
                    });

                    if (isValid) {
                        binding.payments.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            changeFragment(new FragmentModeratePayment(), true);
                            enableButton.call(true);
                        });

                        binding.requests.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            changeFragment(new FragmentModerateRequest(), true);
                            enableButton.call(true);
                        });
                    }
                });
            });
        });


    }
}
