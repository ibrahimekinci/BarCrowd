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

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private NavController navController;
    private VenueAdapter venueAdapter;
    private LiveUpdateAdapter liveUpdateAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Get Dependencies ---
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        HomeViewModelFactory factory = new HomeViewModelFactory(
                injector.getGetHomePageVenuesUseCase(),
                injector.getGetRecentLiveUpdatesUseCase()
        );
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        // --- 2. Find Views ---
        navController = Navigation.findNavController(view);
        Button btnShowAllUpdates = view.findViewById(R.id.btn_show_all_updates);

        // --- 3. Setup RecyclerViews ---
        setupLiveUpdatesRecyclerView(view);
        setupVenuesRecyclerView(view);

        // --- 4. Setup Observers ---
        observeViewModel();

        // --- 5. Setup Listeners ---
        btnShowAllUpdates.setOnClickListener(v -> {
            // TODO: Navigate to the "YouTube Shorts" style feed
            // This action needs to be added to nav_graph.xml
            // navController.navigate(R.id.action_homeFragment_to_liveUpdateFeedFragment);
        });
    }

    private void setupLiveUpdatesRecyclerView(View view) {
        RecyclerView rvLiveUpdates = view.findViewById(R.id.rv_live_updates);
        // Use a horizontal layout manager
        rvLiveUpdates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        liveUpdateAdapter = new LiveUpdateAdapter();
        rvLiveUpdates.setAdapter(liveUpdateAdapter);
    }

    private void setupVenuesRecyclerView(View view) {
        RecyclerView rvVenues = view.findViewById(R.id.rv_venues);
        // Use a vertical layout manager
        rvVenues.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        venueAdapter = new VenueAdapter();
        rvVenues.setAdapter(venueAdapter);
    }

    private void observeViewModel() {
        // Observe the featured venues
        viewModel.getHomePageVenues().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null) {
                venueAdapter.submitList(venues);
            }
        });

        // Observe the recent live updates
        viewModel.getRecentLiveUpdates().observe(getViewLifecycleOwner(), updates -> {
            if (updates != null) {
                liveUpdateAdapter.submitList(updates);
            }
        });
    }
}