package com.unirest.ui.fragments.moderate.payment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unirest.R;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Payment;
import com.unirest.databinding.FragmentModeratePaymentBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.payment.FragmentPaymentView;

import java.util.List;

public class FragmentModeratePayment extends BaseFragment<FragmentModeratePaymentBinding> {
    private final PaymentModerateAdapter adapter = new PaymentModerateAdapter();
    private int fabCounter = 0;

    private int sortType = 0;
    private int groupType = 0;

    public FragmentModeratePayment() {
        super(FragmentModeratePaymentBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);

        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                DataNetHandler.getInstance().verifyAuth(token, isValid -> {
                    if (isValid) {
                        DataNetHandler.getInstance().getDormitoryPayments(user.getDormitoryId(), payments -> {
                            handleSort(payments);
                            adapter.setItems(payments);

                            adapter.setPaymentICallback(payment -> {
                                mainViewModel.payment.setValue(payment);
                                FragmentPaymentView fragment = new FragmentPaymentView(returnValue -> {
                                    if (returnValue) {

                                    }
                                });
                                changeFragment(fragment, true);
                            });

                            if (isVisible()) {
                                binding.fab.setOnClickListener(viewFab -> {
                                    fabCounter++;
                                    if (fabCounter == 1) {
                                        binding.fab.setText(R.string.text_fab_options);
                                        binding.fab.extend();
                                    }
                                    if (fabCounter > 1) {
                                        fabCounter = 0;
                                        PopupMenu popupMenu = new PopupMenu(requireContext(), viewFab);
                                        popupMenu.inflate(R.menu.moderate_fab_payment);
                                        popupMenu.setOnMenuItemClickListener(item -> {
                                            int id = item.getItemId();
                                            if (id == R.id.group_date) {
                                                groupType = 0;
                                            }
                                            if (id == R.id.group_sum) {
                                                groupType = 1;
                                            }
                                            if (id == R.id.group_moderate) {
                                                groupType = 2;
                                            }
                                            if (id == R.id.sort_acceding) {
                                                sortType = 0;
                                            }
                                            if (id == R.id.sort_descending) {
                                                sortType = 1;
                                            }
                                            handleSort(payments);
                                            adapter.setItems(payments);
                                            binding.itemsRV.getLayoutManager().scrollToPosition( 0);
                                            return true;
                                        });
                                        popupMenu.show();
                                        binding.fab.shrink();
                                    }
                                });
                            }
                        });
                    }
                });
            });
        });
    }

    public void handleSort(List<Payment> payments) {
        if (sortType == 0) {
            switch (groupType) {
                case 0:
                    payments.sort(Payment.Comparators::compareDate);
                    break;
                case 1:
                    payments.sort(Payment.Comparators::compareSum);
                    break;
                case 2:
                    payments.sort(Payment.Comparators::compareIfModerated);
                    break;
            }
        } else {
            switch (groupType) {
                case 0:
                    payments.sort(Payment.ReverseComparators::compareDate);
                    break;
                case 1:
                    payments.sort(Payment.ReverseComparators::compareSum);
                    break;
                case 2:
                    payments.sort(Payment.ReverseComparators::compareIfModerated);
                    break;
            }
        }
    }
}
