package com.ibrahimekinci.barcrowd.data.local;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class LiveUpdateEntityUnitTest {
    @Test
    public void entityConstructionAndGetters() {
        LiveUpdateEntity entity = new LiveUpdateEntity("update1", "venue1", "user1", 1730026985000L, "medium", 15, "20-30", "photo.url", "video.url", -37.8173, 144.9552, false);
        assertNotNull(entity.getId());
        assertEquals("update1", entity.getId());
        assertEquals("venue1", entity.getVenueId());
        assertEquals("user1", entity.getUserId());
        assertEquals(1730026985000L, entity.getTimestamp());
        assertEquals("medium", entity.getCrowdLevel());
        assertEquals(15, entity.getWaitTime());
        assertEquals("20-30", entity.getAgeRange());
        assertEquals("photo.url", entity.getPhotoUrl());
        assertEquals("video.url", entity.getVideoUrl());
        assertEquals(-37.8173, entity.getLatitude(), 0.0001);
        assertEquals(144.9552, entity.getLongitude(), 0.0001);
        assertFalse(entity.isSyncStatus());
    }

    @Test
    public void settersUpdateFields() {
        LiveUpdateEntity entity = new LiveUpdateEntity();
        entity.setId("update2");
        entity.setVenueId("venue2");
        entity.setUserId("user2");
        entity.setTimestamp(1730026986000L);
        entity.setCrowdLevel("high");
        entity.setWaitTime(30);
        entity.setAgeRange("25-35");
        entity.setPhotoUrl("newphoto.url");
        entity.setVideoUrl("newvideo.url");
        entity.setLatitude(-37.8150);
        entity.setLongitude(144.9661);
        entity.setSyncStatus(true);

        assertEquals("update2", entity.getId());
        assertEquals("venue2", entity.getVenueId());
        assertEquals("user2", entity.getUserId());
        assertEquals(1730026986000L, entity.getTimestamp());
        assertEquals("high", entity.getCrowdLevel());
        assertEquals(30, entity.getWaitTime());
        assertEquals("25-35", entity.getAgeRange());
        assertEquals("newphoto.url", entity.getPhotoUrl());
        assertEquals("newvideo.url", entity.getVideoUrl());
        assertEquals(-37.8150, entity.getLatitude(), 0.0001);
        assertEquals(144.9661, entity.getLongitude(), 0.0001);
        assertTrue(entity.isSyncStatus());
    }

    @Test
    public void defaultConstructorAndSetters() {
        LiveUpdateEntity entity = new LiveUpdateEntity();
        entity.setId("update3");
        entity.setVenueId("venue1");
        entity.setUserId("user1");
        entity.setTimestamp(0L);
        entity.setCrowdLevel("low");
        entity.setWaitTime(5);
        entity.setAgeRange("20-30");
        entity.setPhotoUrl(null);
        entity.setVideoUrl(null);
        entity.setLatitude(0.0);
        entity.setLongitude(0.0);
        entity.setSyncStatus(false);

        assertEquals("update3", entity.getId());
        assertEquals("venue1", entity.getVenueId());
        assertEquals("user1", entity.getUserId());
        assertEquals(0L, entity.getTimestamp());
        assertEquals("low", entity.getCrowdLevel());
        assertEquals(5, entity.getWaitTime());
        assertEquals("20-30", entity.getAgeRange());
        assertNull(entity.getPhotoUrl());
        assertNull(entity.getVideoUrl());
        assertEquals(0.0, entity.getLatitude(), 0.0001);
        assertEquals(0.0, entity.getLongitude(), 0.0001);
        assertFalse(entity.isSyncStatus());
    }
}