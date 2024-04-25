package com.unirest.ui.fragments.register;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataLocalHandler;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentRegisterBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.profile.FragmentProfile;
import com.unirest.utils.AsyncUtils;

import java.lang.ref.Reference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FragmentRegister extends BaseFragment<FragmentRegisterBinding> {
    private final HashMap<Object, Boolean> validMap = new HashMap<>();

    public FragmentRegister() {
        super(FragmentRegisterBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText editText = binding.layoutEmail.getEditText();
        assert editText != null;
        Editable editable = editText.getText();

        editText.setOnFocusChangeListener((view1, hasFocus) -> {
            if (!hasFocus) {
                if (!editable.toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches()) {
                    DataNetHandler.getInstance().checkEmail(editable.toString(), isEmailFree -> {
                        if (!isEmailFree) {
                            binding.layoutEmail.setError(getString(R.string.email_exists));
                        }
                    });
                } else {
                    binding.layoutEmail.setError(getString(R.string.email_not_valid));
                }
            } else {
                binding.layoutEmail.setError(null);
            }
        });

        EditText editTextPass = binding.layoutPassword.getEditText();
        editTextPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatePasswords(binding.layoutPassword, binding.layoutRepeatPassword);
            }
        });
        EditText editTextRepeatPass = binding.layoutRepeatPassword.getEditText();
        editTextRepeatPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatePasswords(binding.layoutPassword, binding.layoutRepeatPassword);
            }
        });

        addSafeButtonCheck(binding.register, binding.layoutEmail);
        addSafeButtonCheck(binding.register, binding.layoutPassword);
        addSafeButtonCheck(binding.register, binding.layoutRepeatPassword);

        binding.register.setOnClickListener((OnClickCallback) (v, enableButton) -> {
            getActivityMain().hideKeyboard();
            if (!validatePasswords(binding.layoutPassword, binding.layoutRepeatPassword)) {
                enableButton.call(true);
                return;
            }

            binding.layoutInputs.setVisibility(View.GONE);
            binding.layoutProgress.setVisibility(View.VISIBLE);

            AsyncUtils.async(() -> {
                int dotCount = 0;
                while (isVisible() && binding.layoutProgress.getVisibility() == View.VISIBLE) {
                    AsyncUtils.wait(400);
                    ui(() -> binding.textProgress.append("."));

                    if (dotCount++ >= 3) {
                        ui(() -> binding.textProgress.setText(R.string.registering));
                        dotCount = 0;
                    }
                }
            });

            AsyncUtils.async(() -> {
                String email = binding.layoutEmail.getEditText().getText().toString();
                String password = binding.layoutPassword.getEditText().getText().toString();
                DataNetHandler.getInstance().register(email, encryptSHA256(password), token -> {
                    ui(() -> {
                        mainViewModel.token.setValue(token);
                        Snackbar.make(v, R.string.success_registration_going_to_profile, Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
                        DataLocalHandler.getInstance().saveToken();
                        AsyncUtils.waitAsync(1500, () -> {
                            ui(() -> {
                                enableButton.call(true);
                            });
                            changeFragment(new FragmentProfile());
                        });
                    });
                }, error -> {
                    ui(() -> {
                        enableButton.call(true);
                        binding.layoutInputs.setVisibility(View.VISIBLE);
                        binding.layoutProgress.setVisibility(View.GONE);
                    });
                });
            });
        });
    }

    public boolean validatePasswords(TextInputLayout passwordInputLayout, TextInputLayout confirmPasswordInputLayout) {
        String password = Objects.requireNonNull(passwordInputLayout.getEditText()).getText().toString().trim();
        String confirmPassword = Objects.requireNonNull(confirmPasswordInputLayout.getEditText()).getText().toString().trim();

        if (TextUtils.isEmpty(password) && password.length() >= 6) {
            passwordInputLayout.setError(getString(R.string.password_must_contains));
            return false;
        } else if (!confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError(getString(R.string.password_not_equals));
            return false;
        } else {
            passwordInputLayout.setError(null);
            confirmPasswordInputLayout.setError(null);
            return true;
        }
    }

    public void addSafeButtonCheck(MaterialButton button, TextInputLayout textInputLayout) {
        validMap.put(textInputLayout, false);

        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validMap.put(textInputLayout, !editable.toString().isEmpty());
                checkValid(button);
            }
        });

    }

    private void checkValid(MaterialButton button) {
        button.setEnabled(false);
        for (Boolean value : validMap.values()) {
            if (!value) {
                return;
            }
        }
        button.setEnabled(true);
    }

    private String encryptSHA256(String str) {
        String ps;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            ps = transformHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            Log.e("Encrypt SHA256", e.getMessage());
            return "";
        }
        System.out.println(ps);
        return ps;
    }

    private String transformHex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp;
        for (byte bt : bts) {
            tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }


}
