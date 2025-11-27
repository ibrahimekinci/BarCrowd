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

public class VenueDetailsViewModel extends ViewModel {

    private final GetVenueByIdUseCase getVenueByIdUseCase;
    private final GetUpdatesForVenueUseCase getUpdatesForVenueUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Venue> venue = new MutableLiveData<>();
    private final MutableLiveData<String> venueIdLiveData = new MutableLiveData<>();
    private final LiveData<List<LiveUpdate>> updates;

    public VenueDetailsViewModel(GetVenueByIdUseCase getVenueByIdUseCase, GetUpdatesForVenueUseCase getUpdatesForVenueUseCase) {
        this.getVenueByIdUseCase = getVenueByIdUseCase;
        this.getUpdatesForVenueUseCase = getUpdatesForVenueUseCase;

        this.updates = Transformations.switchMap(venueIdLiveData, id -> {
            if (id == null || id.isEmpty()) {
                return new MutableLiveData<>(Collections.emptyList());
            }
            return this.getUpdatesForVenueUseCase.execute(id);
        });
    }

    public void loadVenueData(String venueId) {
        // 1. Fetch Venue
        executor.execute(() -> {
            try {
                Venue venueData = getVenueByIdUseCase.execute(venueId);
                venue.postValue(venueData);
            } catch (Exception e) {
                AppLogger.e("Failed to get venue by ID", e);
                venue.postValue(null);
            }
        });

        // 2. Fetch Updates
        venueIdLiveData.setValue(venueId);
    }

    public LiveData<Venue> getVenue() {
        return venue;
    }

    public LiveData<List<LiveUpdate>> getUpdates() {
        return updates;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}