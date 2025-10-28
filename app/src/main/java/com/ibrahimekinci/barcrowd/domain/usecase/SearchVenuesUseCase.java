package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import java.util.List;

/**
 * Use case for searching venues.
 */
public class SearchVenuesUseCase {
    private final VenueRepository venueRepository;

    public SearchVenuesUseCase(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public LiveData<List<Venue>> execute(String search) {
        // FIX: Wrap the search term in wildcards for SQL LIKE matching
        return venueRepository.searchVenues("%" + search + "%");
    }
}