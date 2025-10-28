// GetVenueByIdUseCase.java
package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

/**
 * Use case for getting a venue by ID.
 */
public class GetVenueByIdUseCase {
    private final VenueRepository venueRepository;

    public GetVenueByIdUseCase(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public Venue execute(String venueId) {
        return venueRepository.getVenueById(venueId);
    }
}