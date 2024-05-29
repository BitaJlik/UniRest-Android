package com.unirest.ui.fragments.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.unirest.R;
import com.unirest.api.OnClickCallback;
import com.unirest.data.DataNetHandler;
import com.unirest.databinding.FragmentSearchBinding;
import com.unirest.ui.common.BaseFragment;

public class FragmentSearch extends BaseFragment<FragmentSearchBinding> {
    private final SearchUserAdapter adapter = new SearchUserAdapter();

    public FragmentSearch() {
        super(FragmentSearchBinding.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsRV.setAdapter(adapter);

        mainViewModel.user.observe(getViewLifecycleOwner(), user -> {
            binding.search.setOnClickListener((OnClickCallback) (v, enableButton) -> {
                String name = binding.layoutSearch.getEditText().getText().toString();
                if (name.isEmpty()) {
                    Snackbar.make(view, R.string.inputs_cant_be_empty, Snackbar.LENGTH_SHORT).show();
                    enableButton.call(true);
                    return;
                }
                Long dormitoryId = binding.checkbox.isChecked() ? user.getDormitoryId() : null;
                binding.progress.setVisibility(View.VISIBLE);
                binding.itemsRV.setVisibility(View.GONE);
                binding.text.setVisibility(View.GONE);
                DataNetHandler.getInstance().searchUser(name, dormitoryId, users -> {
                    ui(() -> {
                        if (users.isEmpty()) {
                            binding.progress.setVisibility(View.GONE);
                            binding.itemsRV.setVisibility(View.GONE);
                            binding.text.setVisibility(View.VISIBLE);
                        } else {
                            adapter.setItems(users);
                            binding.progress.setVisibility(View.GONE);
                            binding.itemsRV.setVisibility(View.VISIBLE);
                            binding.text.setVisibility(View.GONE);
                        }
                    });
                    enableButton.call(true);
                });
            });
        });
    }
}
