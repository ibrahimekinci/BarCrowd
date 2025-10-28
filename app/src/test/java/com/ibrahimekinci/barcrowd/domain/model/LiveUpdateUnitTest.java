package com.ibrahimekinci.barcrowd.domain.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LiveUpdateUnitTest {
    @Test
    public void isCompleteTrue() {
        LiveUpdate update = new LiveUpdate("update1", "venue1", "user1", 123L, "medium", 10, "20-30", "photo.url", "video.url", 37.77, -122.41);
        assertTrue(update.isComplete());
    }

    @Test
    public void isCompleteFalseEmptyCrowdLevel() {
        LiveUpdate update = new LiveUpdate("update1", "venue1", "user1", 123L, "", 10, "20-30", "photo.url", "video.url", 37.77, -122.41);
        assertFalse(update.isComplete());
    }

    @Test
    public void isCompleteFalseNegativeWaitTime() {
        LiveUpdate update = new LiveUpdate("update1", "venue1", "user1", 123L, "medium", -1, "20-30", "photo.url", "video.url", 37.77, -122.41);
        assertFalse(update.isComplete());
    }
}