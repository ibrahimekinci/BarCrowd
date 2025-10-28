package com.ibrahimekinci.barcrowd.data.local;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Integration tests for {@link UserDao}.
 * This test uses a real, in-memory Room database to verify
 * User entity operations.
 */
@RunWith(AndroidJUnit4.class)
public class UserDaoTest {

    // This rule is REQUIRED to test LiveData synchronously (if we had any).
    // It's good practice to keep it for DAOs.
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private UserDao userDao;

    // Test data
    private UserEntity user1 = new UserEntity("u1", "user1@example.com", 1000L);
    private UserEntity user2 = new UserEntity("u2", "user2@example.com", 2000L);

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Create an in-memory database for testing
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsertAndGetUserByEmail() throws Exception {
        // 1. Arrange
        userDao.insert(user1);

        // 2. Act
        UserEntity retrieved = userDao.getUserByEmail("user1@example.com");

        // 3. Assert
        assertNotNull(retrieved);
        assertEquals(user1.getId(), retrieved.getId());
        assertEquals(user1.getEmail(), retrieved.getEmail());
    }

    @Test
    public void testGetUserByEmail_WhenUserNotExists_ReturnsNull() throws Exception {
        // 1. Arrange
        userDao.insert(user1);

        // 2. Act
        UserEntity retrieved = userDao.getUserByEmail("nonexistent@example.com");

        // 3. Assert
        assertNull(retrieved);
    }

    @Test
    public void testInsert_OnConflict_ReplacesExistingUser() throws Exception {
        // 1. Arrange
        userDao.insert(user1); // Original user

        // New user data with the same ID (u1)
        UserEntity updatedUser = new UserEntity("u1", "updated@example.com", 3000L);

        // 2. Act
        userDao.insert(updatedUser); // OnConflictStrategy.REPLACE should trigger

        UserEntity retrievedById = userDao.getUserByEmail("user1@example.com");
        UserEntity retrievedByEmail = userDao.getUserByEmail("updated@example.com");

        // 3. Assert
        assertNull("Old email should no longer be findable", retrievedById);
        assertNotNull("New email should be findable", retrievedByEmail);
        assertEquals("u1", retrievedByEmail.getId()); // ID remains the same
        assertEquals(3000L, retrievedByEmail.getCreatedAt()); // Timestamp is updated
    }
}