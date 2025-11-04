package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.OpeningHoursEmbedded;
import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.google.firebase.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps between the Venue (domain model) and VenueEntity (local cache entity).
 */
public class VenueMapper {

    /**
     * Converts a Venue domain model (using Timestamp/Map) to a VenueEntity (using Date/Embedded).
     */
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

        // Map the Map<String, String> to the OpeningHoursEmbedded object
        if (model.getOpeningHours() != null) {
            OpeningHoursEmbedded hours = new OpeningHoursEmbedded();
            hours.setOhMonday(model.getOpeningHours().get("Monday"));
            hours.setOhTuesday(model.getOpeningHours().get("Tuesday"));
            hours.setOhWednesday(model.getOpeningHours().get("Wednesday"));
            hours.setOhThursday(model.getOpeningHours().get("Thursday"));
            hours.setOhFriday(model.getOpeningHours().get("Friday"));
            hours.setOhSaturday(model.getOpeningHours().get("Saturday"));
            hours.setOhSunday(model.getOpeningHours().get("Sunday"));
            entity.setOpeningHours(hours);
        }

        entity.setAverageCrowdLevel(model.getAverageCrowdLevel());
        entity.setLastCrowdLevel(model.getLastCrowdLevel());
        entity.setAverageWaitTime(model.getAverageWaitTime());
        entity.setLastWaitTime(model.getLastWaitTime());

        // Convert Timestamp (Firestore) to Date (Room)
        entity.setCrowdLevelUpdatedAt(model.getCrowdLevelUpdatedAt() != null ? model.getCrowdLevelUpdatedAt().toDate() : null);

        entity.setMostPopulousAge(model.getMostPopulousAge());
        entity.setShowOnHomePage(model.isShowOnHomePage());

        // Convert Timestamps to Dates
        entity.setUpdatedAt(model.getUpdatedAt() != null ? model.getUpdatedAt().toDate() : null);
        entity.setCreatedAt(model.getCreatedAt() != null ? model.getCreatedAt().toDate() : null);

        return entity;
    }

    /**
     * Converts a VenueEntity (using Date/Embedded) back to a Venue domain model (using Timestamp/Map).
     */
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

        // Map the OpeningHoursEmbedded object back to a Map<String, String>
        if (entity.getOpeningHours() != null) {
            Map<String, String> hoursMap = new HashMap<>();
            hoursMap.put("Monday", entity.getOpeningHours().getOhMonday());
            hoursMap.put("Tuesday", entity.getOpeningHours().getOhTuesday());
            hoursMap.put("Wednesday", entity.getOpeningHours().getOhWednesday());
            hoursMap.put("Thursday", entity.getOpeningHours().getOhThursday());
            hoursMap.put("Friday", entity.getOpeningHours().getOhFriday());
            hoursMap.put("Saturday", entity.getOpeningHours().getOhSaturday());
            hoursMap.put("Sunday", entity.getOpeningHours().getOhSunday());
            model.setOpeningHours(hoursMap);
        }

        model.setAverageCrowdLevel(entity.getAverageCrowdLevel());
        model.setLastCrowdLevel(entity.getLastCrowdLevel());
        model.setAverageWaitTime(entity.getAverageWaitTime());
        model.setLastWaitTime(entity.getLastWaitTime());

        // Convert Date (Room) to Timestamp (Firestore)
        model.setCrowdLevelUpdatedAt(entity.getCrowdLevelUpdatedAt() != null ? new Timestamp(entity.getCrowdLevelUpdatedAt()) : null);

        model.setMostPopulousAge(entity.getMostPopulousAge());
        model.setShowOnHomePage(entity.isShowOnHomePage());

        // Convert Dates to Timestamps
        model.setUpdatedAt(entity.getUpdatedAt() != null ? new Timestamp(entity.getUpdatedAt()) : null);
        model.setCreatedAt(entity.getCreatedAt() != null ? new Timestamp(entity.getCreatedAt()) : null);

        return model;
    }
}