package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

public class LiveUpdateMapper {
    public static LiveUpdateEntity toEntity(LiveUpdate model) {
        if (model == null) return null;
        return new LiveUpdateEntity(
                model.getId(),
                model.getVenueId(),
                model.getUserId(),
                model.getTimestamp(),
                model.getCrowdLevel(),
                model.getWaitTime(),
                model.getAgeRange(),
                model.getPhotoUrl(),
                model.getVideoUrl(),
                model.getLatitude(),
                model.getLongitude(),
                false  // Default to unsynced for local inserts
        );
    }

    public static LiveUpdate toModel(LiveUpdateEntity entity) {
        if (entity == null) return null;
        return new LiveUpdate(
                entity.getId(),
                entity.getVenueId(),
                entity.getUserId(),
                entity.getTimestamp(),
                entity.getCrowdLevel(),
                entity.getWaitTime(),
                entity.getAgeRange(),
                entity.getPhotoUrl(),
                entity.getVideoUrl(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}