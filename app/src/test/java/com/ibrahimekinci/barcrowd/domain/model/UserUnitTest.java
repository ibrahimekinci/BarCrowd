package com.ibrahimekinci.barcrowd.domain.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserUnitTest {
    @Test
    public void isValidTrue() {
        User user = new User("user1", "test@email.com", 123L);
        assertTrue(user.isValid());
    }

    @Test
    public void isValidFalseEmptyEmail() {
        User user = new User("user1", "", 123L);
        assertFalse(user.isValid());
    }

    @Test
    public void isValidFalseNullEmail() {
        User user = new User("user1", null, 123L);
        assertFalse(user.isValid());
    }
}