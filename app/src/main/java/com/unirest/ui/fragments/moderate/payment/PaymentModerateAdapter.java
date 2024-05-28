package com.unirest.ui.fragments.moderate.payment;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.Payment;
import com.unirest.databinding.ItemModeratePaymentBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentModerateAdapter extends BaseAdapter<Payment> {
    private static DateFormat format;
    private static ICallback<Payment> paymentICallback;

    {
        addHolder(new HolderPair(PaymentModerateHolder.class, R.layout.item_moderate_payment));
        format = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
    }

    public void setPaymentICallback(ICallback<Payment> paymentICallback) {
        PaymentModerateAdapter.paymentICallback = paymentICallback;
    }

    public static class PaymentModerateHolder extends BaseHolder<Payment> {
        private final ItemModeratePaymentBinding binding;

        public PaymentModerateHolder(@NonNull View view) {
            super(view);
            binding = ItemModeratePaymentBinding.bind(view);
        }

        @Override
        public void bind(Payment payment) {
            Context context = itemView.getContext();
            binding.textBalance.setText(String.format("%s: %s", context.getString(R.string.sum), payment.getBalance()));
            binding.textDate.setText(String.format("%s: %s", context.getString(R.string.date), format.format(new Date(payment.getDate()))));
            if (payment.isModerated()) {
                binding.textModerated.setText(context.getString(R.string.moderated_yes));
                binding.textModerated.setTextColor(ContextCompat.getColor(context, R.color.green));

                binding.textValid.setVisibility(View.VISIBLE);
                if (payment.isValid()) {
                    binding.textValid.setText(R.string.valid_payment_yes);
                } else {
                    binding.textValid.setText(R.string.valid_payment_no);
                }
            } else {
                binding.textValid.setVisibility(View.GONE);
                binding.textModerated.setText(context.getString(R.string.moderated_no));
                binding.textModerated.setTextColor(ContextCompat.getColor(context, R.color.red_lite));
            }

            if (paymentICallback != null) {
                binding.getRoot().setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    paymentICallback.call(payment);
                    enableButton.call(true);
                });
            }
        }
    }

}
