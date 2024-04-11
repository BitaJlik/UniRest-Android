package com.unirest.ui.common;

import androidx.core.view.MenuProvider;
import androidx.viewbinding.ViewBinding;

public abstract class BaseMenuFragment<Binding extends ViewBinding> extends BaseFragment<Binding> {
    private MenuProvider menuProvider;

    public BaseMenuFragment(Class<Binding> bindingClass) {
        super(bindingClass);
    }

    public void setMenuProvider(MenuProvider menuProvider) {
        this.menuProvider = menuProvider;
    }

    public MenuProvider getMenuProvider() {
        if (menuProvider == null) {
            throw new RuntimeException("MenuFragment used but not set menu");
        }
        return menuProvider;
    }

    @Override
    public void onResume() {
        requireActivity().addMenuProvider(getMenuProvider());
        super.onResume();
    }

    @Override
    public void onPause() {
        requireActivity().removeMenuProvider(getMenuProvider());
        super.onPause();
    }
}