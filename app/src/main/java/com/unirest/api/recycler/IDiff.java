package com.unirest.api.recycler;

public interface IDiff<T> {
    default boolean areItemsTheSame(T newItem) {
        return this == newItem;
    }

    default boolean areContentsTheSame(T newItem) {
        return this.equals(newItem);
    }

}