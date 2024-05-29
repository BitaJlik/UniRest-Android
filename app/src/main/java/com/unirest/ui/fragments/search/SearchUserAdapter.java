package com.unirest.ui.fragments.search;

import android.view.View;

import androidx.annotation.NonNull;

import com.unirest.R;
import com.unirest.api.recycler.BaseAdapter;
import com.unirest.data.models.UserSearch;
import com.unirest.databinding.ItemUserSearchBinding;

public class SearchUserAdapter extends BaseAdapter<UserSearch> {

    {
        addHolder(new HolderPair(SearchUserHolder.class, R.layout.item_user_search));
    }

    public static class SearchUserHolder extends BaseHolder<UserSearch> {
        private final ItemUserSearchBinding binding;

        public SearchUserHolder(@NonNull View view) {
            super(view);
            binding = ItemUserSearchBinding.bind(view);
        }

        @Override
        public void bind(UserSearch userSearch) {
            binding.name.setText(userSearch.getName());
            binding.surname.setText(userSearch.getLastName());
            binding.room.setText(String.format("%s: %s", itemView.getContext().getString(R.string.room), userSearch.getRoomNumber()));
        }
    }


}
