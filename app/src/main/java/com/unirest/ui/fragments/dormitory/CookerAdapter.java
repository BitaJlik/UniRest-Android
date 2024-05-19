package com.unirest.ui.fragments.dormitory;

import android.view.View;

import androidx.annotation.NonNull;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.Cooker;
import com.unirest.databinding.ItemCookerBinding;

public class CookerAdapter extends BaseAdapter<Cooker> {
    private static ICallback<Cooker> cookerICallback;

    {
        addHolder(new HolderPair(CookerHolder.class, R.layout.item_cooker));
    }

    public static class CookerHolder extends BaseHolder<Cooker> {
        private final ItemCookerBinding binding;

        public CookerHolder(@NonNull View view) {
            super(view);
            binding = ItemCookerBinding.bind(view);
        }

        @Override
        public void bind(Cooker cooker) {
            binding.text.setText(String.format("%s %s", itemView.getContext().getString(R.string.cooker), getAdapterPosition() + 1));

            if (cookerICallback != null) {
                binding.getRoot().setOnClickListener((OnClickCallback) (v, enableButton) -> {
                    cookerICallback.call(cooker);
                    enableButton.call(true);
                });
            }

        }
    }

    public void setCookerICallback(ICallback<Cooker> cookerICallback) {
        CookerAdapter.cookerICallback = cookerICallback;
    }
}
