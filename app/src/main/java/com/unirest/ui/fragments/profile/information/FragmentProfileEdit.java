package com.unirest.ui.fragments.profile.information;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.data.models.User;
import com.unirest.databinding.FragmentProfileEditBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.utils.AsyncUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class FragmentProfileEdit extends BaseFragment<FragmentProfileEditBinding> {
    ActivityResultLauncher<String> imagePickerLauncher;
    private User user;

    public FragmentProfileEdit() {
        super(FragmentProfileEditBinding.class);
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
                            File f = File.createTempFile("imageTemp", user.getEmail() + "." + getFileExtension(result));
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
                            MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), reqFile);

                            DataNetHandler.getInstance().uploadUser(user.getId(), body, uploaded -> {
                                if (uploaded) {
                                    Snackbar.make(requireView(), getString(R.string.success_upload), Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                } else {
                                    Snackbar.make(requireView(), getString(R.string.fail_upload), Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivityMain().updateUser();
        mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                binding.layoutUsername.getEditText().setText(user.getUsername());
                binding.layoutName.getEditText().setText(user.getName());
                binding.layoutLastName.getEditText().setText(user.getLastName());
                binding.layoutSurname.getEditText().setText(user.getSurName());
                binding.layoutCourse.getEditText().setText(String.valueOf(user.getCourse()));
                binding.layoutPhone.getEditText().setText(user.getPhoneNumber());
                binding.layoutEmail.getEditText().setText(user.getEmail());

                binding.imageSelect.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    imagePickerLauncher.launch("image/*");
                });

                DataNetHandler.getInstance().getUrlImageUser(user, url -> {
                    Picasso.get().load(url).into(binding.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            if(!isVisible()) return;
                            binding.progress.setVisibility(View.GONE);
                            binding.image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            if(!isVisible()) return;
                            binding.progress.setVisibility(View.GONE);
                            binding.imageError.setVisibility(View.VISIBLE);
                        }
                    });
                });

                final EditText edt = binding.layoutPhone.getEditText();

                edt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().startsWith("380")) {
                            edt.setText("380");
                            Selection.setSelection(edt.getText(), edt.getText().length());
                        }
                    }
                });

                binding.layoutPassword.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() >= 6) {
                            binding.layoutPasswordRepeat.setVisibility(View.VISIBLE);
                            AsyncUtils.waitAsync(300, () -> {
                                ui(() -> {
                                    View lastChild = binding.scrollView.getChildAt(binding.scrollView.getChildCount() - 1);
                                    int bottom = lastChild.getBottom() + binding.scrollView.getPaddingBottom();
                                    int sy = binding.scrollView.getScrollY();
                                    int sh = binding.scrollView.getHeight();
                                    int delta = bottom - (sy + sh);

                                    binding.scrollView.smoothScrollBy(0, delta);
                                });
                            });
                        } else {
                            binding.layoutPasswordRepeat.setVisibility(View.GONE);
                        }
                    }
                });

                binding.save.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    User userNew = new User();
                    userNew.setUsername(binding.layoutUsername.getEditText().getText().toString());
                    userNew.setName(binding.layoutName.getEditText().getText().toString());
                    userNew.setLastName(binding.layoutLastName.getEditText().getText().toString());
                    userNew.setSurName(binding.layoutSurname.getEditText().getText().toString());
                    userNew.setCourse(Integer.parseInt(binding.layoutCourse.getEditText().getText().toString()));
                    userNew.setPhoneNumber(binding.layoutPhone.getEditText().getText().toString());
                    userNew.setEmail(binding.layoutEmail.getEditText().getText().toString());

                    DataNetHandler.getInstance().updateUser(user.getId(), userNew, callBack -> {
                        enableButton.call(true);
                    });
                });
            } else {
                binding.save.setEnabled(false);
            }
        });
    }

    public String getFileExtension(Uri uri) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(requireActivity().getContentResolver().getType(uri));

    }

}
