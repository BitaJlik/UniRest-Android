package com.unirest.ui.fragments.scanner;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.BarcodeFormat;
import com.unirest.api.ICallback;
import com.unirest.api.UniCode;
import com.unirest.databinding.FragmentScannerBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.permit.FragmentUserPermit;

import java.util.Collections;

public class FragmentScanner extends BaseFragment<FragmentScannerBinding> {
    private CodeScanner codeScanner;
    private ICallback<String> callbackCode;

    public FragmentScanner() {
        super(FragmentScannerBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        codeScanner = new CodeScanner(requireContext(), binding.scannerView);

        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(Collections.singletonList(BarcodeFormat.CODE_128));
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);

        codeScanner.setDecodeCallback(result -> ui(() -> {
            String code = result.getText();
            for (String prefix : UniCode.PREFIXES) {
                if (code.startsWith(prefix)) {
                    codeScanner.stopPreview();
                    String stringCode = result.getText().substring(1);
                    mainViewModel.barcode.setValue(stringCode);
                    getActivityMain().onBackPressed();
                    if (callbackCode != null) {
                        callbackCode.call(code);
                    } else {
                        UniCode uniCode = new UniCode(code);
                        if (uniCode.isValid() && uniCode.isCodeUser()) {
                            requireActivity().onBackPressed();
                            changeFragment(new FragmentUserPermit(), true);
                            // TODO: 10.04.2024 Show permit fragment
                        } else if (uniCode.isValid() && uniCode.isCodeCooker()) {
                            // TODO: 10.04.2024 Show cooker fragment
                        } else if (uniCode.isValid() && uniCode.isCodeWashing()) {
                            // TODO: 10.04.2024 Show washer fragment
                        }
                    }
                }
            }
        }));

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivityMain().handleNavBar(false);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (codeScanner != null) {
            codeScanner.startPreview();
        }
    }

    @Override
    public void onPause() {
        if (codeScanner != null) {
            codeScanner.releaseResources();
        }
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getActivityMain().handleNavBar(true);
        super.onPause();
    }

    public void setCallbackCode(ICallback<String> callbackCode) {
        this.callbackCode = callbackCode;
    }
}
