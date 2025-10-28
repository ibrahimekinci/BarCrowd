package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VenueMapperUnitTest {
    @Test
    public void toEntityAndBack() {
        Venue model = new Venue("venue1", "Bar", "Addr", 37.77, -122.41, "Desc");
        VenueEntity entity = VenueMapper.toEntity(model);
        assertEquals("venue1", entity.getId());
        assertEquals(37.77, entity.getLatitude(), 0.001);

        Venue mapped = VenueMapper.toModel(entity);
        assertEquals(model.getId(), mapped.getId());
        assertEquals(model.getLatitude(), mapped.getLatitude(), 0.001);
    }

    @Test
    public void nullHandling() {
        assertNull(VenueMapper.toEntity(null));
        assertNull(VenueMapper.toModel(null));
    }
}