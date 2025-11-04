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
import java.util.Date;
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

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private LiveUpdateDao liveUpdateDao;

    // Test data
    private LiveUpdateEntity update1_v1; // Synced update for venue 1
    private LiveUpdateEntity update2_v1; // Pending (unsynced) update for venue 1
    private LiveUpdateEntity update3_v2; // Synced update for venue 2

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        liveUpdateDao = db.liveUpdateDao();

        // Initialize test data
        long now = System.currentTimeMillis();

        update1_v1 = new LiveUpdateEntity();
        update1_v1.setUpdateId("u1");
        update1_v1.setVenueId("v1");
        update1_v1.setUserId("user1");
        update1_v1.setCreatedAt(new Date(now - 2000));
        update1_v1.setCrowdLevel("Low");
        update1_v1.setWaitTime("0–5");
        update1_v1.setAgeRange("18–21");
        update1_v1.setSyncStatus(true); // Synced

        update2_v1 = new LiveUpdateEntity();
        update2_v1.setUpdateId("u2");
        update2_v1.setVenueId("v1");
        update2_v1.setUserId("user2");
        update2_v1.setCreatedAt(new Date(now - 1000)); // Newer
        update2_v1.setCrowdLevel("Medium");
        update2_v1.setWaitTime("5–15");
        update2_v1.setAgeRange("21–24");
        update2_v1.setSyncStatus(false); // Pending

        update3_v2 = new LiveUpdateEntity();
        update3_v2.setUpdateId("u3");
        update3_v2.setVenueId("v2");
        update3_v2.setUserId("user1");
        update3_v2.setCreatedAt(new Date(now));
        update3_v2.setCrowdLevel("High");
        update3_v2.setWaitTime("15–30");
        update3_v2.setAgeRange("25–30");
        update3_v2.setSyncStatus(true); // Synced
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsertAndGetUpdatesForVenue() throws Exception {
        liveUpdateDao.insert(update1_v1); // v1
        liveUpdateDao.insert(update2_v1); // v1
        liveUpdateDao.insert(update3_v2); // v2

        // Get all updates for "v1"
        List<LiveUpdateEntity> v1_updates = getOrAwaitValue(liveUpdateDao.getUpdatesForVenue("v1"));

        assertNotNull(v1_updates);
        assertEquals(2, v1_updates.size());
        // DAO query orders by createdAt DESC, so update2 (newer) is first
        assertEquals("u2", v1_updates.get(0).getUpdateId());
        assertEquals("u1", v1_updates.get(1).getUpdateId());
    }

    @Test
    public void testGetPendingUpdates() throws Exception {
        liveUpdateDao.insert(update1_v1); // Synced
        liveUpdateDao.insert(update2_v1); // Pending
        liveUpdateDao.insert(update3_v2); // Synced

        // Get all updates where syncStatus is false
        List<LiveUpdateEntity> pendingUpdates = liveUpdateDao.getPendingUpdates();

        assertNotNull(pendingUpdates);
        assertEquals(1, pendingUpdates.size());
        assertEquals("u2", pendingUpdates.get(0).getUpdateId());
    }

    @Test
    public void testMarkAsSynced() throws Exception {
        liveUpdateDao.insert(update2_v1); // Insert as pending (syncStatus = false)

        // Act
        liveUpdateDao.markAsSynced("u2");

        // Assert
        // Check that it no longer appears in pending updates
        List<LiveUpdateEntity> pendingUpdates = liveUpdateDao.getPendingUpdates();
        assertTrue("Pending updates list should be empty", pendingUpdates.isEmpty());
    }

    @Test
    public void testInsert_OnConflict_ReplacesExisting() throws Exception {
        liveUpdateDao.insert(update1_v1); // "Low" crowd level

        LiveUpdateEntity updated_u1 = new LiveUpdateEntity();
        updated_u1.setUpdateId("u1"); // Same ID
        updated_u1.setVenueId("v1");
        updated_u1.setUserId("user1");
        updated_u1.setCreatedAt(new Date(System.currentTimeMillis() + 1000)); // New time
        updated_u1.setCrowdLevel("High"); // New data
        updated_u1.setWaitTime("15–30"); // New data
        updated_u1.setAgeRange("25–30");
        updated_u1.setSyncStatus(true);

        // Act
        liveUpdateDao.insert(updated_u1); // OnConflictStrategy.REPLACE

        List<LiveUpdateEntity> v1_updates = getOrAwaitValue(liveUpdateDao.getUpdatesForVenue("v1"));

        // Assert
        assertNotNull(v1_updates);
        assertEquals(1, v1_updates.size()); // Should only be one entry
        assertEquals("u1", v1_updates.get(0).getUpdateId());
        assertEquals("High", v1_updates.get(0).getCrowdLevel()); // Data is updated
    }

    @Test
    public void testGetRecentLiveUpdates_ReturnsCorrectLimitAndOrder() throws Exception {
        // Insert 6 updates
        liveUpdateDao.insert(update1_v1); // createdAt = now - 2000
        liveUpdateDao.insert(update2_v1); // createdAt = now - 1000
        liveUpdateDao.insert(update3_v2); // createdAt = now
        // Create 3 more
        LiveUpdateEntity update4 = new LiveUpdateEntity(); update4.setUpdateId("u4"); update4.setCreatedAt(new Date(System.currentTimeMillis() + 1000)); update4.setVenueId("v1"); update4.setUserId("user1"); update4.setCrowdLevel("Low"); update4.setWaitTime("0–5"); update4.setAgeRange("18–21");
        LiveUpdateEntity update5 = new LiveUpdateEntity(); update5.setUpdateId("u5"); update5.setCreatedAt(new Date(System.currentTimeMillis() + 2000)); update5.setVenueId("v2"); update5.setUserId("user1"); update5.setCrowdLevel("Low"); update5.setWaitTime("0–5"); update5.setAgeRange("18–21");
        LiveUpdateEntity update6_deleted = new LiveUpdateEntity(); update6_deleted.setUpdateId("u6"); update6_deleted.setCreatedAt(new Date(System.currentTimeMillis() + 3000)); update6_deleted.setVenueId("v2"); update6_deleted.setUserId("user1"); update6_deleted.setCrowdLevel("Low"); update6_deleted.setWaitTime("0–5"); update6_deleted.setAgeRange("18–21");
        update6_deleted.setDeleted(true); // This one is deleted

        liveUpdateDao.insert(update4);
        liveUpdateDao.insert(update5);
        liveUpdateDao.insert(update6_deleted);

        // Act
        List<LiveUpdateEntity> recentUpdates = getOrAwaitValue(liveUpdateDao.getRecentLiveUpdates());

        // Assert
        assertNotNull(recentUpdates);
        assertEquals(5, recentUpdates.size()); // Should only return 5
        assertEquals("u5", recentUpdates.get(0).getUpdateId()); // newest
        assertEquals("u4", recentUpdates.get(1).getUpdateId());
        assertEquals("u3", recentUpdates.get(2).getUpdateId());
        assertEquals("u2", recentUpdates.get(3).getUpdateId());
        assertEquals("u1", recentUpdates.get(4).getUpdateId()); // oldest
        // u6 (deleted) should not be in the list
    }


    /**
     * Helper method to get the value from a LiveData object.
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);

        liveData.observeForever(o -> {
            data[0] = o;
            latch.countDown();
        });

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }

        //noinspection unchecked
        return (T) data[0];
    }
}