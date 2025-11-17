package com.ibrahimekinci.barcrowd.ui.venues;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
//import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.ui.home.LiveUpdateAdapter; // Reusing adapter
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment to display details for a single venue.
 * Implements OnMapReadyCallback to handle the Google Map.
 */
public class VenueDetailsFragment extends Fragment implements OnMapReadyCallback {

    private VenueDetailsViewModel viewModel;
    private NavController navController;
    private LiveUpdateAdapter liveUpdateAdapter;
    private String venueId;

    // Views
    //private MapView mapView;
    private GoogleMap googleMap;
    private ImageView ivVenueLogo;
    private TextView tvVenueName, tvVenueType, tvCrowdLevel, tvWaitTime, tvAgeRange, tvUpdatedAt, tvAddress, tvOpeningHours;
    private RecyclerView rvRecentUpdates;
    private Button btnAddNewUpdate;

    // Required for MapView lifecycle
    private Bundle savedMapState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the venueId from SafeArgs
        if (getArguments() != null) {
            venueId = com.ibrahimekinci.barcrowd.ui.venues.VenueDetailsFragmentArgs.fromBundle(getArguments()).getVenueId();
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

        // --- 1. Find Views & NavController ---
        navController = Navigation.findNavController(view);
        findViews(view);

        // --- 2. Get Dependencies & ViewModel ---
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        VenueDetailsViewModelFactory factory = new VenueDetailsViewModelFactory(
                injector.getGetVenueByIdUseCase(),
                injector.getGetUpdatesForVenueUseCase()
        );
        viewModel = new ViewModelProvider(this, factory).get(VenueDetailsViewModel.class);

        // --- 3. Setup Toolbar ---
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        // --- 4. Setup RecyclerView ---
        setupRecyclerView();

        // --- 5. Setup MapView ---
        // IMPORTANT: Handle MapView lifecycle
        //savedMapState = (savedInstanceState != null) ? savedInstanceState.getBundle("mapViewSaveState") : null;
        //mapView.onCreate(savedMapState);
        //mapView.getMapAsync(this); // Triggers onMapReady

        // --- 6. Observe ViewModel ---
        observeViewModel();

        // --- 7. Load Data ---
        if (venueId != null) {
            viewModel.loadVenueData(venueId);
        } else {
            AppLogger.e("VenueId is null. Cannot load details.");
            navController.popBackStack(); // Go back if no ID
        }

        // --- 8. Setup Listeners ---
        btnAddNewUpdate.setOnClickListener(v -> {
            // TODO: Navigate to NewLiveUpdateFragment, passing the venueId
            // Action needs to be created in nav_graph.xml
            // VenueDetailsFragmentDirections.ActionVenueDetailsFragmentToNewLiveUpdateFragment action =
            //         VenueDetailsFragmentDirections.actionVenueDetailsFragmentToNewLiveUpdateFragment(venueId);
            // navController.navigate(action);
            AppLogger.d("Add New Update clicked. Navigation not implemented yet.");
        });
    }

    private void findViews(View view) {
        ivVenueLogo = view.findViewById(R.id.iv_venue_logo);
        tvVenueName = view.findViewById(R.id.tv_venue_name);
        tvVenueType = view.findViewById(R.id.tv_venue_type);
        tvCrowdLevel = view.findViewById(R.id.tv_crowd_level);
        tvWaitTime = view.findViewById(R.id.tv_wait_time);
        tvAgeRange = view.findViewById(R.id.tv_age_range);
        tvUpdatedAt = view.findViewById(R.id.tv_updated_at);
        tvAddress = view.findViewById(R.id.tv_address);
        tvOpeningHours = view.findViewById(R.id.tv_opening_hours);
        rvRecentUpdates = view.findViewById(R.id.rv_recent_updates);
        btnAddNewUpdate = view.findViewById(R.id.btn_add_new_update);
        //mapView = view.findViewById(R.id.map_view);
    }

    private void setupRecyclerView() {
        rvRecentUpdates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        liveUpdateAdapter = new LiveUpdateAdapter(); // Reusing the adapter
        rvRecentUpdates.setAdapter(liveUpdateAdapter);
    }

    private void observeViewModel() {
        // Observe the Venue object
        viewModel.getVenue().observe(getViewLifecycleOwner(), venue -> {
            if (venue != null) {
                populateVenueData(venue);
                updateMap(venue.getLatitude(), venue.getLongitude(), venue.getName());
            } else {
                // Handle case where venue isn't found
                AppLogger.w("Venue data is null.");
            }
        });

        // Observe the list of LiveUpdates
        viewModel.getUpdates().observe(getViewLifecycleOwner(), updates -> {
            if (updates != null) {
                liveUpdateAdapter.submitList(updates);
            }
        });
    }

    /**
     * Populates all UI fields with data from the Venue object.
     */
    private void populateVenueData(Venue venue) {
        tvVenueName.setText(venue.getName());
        tvVenueType.setText(venue.getType());
        tvAddress.setText(venue.getAddress());

        // Set stats
        tvCrowdLevel.setText("Crowd Level: " + venue.getLastCrowdLevel());
        tvWaitTime.setText("Wait Time: " + venue.getLastWaitTime());
        tvAgeRange.setText("Age Range: " + venue.getMostPopulousAge());

        // Format the timestamp (This is the logic you provided)
        if (venue.getCrowdLevelUpdatedAt() != null) {
            Date date = venue.getCrowdLevelUpdatedAt().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
            // Use the corrected string resource
            tvUpdatedAt.setText(getString(R.string.details_last_updated, sdf.format(date)));
        } else {
            // Use the new "N/A" string
            tvUpdatedAt.setText(getString(R.string.details_last_updated_na));
        }

        // Load logo
        Glide.with(this)
                .load(venue.getLogoUrl())
                .placeholder(R.drawable.venue_placeholder)
                .error(R.drawable.venue_placeholder)
                .centerCrop()
                .into(ivVenueLogo);

        // Format opening hours map
        tvOpeningHours.setText(formatOpeningHours(venue.getOpeningHours()));
    }

    /**
     * Formats the opening hours map into a displayable string.
     */
    private String formatOpeningHours(Map<String, String> hoursMap) {
        if (hoursMap == null || hoursMap.isEmpty()) {
            return "Not available.";
        }
        // Define the order
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        StringBuilder sb = new StringBuilder();
        for (String day : days) {
            String hours = hoursMap.getOrDefault(day, "Closed");
            sb.append(day).append(": ").append(hours).append("\n");
        }
        // Remove the last newline
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Called when the GoogleMap is ready.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setScrollGesturesEnabled(false); // Disable gestures inside ScrollView
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        // If venue data is already loaded, update the map
        Venue venue = viewModel.getVenue().getValue();
        if (venue != null) {
            updateMap(venue.getLatitude(), venue.getLongitude(), venue.getName());
        }
    }

    /**
     * Clears the map and adds a new marker for the venue.
     */
    private void updateMap(double latitude, double longitude, String title) {
        if (googleMap != null) {
            googleMap.clear();
            LatLng venueLocation = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(venueLocation).title(title));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLocation, 15f)); // Zoom in
        }
    }

    // --- MapView Lifecycle Delegation ---
    // These are REQUIRED to make the MapView work correctly.

    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
       // mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        //mapView.onStop();
    }

    @Override
    public void onPause() {
        //mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
       // mapView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
      //  mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
       // Bundle mapState = (savedMapState != null) ? savedMapState : new Bundle();
       // mapView.onSaveInstanceState(mapState);
        //outState.putBundle("mapViewSaveState", mapState);
    }
}