package com.unirest.ui.fragments.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unirest.R;
import com.unirest.api.IBackPressed;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataLocalHandler;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentLoginBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.profile.FragmentProfile;
import com.unirest.ui.fragments.register.FragmentRegister;
import com.unirest.utils.AsyncUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FragmentLogin extends BaseFragment<FragmentLoginBinding> implements IBackPressed {
    public FragmentLogin() {
        super(FragmentLoginBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.registerLayout.setOnClickListener(v -> {
            changeFragment(new FragmentRegister(), true);
        });

        binding.login.setOnClickListener((OnClickCallback) (v, enableButton) -> {
            getActivityMain().hideKeyboard();
            binding.layoutInputs.setVisibility(binding.layoutInputs.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            binding.layoutProgress.setVisibility(binding.layoutInputs.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

            AsyncUtils.async(() -> {
                int dotCount = 0;
                while (isVisible() && binding.layoutProgress.getVisibility() == View.VISIBLE) {
                    AsyncUtils.wait(400);
                    ui(() -> binding.textProgress.append("."));

                    if (dotCount++ >= 3) {
                        ui(() -> binding.textProgress.setText(R.string.logging));
                        dotCount = 0;
                    }
                }
            });

            AsyncUtils.async(() -> {
                String email = binding.layoutEmail.getEditText().getText().toString();
                String password = binding.layoutPassword.getEditText().getText().toString();
                DataNetHandler.getInstance().login(email, encryptSHA256(password), token -> {
                    ui(() -> {
                        mainViewModel.token.setValue(token);
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

        binding.loginByGoogle.setOnClickListener((OnClickCallback) (v, enableButton) -> {
            AsyncUtils.waitAsync(2000, () -> {
                ui(() -> {
                    enableButton.call(true);
                });
            });
        });

        binding.loginByFacebook.setOnClickListener((OnClickCallback) (v, enableButton) -> {
            AsyncUtils.waitAsync(2000, () -> {
                ui(() -> enableButton.call(true));
            });
        });

    }

    @Override
    public boolean onBackPressed() {
        return true;
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
