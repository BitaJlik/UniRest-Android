package com.unirest.ui.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.unirest.R;
import com.unirest.databinding.LoadingWrapperViewBinding;

@SuppressWarnings("unused")
public class LoadingWrapperView extends LinearLayout {
    private LoadingWrapperViewBinding binding;

    public LoadingWrapperView(Context context) {
        super(context);
        init(context);
    }

    public LoadingWrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        binding.inner.addView(child, index, params);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.loading_wrapper_view, this, true);
        binding = LoadingWrapperViewBinding.bind(view);

        binding.loadingBar.setVisibility(VISIBLE);
        binding.imageError.setVisibility(GONE);
        binding.inner.setVisibility(GONE);
    }

}