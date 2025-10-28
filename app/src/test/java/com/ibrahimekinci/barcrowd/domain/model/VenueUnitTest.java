package com.ibrahimekinci.barcrowd.domain.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VenueUnitTest {
    @Test
    public void hasLocationTrue() {
        Venue venue = new Venue("venue1", "Bar", "Addr", 37.77, -122.41, "Desc");
        assertTrue(venue.hasLocation());
    }

    @Test
    public void hasLocationFalseZero() {
        Venue venue = new Venue("venue1", "Bar", "Addr", 0.0, 0.0, "Desc");
        assertFalse(venue.hasLocation());
    }
}