package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import java.util.List;

public interface VenueRepository {
    void insertVenues(List<Venue> venues);
    LiveData<List<Venue>> getAllVenues();
    Venue getVenueById(String venueId);
    LiveData<List<Venue>> searchVenues(String search);
    void syncVenues();
}