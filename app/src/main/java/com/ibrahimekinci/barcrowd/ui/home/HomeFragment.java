package com.ibrahimekinci.barcrowd.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements VenueAdapter.OnVenueClickListener, LiveUpdateAdapter.OnLiveUpdateClickListener {

    private HomeViewModel viewModel;
    private NavController navController;
    private VenueAdapter venueAdapter;
    private LiveUpdateAdapter liveUpdateAdapter;
    private RecyclerView rvLiveUpdates;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        HomeViewModelFactory factory = new HomeViewModelFactory(
                injector.getGetHomePageVenuesUseCase(),
                injector.getGetRecentLiveUpdatesUseCase(),
                injector.getSyncHomeDataUseCase()
        );
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        navController = Navigation.findNavController(view);
        Button btnShowAllUpdates = view.findViewById(R.id.btn_show_all_updates);
        Button btnShowAllVenues = view.findViewById(R.id.btn_show_all_venues);

        setupLiveUpdatesRecyclerView(view);
        setupVenuesRecyclerView(view);

        observeViewModel();

        btnShowAllUpdates.setOnClickListener(v -> navController.navigate(R.id.action_homeFragment_to_allLiveUpdatesFragment));
        btnShowAllVenues.setOnClickListener(v -> navController.navigate(R.id.action_homeFragment_to_allVenuesFragment));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshData();
        }
    }

    private void setupLiveUpdatesRecyclerView(View view) {
        rvLiveUpdates = view.findViewById(R.id.rv_live_updates);

        rvLiveUpdates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        liveUpdateAdapter = new LiveUpdateAdapter(this);
        rvLiveUpdates.setAdapter(liveUpdateAdapter);
    }

    private void setupVenuesRecyclerView(View view) {
        RecyclerView rvVenues = view.findViewById(R.id.rv_venues);
        rvVenues.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        venueAdapter = new VenueAdapter(this);
        rvVenues.setAdapter(venueAdapter);
    }

    private void observeViewModel() {
        viewModel.getHomePageVenues().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null) {
                venueAdapter.submitList(new ArrayList<>(venues));
            }
        });

        viewModel.getRecentLiveUpdates().observe(getViewLifecycleOwner(), updates -> {
            if (updates != null) {
                liveUpdateAdapter.submitList(new ArrayList<>(updates), () -> {
                    if (!updates.isEmpty()) {
                        rvLiveUpdates.scrollToPosition(0);
                    }
                });
            }
        });
    }

    @Override
    public void onVenueClick(Venue venue) {
        try {
            HomeFragmentDirections.ActionHomeFragmentToVenueDetailsFragment action =
                    HomeFragmentDirections.actionHomeFragmentToVenueDetailsFragment(venue.getVenueId());
            navController.navigate(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLiveUpdateClick(LiveUpdate liveUpdate) {
        try {
            HomeFragmentDirections.ActionHomeFragmentToLiveUpdateDetailFragment action =
                    HomeFragmentDirections.actionHomeFragmentToLiveUpdateDetailFragment(liveUpdate);
            navController.navigate(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}