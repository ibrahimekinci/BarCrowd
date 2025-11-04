package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.OpeningHoursEmbedded;
import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.google.firebase.Timestamp;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class VenueMapperUnitTest {

    @Test
    public void testToEntity_MapsAllFieldsCorrectly() {
        // 1. Arrange
        Venue model = new Venue();
        model.setVenueId("v1");
        model.setName("Test Venue");
        model.setType("Bar");
        model.getOpeningHours().put("Monday", "10:00-20:00");
        model.setCreatedAt(new Timestamp(new Date(12345L))); // Use a known time

        // 2. Act
        VenueEntity entity = VenueMapper.toEntity(model);

        // 3. Assert
        assertNotNull(entity);
        assertEquals("v1", entity.getVenueId());
        assertEquals("Test Venue", entity.getName());
        assertEquals("Bar", entity.getType());

        // Check timestamp conversion (Timestamp -> Date -> Long)
        assertEquals(12345L, entity.getCreatedAt().getTime());

        // Check embedded mapping (Map -> Embedded)
        assertNotNull(entity.getOpeningHours());
        assertEquals("10:00-20:00", entity.getOpeningHours().getOhMonday());
        assertNull(entity.getOpeningHours().getOhTuesday());
    }

    @Test
    public void testToModel_MapsAllFieldsCorrectly() {
        // 1. Arrange
        OpeningHoursEmbedded hours = new OpeningHoursEmbedded();
        hours.setOhMonday("10:00-20:00");
        hours.setOhTuesday("Closed");

        VenueEntity entity = new VenueEntity();
        entity.setVenueId("v1");
        entity.setName("Test Venue");
        entity.setType("Bar");
        entity.setOpeningHours(hours);
        entity.setCreatedAt(new Date(12345L)); // Use a known time

        // 2. Act
        Venue model = VenueMapper.toModel(entity);

        // 3. Assert
        assertNotNull(model);
        assertEquals("v1", model.getVenueId());
        assertEquals("Test Venue", model.getName());
        assertEquals("Bar", model.getType());

        // Check timestamp conversion (Long -> Date -> Timestamp)
        assertEquals(12345L, model.getCreatedAt().toDate().getTime());

        // Check embedded mapping (Embedded -> Map)
        assertNotNull(model.getOpeningHours());
        assertEquals(7, model.getOpeningHours().size()); // Mapper fills all 7 days
        assertEquals("10:00-20:00", model.getOpeningHours().get("Monday"));
        assertEquals("Closed", model.getOpeningHours().get("Tuesday"));
    }

    @Test
    public void testNullHandling() {
        assertNull(VenueMapper.toEntity(null));
        assertNull(VenueMapper.toModel(null));
    }
}