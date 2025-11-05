package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import java.util.List;

/**
 * Use case for getting ALL venues, ordered by name.
 * Used for spinners (e.g., in NewLiveUpdateFragment) or map view.
 */
public class GetAllVenuesUseCase {
    private final VenueRepository venueRepository;

    public GetAllVenuesUseCase(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public LiveData<List<Venue>> execute() {
        // Calls the new repository method
        return venueRepository.getAllVenuesSortedByName();
    }
}