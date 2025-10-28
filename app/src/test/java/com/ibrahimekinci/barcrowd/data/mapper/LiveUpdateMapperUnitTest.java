package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class LiveUpdateMapperUnitTest {
    @Test
    public void toEntityAndBack() {
        LiveUpdate model = new LiveUpdate("update1", "venue1", "user1", 123L, "medium", 10, "20-30", "photo.url", "video.url", 37.77, -122.41);
        LiveUpdateEntity entity = LiveUpdateMapper.toEntity(model);
        assertEquals("update1", entity.getId());
        assertEquals("medium", entity.getCrowdLevel());
        assertFalse(entity.isSyncStatus()); // Assuming default false

        LiveUpdate mapped = LiveUpdateMapper.toModel(entity);
        assertEquals(model.getId(), mapped.getId());
        assertEquals(model.getCrowdLevel(), mapped.getCrowdLevel());
    }

    @Test
    public void nullHandling() {
        assertNull(LiveUpdateMapper.toEntity(null));
        assertNull(LiveUpdateMapper.toModel(null));
    }
}