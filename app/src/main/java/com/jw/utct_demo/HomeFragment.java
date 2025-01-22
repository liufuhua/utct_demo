package com.jw.utct_demo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends Fragment {
    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置 ActionBar 标题和返回按钮
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        setOnClicks(view);
    }

    private void setOnClicks(@NonNull View view) {
        // 设置点击事件
        CardView itemSearch = view.findViewById(R.id.item_search);
        itemSearch.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeFragmentToPatientList();
            NavHostFragment.findNavController(HomeFragment.this).navigate(action);
        });

        CardView itemSync = view.findViewById(R.id.item_sync);
        itemSync.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeFragmentToSyncFragment();
            NavHostFragment.findNavController(HomeFragment.this).navigate(action);
        });

        CardView itemPeriodicSync = view.findViewById(R.id.item_periodic_sync);
        itemPeriodicSync.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeFragmentToPeriodicSyncFragment();
            NavHostFragment.findNavController(HomeFragment.this).navigate(action);
        });

        CardView itemCrud = view.findViewById(R.id.item_crud);
        itemCrud.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeFragmentToCrudOperationFragment();
            NavHostFragment.findNavController(HomeFragment.this).navigate(action);
        });
    }
}
