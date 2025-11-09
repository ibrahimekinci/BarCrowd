package com.ibrahimekinci.barcrowd.ui.venues;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.usecase.GetUpdatesForVenueUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetVenueByIdUseCase;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for the VenueDetailsFragment.
 * Manages fetching the specific Venue and its associated LiveUpdates.
 */
public class VenueDetailsViewModel extends ViewModel {

    private final GetVenueByIdUseCase getVenueByIdUseCase;
    private final GetUpdatesForVenueUseCase getUpdatesForVenueUseCase; // Store this
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // LiveData for the Venue object
    private final MutableLiveData<Venue> venue = new MutableLiveData<>();

    // This LiveData holds the ID
    private final MutableLiveData<String> venueIdLiveData = new MutableLiveData<>();
    // This LiveData observes the ID and fetches updates when it changes
    private final LiveData<List<LiveUpdate>> updates;


    public VenueDetailsViewModel(GetVenueByIdUseCase getVenueByIdUseCase, GetUpdatesForVenueUseCase getUpdatesForVenueUseCase) {
        this.getVenueByIdUseCase = getVenueByIdUseCase;
        this.getUpdatesForVenueUseCase = getUpdatesForVenueUseCase; // Store it

        this.updates = Transformations.switchMap(venueIdLiveData, id -> {
            if (id == null || id.isEmpty()) {
                // Return an empty list if there's no ID
                return new MutableLiveData<>(Collections.emptyList());
            }
            // Fetch updates from the use case
            return this.getUpdatesForVenueUseCase.execute(id);
        });
        // ------------------------------------------------
    }

    /**
     * Loads all necessary data for the given venueId.
     * Fetches the Venue object from Room (blocking) on a background thread.
     * Initializes the LiveData stream for updates by setting the venueId.
     *
     * @param venueId The ID of the venue to load.
     */
    public void loadVenueData(String venueId) {
        // 1. Fetch the static Venue object on a background thread
        executor.execute(() -> {
            try {
                Venue venueData = getVenueByIdUseCase.execute(venueId);
                venue.postValue(venueData); // Post the result to the main thread
            } catch (Exception e) {
                AppLogger.e("Failed to get venue by ID", e);
                venue.postValue(null);
            }
        });

        // 2. Set the venueId. This will trigger the 'updates' switchMap
        //    to fetch the list of updates.
        venueIdLiveData.setValue(venueId);
    }

    // Getter for the Fragment to observe the Venue
    public LiveData<Venue> getVenue() {
        return venue;
    }

    // Getter for the Fragment to observe the LiveUpdates
    // This now returns the non-null 'updates' LiveData
    public LiveData<List<LiveUpdate>> getUpdates() {
        return updates;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}