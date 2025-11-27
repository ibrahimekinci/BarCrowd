package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.model.VenueFilterOptions;

import java.util.List;

public class SearchVenuesUseCase {
    private final VenueRepository venueRepository;

    public SearchVenuesUseCase(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public LiveData<List<Venue>> execute(VenueFilterOptions filters) {
        return venueRepository.searchVenues(filters);
    }
}