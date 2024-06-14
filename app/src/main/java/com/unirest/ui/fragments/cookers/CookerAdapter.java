package com.unirest.ui.fragments.cookers;

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
    private static ICallback<Cooker> removeICallback;
    private static boolean modeEdit = false;

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

            if (modeEdit) {
                binding.removeImage.setVisibility(View.VISIBLE);
                if (removeICallback != null) {
                    binding.removeImage.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                        removeICallback.call(cooker);
                    });
                }
            } else {
                binding.removeImage.setVisibility(View.GONE);
            }

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

    public void setModeEdit(boolean modeEdit) {
        CookerAdapter.modeEdit = modeEdit;
    }

    public void setRemoveICallback(ICallback<Cooker> removeICallback) {
        CookerAdapter.removeICallback = removeICallback;
    }
}
