package com.unirest.ui.fragments.payment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentPaymentBinding;
import com.unirest.ui.common.BaseFragment;

public class FragmentPayment extends BaseFragment<FragmentPaymentBinding> {
    private final PaymentAdapter adapter = new PaymentAdapter();

    public FragmentPayment() {
        super(FragmentPaymentBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                DataNetHandler.getInstance().verifyAuth(token, isValid -> {
                    if (!isVisible() || user == null) return;

                    binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.itemsRV.setAdapter(adapter);

                    if (isValid) {
                        DataNetHandler.getInstance().getPayments(user.getId(), payments -> {
                            if (payments != null && !payments.isEmpty()) {
                                adapter.setItems(payments);

                                adapter.setPaymentICallback(payment -> {
                                    mainViewModel.payment.setValue(payment);
                                    changeFragment(new FragmentPaymentView(), true);
                                });
                            }
                        });

                        binding.add.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            changeFragment(new FragmentPaymentEdit(), true);
                            enableButton.call(true);
                        });
                    }
                });
            });
        });
    }
}
