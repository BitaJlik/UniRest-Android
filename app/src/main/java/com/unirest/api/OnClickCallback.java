package com.unirest.api;

import android.view.View;

public interface OnClickCallback extends View.OnClickListener {

    @Override
    default void onClick(View view) {
        view.setEnabled(false);
        view.setClickable(false);
        this.onClickCallback(view, enabled -> {
            view.setEnabled(enabled);
            view.setClickable(enabled);
        });
    }

    void onClickCallback(View v, ICallback<Boolean> enableButton);
}
