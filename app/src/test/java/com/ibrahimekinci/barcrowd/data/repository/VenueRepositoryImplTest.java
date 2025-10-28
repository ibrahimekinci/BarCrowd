package com.ibrahimekinci.barcrowd.data.repository;

// import android.util.Log; // Artık buna gerek yok
import com.ibrahimekinci.barcrowd.util.AppLogger; // <-- Bunu import ediyoruz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.VenueDao;
import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.data.mapper.VenueMapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

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
 * Unit tests for {@link VenueRepositoryImpl}.
 * This test class verifies the repository's logic for data fetching,
 * caching, and synchronization between the local DAO and remote Firestore.
 */
@RunWith(MockitoJUnitRunner.class)
public class VenueRepositoryImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AppDatabase mockDb;
    @Mock
    private VenueDao mockVenueDao;
    @Mock
    private FirestoreWrapper mockFirestore;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private DocumentSnapshot mockDocSnapshot;

    @Captor
    private ArgumentCaptor<FirestoreWrapper.Listener<QuerySnapshot>> firestoreListenerCaptor;
    @Captor
    private ArgumentCaptor<List<VenueEntity>> venueEntityListCaptor;

    private VenueRepositoryImpl repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockDb.venueDao()).thenReturn(mockVenueDao);
        repository = new VenueRepositoryImpl(mockDb, mockFirestore);
    }

    @Test
    public void testGetVenueById_CallsDaoAndMapsToModel() {
        // 1. Arrange
        VenueEntity testEntity = new VenueEntity("v1", "Test Venue", "123 Main St", 0.0, 0.0, "Desc");
        when(mockVenueDao.getVenueById("v1")).thenReturn(testEntity);

        // 2. Act
        Venue result = repository.getVenueById("v1");

        // 3. Assert
        verify(mockVenueDao).getVenueById("v1");
        assertNotNull(result);
        assertEquals("v1", result.getId());
        assertEquals("Test Venue", result.getName());
    }

    @Test
    public void testInsertVenues_CallsDaoWithMappedEntities() {
        // Statik AppLogger sınıfını mock'luyoruz
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            Venue testModel = new Venue("v1", "Test Venue", "123 Main St", 0.0, 0.0, "Desc");
            List<Venue> models = Collections.singletonList(testModel);

            // 2. Act
            repository.insertVenues(models);

            // 3. Assert
            // DAO çağrısını doğrula
            verify(mockVenueDao).insertAll(venueEntityListCaptor.capture());

            // Listeyi kontrol et
            List<VenueEntity> capturedList = venueEntityListCaptor.getValue();
            assertNotNull(capturedList);
            assertEquals(1, capturedList.size());
            assertEquals("v1", capturedList.get(0).getId());

            // Log'un çağrıldığını doğrula (AppLogger üzerinden)
            mockedLogger.verify(() -> AppLogger.d("Inserted 1 venues locally"));
        }
    }

    @Test
    public void testGetAllVenues_TriggersSyncAndReturnsMappedLiveData() throws InterruptedException {
        // 1. Arrange
        VenueEntity testEntity = new VenueEntity("v1", "Test Venue", "123 Main St", 0.0, 0.0, "Desc");
        List<VenueEntity> entities = Collections.singletonList(testEntity);

        MutableLiveData<List<VenueEntity>> fakeDbData = new MutableLiveData<>();
        fakeDbData.setValue(entities);

        when(mockVenueDao.getAllVenues()).thenReturn(fakeDbData);

        // 2. Act
        LiveData<List<Venue>> resultLiveData = repository.getAllVenues();

        // 3. Assert
        // Sync'in tetiklendiğini doğrula
        verify(mockFirestore).listenForChanges(
                eq("venues"),
                isNull(),
                isNull(),
                any(FirestoreWrapper.Listener.class)
        );

        // DAO'nun çağrıldığını doğrula
        verify(mockVenueDao).getAllVenues();

        // LiveData sonucunu kontrol et
        List<Venue> resultModels = getOrAwaitValue(resultLiveData);
        assertNotNull(resultModels);
        assertEquals(1, resultModels.size());
        assertEquals("v1", resultModels.get(0).getId());
    }

    @Test
    public void testSyncVenues_OnFirestoreUpdate_InsertsVenuesToDao() {
        // Statik AppLogger sınıfını mock'luyoruz
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange (Firestore verisi için mock'lar)
            Venue firestoreVenue = new Venue("v2", "Firestore Venue", "456 Cloud St", 0.0, 0.0, "Desc");

            when(mockDocSnapshot.toObject(Venue.class)).thenReturn(firestoreVenue);
            when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocSnapshot));

            // 2. Act
            // Listener'ı kaydetmek için syncVenues() çağır
            repository.syncVenues();

            // Listener'ı yakala
            verify(mockFirestore).listenForChanges(
                    eq("venues"),
                    isNull(),
                    isNull(),
                    firestoreListenerCaptor.capture()
            );

            // Listener'ı manuel tetikle
            firestoreListenerCaptor.getValue().onUpdate(mockQuerySnapshot);

            // 3. Assert
            // DAO'ya eklemeyi kontrol et
            verify(mockVenueDao).insertAll(venueEntityListCaptor.capture());

            List<VenueEntity> capturedList = venueEntityListCaptor.getValue();

            assertNotNull(capturedList);
            assertEquals(1, capturedList.size());
            assertEquals("v2", capturedList.get(0).getId());

            // Log'ların çağrıldığını doğrula (AppLogger üzerinden)
            mockedLogger.verify(() -> AppLogger.d("Inserted 1 venues locally"));
            mockedLogger.verify(() -> AppLogger.i("Synced 1 venues from Firestore"));
        }
    }


    /**
     * LiveData'dan değer almak için yardımcı metod.
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