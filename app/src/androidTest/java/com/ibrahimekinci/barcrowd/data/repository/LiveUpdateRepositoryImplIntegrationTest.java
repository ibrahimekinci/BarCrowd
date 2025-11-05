package com.ibrahimekinci.barcrowd.data.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.Timestamp;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class LiveUpdateRepositoryImplIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private AppDatabase db;
    private LiveUpdateDao realDao;

    @Mock
    private FirestoreWrapper mockFirestore;
    @Mock
    private Application mockApp;
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

    private LiveUpdateRepositoryImpl repository;

    private LiveUpdate testUpdate;
    private LiveUpdateEntity testEntity;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        realDao = db.liveUpdateDao();
        repository = new LiveUpdateRepositoryImpl(db, mockFirestore, mockApp);

        // Setup test data
        testUpdate = new LiveUpdate();
        testUpdate.setUpdateId("update1");
        testUpdate.setVenueId("v1");
        testUpdate.setUserId("user1");
        testUpdate.setCrowdLevel("Medium");
        testUpdate.setWaitTime("5–15");
        testUpdate.setAgeRange("21–24");
        testUpdate.setCreatedAt(new Timestamp(new Date(System.currentTimeMillis())));

        testEntity = new LiveUpdateEntity();
        testEntity.setUpdateId("update1");
        testEntity.setVenueId("v1");
        testEntity.setUserId("user1");
        testEntity.setCrowdLevel("Medium");
        testEntity.setWaitTime("5–15");
        testEntity.setAgeRange("21–24");
        testEntity.setCreatedAt(new Date(System.currentTimeMillis()));
        testEntity.setSyncStatus(false);
    }

    @After
    public void tearDown() throws IOException {
        db.close();
    }

    @Test
    public void testPostUpdate_WritesToRealDao() throws Exception {
        // The static mock for ConnectivityUtil is no longer needed
        // because the production code was fixed.

        // Act
        repository.postUpdate(testUpdate);

        // Give the background thread time to run
        Thread.sleep(500);

        // Assert
        LiveUpdateEntity writtenEntity = getOrAwaitValue(realDao.getUpdatesForVenue("v1")).get(0);

        assertNotNull(writtenEntity);
        assertEquals("update1", writtenEntity.getUpdateId());
        assertEquals("Medium", writtenEntity.getCrowdLevel());
        assertFalse(writtenEntity.isSyncStatus()); // Should be pending
    }

    @Test
    public void testSyncPending_ReadsFromRealDaoAndWritesToFirestore() throws InterruptedException {
        realDao.insert(testEntity);

        doAnswer(invocation -> {
            FirestoreWrapper.Callback<Void> callback = invocation.getArgument(3);
            callback.onSuccess(null);
            return null;
        }).when(mockFirestore).setDocument(
                eq("LiveUpdates"),
                eq("update1"),
                liveUpdateCaptor.capture(),
                firestoreCallbackCaptor.capture()
        );

        repository.syncPending();

        Thread.sleep(500); // Allow background threads to complete

        verify(mockFirestore).setDocument(
                eq("LiveUpdates"),
                eq("update1"),
                any(LiveUpdate.class),
                any()
        );

        LiveUpdate capturedUpdate = liveUpdateCaptor.getValue();
        assertEquals("update1", capturedUpdate.getUpdateId());
        assertEquals("Medium", capturedUpdate.getCrowdLevel());

        List<LiveUpdateEntity> pendingUpdates = realDao.getPendingUpdates();
        assertTrue("Pending updates list should be empty", pendingUpdates.isEmpty());
    }

    @Test
    public void testSyncUpdates_OnFirestoreUpdate_WritesToRealDaoAsSynced() throws Exception {
        LiveUpdate firestoreUpdate = new LiveUpdate();
        firestoreUpdate.setUpdateId("fs-update");
        firestoreUpdate.setVenueId("v1");
        firestoreUpdate.setUserId("user2");
        firestoreUpdate.setCrowdLevel("High");
        firestoreUpdate.setWaitTime("15–30");
        firestoreUpdate.setAgeRange("25–35");

        when(mockDocSnapshot.toObject(LiveUpdate.class)).thenReturn(firestoreUpdate);
        when(mockDocChange.getType()).thenReturn(DocumentChange.Type.ADDED);
        when(mockDocChange.getDocument()).thenReturn(mockDocSnapshot);
        when(mockQuerySnapshot.getDocumentChanges()).thenReturn(Collections.singletonList(mockDocChange));

        repository.syncUpdates("v1");

        verify(mockFirestore).listenForChanges(
                eq("LiveUpdates"),
                eq("venueId"),
                eq("v1"),
                firestoreListenerCaptor.capture()
        );

        firestoreListenerCaptor.getValue().onUpdate(mockQuerySnapshot);

        Thread.sleep(500); // Allow background thread to run

        LiveUpdateEntity writtenEntity = getOrAwaitValue(realDao.getUpdatesForVenue("v1")).get(0);

        assertNotNull(writtenEntity);
        assertEquals("fs-update", writtenEntity.getUpdateId());
        assertEquals("High", writtenEntity.getCrowdLevel());
        assertTrue("Data from Firestore listener should be marked as synced", writtenEntity.isSyncStatus());
    }

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