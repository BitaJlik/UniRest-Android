package com.unirest.ui.fragments.payment;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.Payment;
import com.unirest.databinding.ItemPaymentBinding;

import java.util.Date;


public class PaymentAdapter extends BaseAdapter<Payment> {
    private static final DateFormat format = SimpleDateFormat.getDateInstance();

    private static ICallback<Payment> paymentICallback;

    {
        addHolder(new HolderPair(PaymentHolder.class, R.layout.item_payment));
    }

    public static class PaymentHolder extends BaseHolder<Payment> {
        private final ItemPaymentBinding binding;

        public PaymentHolder(@NonNull View view) {
            super(view);
            binding = ItemPaymentBinding.bind(view);
        }

        @Override
        public void bind(Payment payment) {
            Context context = itemView.getContext();
            binding.balance.setText(String.format("%s: %s", context.getString(R.string.sum), payment.getBalance()));
            binding.date.setText(format.format(new Date(payment.getDate())));
            if (payment.isModerated()) {
                binding.moderate.setText(context.getString(R.string.moderated_yes));
                binding.moderate.setTextColor(ContextCompat.getColor(context, R.color.green));

                binding.valid.setVisibility(View.VISIBLE);
                if (payment.isValid()) {
                    binding.valid.setText(R.string.valid_payment_yes);
                } else {
                    binding.valid.setText(R.string.valid_payment_no);
                }

            } else {
                binding.moderate.setText(context.getString(R.string.moderated_no));
                binding.moderate.setTextColor(ContextCompat.getColor(context, R.color.red_lite));
            }

            if (paymentICallback != null) {
                binding.viewImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    paymentICallback.call(payment);
                    enableButton.call(true);
                });
            }
        }
    }

    public void setPaymentICallback(ICallback<Payment> paymentICallback) {
        PaymentAdapter.paymentICallback = paymentICallback;
    }
}
