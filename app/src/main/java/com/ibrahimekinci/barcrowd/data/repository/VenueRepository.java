package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import java.util.List;

public interface VenueRepository {

    // This method is no longer needed as syncVenues does the insertion
    // void insertVenues(List<Venue> venues);

    /**
     * Gets all venues marked with showOnHomePage=true from the local cache.
     * Automatically triggers a background sync with Firestore.
     */
    LiveData<List<Venue>> getHomePageVenues();

    /**
     * Gets a single venue by its ID from the local cache.
     */
    Venue getVenueById(String venueId);

    /**
     * Searches the local cache for venues where the name matches the query.
     */
    LiveData<List<Venue>> searchVenues(String query);

    /**
     * Triggers a one-way sync from Firestore to the local Room database.
     */
    void syncVenues();

    /**
     * Gets all venues from the local cache, sorted by name.
     */
    LiveData<List<Venue>> getAllVenuesSortedByName();
}