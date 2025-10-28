package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

public class VenueMapper {
    public static VenueEntity toEntity(Venue model) {
        if (model == null) return null;
        return new VenueEntity(
                model.getId(),
                model.getName(),
                model.getAddress(),
                model.getLatitude(),
                model.getLongitude(),
                model.getDescription()
        );
    }

    public static Venue toModel(VenueEntity entity) {
        if (entity == null) return null;
        return new Venue(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getDescription()
        );
    }
}