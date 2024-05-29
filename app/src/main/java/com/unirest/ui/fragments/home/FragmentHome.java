package com.unirest.ui.fragments.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;
import com.unirest.R;
import com.unirest.api.UniCode;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentHomeBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.login.FragmentLogin;
import com.unirest.utils.AsyncUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentHome extends BaseFragment<FragmentHomeBinding> {
    private final DateFormat fmtOut = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
    private boolean barcodeValid;

    public FragmentHome() {
        super(FragmentHomeBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            if (token != null) {
                getActivityMain().updateUser();
                mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        DataNetHandler.getInstance().getUrlImageUser(user, url -> {
                            Picasso.get().load(url).into(binding.image);
                        });

                        binding.name.setText(user.getName());
                        binding.lastName.setText(user.getLastName());
                        binding.course.setText(String.format("%s %s", getString(R.string.course), user.getCourse()));
                        binding.expire.setText(String.format("%s %s", getString(R.string.expire), fmtOut.format(new Date(user.getExpire()))));

                        binding.main.setVisibility(View.VISIBLE);
                        binding.shimmer.setVisibility(View.GONE);
                        binding.shimmer.hideShimmer();

                        String barcodeTip = UniCode.PREFIX_USER + fixNumber(user.getId(), 7);

                        binding.barcode.setText(barcodeTip);

                        binding.imageBarcode.post(() -> {
                            binding.imageBarcode.setVisibility(View.VISIBLE);
                            binding.progress.setVisibility(View.GONE);
                            int width = binding.imageBarcode.getWidth();
                            int height = binding.imageBarcode.getHeight();
                            if (width > 0 && height > 0) {
                                try {
                                    Bitmap image = createImage(barcodeTip, width, height);
                                    binding.imageBarcode.setImageBitmap(image);
                                    barcodeValid = true;
                                } catch (WriterException e) {
                                    barcodeValid = false;
                                }
                            }
                        });
                    }
                });
            }
        });

        AsyncUtils.waitAsync(5000, () -> {
            if (!barcodeValid) {
                ui(() -> {
                    binding.progress.setVisibility(View.GONE);
                    binding.imageError.setVisibility(View.VISIBLE);
                });
            }
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

    public Bitmap createImage(String message, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_128, width, height);
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (bitMatrix.get(j, i)) {
                    pixels[i * width + j] = 0xff000000;
                } else {
                    pixels[i * width + j] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
