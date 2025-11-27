package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;

public class SyncHomeDataUseCase {
    private final VenueRepository venueRepository;
    private final LiveUpdateRepository liveUpdateRepository;

    public SyncHomeDataUseCase(VenueRepository venueRepository, LiveUpdateRepository liveUpdateRepository) {
        this.venueRepository = venueRepository;
        this.liveUpdateRepository = liveUpdateRepository;
    }

    public void execute() {

        venueRepository.syncVenues();
        liveUpdateRepository.syncRecentLiveUpdates();
    }
}