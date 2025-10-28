package com.ibrahimekinci.barcrowd.data.local;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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
 * Integration tests for {@link LiveUpdateDao}.
 * This test uses a real, in-memory Room database to verify
 * LiveUpdate entity operations and queries.
 */
@RunWith(AndroidJUnit4.class)
public class LiveUpdateDaoTest {

    // This rule is REQUIRED to test LiveData synchronously.
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private LiveUpdateDao liveUpdateDao;

    // Test data
    // Synced update for venue 1
    private LiveUpdateEntity update1_v1 = new LiveUpdateEntity(
            "u1", "v1", "user1", 1000L, "low", 5, "18-25",
            null, null, 0.0, 0.0, true); // true = synced

    // Pending (unsynced) update for venue 1
    private LiveUpdateEntity update2_v1 = new LiveUpdateEntity(
            "u2", "v1", "user2", 2000L, "medium", 15, "25-35",
            null, null, 0.0, 0.0, false); // false = pending

    // Synced update for venue 2
    private LiveUpdateEntity update3_v2 = new LiveUpdateEntity(
            "u3", "v2", "user1", 3000L, "high", 30, "35+",
            null, null, 0.0, 0.0, true); // true = synced


    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Create an in-memory database for testing
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        liveUpdateDao = db.liveUpdateDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsertAndGetUpdatesForVenue() throws Exception {
        // 1. Arrange
        liveUpdateDao.insert(update1_v1); // v1
        liveUpdateDao.insert(update2_v1); // v1
        liveUpdateDao.insert(update3_v2); // v2

        // 2. Act
        // Get all updates for "v1"
        List<LiveUpdateEntity> v1_updates = getOrAwaitValue(liveUpdateDao.getUpdatesForVenue("v1"));

        // 3. Assert
        assertNotNull(v1_updates);
        assertEquals(2, v1_updates.size());
        // DAO query orders by timestamp DESC, so update2 (2000L) should be first
        assertEquals("u2", v1_updates.get(0).getId()); // pending update
        assertEquals("u1", v1_updates.get(1).getId()); // synced update
    }

    @Test
    public void testGetPendingUpdates() throws Exception {
        // 1. Arrange
        liveUpdateDao.insert(update1_v1); // Synced
        liveUpdateDao.insert(update2_v1); // Pending
        liveUpdateDao.insert(update3_v2); // Synced

        // 2. Act
        // Get all updates where isSyncStatus is false
        List<LiveUpdateEntity> pendingUpdates = liveUpdateDao.getPendingUpdates();

        // 3. Assert
        assertNotNull(pendingUpdates);
        assertEquals(1, pendingUpdates.size());
        assertEquals("u2", pendingUpdates.get(0).getId());
        assertEquals("v1", pendingUpdates.get(0).getVenueId());
    }

    @Test
    public void testMarkAsSynced() throws Exception {
        // 1. Arrange
        liveUpdateDao.insert(update2_v1); // Insert as pending (syncStatus = false)

        // 2. Act
        liveUpdateDao.markAsSynced("u2"); // Mark it as synced

        // 3. Assert
        // Check that it no longer appears in pending updates
        List<LiveUpdateEntity> pendingUpdates = liveUpdateDao.getPendingUpdates();
        assertTrue("Pending updates list should be empty", pendingUpdates.isEmpty());

        // (Optional) Check that the item itself now has syncStatus = true
        // Note: This requires adding a "getById" method to your DAO
        // If you add: @Query("SELECT * FROM live_updates WHERE id = :id")
        // LiveUpdateEntity getById(String id);
        // Then you could uncomment this:
        // LiveUpdateEntity retrieved = liveUpdateDao.getById("u2");
        // assertNotNull(retrieved);
        // assertTrue(retrieved.isSyncStatus());
    }

    @Test
    public void testInsert_OnConflict_ReplacesExisting() throws Exception {
        // 1. Arrange
        liveUpdateDao.insert(update1_v1); // "low" crowd level

        LiveUpdateEntity updated_u1 = new LiveUpdateEntity(
                "u1", "v1", "user1", 4000L, "high", 45, "18-25",
                null, null, 0.0, 0.0, true); // Same ID (u1)

        // 2. Act
        liveUpdateDao.insert(updated_u1); // OnConflictStrategy.REPLACE

        List<LiveUpdateEntity> v1_updates = getOrAwaitValue(liveUpdateDao.getUpdatesForVenue("v1"));

        // 3. Assert
        assertNotNull(v1_updates);
        assertEquals(1, v1_updates.size()); // Should only be one entry
        assertEquals("u1", v1_updates.get(0).getId());
        assertEquals("high", v1_updates.get(0).getCrowdLevel()); // Data is updated
        assertEquals(4000L, v1_updates.get(0).getTimestamp()); // Timestamp is updated
    }


    /**
     * Helper method to get the value from a LiveData object.
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);

        // --- THIS LINE IS NOW CORRECTED ---
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