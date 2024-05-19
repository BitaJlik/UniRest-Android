package com.unirest.ui.fragments.washers;

import android.view.View;

import androidx.annotation.NonNull;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.Washer;
import com.unirest.databinding.ItemWasherBinding;

public class WasherAdapter extends BaseAdapter<Washer> {
    private static ICallback<Washer> washerICallback;

    {
        addHolder(new HolderPair(WasherHolder.class, R.layout.item_washer));
    }

    public static class WasherHolder extends BaseHolder<Washer> {
        private final ItemWasherBinding binding;

        public WasherHolder(@NonNull View view) {
            super(view);
            binding = ItemWasherBinding.bind(view);
        }

        @Override
        public void bind(Washer washer) {
            binding.text.setText(String.format("%s %s", itemView.getContext().getString(R.string.washer), getAdapterPosition() + 1));
            if (washerICallback != null) {
                binding.getRoot().setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    washerICallback.call(washer);
                    enableButton.call(true);
                });
            }

        }
    }

    public void setWasherICallback(ICallback<Washer> washerICallback) {
        WasherAdapter.washerICallback = washerICallback;
    }
}
