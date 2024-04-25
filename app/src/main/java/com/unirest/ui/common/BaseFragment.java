package com.unirest.ui.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.transition.MaterialSharedAxis;
import com.unirest.ActivityMain;
import com.unirest.data.viewmodels.ErrorViewModel;
import com.unirest.data.viewmodels.MainViewModel;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("All")
public abstract class BaseFragment<Binding extends ViewBinding> extends Fragment {
    private final Class<Binding> bindingClass;
    protected Binding binding;
    protected MainViewModel mainViewModel;
    protected ErrorViewModel errorViewModel;

    private boolean arrowBackEnabled = true;

    public BaseFragment(Class<Binding> bindingClass) {
        this.bindingClass = bindingClass;

        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, true));
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, false));
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, false));
    }

    @NonNull
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainViewModel = initViewModel(MainViewModel.class);
        errorViewModel = initViewModel(ErrorViewModel.class);
        try {
            binding = (Binding) bindingClass.getDeclaredMethod("inflate", LayoutInflater.class).invoke(null, inflater);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        if (!isArrowBackEnabled()) {
            if (requireActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }

        if (binding != null) {
            return binding.getRoot();
        }
        throw new RuntimeException("Binding are not loaded");
    }

    @NonNull
    public final ActivityMain getActivityMain() {
        return ((ActivityMain) requireActivity());
    }

    public <T extends ViewModel> T initViewModel(Class<T> tClass) {
        return new ViewModelProvider(requireActivity()).get(tClass);
    }

    @Override
    public abstract void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

    public void changeFragment(Fragment fragment) {
        getActivityMain().changeFragment(fragment);
    }

    public void changeFragment(Fragment fragment, boolean addBackStack) {
        getActivityMain().changeFragment(fragment, addBackStack);
    }

    public void ui(Runnable uiRunnable) {
        if (!isVisible()) return;
        requireActivity().runOnUiThread(uiRunnable);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    public boolean isArrowBackEnabled() {
        return arrowBackEnabled;
    }

    public void setArrowBackEnabled(boolean arrowBackEnabled) {
        this.arrowBackEnabled = arrowBackEnabled;
    }
}
