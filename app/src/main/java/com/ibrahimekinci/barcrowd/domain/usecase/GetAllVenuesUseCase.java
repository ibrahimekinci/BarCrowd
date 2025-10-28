// GetAllVenuesUseCase.java
package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import java.util.List;

/**
 * Use case for getting all venues.
 */
public class GetAllVenuesUseCase {
    private final VenueRepository venueRepository;

    public GetAllVenuesUseCase(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public LiveData<List<Venue>> execute() {
        return venueRepository.getAllVenues();
    }
}