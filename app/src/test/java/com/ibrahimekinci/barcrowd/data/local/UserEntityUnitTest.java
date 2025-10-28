package com.ibrahimekinci.barcrowd.data.local;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserEntityUnitTest {
    @Test
    public void entityConstructionAndGetters() {
        UserEntity entity = new UserEntity("user1", "test@email.com", 123L);
        assertNotNull(entity.getId());
        assertEquals("user1", entity.getId());
        assertEquals("test@email.com", entity.getEmail());
        assertEquals(123L, entity.getCreatedAt());
    }

    @Test
    public void settersUpdateFields() {
        UserEntity entity = new UserEntity();
        entity.setId("user2");
        entity.setEmail("new@email.com");
        entity.setCreatedAt(124L);
        assertEquals("user2", entity.getId());
        assertEquals("new@email.com", entity.getEmail());
        assertEquals(124L, entity.getCreatedAt());
    }
}