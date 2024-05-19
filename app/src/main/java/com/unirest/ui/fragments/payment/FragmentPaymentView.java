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

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentPaymentViewBinding;
import com.unirest.ui.common.BaseFragment;

import java.util.Date;

public class FragmentPaymentView extends BaseFragment<FragmentPaymentViewBinding> {
    private static final DateFormat f = SimpleDateFormat.getDateInstance(android.icu.text.DateFormat.SHORT);

    public FragmentPaymentView() {
        super(FragmentPaymentViewBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.payment.observe(getViewLifecycleOwner(), payment -> {
            if (payment != null) {
                DataNetHandler.getInstance().getUrlPayment(payment, url -> {
                    if (url == null) {
                        binding.save.setEnabled(false);
                        Snackbar.make(requireView(), getString(R.string.not_found_image), Snackbar.LENGTH_LONG).show();
                        return;
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

                    binding.save.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, payment.getCheckId() + ".png");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setTitle(getString(R.string.receipt) + "_" + f.format(new Date(payment.getDate())));
                        DownloadManager manager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                        enableButton.call(true);
                    });
                });
            }
        });
    }
}
