package com.unirest.ui.fragments.payment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.Payment;
import com.unirest.databinding.FragmentPaymentEditBinding;
import com.unirest.ui.common.BaseFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FragmentPaymentEdit extends BaseFragment<FragmentPaymentEditBinding> {
    private final DateFormat format = SimpleDateFormat.getDateInstance();
    private ActivityResultLauncher<String> imagePickerLauncher;
    private MultipartBody.Part body;

    public FragmentPaymentEdit() {
        super(FragmentPaymentEditBinding.class);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), result);
                            File f = File.createTempFile("imageTemp", "tempPayment" + "." + getFileExtension(result));
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                            byte[] bitmapData = bos.toByteArray();
                            try {
                                FileOutputStream fos = new FileOutputStream(f);
                                try {
                                    fos.write(bitmapData);
                                    fos.flush();
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
                            body = MultipartBody.Part.createFormData("image", f.getName(), reqFile);
                            ui(() -> binding.selectedImage.setText(result.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainViewModel.token.observe(getViewLifecycleOwner(), token -> {
            mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
                DataNetHandler.getInstance().verifyAuth(token, isValid -> {
                    if (!isVisible()) return;

                    if (isValid) {
                        binding.selectImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                            imagePickerLauncher.launch("image/*");
                            enableButton.call(true);
                        });
                        Calendar calendar = Calendar.getInstance();

                        if (mainViewModel.creatingPayment.getValue() == null) {
                            mainViewModel.creatingPayment.setValue(new Payment());
                        }

                        mainViewModel.creatingPayment.observe(getViewLifecycleOwner(), payment -> {
                            binding.selectDate.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                                DatePickerDialog dpd = new DatePickerDialog(requireContext(), null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                                dpd.setOnDateSetListener((datePicker, i, i1, i2) -> {
                                    calendar.set(i, i1, i2);
                                    payment.setDate(calendar.getTimeInMillis());
                                    binding.layoutDate.getEditText().setText(format.format(new Date(payment.getDate())));
                                });
                                dpd.show();
                                enableButton.call(true);
                            });

                            binding.send.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                                if (body != null) {
                                    payment.setBalance(Double.parseDouble(binding.layoutBalance.getEditText().getText().toString()));
                                    payment.setDormitoryId(user.getDormitoryId());
                                    DataNetHandler.getInstance().uploadPayment(user, payment, uploadedId -> {
                                        if (uploadedId != null) {
                                            DataNetHandler.getInstance().uploadPaymentCheck(uploadedId, body, uploaded -> {
                                                if (uploaded) {
                                                    mainViewModel.creatingPayment.setValue(null);
                                                    Snackbar.make(view, getString(R.string.success_upload), Snackbar.LENGTH_LONG).show();
                                                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                                }
                                            });
                                        }
                                        enableButton.call(true);
                                    });
                                }
                            });
                        });
                    }
                });
            });
        });
    }

    public String getFileExtension(Uri uri) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(requireActivity().getContentResolver().getType(uri));
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = requireActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) return null;
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = requireActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        if (cursor == null) return null;

        cursor.moveToFirst();
        @SuppressLint("Range")
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
