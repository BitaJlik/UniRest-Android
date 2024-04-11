package com.unirest.api.recycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public abstract class BaseAdapter<Item extends IDiff<Item>> extends RecyclerView.Adapter<BaseAdapter.BaseHolder<Item>> {
    private final List<Item> items = new ArrayList<>();
    private final HashMap<Integer, HolderPair> holderTypes = new HashMap<>();

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public BaseHolder<Item> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HolderPair holderPair = holderTypes.get(viewType);
        if (holderPair == null) {
            throw new RuntimeException("Not available HolderPair for viewType=" + viewType);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(holderPair.layout, parent, false);
        try {
            return (BaseHolder<Item>) holderPair.holder.getDeclaredConstructor(View.class).newInstance(view);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder<Item> holder, int position) {
        if (position < getItems().size()) {
            holder.bind(getItems().get(position));
        } else {
            Log.e("BaseAdapter", String.format("Position are over index request=%s, available=%s", position, getItems().size()));
            holder.bind(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected static abstract class BaseHolder<Item extends IDiff<Item>> extends RecyclerView.ViewHolder {
        public BaseHolder(@NonNull View view) {
            super(view);
        }

        public abstract void bind(Item item);
    }

    // Utils
    public void addHolder(HolderPair holderPair) {
        addHolder(holderTypes.size(), holderPair);
    }

    public void addHolder(int viewType, HolderPair holderPair) {
        holderTypes.put(viewType, holderPair);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        if (items != null && !items.isEmpty()) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemDiffCallback<>(this.items, items));
            this.items.clear();
            this.items.addAll(items);
            diffResult.dispatchUpdatesTo(new AdapterListUpdateCallback(this));
        } else {
            int size = this.items.size();
            this.items.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public static class HolderPair {
        private final Class<? extends BaseHolder<?>> holder;
        private final @LayoutRes int layout;

        public HolderPair(Class<? extends BaseHolder<?>> holder, int layout) {
            this.holder = holder;
            this.layout = layout;
        }

        public Class<? extends BaseHolder<?>> getHolder() {
            return holder;
        }

        public int getLayout() {
            return layout;
        }
    }
}
