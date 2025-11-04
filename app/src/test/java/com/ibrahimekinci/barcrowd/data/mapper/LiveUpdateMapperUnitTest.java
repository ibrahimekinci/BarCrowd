package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.google.firebase.Timestamp;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LiveUpdateMapperUnitTest {

    @Test
    public void testToEntity_MapsAllFieldsCorrectly() {
        // 1. Arrange
        LiveUpdate model = new LiveUpdate();
        model.setUpdateId("u1");
        model.setVenueId("v1");
        model.setUserId("user1");
        model.setCrowdLevel("Medium");
        model.setWaitTime("5–15");
        model.setVenueName("Test Venue"); // Denormalized data
        model.setCreatedAt(new Timestamp(new Date(12345L)));

        // 2. Act
        LiveUpdateEntity entity = LiveUpdateMapper.toEntity(model);

        // 3. Assert
        assertNotNull(entity);
        assertEquals("u1", entity.getUpdateId());
        assertEquals("v1", entity.getVenueId());
        assertEquals("Medium", entity.getCrowdLevel());
        assertEquals("5–15", entity.getWaitTime());
        assertEquals("Test Venue", entity.getVenueName());

        // Check timestamp conversion (Timestamp -> Date -> Long)
        assertEquals(12345L, entity.getCreatedAt().getTime());

        // Check default
        assertFalse(entity.isSyncStatus()); // Mapper doesn't set this
    }

    @Test
    public void testToModel_MapsAllFieldsCorrectly() {
        // 1. Arrange
        LiveUpdateEntity entity = new LiveUpdateEntity();
        entity.setUpdateId("u1");
        entity.setVenueId("v1");
        entity.setUserId("user1");
        entity.setCrowdLevel("Medium");
        entity.setWaitTime("5–15");
        entity.setVenueName("Test Venue");
        entity.setCreatedAt(new Date(12345L));
        entity.setSyncStatus(true); // This field should be ignored by the mapper

        // 2. Act
        LiveUpdate model = LiveUpdateMapper.toModel(entity);

        // 3. Assert
        assertNotNull(model);
        assertEquals("u1", model.getUpdateId());
        assertEquals("v1", model.getVenueId());
        assertEquals("Medium", model.getCrowdLevel());
        assertEquals("5–15", model.getWaitTime());
        assertEquals("Test Venue", model.getVenueName());

        // Check timestamp conversion (Long -> Date -> Timestamp)
        assertEquals(12345L, model.getCreatedAt().toDate().getTime());
    }

    @Test
    public void testNullHandling() {
        assertNull(LiveUpdateMapper.toEntity(null));
        assertNull(LiveUpdateMapper.toModel(null));
    }
}