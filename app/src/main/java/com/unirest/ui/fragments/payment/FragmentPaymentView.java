package com.unirest.ui.fragments.payment;

import android.app.DownloadManager;
import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentPaymentViewBinding;
import com.unirest.ui.common.BaseFragment;

import java.util.Date;

public class FragmentPaymentView extends BaseFragment<FragmentPaymentViewBinding> {
    private static final DateFormat f = SimpleDateFormat.getDateInstance(android.icu.text.DateFormat.SHORT);
    private final ICallback<Boolean> callbackModerate;

    public FragmentPaymentView() {
        this(null);
    }

    public FragmentPaymentView(ICallback<Boolean> callbackModerate) {
        super(FragmentPaymentViewBinding.class);
        this.callbackModerate = callbackModerate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.payment.observe(getViewLifecycleOwner(), payment -> {
            if (payment != null) {
                DataNetHandler.getInstance().getUrlPayment(payment, url -> {
                    if (url == null) {
                        binding.save.setEnabled(false);
                        Snackbar.make(requireView(), getString(R.string.not_found_image), Snackbar.LENGTH_LONG).show();
                    }

                    Picasso.get().load(url).into(binding.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            binding.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                    if (callbackModerate != null) {
                        binding.validNo.setVisibility(View.VISIBLE);
                        binding.validYes.setVisibility(View.VISIBLE);
                        binding.save.setVisibility(View.GONE);

                        binding.validNo.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle(String.format("%s?", getString(R.string.confirm)));
                            String message = getString(R.string.dialog_valid_payment);
                            if (payment.isModerated()) {
                                message += "\n\n" + getString(R.string.dialog_re_moderate);
                            }
                            builder.setMessage(String.format("%s?", message))
                                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                        DataNetHandler.getInstance().moderatePayment(payment, false, success -> {
                                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                            enableButton.call(true);
                                        });
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                        enableButton.call(true);
                                        dialog.dismiss();
                                    });
                            builder.show();

                        });

                        binding.validYes.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle(String.format("%s?", getString(R.string.confirm)));
                            builder.setMessage(String.format("%s?", getString(R.string.dialog_valid_payment)))
                                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                        DataNetHandler.getInstance().moderatePayment(payment, true, success -> {
                                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                            enableButton.call(true);
                                        });
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                        enableButton.call(true);
                                        dialog.dismiss();
                                    });
                            builder.show();
                        });

                        if(payment.isModerated()){
                            if (payment.isValid()) {
                                binding.validYes.setEnabled(false);
                            } else {
                                binding.validNo.setEnabled(false);
                            }
                        }
                    } else {
                        binding.save.setVisibility(View.VISIBLE);
                        binding.validNo.setVisibility(View.GONE);
                        binding.validYes.setVisibility(View.GONE);

                        binding.save.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, payment.getCheckId() + ".png");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setTitle(getString(R.string.receipt) + "_" + f.format(new Date(payment.getDate())));
                            DownloadManager manager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);
                            enableButton.call(true);
                        });
                    }
                });
            }
        });
    }
}
