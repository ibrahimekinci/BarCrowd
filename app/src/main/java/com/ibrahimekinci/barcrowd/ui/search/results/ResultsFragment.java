package com.ibrahimekinci.barcrowd.ui.search.results;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.VenueFilterOptions;
import com.ibrahimekinci.barcrowd.ui.search.SearchVenueAdapter;

public class ResultsFragment extends Fragment {

    private ResultsViewModel viewModel;
    private SearchVenueAdapter adapter;
    private NavController navController;

    private RecyclerView rvResults;
    private TextView tvNoResults;
    private ProgressBar pbLoading;
    private MaterialToolbar toolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        rvResults = view.findViewById(R.id.rv_results_list);
        tvNoResults = view.findViewById(R.id.tv_no_results);
        pbLoading = view.findViewById(R.id.pb_loading);
        toolbar = view.findViewById(R.id.toolbar);

        // Setup Toolbar Back Button
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        setupRecyclerView();
        setupViewModel();

        if (getArguments() != null) {
            ResultsFragmentArgs args = ResultsFragmentArgs.fromBundle(getArguments());
            VenueFilterOptions options = args.getFilterOptions();
            viewModel.search(options);
        }
    }

    private void setupRecyclerView() {
        adapter = new SearchVenueAdapter(venue -> {
            // Navigation to Venue Details
            try {
                // Use SafeArgs to pass the venueId
                ResultsFragmentDirections.ActionResultsFragmentToVenueDetailsFragment action =
                        ResultsFragmentDirections.actionResultsFragmentToVenueDetailsFragment(venue.getVenueId());
                navController.navigate(action);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        rvResults.setAdapter(adapter);
    }

    private void setupViewModel() {
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        ResultsViewModelFactory factory = new ResultsViewModelFactory(injector.getSearchVenuesUseCase());
        viewModel = new ViewModelProvider(this, factory).get(ResultsViewModel.class);

        viewModel.searchResults.observe(getViewLifecycleOwner(), venues -> {
            pbLoading.setVisibility(View.GONE);
            if (venues == null || venues.isEmpty()) {
                rvResults.setVisibility(View.GONE);
                tvNoResults.setVisibility(View.VISIBLE);
            } else {
                rvResults.setVisibility(View.VISIBLE);
                tvNoResults.setVisibility(View.GONE);
                adapter.setVenues(venues);
            }
        });
    }
}