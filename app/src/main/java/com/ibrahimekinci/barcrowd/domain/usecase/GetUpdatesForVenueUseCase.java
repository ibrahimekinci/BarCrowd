// GetUpdatesForVenueUseCase.java
package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import java.util.List;

/**
 * Use case for getting updates for a venue.
 */
public class GetUpdatesForVenueUseCase {
    private final LiveUpdateRepository repository;

    public GetUpdatesForVenueUseCase(LiveUpdateRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<LiveUpdate>> execute(String venueId) {
        return repository.getUpdatesForVenue(venueId);
    }
}