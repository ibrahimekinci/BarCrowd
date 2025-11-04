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
import com.ibrahimekinci.barcrowd.data.local.OpeningHoursEmbedded;
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
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

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
    private VenueEntity venueEntity1;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        realDao = db.venueDao();
        repository = new VenueRepositoryImpl(db, mockFirestore);

        // Init test entity
        venueEntity1 = new VenueEntity();
        venueEntity1.setVenueId("v1");
        venueEntity1.setName("Test Bar 1");
        venueEntity1.setAddress("Address 1");
        venueEntity1.setLogoUrl("logo1.url");
        venueEntity1.setType("Bar");
        venueEntity1.setCreatedAt(new Date());
        venueEntity1.setShowOnHomePage(true);
        venueEntity1.setOpeningHours(new OpeningHoursEmbedded());
    }

    @After
    public void tearDown() throws IOException {
        db.close();
    }

    @Test
    public void testGetVenueById_ReadsFromRealDaoAndMapsToModel() {
        realDao.insertAll(Arrays.asList(venueEntity1));

        Venue result = repository.getVenueById("v1"); // This is a blocking call

        assertNotNull(result);
        assertEquals("v1", result.getVenueId());
        assertEquals("Test Bar 1", result.getName());
    }

    @Test
    public void testGetHomePageVenues_ReadsFromRealDaoAndTriggersSync() throws Exception {
        realDao.insertAll(Arrays.asList(venueEntity1));

        LiveData<List<Venue>> liveDataResult = repository.getHomePageVenues();

        List<Venue> resultList = getOrAwaitValue(liveDataResult);
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals("v1", resultList.get(0).getVenueId());

        verify(mockFirestore).listenForChanges(
                eq("Venues"),
                isNull(),
                isNull(),
                any(FirestoreWrapper.Listener.class)
        );
    }

    @Test
    public void testSyncVenues_OnFirestoreUpdate_WritesToRealDao() throws Exception {
        // This is the data we will "fake" coming from Firestore
        Venue firestoreVenue = new Venue();
        firestoreVenue.setVenueId("v_fs");
        firestoreVenue.setName("Firestore Venue");
        firestoreVenue.setAddress("Cloud Address");
        firestoreVenue.setType("Pub");
        firestoreVenue.setLogoUrl("logo.url");

        when(mockDocSnapshot.toObject(Venue.class)).thenReturn(firestoreVenue);
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocSnapshot));

        // Act
        repository.syncVenues();

        verify(mockFirestore).listenForChanges(
                eq("Venues"),
                isNull(),
                isNull(),
                firestoreListenerCaptor.capture()
        );

        // Simulate an update from Firestore
        firestoreListenerCaptor.getValue().onUpdate(mockQuerySnapshot);

        // Allow the background thread from insertVenues to run
        Thread.sleep(500);

        // Assert
        // Check the real database to see if the data was *actually* written
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