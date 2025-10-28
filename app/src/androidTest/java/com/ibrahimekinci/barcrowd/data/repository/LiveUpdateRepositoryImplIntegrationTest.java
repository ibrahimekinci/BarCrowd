package com.ibrahimekinci.barcrowd.data.repository;

import android.app.Application;
import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateDao;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link LiveUpdateRepositoryImpl}.
 * This test uses a real in-memory Room database and MOCKED external dependencies
 * (FirestoreWrapper, Application).
 */
@RunWith(AndroidJUnit4.class)
public class LiveUpdateRepositoryImplIntegrationTest {

    // --- JUNIT RULES ---
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    // --- Real Database Components ---
    private AppDatabase db;
    private LiveUpdateDao realDao; // We will use the real DAO

    // --- Mocked External Dependencies ---
    @Mock
    private FirestoreWrapper mockFirestore;
    @Mock
    private Application mockApp; // Required by repository constructor
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private QueryDocumentSnapshot mockDocSnapshot;
    @Mock
    private DocumentChange mockDocChange;

    @Captor
    private ArgumentCaptor<FirestoreWrapper.Callback<Void>> firestoreCallbackCaptor;
    @Captor
    private ArgumentCaptor<FirestoreWrapper.Listener<QuerySnapshot>> firestoreListenerCaptor;
    @Captor
    private ArgumentCaptor<LiveUpdate> liveUpdateCaptor;

    // --- Class Under Test ---
    private LiveUpdateRepositoryImpl repository;

    // Test data
    private LiveUpdate testUpdate;
    private LiveUpdateEntity testEntity;

    @Before
    public void setUp() {
        // 1. Create real in-memory database
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        // 2. Get the real DAO from the real database
        realDao = db.liveUpdateDao();

        // 3. Create the repository with the REAL db and MOCKED wrappers
        repository = new LiveUpdateRepositoryImpl(db, mockFirestore, mockApp);

        // 4. Setup common test data
        testUpdate = new LiveUpdate("update1", "v1", "user1", 123L, "medium", 10, "20-30", null, null, 0.0, 0.0);
        testEntity = new LiveUpdateEntity("update1", "v1", "user1", 123L, "medium", 10, "20-30", null, null, 0.0, 0.0, false);
    }

    @After
    public void tearDown() throws IOException {
        db.close();
    }

    @Test
    public void testPostUpdate_WritesToRealDao() throws Exception { // <-- THIS IS THE FIX
        // 1. Arrange
        // (We will not test the static ConnectivityUtil call in this integration test)

        // 2. Act
        repository.postUpdate(testUpdate);

        // 3. Assert
        // Check that the data was *actually* written to the real database
        LiveUpdateEntity writtenEntity = getOrAwaitValue(realDao.getUpdatesForVenue("v1")).get(0);

        assertNotNull(writtenEntity);
        assertEquals("update1", writtenEntity.getId());
        assertEquals("medium", writtenEntity.getCrowdLevel());
        assertEquals(false, writtenEntity.isSyncStatus()); // Should be pending
    }

    @Test
    public void testSyncPending_ReadsFromRealDaoAndWritesToFirestore() {
        // 1. Arrange
        // Insert a pending update directly into the real DAO
        realDao.insert(testEntity); // syncStatus = false

        // Mock the Firestore "onSuccess" callback
        doAnswer(invocation -> {
            FirestoreWrapper.Callback<Void> callback = invocation.getArgument(3);
            callback.onSuccess(null);
            return null;
        }).when(mockFirestore).setDocument(
                eq("live_updates"),
                eq("update1"),
                liveUpdateCaptor.capture(),
                firestoreCallbackCaptor.capture()
        );

        // 2. Act
        repository.syncPending();

        // 3. Assert
        // Verify Firestore was called with the correct data
        verify(mockFirestore).setDocument(
                eq("live_updates"),
                eq("update1"),
                any(LiveUpdate.class),
                any()
        );

        // Verify the data sent to Firestore was mapped correctly from the entity
        LiveUpdate capturedUpdate = liveUpdateCaptor.getValue();
        assertEquals("update1", capturedUpdate.getId());
        assertEquals("medium", capturedUpdate.getCrowdLevel());

        // Verify the DAO was updated to mark it as synced
        List<LiveUpdateEntity> pendingUpdates = realDao.getPendingUpdates();
        assertTrue("Pending updates list should be empty", pendingUpdates.isEmpty());
    }

    @Test
    public void testGetUpdatesForVenue_ReadsFromRealDaoAndMapsLiveData() throws Exception {
        // 1. Arrange
        // Insert data directly into the real DAO
        realDao.insert(testEntity);

        // 2. Act
        LiveData<List<LiveUpdate>> liveDataResult = repository.getUpdatesForVenue("v1");

        // 3. Assert
        // Verify the LiveData from the real DAO is correctly mapped to the domain model
        List<LiveUpdate> resultList = getOrAwaitValue(liveDataResult);
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals("update1", resultList.get(0).getId());
        assertEquals("medium", resultList.get(0).getCrowdLevel());
    }

    @Test
    public void testSyncUpdates_OnFirestoreUpdate_WritesToRealDaoAsSynced() throws Exception {
        // 1. Arrange
        // This is the data we will "fake" coming from Firestore
        LiveUpdate firestoreUpdate = new LiveUpdate("fs-update", "v1", "user2", 456L, "high", 30, "25-35", null, null, 0.0, 0.0);

        // Setup mock Firestore response
        when(mockDocSnapshot.toObject(LiveUpdate.class)).thenReturn(firestoreUpdate);
        when(mockDocChange.getType()).thenReturn(DocumentChange.Type.ADDED);
        when(mockDocChange.getDocument()).thenReturn(mockDocSnapshot);
        when(mockQuerySnapshot.getDocumentChanges()).thenReturn(Collections.singletonList(mockDocChange));

        // 2. Act
        // Call syncUpdates() to register the listener
        repository.syncUpdates("v1");

        // Capture the listener
        verify(mockFirestore).listenForChanges(
                eq("live_updates"),
                eq("venueId"),
                eq("v1"),
                firestoreListenerCaptor.capture()
        );

        // Manually trigger the listener, simulating an update from Firestore
        firestoreListenerCaptor.getValue().onUpdate(mockQuerySnapshot);

        // 3. Assert
        // Check the real database to see if the data was *actually* written
        LiveUpdateEntity writtenEntity = getOrAwaitValue(realDao.getUpdatesForVenue("v1")).get(0);

        assertNotNull(writtenEntity);
        assertEquals("fs-update", writtenEntity.getId());
        assertEquals("high", writtenEntity.getCrowdLevel());
        assertTrue("Data from Firestore listener should be marked as synced", writtenEntity.isSyncStatus());
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

        // Don't wait forever
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }

        //noinspection unchecked
        return (T) data[0];
    }
}