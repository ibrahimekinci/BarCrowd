package com.ibrahimekinci.barcrowd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExceptionsUnitTest {
    @Test
    public void authExceptionConstructor() {
        AuthException e = new AuthException("message");
        assertEquals("message", e.getMessage());

        Throwable cause = new Throwable();
        AuthException e2 = new AuthException("message", cause);
        assertEquals("message", e2.getMessage());
        assertEquals(cause, e2.getCause());
    }

    @Test
    public void networkExceptionConstructor() {
        NetworkException e = new NetworkException("message");
        assertEquals("message", e.getMessage());

        Throwable cause = new Throwable();
        NetworkException e2 = new NetworkException("message", cause);
        assertEquals("message", e2.getMessage());
        assertEquals(cause, e2.getCause());
    }

    @Test
    public void storageExceptionConstructor() {
        StorageException e = new StorageException("message");
        assertEquals("message", e.getMessage());

        Throwable cause = new Throwable();
        StorageException e2 = new StorageException("message", cause);
        assertEquals("message", e2.getMessage());
        assertEquals(cause, e2.getCause());
    }

    @Test
    public void validationExceptionConstructor() {
        ValidationException e = new ValidationException("message");
        assertEquals("message", e.getMessage());

        Throwable cause = new Throwable();
        ValidationException e2 = new ValidationException("message", cause);
        assertEquals("message", e2.getMessage());
        assertEquals(cause, e2.getCause());
    }
}