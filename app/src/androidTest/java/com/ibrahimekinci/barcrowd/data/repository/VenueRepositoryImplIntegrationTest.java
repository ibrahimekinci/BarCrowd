package com.ibrahimekinci.barcrowd.data.repository;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.VenueDao;
import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit; // <-- Import
import org.mockito.junit.MockitoRule; // <-- Import

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link VenueRepositoryImpl}.
 */
@RunWith(AndroidJUnit4.class)
public class VenueRepositoryImplIntegrationTest {

    // --- JUNIT RULES ---
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule(); // <-- Use Mockito's rule

    // --- Real Database Components ---
    private AppDatabase db;
    private VenueDao realDao;

    // --- Mocked External Dependencies ---
    @Mock
    private FirestoreWrapper mockFirestore;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private DocumentSnapshot mockDocSnapshot;

    @Captor
    private ArgumentCaptor<FirestoreWrapper.Listener<QuerySnapshot>> firestoreListenerCaptor;

    // --- Class Under Test ---
    private VenueRepositoryImpl repository;

    // Test data
    private VenueEntity venueEntity1 = new VenueEntity("v1", "Test Bar 1", "Address 1", 0.0, 0.0, "Desc1");
    private VenueEntity venueEntity2 = new VenueEntity("v2", "Test Cafe 2", "Address 2", 0.0, 0.0, "Desc2");

    @Before
    public void setUp() {
        // MockitoAnnotations.initMocks(this); // <-- We no longer need this

        // 1. Create real in-memory database
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        // 2. Get the real DAO from the real database
        realDao = db.venueDao();

        // 3. Create the repository with the REAL db and MOCKED firestore
        repository = new VenueRepositoryImpl(db, mockFirestore);
    }

    @After
    public void tearDown() throws IOException {
        // This should no longer crash as 'db' will be successfully initialized
        db.close();
    }

    @Test
    public void testGetVenueById_ReadsFromRealDaoAndMapsToModel() {
        // 1. Arrange
        realDao.insertAll(Arrays.asList(venueEntity1));

        // 2. Act
        Venue result = repository.getVenueById("v1");

        // 3. Assert
        assertNotNull(result);
        assertEquals("v1", result.getId());
        assertEquals("Test Bar 1", result.getName());
    }

    @Test
    public void testGetAllVenues_ReadsFromRealDaoAndTriggersSync() throws Exception {
        // 1. Arrange
        realDao.insertAll(Arrays.asList(venueEntity1, venueEntity2));

        // 2. Act
        LiveData<List<Venue>> liveDataResult = repository.getAllVenues();

        // 3. Assert
        List<Venue> resultList = getOrAwaitValue(liveDataResult);
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals("v1", resultList.get(0).getId());

        verify(mockFirestore).listenForChanges(
                eq("venues"),
                isNull(),
                isNull(),
                any(FirestoreWrapper.Listener.class)
        );
    }

    @Test
    public void testSyncVenues_OnFirestoreUpdate_WritesToRealDao() throws Exception {
        // 1. Arrange
        Venue firestoreVenue = new Venue("v_fs", "Firestore Venue", "Cloud Address", 1.0, 1.0, "FS Desc");
        when(mockDocSnapshot.toObject(Venue.class)).thenReturn(firestoreVenue);
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocSnapshot));

        // 2. Act
        repository.syncVenues();
        verify(mockFirestore).listenForChanges(
                eq("venues"),
                isNull(),
                isNull(),
                firestoreListenerCaptor.capture()
        );
        firestoreListenerCaptor.getValue().onUpdate(mockQuerySnapshot);

        // 3. Assert
        VenueEntity writtenEntity = realDao.getVenueById("v_fs");
        assertNotNull(writtenEntity);
        assertEquals("Firestore Venue", writtenEntity.getName());
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