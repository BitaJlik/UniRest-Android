package com.unirest.ui.fragments.floors;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.Floor;
import com.unirest.databinding.ItemFloorBinding;

public class FloorAdapter extends BaseAdapter<Floor> {
    private static ICallback<Floor> floorICallback;


    {
        addHolder(new HolderPair(FloorHolder.class, R.layout.item_floor));
    }


    public static class FloorHolder extends BaseHolder<Floor> {
        private final ItemFloorBinding binding;

        public FloorHolder(@NonNull View view) {
            super(view);
            binding = ItemFloorBinding.bind(view);
        }

        @Override
        public void bind(Floor floor) {
            Context context = itemView.getContext();
            binding.text.setText(String.format("%s %s", floor.getNumber(), context.getString(R.string.floor)));
            binding.cooker.setText(String.valueOf(floor.getCookers().size()));
            binding.washing.setText(String.valueOf(floor.getCookers().size()));
            binding.shortName.setText(floor.getShortName());
            binding.rooms.setText(String.format("%s - %s", floor.getMinRoomNumber(), floor.getMaxRoomNumber()));

            binding.getRoot().setOnClickListener((OnClickCallback) (v, enableButton) -> {
                if (floorICallback != null) {
                    floorICallback.call(floor);
                }
                enableButton.call(true);
            });
        }
    }

    public void setFloorICallback(ICallback<Floor> floorICallback) {
        FloorAdapter.floorICallback = floorICallback;
    }
}
