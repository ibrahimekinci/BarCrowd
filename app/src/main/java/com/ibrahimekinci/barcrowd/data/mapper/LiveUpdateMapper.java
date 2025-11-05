package com.ibrahimekinci.barcrowd.data.mapper;

import com.google.firebase.Timestamp;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

/**
 * Maps between the LiveUpdate (domain model) and LiveUpdateEntity (local cache entity).
 */
public class LiveUpdateMapper {

    /**
     * Converts a LiveUpdate domain model (using Timestamp) to a LiveUpdateEntity (using Date).
     */
    public static LiveUpdateEntity toEntity(LiveUpdate model) {
        if (model == null) return null;

        LiveUpdateEntity entity = new LiveUpdateEntity();
        entity.setUpdateId(model.getUpdateId());
        entity.setVenueId(model.getVenueId());
        entity.setUserId(model.getUserId());
        entity.setCrowdLevel(model.getCrowdLevel());
        entity.setWaitTime(model.getWaitTime()); // String to String
        entity.setAgeRange(model.getAgeRange());
        entity.setDescription(model.getDescription());
        entity.setMediaUrl(model.getMediaUrl());
        entity.setThumbnailUrl(model.getThumbnailUrl());
        entity.setDeleted(model.isDeleted());

        // Convert Timestamp to Date
        entity.setDeletedAt(model.getDeletedAt() != null ? model.getDeletedAt().toDate() : null);

        // Map denormalized data
        entity.setVenueName(model.getVenueName());
        entity.setVenueType(model.getVenueType());
        entity.setVenueLogoUrl(model.getVenueLogoUrl());

        // Convert Timestamps to Dates
        entity.setUpdatedAt(model.getUpdatedAt() != null ? model.getUpdatedAt().toDate() : null);
        entity.setCreatedAt(model.getCreatedAt() != null ? model.getCreatedAt().toDate() : null);

        // Note: syncStatus is handled by the Repository logic, not the mapper.
        // It's set to 'false' when a new update is posted locally.
        // It's set to 'true' when an update is synced from remote.

        return entity;
    }

    /**
     * Converts a LiveUpdateEntity (using Date) back to a LiveUpdate domain model (using Timestamp).
     */
    public static LiveUpdate toModel(LiveUpdateEntity entity) {
        if (entity == null) return null;

        LiveUpdate model = new LiveUpdate();
        model.setUpdateId(entity.getUpdateId());
        model.setVenueId(entity.getVenueId());
        model.setUserId(entity.getUserId());
        model.setCrowdLevel(entity.getCrowdLevel());
        model.setWaitTime(entity.getWaitTime());
        model.setAgeRange(entity.getAgeRange());
        model.setDescription(entity.getDescription());
        model.setMediaUrl(entity.getMediaUrl());
        model.setThumbnailUrl(entity.getThumbnailUrl());
        model.setDeleted(entity.isDeleted());

        // Convert Date to Timestamp
        model.setDeletedAt(entity.getDeletedAt() != null ? new Timestamp(entity.getDeletedAt()) : null);

        // Map denormalized data
        model.setVenueName(entity.getVenueName());
        model.setVenueType(entity.getVenueType());
        model.setVenueLogoUrl(entity.getVenueLogoUrl());

        // Convert Dates to Timestamps
        model.setUpdatedAt(entity.getUpdatedAt() != null ? new Timestamp(entity.getUpdatedAt()) : null);
        model.setCreatedAt(entity.getCreatedAt() != null ? new Timestamp(entity.getCreatedAt()) : null);

        // We don't map syncStatus to the domain model, as it's a local-only concern.

        return model;
    }
}