package com.ibrahimekinci.barcrowd.ui.venues;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.ui.home.VenueAdapter;

/**
 * Fragment to display a complete list of all venues, sorted alphabetically.
 */
public class AllVenuesFragment extends Fragment implements VenueAdapter.OnVenueClickListener {

    private AllVenuesViewModel viewModel;
    private NavController navController;
    private VenueAdapter venueAdapter; // Reusing the existing VenueAdapter

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_venues, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Get Dependencies ---
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        AllVenuesViewModelFactory factory = new AllVenuesViewModelFactory(
                injector.getGetAllVenuesUseCase()
        );
        viewModel = new ViewModelProvider(this, factory).get(AllVenuesViewModel.class);

        // --- 2. Find Views & NavController ---
        navController = Navigation.findNavController(view);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        RecyclerView rvAllVenues = view.findViewById(R.id.rv_all_venues);

        // --- 3. Setup Toolbar ---
        toolbar.setNavigationOnClickListener(v -> {
            // Handle back navigation
            navController.popBackStack();
        });

        // --- 4. Setup RecyclerView ---

        venueAdapter = new VenueAdapter(this);
        rvAllVenues.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAllVenues.setAdapter(venueAdapter);

        // --- 5. Setup Observers ---
        observeViewModel();
    }

    /**
     * Observes the LiveData from the ViewModel and submits the list to the adapter.
     */
    private void observeViewModel() {
        viewModel.getAllVenues().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null) {
                // Submit the full list of venues to the adapter
                venueAdapter.submitList(venues);
            }
        });
    }
    @Override
    public void onVenueClick(Venue venue) {
        // Navigate to VenueDetailsFragment using the generated SafeArgs class
        AllVenuesFragmentDirections.ActionAllVenuesFragmentToVenueDetailsFragment action =
                AllVenuesFragmentDirections.actionAllVenuesFragmentToVenueDetailsFragment(venue.getVenueId());
        navController.navigate(action);
    }
}