package com.ibrahimekinci.barcrowd.data.repository;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateDao;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.ConnectivityUtil;

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
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link LiveUpdateRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LiveUpdateRepositoryImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // Bağımlılıklar için Mock'lar
    @Mock
    private AppDatabase mockDb;
    @Mock
    private LiveUpdateDao mockDao;
    @Mock
    private FirestoreWrapper mockFirestore;
    @Mock
    private Application mockApp; // ConnectivityUtil için

    // Veri objeleri için Mock'lar
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private QueryDocumentSnapshot mockDocSnapshot;
    @Mock
    private DocumentChange mockDocChange;
    @Mock
    private UUID mockUuid;

    // Captor'lar
    @Captor
    private ArgumentCaptor<LiveUpdateEntity> entityCaptor;
    @Captor
    private ArgumentCaptor<FirestoreWrapper.Callback<Void>> firestoreCallbackCaptor;
    @Captor
    private ArgumentCaptor<FirestoreWrapper.Listener<QuerySnapshot>> firestoreListenerCaptor;

    // Test edilecek sınıf
    private LiveUpdateRepositoryImpl repository;

    // Test verisi
    private LiveUpdate testUpdate;
    private LiveUpdateEntity testEntity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockDb.liveUpdateDao()).thenReturn(mockDao);
        repository = new LiveUpdateRepositoryImpl(mockDb, mockFirestore, mockApp);

        // Standart test verisi
        testUpdate = new LiveUpdate("update1", "venue1", "user1", 123L, "medium", 10, "20-30", null, null, 0.0, 0.0);
        testEntity = new LiveUpdateEntity("update1", "venue1", "user1", 123L, "medium", 10, "20-30", null, null, 0.0, 0.0, false);
    }

    @Test
    public void testPostUpdate_WhenOnline_InsertsLocalAndSyncs() {
        // AppLogger, ConnectivityUtil statik mock'larını try-with-resources ile yönet
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class);
             MockedStatic<ConnectivityUtil> mockedConn = Mockito.mockStatic(ConnectivityUtil.class)) {

            // 1. Arrange
            // Cihaz "online" olsun
            mockedConn.when(() -> ConnectivityUtil.isOnline(mockApp)).thenReturn(true);

            // syncPending'in çalışması için bekleyen bir güncelleme döndür
            when(mockDao.getPendingUpdates()).thenReturn(Collections.singletonList(testEntity));

            // Firestore'a setDocument çağrıldığında başarılı olsun
            doAnswer(invocation -> {
                FirestoreWrapper.Callback<Void> callback = invocation.getArgument(3);
                callback.onSuccess(null);
                return null;
            }).when(mockFirestore).setDocument(eq("live_updates"), eq(testUpdate.getId()), any(LiveUpdate.class), any());

            // 2. Act
            repository.postUpdate(testUpdate);

            // 3. Assert
            // DAO'ya insert çağrılmalı ve syncStatus 'false' olmalı
            verify(mockDao).insert(entityCaptor.capture());
            assertFalse(entityCaptor.getValue().isSyncStatus());
            assertEquals(testUpdate.getId(), entityCaptor.getValue().getId());

            // Log'un çağrıldığını doğrula
            mockedLogger.verify(() -> AppLogger.d("Inserted local update: update1"));

            // Cihaz online olduğu için syncPending tetiklenmeli
            mockedConn.verify(() -> ConnectivityUtil.isOnline(mockApp));
            verify(mockDao).getPendingUpdates();
            verify(mockFirestore).setDocument(eq("live_updates"), eq(testUpdate.getId()), any(LiveUpdate.class), any());

            // Sync başarılı olduğu için 'markAsSynced' çağrılmalı
            verify(mockDao).markAsSynced(testUpdate.getId());
            mockedLogger.verify(() -> AppLogger.i("Synced update: update1"));
        }
    }

    @Test
    public void testPostUpdate_WhenOffline_InsertsLocalOnly() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class);
             MockedStatic<ConnectivityUtil> mockedConn = Mockito.mockStatic(ConnectivityUtil.class)) {

            // 1. Arrange
            // Cihaz "offline" olsun
            mockedConn.when(() -> ConnectivityUtil.isOnline(mockApp)).thenReturn(false);

            // 2. Act
            repository.postUpdate(testUpdate);

            // 3. Assert
            // DAO'ya insert çağrılmalı
            verify(mockDao).insert(entityCaptor.capture());
            assertFalse(entityCaptor.getValue().isSyncStatus());

            // Log'un çağrıldığını doğrula
            mockedLogger.verify(() -> AppLogger.d("Inserted local update: update1"));

            // Cihaz offline olduğu için syncPending tetiklenmemeli
            mockedConn.verify(() -> ConnectivityUtil.isOnline(mockApp));
            verify(mockDao, never()).getPendingUpdates();
            verify(mockFirestore, never()).setDocument(anyString(), anyString(), any(), any());
            verify(mockDao, never()).markAsSynced(anyString());
        }
    }

    @Test
    public void testPostUpdate_WithNullId_GeneratesUuid() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class);
             MockedStatic<ConnectivityUtil> mockedConn = Mockito.mockStatic(ConnectivityUtil.class);
             MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {

            // 1. Arrange
            mockedConn.when(() -> ConnectivityUtil.isOnline(mockApp)).thenReturn(false); // Sync'i tetiklemesin
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);
            when(mockUuid.toString()).thenReturn("test-uuid");

            LiveUpdate updateWithNullId = new LiveUpdate(null, "venue1", "user1", 123L, "medium", 10, "20-30", null, null, 0.0, 0.0);

            // 2. Act
            repository.postUpdate(updateWithNullId);

            // 3. Assert
            // DAO'ya insert çağrılırken ID'nin "test-uuid" olarak set edilmiş olması gerekir
            verify(mockDao).insert(entityCaptor.capture());
            assertEquals("test-uuid", entityCaptor.getValue().getId());
            mockedLogger.verify(() -> AppLogger.d("Inserted local update: test-uuid"));
        }
    }

    @Test
    public void testSyncPending_OnSuccess_MarksAsSynced() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            when(mockDao.getPendingUpdates()).thenReturn(Collections.singletonList(testEntity));

            // Firestore setDocument çağrıldığında 'onSuccess' tetikle
            doAnswer(invocation -> {
                FirestoreWrapper.Callback<Void> callback = invocation.getArgument(3);
                callback.onSuccess(null);
                return null;
            }).when(mockFirestore).setDocument(eq("live_updates"), eq(testEntity.getId()), any(LiveUpdate.class), firestoreCallbackCaptor.capture());

            // 2. Act
            repository.syncPending();

            // 3. Assert
            verify(mockDao).getPendingUpdates();
            verify(mockFirestore).setDocument(eq("live_updates"), eq(testEntity.getId()), any(LiveUpdate.class), any());

            // Başarı durumunda DAO güncellenmeli
            verify(mockDao).markAsSynced(testEntity.getId());
            mockedLogger.verify(() -> AppLogger.i("Synced update: " + testEntity.getId()));
        }
    }

    @Test
    public void testSyncPending_OnFailure_DoesNotMarkAsSynced() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            Exception testException = new Exception("Firestore failed");
            when(mockDao.getPendingUpdates()).thenReturn(Collections.singletonList(testEntity));

            // Firestore setDocument çağrıldığında 'onFailure' tetikle
            doAnswer(invocation -> {
                FirestoreWrapper.Callback<Void> callback = invocation.getArgument(3);
                callback.onFailure(testException);
                return null;
            }).when(mockFirestore).setDocument(eq("live_updates"), eq(testEntity.getId()), any(LiveUpdate.class), firestoreCallbackCaptor.capture());

            // 2. Act
            repository.syncPending();

            // 3. Assert
            verify(mockDao).getPendingUpdates();
            verify(mockFirestore).setDocument(eq("live_updates"), eq(testEntity.getId()), any(LiveUpdate.class), any());

            // Hata durumunda DAO güncellenmemeli
            verify(mockDao, never()).markAsSynced(anyString());
            mockedLogger.verify(() -> AppLogger.e("Failed to sync update: " + testEntity.getId(), testException));
        }
    }

    @Test
    public void testGetUpdatesForVenue_TriggersSyncAndReturnsMappedData() throws InterruptedException {
        // 1. Arrange
        MutableLiveData<List<LiveUpdateEntity>> fakeDbData = new MutableLiveData<>();
        fakeDbData.setValue(Collections.singletonList(testEntity));
        when(mockDao.getUpdatesForVenue("venue1")).thenReturn(fakeDbData);

        // 2. Act
        LiveData<List<LiveUpdate>> resultLiveData = repository.getUpdatesForVenue("venue1");

        // 3. Assert
        // syncUpdates'in tetiklendiğini (yani listener'ın kaydedildiğini) doğrula
        verify(mockFirestore).listenForChanges(eq("live_updates"), eq("venueId"), eq("venue1"), any());

        // DAO'dan LiveData'nın alındığını doğrula
        verify(mockDao).getUpdatesForVenue("venue1");

        // Dönen LiveData'nın doğru veriyi içerdiğini doğrula (Mapper'ı da test eder)
        List<LiveUpdate> resultModels = getOrAwaitValue(resultLiveData);
        assertNotNull(resultModels);
        assertEquals(1, resultModels.size());
        assertEquals(testEntity.getId(), resultModels.get(0).getId());
        assertEquals(testEntity.getCrowdLevel(), resultModels.get(0).getCrowdLevel());
    }

    @Test
    public void testSyncUpdates_OnFirestoreUpdate_InsertsToDaoAsSynced() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            LiveUpdate firestoreUpdate = new LiveUpdate("fs-update", "venue1", "user2", 456L, "high", 30, "25-35", null, null, 0.0, 0.0);

            // Firestore snapshot zincirini mock'la
            when(mockDocSnapshot.toObject(LiveUpdate.class)).thenReturn(firestoreUpdate);
            when(mockDocChange.getType()).thenReturn(DocumentChange.Type.ADDED);
            when(mockDocChange.getDocument()).thenReturn(mockDocSnapshot);
            when(mockQuerySnapshot.getDocumentChanges()).thenReturn(Collections.singletonList(mockDocChange));

            // 2. Act
            repository.syncUpdates("venue1"); // Listener'ı kaydeder

            // Listener'ı yakala
            verify(mockFirestore).listenForChanges(
                    eq("live_updates"),
                    eq("venueId"),
                    eq("venue1"),
                    firestoreListenerCaptor.capture()
            );

            // Listener'ı manuel tetikle
            firestoreListenerCaptor.getValue().onUpdate(mockQuerySnapshot);

            // 3. Assert
            // Firestore'dan gelen verinin DAO'ya "synced=true" olarak eklendiğini doğrula
            verify(mockDao).insert(entityCaptor.capture());
            LiveUpdateEntity capturedEntity = entityCaptor.getValue();

            assertEquals("fs-update", capturedEntity.getId());
            assertTrue(capturedEntity.isSyncStatus()); // En önemli kontrol
            mockedLogger.verify(() -> AppLogger.d("Synced remote update for venue: venue1, id: fs-update"));
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