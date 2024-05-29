package com.unirest.ui.fragments.scanner;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.BarcodeFormat;
import com.unirest.api.ICallback;
import com.unirest.api.UniCode;
import com.unirest.databinding.FragmentScannerBinding;
import com.unirest.ui.common.BaseFragment;
import com.unirest.ui.fragments.cooker.FragmentCooker;
import com.unirest.ui.fragments.permit.FragmentUserPermit;
import com.unirest.ui.fragments.washer.FragmentWasher;

import java.util.Collections;

public class FragmentScanner extends BaseFragment<FragmentScannerBinding> {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private CodeScanner codeScanner;
    private ICallback<String> callbackCode;

    public FragmentScanner() {
        super(FragmentScannerBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (checkPermission()) {
            initCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            if (checkPermission()) {
                initCamera();
            }
        }
    }

    private void initCamera() {
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
                    mainViewModel.barcode.setValue(code);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    if (callbackCode != null) {
                        callbackCode.call(code);
                    } else {
                        UniCode uniCode = new UniCode(code);
                        if (uniCode.isValid() && uniCode.isCodeUser()) {
                            changeFragment(new FragmentUserPermit(), true);
                        } else if (uniCode.isValid() && uniCode.isCodeCooker()) {
                            changeFragment(new FragmentCooker(), true);
                        } else if (uniCode.isValid() && uniCode.isCodeWashing()) {
                            changeFragment(new FragmentWasher(), true);
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

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
