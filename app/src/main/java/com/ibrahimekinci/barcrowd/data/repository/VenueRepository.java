package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.model.VenueFilterOptions;

import java.util.List;

public interface VenueRepository {

    LiveData<List<Venue>> getAllVenues();

    LiveData<List<Venue>> getHomePageVenues();

    Venue getVenueById(String venueId);

    void syncVenues();

    // Search functionality
    LiveData<List<Venue>> searchVenues(VenueFilterOptions filters);

    // Update stats when a new LiveUpdate is posted
    void updateVenueStats(String venueId, String crowd, String wait, String age);
}