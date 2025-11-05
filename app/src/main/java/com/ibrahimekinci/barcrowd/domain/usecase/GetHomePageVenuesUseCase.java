package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import java.util.List;

/**
 * Use case for getting only the "Featured" venues for the home page.
 * (Where showOnHomePage = true)
 */
public class GetHomePageVenuesUseCase {
    private final VenueRepository venueRepository;

    public GetHomePageVenuesUseCase(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public LiveData<List<Venue>> execute() {
        // This relies on the new getHomePageVenues() method in the repository
        return venueRepository.getHomePageVenues();
    }
}