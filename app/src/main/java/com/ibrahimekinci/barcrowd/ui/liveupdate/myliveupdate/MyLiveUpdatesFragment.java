package com.ibrahimekinci.barcrowd.ui.liveupdate.myliveupdate;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

public class MyLiveUpdatesFragment extends Fragment implements MyLiveUpdatesAdapter.OnUpdateActionListener {

    private MyLiveUpdatesViewModel viewModel;
    private MyLiveUpdatesAdapter adapter;
    private NavController navController;

    private RecyclerView rvUpdates;
    private ProgressBar pbLoading;
    private TextView tvEmptyView;
    private MaterialToolbar toolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_live_updates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Find Views
        rvUpdates = view.findViewById(R.id.rv_my_updates);
        pbLoading = view.findViewById(R.id.pb_loading);
        tvEmptyView = view.findViewById(R.id.tv_empty_view);
        toolbar = view.findViewById(R.id.toolbar);

        // Setup Toolbar (Back Navigation)
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
        }

        setupRecyclerView();

        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        MyLiveUpdatesViewModelFactory factory = new MyLiveUpdatesViewModelFactory(
                injector.getLiveUpdateRepository(),
                injector.getGetCurrentUserUseCase(),
                injector.getSoftDeleteLiveUpdateUseCase()
        );
        viewModel = new ViewModelProvider(this, factory).get(MyLiveUpdatesViewModel.class);

        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new MyLiveUpdatesAdapter(this);
        rvUpdates.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpdates.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getMyUpdates().observe(getViewLifecycleOwner(), updates -> {
            if (updates == null || updates.isEmpty()) {
                tvEmptyView.setVisibility(View.VISIBLE);
                rvUpdates.setVisibility(View.GONE);
            } else {
                tvEmptyView.setVisibility(View.GONE);
                rvUpdates.setVisibility(View.VISIBLE);
                adapter.setUpdates(updates);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getDeleteMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(LiveUpdate update) {
        try {
            MyLiveUpdatesFragmentDirections.ActionMyLiveUpdatesToDetail action =
                    MyLiveUpdatesFragmentDirections.actionMyLiveUpdatesToDetail(update);
            navController.navigate(action);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Navigation Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDeleteClick(LiveUpdate update) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Update")
                .setMessage("Are you sure you want to delete this update?")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteUpdate(update))
                .setNegativeButton("Cancel", null)
                .show();
    }
}