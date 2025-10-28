package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.UserEntity;
import com.ibrahimekinci.barcrowd.domain.model.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserMapperUnitTest {
    @Test
    public void toEntityAndBack() {
        User model = new User("user1", "test@email.com", 123L);
        UserEntity entity = UserMapper.toEntity(model);
        assertEquals("user1", entity.getId());
        assertEquals("test@email.com", entity.getEmail());

        User mapped = UserMapper.toModel(entity);
        assertEquals(model.getId(), mapped.getId());
        assertEquals(model.getEmail(), mapped.getEmail());
    }

    @Test
    public void nullHandling() {
        assertNull(UserMapper.toEntity(null));
        assertNull(UserMapper.toModel(null));
    }
}