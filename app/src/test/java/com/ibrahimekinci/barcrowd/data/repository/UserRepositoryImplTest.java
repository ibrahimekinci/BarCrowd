package com.ibrahimekinci.barcrowd.data.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.UserDao;
import com.ibrahimekinci.barcrowd.data.local.UserEntity;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.User;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // Bağımlılıklar için Mock'lar
    @Mock
    private AppDatabase mockDb;
    @Mock
    private UserDao mockUserDao;
    @Mock
    private FirestoreWrapper mockFirestore;
    @Mock
    private FirebaseAuthWrapper mockAuthWrapper;

    // Veri objeleri için Mock'lar
    @Mock
    private FirebaseUser mockFirebaseUser;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private QueryDocumentSnapshot mockDocSnapshot;
    @Mock
    private DocumentChange mockDocChange;

    // Callback'leri ve argümanları yakalamak için Captor'lar
    @Captor
    private ArgumentCaptor<FirebaseAuthWrapper.AuthCallback> authCallbackCaptor;
    @Captor
    private ArgumentCaptor<FirestoreWrapper.Listener<QuerySnapshot>> firestoreListenerCaptor;
    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    // Test edilecek sınıf
    private UserRepositoryImpl repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Depo, veritabanından DAO'yu istediğinde, mock DAO'muzu döndür
        when(mockDb.userDao()).thenReturn(mockUserDao);
        repository = new UserRepositoryImpl(mockDb, mockFirestore, mockAuthWrapper);
    }

    @Test
    public void testInsertUser_CallsDaoWithMappedEntity() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            User testUser = new User("uid1", "test@example.com", 123L);

            // 2. Act
            repository.insertUser(testUser);

            // 3. Assert
            // DAO'ya doğru entity'nin gönderildiğini doğrula
            verify(mockUserDao).insert(userEntityCaptor.capture());
            UserEntity capturedEntity = userEntityCaptor.getValue();

            assertNotNull(capturedEntity);
            assertEquals("uid1", capturedEntity.getId());
            assertEquals("test@example.com", capturedEntity.getEmail());

            // Log'un çağrıldığını doğrula
            mockedLogger.verify(() -> AppLogger.d("Local user cached: uid1"));
        }
    }

    @Test
    public void testGetUserByEmail_CallsDaoAndMapsToModel() {
        // 1. Arrange
        UserEntity testEntity = new UserEntity("uid1", "test@example.com", 123L);
        when(mockUserDao.getUserByEmail("test@example.com")).thenReturn(testEntity);

        // 2. Act
        User result = repository.getUserByEmail("test@example.com");

        // 3. Assert
        verify(mockUserDao).getUserByEmail("test@example.com");
        assertNotNull(result);
        assertEquals("uid1", result.getId());
    }

    @Test
    public void testGetCurrentUser_WhenAuthUserExists_ReturnsCachedUser() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            // Firebase kullanıcısını ayarla
            when(mockAuthWrapper.getCurrentUser()).thenReturn(mockFirebaseUser);
            when(mockFirebaseUser.getEmail()).thenReturn("cached@example.com");
            // --- when(mockFirebaseUser.getUid()).thenReturn("uid-cached"); --- // <-- REMOVED

            // DAO'da (cache) bu kullanıcı varmış gibi davran
            UserEntity cachedEntity = new UserEntity("uid-cached", "cached@example.com", 123L);
            when(mockUserDao.getUserByEmail("cached@example.com")).thenReturn(cachedEntity);

            // 2. Act
            User result = repository.getCurrentUser();

            // 3. Assert
            verify(mockAuthWrapper).getCurrentUser();
            verify(mockUserDao).getUserByEmail("cached@example.com");
            // Kullanıcı cache'de bulunduğu için DAO'ya insert çağrılmamalı
            verify(mockUserDao, never()).insert(any(UserEntity.class));

            assertNotNull(result);
            assertEquals("uid-cached", result.getId());
            assertEquals("cached@example.com", result.getEmail());
        }
    }

    @Test
    public void testGetCurrentUser_WhenAuthUserNotCached_CachesAndReturnsUser() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            // Firebase kullanıcısını ayarla
            when(mockAuthWrapper.getCurrentUser()).thenReturn(mockFirebaseUser);
            when(mockFirebaseUser.getEmail()).thenReturn("new@example.com");
            when(mockFirebaseUser.getUid()).thenReturn("uid-new"); // <-- This one IS necessary here

            // DAO'da (cache) bu kullanıcı YOKMUŞ gibi davran
            when(mockUserDao.getUserByEmail("new@example.com")).thenReturn(null);

            // 2. Act
            User result = repository.getCurrentUser();

            // 3. Assert
            verify(mockAuthWrapper).getCurrentUser();
            verify(mockUserDao).getUserByEmail("new@example.com");

            // Kullanıcı cache'de bulunmadığı için DAO'ya insert EDİLMELİ
            verify(mockUserDao).insert(userEntityCaptor.capture());
            UserEntity capturedEntity = userEntityCaptor.getValue();

            assertNotNull(capturedEntity);
            assertEquals("uid-new", capturedEntity.getId());
            assertEquals("new@example.com", capturedEntity.getEmail());

            // Log'un çağrıldığını doğrula
            mockedLogger.verify(() -> AppLogger.d("Cached current user: uid-new"));

            // Dönen sonuç da doğru olmalı
            assertNotNull(result);
            assertEquals("uid-new", result.getId());
        }
    }

    @Test
    public void testGetCurrentUser_WhenNoAuthUser_ReturnsNull() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            // Firebase kullanıcısı yok (giriş yapılmamış)
            when(mockAuthWrapper.getCurrentUser()).thenReturn(null);

            // 2. Act
            User result = repository.getCurrentUser();

            // 3. Assert
            assertNull(result);
            verify(mockUserDao, never()).getUserByEmail(anyString());
            verify(mockUserDao, never()).insert(any(UserEntity.class));
            mockedLogger.verify(() -> AppLogger.w("Token invalid; force login"));
        }
    }

    @Test
    public void testSignUp_OnSuccess_CachesUserAndCallsCallback() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            String email = "signup@example.com";
            String password = "password123";

            when(mockFirebaseUser.getUid()).thenReturn("uid-signup");
            // --- when(mockFirebaseUser.getEmail()).thenReturn(email); --- // <-- REMOVED

            // Mock AuthCallback (başarı senaryosu)
            // doAnswer: 'signUp' metodu çağrıldığında,
            // 3. argüman (callback) ile 'onSuccess' metodunu tetikle.
            doAnswer(invocation -> {
                FirebaseAuthWrapper.AuthCallback callback = invocation.getArgument(2);
                callback.onSuccess(mockFirebaseUser);
                return null;
            }).when(mockAuthWrapper).signUp(eq(email), eq(password), any(FirebaseAuthWrapper.AuthCallback.class));

            // Testin callback'ini yakalamak için mock bir callback
            FirebaseAuthWrapper.AuthCallback mockTestCallback = Mockito.mock(FirebaseAuthWrapper.AuthCallback.class);

            // 2. Act
            repository.signUp(email, password, mockTestCallback);

            // 3. Assert
            // AuthWrapper'ın çağrıldığını doğrula
            verify(mockAuthWrapper).signUp(eq(email), eq(password), any(FirebaseAuthWrapper.AuthCallback.class));

            // Başarılı olduğu için DAO'ya insert yapılmalı
            verify(mockUserDao).insert(userEntityCaptor.capture());
            assertEquals("uid-signup", userEntityCaptor.getValue().getId());

            // Log'ları doğrula
            mockedLogger.verify(() -> AppLogger.i("Sign-up cached: uid-signup"));

            // Teste verilen asıl callback'in 'onSuccess' metodunun çağrıldığını doğrula
            verify(mockTestCallback).onSuccess(mockFirebaseUser);
            verify(mockTestCallback, never()).onFailure(any());
        }
    }

    @Test
    public void testSignUp_OnFailure_CallsCallbackOnFailure() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            String email = "fail@example.com";
            String password = "password123";
            Exception testException = new Exception("Sign-up failed");

            // Mock AuthCallback (hata senaryosu)
            doAnswer(invocation -> {
                FirebaseAuthWrapper.AuthCallback callback = invocation.getArgument(2);
                callback.onFailure(testException);
                return null;
            }).when(mockAuthWrapper).signUp(eq(email), eq(password), any(FirebaseAuthWrapper.AuthCallback.class));

            FirebaseAuthWrapper.AuthCallback mockTestCallback = Mockito.mock(FirebaseAuthWrapper.AuthCallback.class);

            // 2. Act
            repository.signUp(email, password, mockTestCallback);

            // 3. Assert
            // Hata alındığı için DAO'ya insert yapılmamalı
            verify(mockUserDao, never()).insert(any(UserEntity.class));

            // Log'u doğrula
            mockedLogger.verify(() -> AppLogger.e("Sign-up failed", testException));

            // Teste verilen asıl callback'in 'onFailure' metodunun çağrıldığını doğrula
            verify(mockTestCallback, never()).onSuccess(any());
            verify(mockTestCallback).onFailure(testException);
        }
    }

    @Test
    public void testSyncUsers_OnFirestoreUpdate_InsertsUsersToDao() {
        try (MockedStatic<AppLogger> mockedLogger = Mockito.mockStatic(AppLogger.class)) {
            // 1. Arrange
            User firestoreUser = new User("uid-firestore", "firestore@example.com", 456L);

            // Firestore'dan gelen veriyi mock'la
            when(mockDocSnapshot.toObject(User.class)).thenReturn(firestoreUser);
            when(mockDocChange.getType()).thenReturn(DocumentChange.Type.ADDED);
            when(mockDocChange.getDocument()).thenReturn(mockDocSnapshot);
            when(mockQuerySnapshot.getDocumentChanges()).thenReturn(Collections.singletonList(mockDocChange));

            // 2. Act
            repository.syncUsers(); // Bu, listener'ı kaydeder

            // Firestore listener'ını yakala
            verify(mockFirestore).listenForChanges(
                    eq("users"),
                    isNull(),
                    isNull(),
                    firestoreListenerCaptor.capture()
            );

            // Listener'ı manuel olarak tetikle
            firestoreListenerCaptor.getValue().onUpdate(mockQuerySnapshot);

            // 3. Assert
            // Firestore'dan gelen verinin DAO'ya eklendiğini doğrula
            verify(mockUserDao).insert(userEntityCaptor.capture());
            UserEntity capturedEntity = userEntityCaptor.getValue();

            assertEquals("uid-firestore", capturedEntity.getId());
            assertEquals("firestore@example.com", capturedEntity.getEmail());

            // Log'u doğrula
            mockedLogger.verify(() -> AppLogger.d("Synced user: uid-firestore (email: firestore@example.com)"));
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