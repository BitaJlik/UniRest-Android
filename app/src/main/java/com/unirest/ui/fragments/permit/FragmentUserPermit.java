package com.unirest.ui.fragments.permit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.unirest.R;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentProfileOpenBinding;
import com.unirest.ui.common.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentUserPermit extends BaseFragment<FragmentProfileOpenBinding> {

    public FragmentUserPermit() {
        super(FragmentProfileOpenBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.barcode.observe(getViewLifecycleOwner(), barcode -> {
            DataNetHandler.getInstance().getUserPermit(Long.parseLong(barcode), user -> {
                if (user != null) {
                    requireActivity().runOnUiThread(() -> {
                        DataNetHandler.getInstance().getUrlImageUser(user, url -> {
                            Picasso.get().load(url).into(binding.image);
                        });
                        binding.textName.setText(user.getName());
                        binding.course.setText(String.format("%s: %s", getString(R.string.course), user.getCourse()));
                        binding.expire.setText(String.format("%s: %s", getString(R.string.expire), SimpleDateFormat.getInstance().format(new Date(user.getExpire()))));
                    });
                }
            });
        });
    }
}
