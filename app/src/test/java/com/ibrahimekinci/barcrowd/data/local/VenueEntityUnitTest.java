package com.ibrahimekinci.barcrowd.data.local;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VenueEntityUnitTest {
    @Test
    public void entityConstructionAndGetters() {
        VenueEntity entity = new VenueEntity("venue1", "The Rusty Anchor", "123 Flinders St", -37.8173, 144.9552, "Cozy pub");
        assertNotNull(entity.getId());
        assertEquals("venue1", entity.getId());
        assertEquals("The Rusty Anchor", entity.getName());
        assertEquals("123 Flinders St", entity.getAddress());
        assertEquals(-37.8173, entity.getLatitude(), 0.0001);
        assertEquals(144.9552, entity.getLongitude(), 0.0001);
        assertEquals("Cozy pub", entity.getDescription());
    }

    @Test
    public void settersUpdateFields() {
        VenueEntity entity = new VenueEntity();
        entity.setId("venue2");
        entity.setName("Neon Nights");
        entity.setAddress("45 Collins St");
        entity.setLatitude(-37.8150);
        entity.setLongitude(144.9661);
        entity.setDescription("Vibrant nightclub");

        assertEquals("venue2", entity.getId());
        assertEquals("Neon Nights", entity.getName());
        assertEquals("45 Collins St", entity.getAddress());
        assertEquals(-37.8150, entity.getLatitude(), 0.0001);
        assertEquals(144.9661, entity.getLongitude(), 0.0001);
        assertEquals("Vibrant nightclub", entity.getDescription());
    }

    @Test
    public void defaultConstructorAndSetters() {
        VenueEntity entity = new VenueEntity();
        entity.setId("venue3");
        entity.setName("Test Venue");
        entity.setAddress("Test Address");
        entity.setLatitude(0.0);
        entity.setLongitude(0.0);
        entity.setDescription("Test Desc");

        assertEquals("venue3", entity.getId());
        assertEquals("Test Venue", entity.getName());
        assertEquals("Test Address", entity.getAddress());
        assertEquals(0.0, entity.getLatitude(), 0.0001);
        assertEquals(0.0, entity.getLongitude(), 0.0001);
        assertEquals("Test Desc", entity.getDescription());
    }
}