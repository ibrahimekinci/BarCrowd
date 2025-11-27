package com.ibrahimekinci.barcrowd.data.mapper;

import com.google.firebase.Timestamp;
import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import java.util.Date;

public class VenueMapper {

    public static VenueEntity toEntity(Venue model) {
        if (model == null) return null;

        VenueEntity entity = new VenueEntity();
        entity.setVenueId(model.getVenueId());
        entity.setName(model.getName());
        entity.setType(model.getType());
        entity.setLogoUrl(model.getLogoUrl());
        entity.setAddress(model.getAddress());
        entity.setLatitude(model.getLatitude());
        entity.setLongitude(model.getLongitude());
        entity.setOpeningHours(model.getOpeningHours());

        // Stats Mapping
        entity.setLastLiveUpdateCrowdLevel(model.getLastLiveUpdateCrowdLevel());
        entity.setLastLiveUpdateWaitTime(model.getLastLiveUpdateWaitTime());
        entity.setLastLiveUpdateAgeRange(model.getLastLiveUpdateAgeRange());
        entity.setShowOnHomePage(model.isShowOnHomePage());

        // Timestamp -> Date Conversion
        entity.setLastLiveUpdateCreatedAt(model.getLastLiveUpdateCreatedAt() != null ? model.getLastLiveUpdateCreatedAt().toDate() : null);
        entity.setUpdatedAt(model.getUpdatedAt() != null ? model.getUpdatedAt().toDate() : null);
        entity.setCreatedAt(model.getCreatedAt() != null ? model.getCreatedAt().toDate() : null);

        return entity;
    }

    public static Venue toModel(VenueEntity entity) {
        if (entity == null) return null;

        Venue model = new Venue();
        model.setVenueId(entity.getVenueId());
        model.setName(entity.getName());
        model.setType(entity.getType());
        model.setLogoUrl(entity.getLogoUrl());
        model.setAddress(entity.getAddress());
        model.setLatitude(entity.getLatitude());
        model.setLongitude(entity.getLongitude());

        if (entity.getOpeningHours() != null) {
            model.setOpeningHours(entity.getOpeningHours());
        }

        // Stats Mapping
        model.setLastLiveUpdateCrowdLevel(entity.getLastLiveUpdateCrowdLevel());
        model.setLastLiveUpdateWaitTime(entity.getLastLiveUpdateWaitTime());
        model.setLastLiveUpdateAgeRange(entity.getLastLiveUpdateAgeRange());
        model.setShowOnHomePage(entity.isShowOnHomePage());

        // Date -> Timestamp Conversion
        model.setLastLiveUpdateCreatedAt(entity.getLastLiveUpdateCreatedAt() != null ? new Timestamp(entity.getLastLiveUpdateCreatedAt()) : null);
        model.setUpdatedAt(entity.getUpdatedAt() != null ? new Timestamp(entity.getUpdatedAt()) : null);
        model.setCreatedAt(entity.getCreatedAt() != null ? new Timestamp(entity.getCreatedAt()) : null);

        return model;
    }
}