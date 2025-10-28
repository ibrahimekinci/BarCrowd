package com.ibrahimekinci.barcrowd.data.local;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

// No domain models should be needed here, we are testing the database layer.

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for {@link VenueDao}.
 * This test runs on an Android device or emulator and uses a real,
 * in-memory Room database to verify database operations.
 */
@RunWith(AndroidJUnit4.class)
public class VenueDaoTest {

    // This rule is REQUIRED to test LiveData synchronously.
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private VenueDao venueDao;

    // Test data
    private VenueEntity venue1 = new VenueEntity("v1", "Test Bar 1", "Address 1", 0.0, 0.0, "Desc1");
    private VenueEntity venue2 = new VenueEntity("v2", "Test Cafe 2", "Address 2", 0.0, 0.0, "Desc2");
    private VenueEntity venue3 = new VenueEntity("v3", "Demo Bar 3", "Address 3", 0.0, 0.0, "Desc3");

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Instead of creating a real database file,
        // we create an in-memory database in RAM for testing.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                // Allow queries on the main thread (only for tests)
                .allowMainThreadQueries()
                .build();
        venueDao = db.venueDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsertAndGetById() throws Exception {
        // 1. Arrange
        venueDao.insertAll(Arrays.asList(venue1));

        // 2. Act
        VenueEntity retrieved = venueDao.getVenueById("v1");

        // 3. Assert
        assertNotNull(retrieved);
        assertEquals(venue1.getId(), retrieved.getId());
        assertEquals(venue1.getName(), retrieved.getName());
    }

    @Test
    public void testInsertAllAndGetAllVenues() throws Exception {
        // 1. Arrange
        List<VenueEntity> allVenues = Arrays.asList(venue1, venue2, venue3);
        venueDao.insertAll(allVenues);

        // 2. Act
        // Use 'getOrAwaitValue' helper to get the value from LiveData
        List<VenueEntity> retrievedList = getOrAwaitValue(venueDao.getAllVenues());

        // 3. Assert
        assertNotNull(retrievedList);
        assertEquals(3, retrievedList.size());
        assertEquals("Test Bar 1", retrievedList.get(0).getName());
        assertEquals("Test Cafe 2", retrievedList.get(1).getName());
        assertEquals("Demo Bar 3", retrievedList.get(2).getName());
    }

    @Test
    public void testSearchVenues() throws Exception {
        // 1. Arrange
        List<VenueEntity> allVenues = Arrays.asList(venue1, venue2, venue3);
        venueDao.insertAll(allVenues);

        // 2. Act
        // Search for venues containing 'Bar' (v1 and v3)
        List<VenueEntity> searchResult = getOrAwaitValue(venueDao.searchVenues("%Bar%"));

        // 3. Assert
        assertNotNull(searchResult);
        assertEquals(2, searchResult.size());
        assertEquals("v1", searchResult.get(0).getId()); // Test Bar 1
        assertEquals("v3", searchResult.get(1).getId()); // Demo Bar 3
    }

    @Test
    public void testInsertAll_OnConflict_ReplacesExisting() throws Exception {
        // 1. Arrange
        venueDao.insertAll(Arrays.asList(venue1)); // "Test Bar 1"

        // A new entity with the same ID (v1) but a different name
        VenueEntity updatedVenue1 = new VenueEntity("v1", "Updated Name", "Address 1", 0.0, 0.0, "Desc1");

        // 2. Act
        venueDao.insertAll(Arrays.asList(updatedVenue1)); // OnConflictStrategy.REPLACE should work

        List<VenueEntity> retrievedList = getOrAwaitValue(venueDao.getAllVenues());

        // 3. Assert
        assertEquals(1, retrievedList.size()); // Should only be 1 record
        assertEquals("Updated Name", retrievedList.get(0).getName()); // Name should be updated
    }


    /**
     * Helper method to get the value from a LiveData object.
     * (Same method we used in unit tests)
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);

        liveData.observeForever(o -> {
            data[0] = o;
            latch.countDown();
        });

        // Don't wait forever
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }

        //noinspection unchecked
        return (T) data[0];
    }
}