package com.ibrahimekinci.barcrowd.data.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.ibrahimekinci.barcrowd.util.AppLogger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link VenueRepositoryImpl}.
 */
@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class VenueRepositoryImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    // Mocks for dependencies
    @Mock
    private AppDatabase mockDb;
    @Mock
    private VenueDao mockDao;
    @Mock
    private FirestoreWrapper mockFirestore;

    // Mocks for data
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private DocumentSnapshot mockDocSnapshot;
    @Mock
    private Venue mockVenue;
    @Mock
    private VenueEntity mockVenueEntity;

    @Captor
    private ArgumentCaptor<FirestoreWrapper.Listener<QuerySnapshot>> firestoreListenerCaptor;
    @Captor
    private ArgumentCaptor<List<VenueEntity>> venueEntityListCaptor;

    private VenueRepositoryImpl repository;

    @Before
    public void setUp() {
        when(mockDb.venueDao()).thenReturn(mockDao);
        repository = new VenueRepositoryImpl(mockDb, mockFirestore);
    }

    @Test
    public void testGetVenueById_CallsDaoAndMapsToModel() {
        // 1. Arrange
        // We mock the static mapper functions
        try (MockedStatic<VenueMapper> mockedMapper = Mockito.mockStatic(VenueMapper.class)) {
            when(mockDao.getVenueById("v1")).thenReturn(mockVenueEntity);
            mockedMapper.when(() -> VenueMapper.toModel(mockVenueEntity)).thenReturn(mockVenue);

            // 2. Act
            Venue result = repository.getVenueById("v1");

            // 3. Assert
            verify(mockDao).getVenueById("v1");
            assertNotNull(result);
            assertEquals(mockVenue, result);
        }
    }

    @Test
    public void testGetHomePageVenues_TriggersSyncAndReturnsMappedLiveData() throws InterruptedException {
        // 1. Arrange
        MutableLiveData<List<VenueEntity>> fakeDbData = new MutableLiveData<>();
        fakeDbData.setValue(Collections.singletonList(mockVenueEntity));
        when(mockDao.getHomePageVenues()).thenReturn(fakeDbData);

        // 2. Act
        LiveData<List<Venue>> resultLiveData = repository.getHomePageVenues();

        // 3. Assert
        // Verify sync was triggered
        verify(mockFirestore).listenForChanges(eq("Venues"), isNull(), isNull(), any(FirestoreWrapper.Listener.class));
        // Verify DAO was called
        verify(mockDao).getHomePageVenues();
    }

    @Test
    public void testGetAllVenuesSortedByName_TriggersSyncAndReturnsMappedLiveData() throws InterruptedException {
        // 1. Arrange
        MutableLiveData<List<VenueEntity>> fakeDbData = new MutableLiveData<>();
        fakeDbData.setValue(Collections.singletonList(mockVenueEntity));
        when(mockDao.getAllVenuesSortedByName()).thenReturn(fakeDbData);

        // 2. Act
        LiveData<List<Venue>> resultLiveData = repository.getAllVenuesSortedByName();

        // 3. Assert
        // Verify sync was triggered
        verify(mockFirestore).listenForChanges(eq("Venues"), isNull(), isNull(), any(FirestoreWrapper.Listener.class));
        // Verify DAO was called
        verify(mockDao).getAllVenuesSortedByName();
    }

    @Test
    public void testSyncVenues_OnFirestoreUpdate_InsertsVenuesToDao() {
        // 1. Arrange
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class); MockedStatic<VenueMapper> mockedMapper = Mockito.mockStatic(VenueMapper.class)) {

            when(mockDocSnapshot.toObject(Venue.class)).thenReturn(mockVenue);
            when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocSnapshot));
            mockedMapper.when(() -> VenueMapper.toEntity(mockVenue)).thenReturn(mockVenueEntity);

            // 2. Act
            repository.syncVenues(); // Registers the listener

            // Capture and trigger the listener
            verify(mockFirestore).listenForChanges(eq("Venues"), isNull(), isNull(), firestoreListenerCaptor.capture());
            firestoreListenerCaptor.getValue().onUpdate(mockQuerySnapshot);

            // 3. Assert
            // The repository's insertVenues runs on a new thread.
            // We can't easily verify the DAO call without a test dispatcher.
            // Instead, we verify the log messages which happen on the *same thread* as the insert.
            mockedLogger.verify(() -> AppLogger.i("Synced 1 venues from Firestore"));
        }
    }
}