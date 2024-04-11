package com.unirest.ui.fragments.rooms;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.unirest.R;
import com.unirest.api.ICallback;
import com.unirest.api.OnClickCallback;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.Room;
import com.unirest.databinding.ItemRoomBinding;

public class RoomAdapter extends BaseAdapter<Room> {
    private static ICallback<Room> roomCallback;

    {
        addHolder(new HolderPair(FloorHolder.class, R.layout.item_room));
    }


    public static class FloorHolder extends BaseHolder<Room> {
        private final ItemRoomBinding binding;

        public FloorHolder(@NonNull View view) {
            super(view);
            binding = ItemRoomBinding.bind(view);
        }

        @Override
        public void bind(Room room) {
            Context context = itemView.getContext();
            binding.text.setText(String.format("%s %s", context.getString(R.string.room), room.getNumber()));
            binding.group.setText(String.format("%s / %s", room.getUsers().size(), room.getBeds()));
            binding.getRoot().setOnClickListener((OnClickCallback) (v, enableButton) -> {
                if (roomCallback != null) {
                    roomCallback.call(room);
                }
                enableButton.call(true);
            });
        }
    }

    public void setRoomCallback(ICallback<Room> roomCallback) {
        RoomAdapter.roomCallback = roomCallback;
    }
}
