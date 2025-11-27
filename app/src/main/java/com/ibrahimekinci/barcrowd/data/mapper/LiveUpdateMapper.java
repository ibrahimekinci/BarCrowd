package com.ibrahimekinci.barcrowd.data.mapper;

import com.google.firebase.Timestamp;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

public class LiveUpdateMapper {

    public static LiveUpdateEntity toEntity(LiveUpdate model) {
        if (model == null) return null;

        LiveUpdateEntity entity = new LiveUpdateEntity();
        entity.setUpdateId(model.getUpdateId());
        entity.setVenueId(model.getVenueId());
        entity.setUserId(model.getUserId());

        entity.setUserName(model.getUserName());
        entity.setUserPhotoUrl(model.getUserPhotoUrl());

        entity.setVenueName(model.getVenueName());
        entity.setVenueType(model.getVenueType());
        entity.setVenueLogoUrl(model.getVenueLogoUrl());

        entity.setCrowdLevel(model.getCrowdLevel());
        entity.setWaitTime(model.getWaitTime());
        entity.setAgeRange(model.getAgeRange());
        entity.setDescription(model.getDescription());
        entity.setMediaUrl(model.getMediaUrl());
        entity.setThumbnailUrl(model.getThumbnailUrl());

        entity.setDeleted(model.getIsDeleted());

        // Timestamps
        entity.setCreatedAt(model.getCreatedAt() != null ? model.getCreatedAt().toDate() : null);
        entity.setDeletedAt(model.getDeletedAt() != null ? model.getDeletedAt().toDate() : null);
        entity.setUpdatedAt(model.getUpdatedAt() != null ? model.getUpdatedAt().toDate() : null);

        return entity;
    }

    public static LiveUpdate toModel(LiveUpdateEntity entity) {
        if (entity == null) return null;

        LiveUpdate model = new LiveUpdate();
        model.setUpdateId(entity.getUpdateId());
        model.setVenueId(entity.getVenueId());
        model.setUserId(entity.getUserId());

        model.setUserName(entity.getUserName());
        model.setUserPhotoUrl(entity.getUserPhotoUrl());

        model.setVenueName(entity.getVenueName());
        model.setVenueType(entity.getVenueType());
        model.setVenueLogoUrl(entity.getVenueLogoUrl());

        model.setCrowdLevel(entity.getCrowdLevel());
        model.setWaitTime(entity.getWaitTime());
        model.setAgeRange(entity.getAgeRange());
        model.setDescription(entity.getDescription());
        model.setMediaUrl(entity.getMediaUrl());
        model.setThumbnailUrl(entity.getThumbnailUrl());

        model.setDeleted(entity.isDeleted());

        // Dates to Timestamps
        model.setCreatedAt(entity.getCreatedAt() != null ? new Timestamp(entity.getCreatedAt()) : null);
        model.setDeletedAt(entity.getDeletedAt() != null ? new Timestamp(entity.getDeletedAt()) : null);
        model.setUpdatedAt(entity.getUpdatedAt() != null ? new Timestamp(entity.getUpdatedAt()) : null);

        return model;
    }
}