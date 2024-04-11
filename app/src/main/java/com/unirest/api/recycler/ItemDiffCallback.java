package com.unirest.api.recycler;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ItemDiffCallback<Item extends IDiff<Item>> extends DiffUtil.Callback {

    private final List<Item> oldList;
    private final List<Item> newList;

    public ItemDiffCallback(List<Item> oldList, List<Item> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Item oldItem = oldList.get(oldItemPosition);
        Item newItem = newList.get(newItemPosition);
        return oldItem.areItemsTheSame(newItem);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Item oldItem = oldList.get(oldItemPosition);
        Item newItem = newList.get(newItemPosition);
        return oldItem.areContentsTheSame(newItem);
    }
}