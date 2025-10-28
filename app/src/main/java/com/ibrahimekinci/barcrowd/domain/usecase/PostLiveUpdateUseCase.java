package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;
import java.util.UUID;

/**
 * Use case for posting live updates, comprehensive validation.
 */
public class PostLiveUpdateUseCase {
    private LiveUpdateRepository liveUpdateRepository;

    public PostLiveUpdateUseCase(LiveUpdateRepository liveUpdateRepository) {
        this.liveUpdateRepository = liveUpdateRepository;
    }

    public void execute(LiveUpdate update) throws ValidationException {
        // Validate core fields
        if (!Validators.isNotEmpty(update.getVenueId())) {
            throw new ValidationException("Venue ID is required");
        }
        if (!Validators.isValidCrowdLevel(update.getCrowdLevel())) {
            throw new ValidationException("Crowd level must be low, medium, or high");
        }
        if (!Validators.isValidWaitTime(update.getWaitTime())) {
            throw new ValidationException("Wait time must be 0-120 minutes");
        }
        if (!Validators.isValidAgeRange(update.getAgeRange())) {
            throw new ValidationException("Age range format: e.g., 20-30");
        }
        if (!Validators.isNotEmpty(update.getUserId())) {
            throw new ValidationException("User ID is required");
        }
        if (update.getTimestamp() <= 0) {
            update.setTimestamp(System.currentTimeMillis()); // Auto-set if invalid
        }
        if (!update.isComplete()) { // Leverage model method
            throw new ValidationException("Update is incomplete");
        }

        // Business logic: Post to repo (local first, sync later)
        liveUpdateRepository.postUpdate(update);
        // Provisional ID for local insert
        if (update.getId() == null || update.getId().isEmpty()) {
            update.setId(UUID.randomUUID().toString());
        }
        update.setTimestamp(System.currentTimeMillis());

        // Post to repo (local first, sync generates Firebase ID)
        liveUpdateRepository.postUpdate(update);
    }
}