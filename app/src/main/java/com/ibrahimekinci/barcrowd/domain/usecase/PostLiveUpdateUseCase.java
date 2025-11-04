package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

/**
 * Use case for validating and posting a new live update.
 * This use case is responsible for fetching the venue's denormalized data
 * before passing the complete LiveUpdate object to the repository.
 */
public class PostLiveUpdateUseCase {

    private final LiveUpdateRepository liveUpdateRepository;
    private final GetVenueByIdUseCase getVenueByIdUseCase; // NEW Dependency

    public PostLiveUpdateUseCase(LiveUpdateRepository liveUpdateRepository, GetVenueByIdUseCase getVenueByIdUseCase) {
        this.liveUpdateRepository = liveUpdateRepository;
        this.getVenueByIdUseCase = getVenueByIdUseCase;
    }

    /**
     * Validates, enriches (with denormalized data), and posts a live update.
     * @param update The LiveUpdate object from the UI (may be missing venueName, etc.)
     * @throws ValidationException if any data is invalid.
     */
    public void execute(LiveUpdate update) throws ValidationException {

        // --- 1. Validate Core Fields ---
        if (!Validators.isNotEmpty(update.getVenueId())) {
            throw new ValidationException("Venue ID is required");
        }
        if (!Validators.isNotEmpty(update.getUserId())) {
            throw new ValidationException("User ID is required");
        }
        if (!Validators.isValidCrowdLevel(update.getCrowdLevel())) {
            throw new ValidationException("A valid crowd level is required");
        }
        if (!Validators.isValidWaitTime(update.getWaitTime())) {
            throw new ValidationException("A valid wait time is required");
        }
        if (!Validators.isValidAgeRange(update.getAgeRange())) {
            throw new ValidationException("A valid age range is required");
        }
        if (!Validators.isNotEmpty(update.getMediaUrl())) {
            throw new ValidationException("A media URL is required");
        }

        // --- 2. Fetch and Add Denormalized Data ---
        // This is a blocking call and MUST be run on a background thread
        // (which the ViewModel's Coroutine or RxJava stream should handle).
        Venue venue = getVenueByIdUseCase.execute(update.getVenueId());
        if (venue == null) {
            throw new ValidationException("Invalid Venue ID. Venue not found.");
        }

        update.setVenueName(venue.getName());
        update.setVenueType(venue.getType());
        update.setVenueLogoUrl(venue.getLogoUrl());

        // --- 3. Validation Passed. Send to Repository ---
        // The repository will handle setting the UUID, Timestamps, and syncStatus.
        liveUpdateRepository.postUpdate(update);
    }
}