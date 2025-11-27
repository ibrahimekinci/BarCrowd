package com.ibrahimekinci.barcrowd.ui.venues;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Added for feedback

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.usecase.IsUserLoggedInUseCase; // Import Auth Check
import com.ibrahimekinci.barcrowd.ui.home.LiveUpdateAdapter;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class VenueDetailsFragment extends Fragment implements OnMapReadyCallback, LiveUpdateAdapter.OnLiveUpdateClickListener {

    private VenueDetailsViewModel viewModel;
    private NavController navController;
    private LiveUpdateAdapter liveUpdateAdapter;
    private String venueId;
    private IsUserLoggedInUseCase isUserLoggedInUseCase; // Auth use case

    // Views
    private MapView mapView;
    private GoogleMap googleMap;
    private ImageView ivVenueLogo;
    private TextView tvVenueName, tvVenueType, tvCrowdLevel, tvWaitTime, tvAgeRange, tvUpdatedAt, tvAddress, tvOpeningHours;
    private RecyclerView rvRecentUpdates;
    private Button btnAddNewUpdate;

    private Bundle savedMapState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            venueId = VenueDetailsFragmentArgs.fromBundle(getArguments()).getVenueId();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venue_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        findViews(view);

        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();

        // Get Auth Use Case
        isUserLoggedInUseCase = injector.getIsUserLoggedInUseCase();

        VenueDetailsViewModelFactory factory = new VenueDetailsViewModelFactory(
                injector.getGetVenueByIdUseCase(),
                injector.getGetUpdatesForVenueUseCase()
        );
        viewModel = new ViewModelProvider(this, factory).get(VenueDetailsViewModel.class);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        setupRecyclerView();

        savedMapState = (savedInstanceState != null) ? savedInstanceState.getBundle("mapViewSaveState") : null;
        if (mapView != null) {
            mapView.onCreate(savedMapState);
            mapView.getMapAsync(this);
        }

        observeViewModel();

        if (venueId != null) {
            viewModel.loadVenueData(venueId);
        } else {
            AppLogger.e("VenueId is null. Cannot load details.");
            navController.popBackStack();
        }

        // --- UPDATED LISTENER WITH AUTH CHECK ---
        btnAddNewUpdate.setOnClickListener(v -> {
            if (isUserLoggedInUseCase.execute()) {
                // Logged In -> Go to New Update
                VenueDetailsFragmentDirections.ActionVenueDetailsFragmentToNewLiveUpdateFragment action =
                        VenueDetailsFragmentDirections.actionVenueDetailsFragmentToNewLiveUpdateFragment();
                action.setVenueId(venueId);
                navController.navigate(action);
            } else {
                // Not Logged In -> Go to Login
                Toast.makeText(getContext(), "Please sign in to post an update.", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_venueDetailsFragment_to_signInFragment);
            }
        });
    }

    private void findViews(View view) {
        ivVenueLogo = view.findViewById(R.id.iv_venue_logo);
        tvVenueName = view.findViewById(R.id.tv_venue_name);
        tvVenueType = view.findViewById(R.id.tv_venue_type);
        tvCrowdLevel = view.findViewById(R.id.tv_stat_crowd);
        tvWaitTime = view.findViewById(R.id.tv_stat_wait);
        tvAgeRange = view.findViewById(R.id.tv_stat_age);
        tvUpdatedAt = view.findViewById(R.id.tv_updated_at);
        tvAddress = view.findViewById(R.id.tv_venue_address);
        tvOpeningHours = view.findViewById(R.id.tv_opening_hours);
        rvRecentUpdates = view.findViewById(R.id.rv_recent_updates);
        btnAddNewUpdate = view.findViewById(R.id.btn_post_update_from_detail);
        // mapView = view.findViewById(R.id.map_view); // Uncomment if map is in layout
    }

    private void setupRecyclerView() {
        rvRecentUpdates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        liveUpdateAdapter = new LiveUpdateAdapter(this);
        rvRecentUpdates.setAdapter(liveUpdateAdapter);
    }

    private void observeViewModel() {
        viewModel.getVenue().observe(getViewLifecycleOwner(), venue -> {
            if (venue != null) {
                populateVenueData(venue);
                updateMap(venue.getLatitude(), venue.getLongitude(), venue.getName());
            } else {
                AppLogger.w("Venue data is null.");
            }
        });

        viewModel.getUpdates().observe(getViewLifecycleOwner(), updates -> {
            if (updates != null) {
                liveUpdateAdapter.submitList(updates);
            }
        });
    }

    private void populateVenueData(Venue venue) {
        tvVenueName.setText(venue.getName());
        tvVenueType.setText(venue.getType());
        tvAddress.setText(venue.getAddress());

        tvCrowdLevel.setText("Crowd: " + (venue.getLastLiveUpdateCrowdLevel() != null ? venue.getLastLiveUpdateCrowdLevel() : "-"));
        tvWaitTime.setText("Wait: " + (venue.getLastLiveUpdateWaitTime() != null ? venue.getLastLiveUpdateWaitTime() : "-"));
        tvAgeRange.setText("Age: " + (venue.getLastLiveUpdateAgeRange() != null ? venue.getLastLiveUpdateAgeRange() : "-"));

        if (venue.getLastLiveUpdateCreatedAt() != null) {
            Date date = venue.getLastLiveUpdateCreatedAt().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
            tvUpdatedAt.setText("Last updated: " + sdf.format(date));
        } else {
            tvUpdatedAt.setText("Last updated: N/A");
        }

        Glide.with(this)
                .load(venue.getLogoUrl())
                .placeholder(R.drawable.venue_placeholder)
                .error(R.drawable.venue_placeholder)
                .centerCrop()
                .into(ivVenueLogo);

        tvOpeningHours.setText(formatOpeningHours(venue.getOpeningHours()));
    }

    private String formatOpeningHours(Map<String, String> hoursMap) {
        if (hoursMap == null || hoursMap.isEmpty()) {
            return "Not available.";
        }
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        StringBuilder sb = new StringBuilder();
        for (String day : days) {
            String hours = hoursMap.getOrDefault(day, "Closed");
            sb.append(day).append(": ").append(hours).append("\n");
        }
        return sb.toString().trim();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        Venue venue = viewModel.getVenue().getValue();
        if (venue != null) {
            updateMap(venue.getLatitude(), venue.getLongitude(), venue.getName());
        }
    }

    private void updateMap(double latitude, double longitude, String title) {
        if (googleMap != null) {
            googleMap.clear();
            LatLng venueLocation = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(venueLocation).title(title));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLocation, 15f));
        }
    }

    @Override
    public void onLiveUpdateClick(LiveUpdate liveUpdate) {
        VenueDetailsFragmentDirections.ActionVenueDetailsFragmentToLiveUpdateDetailFragment action =
                VenueDetailsFragmentDirections.actionVenueDetailsFragmentToLiveUpdateDetailFragment(liveUpdate);
        navController.navigate(action);
    }

    // Lifecycle methods for MapView (Keep these if you use MapView)
    @Override public void onResume() { super.onResume(); if(mapView!=null) mapView.onResume(); }
    @Override public void onStart() { super.onStart(); if(mapView!=null) mapView.onStart(); }
    @Override public void onStop() { super.onStop(); if(mapView!=null) mapView.onStop(); }
    @Override public void onPause() { if(mapView!=null) mapView.onPause(); super.onPause(); }
    @Override public void onDestroyView() { if(mapView!=null) mapView.onDestroy(); super.onDestroyView(); }
    @Override public void onLowMemory() { super.onLowMemory(); if(mapView!=null) mapView.onLowMemory(); }
    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            Bundle mapState = (savedMapState != null) ? savedMapState : new Bundle();
            mapView.onSaveInstanceState(mapState);
            outState.putBundle("mapViewSaveState", mapState);
        }
    }
}