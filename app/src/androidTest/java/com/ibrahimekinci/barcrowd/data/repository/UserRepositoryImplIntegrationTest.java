package com.ibrahimekinci.barcrowd.data.repository;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseUser;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.UserDao;
import com.ibrahimekinci.barcrowd.data.local.UserEntity;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.User;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link UserRepositoryImpl}.
 * This test uses a real in-memory Room database and MOCKED
 * FirebaseAuthWrapper and FirestoreWrapper.
 * It verifies the repository's logic for integrating with the real UserDao.
 */
@RunWith(AndroidJUnit4.class)
public class UserRepositoryImplIntegrationTest {

    // --- JUNIT RULES ---
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    // --- Real Database Components ---
    private AppDatabase db;
    private UserDao realDao; // We will use the real DAO

    // --- Mocked External Dependencies ---
    @Mock
    private FirestoreWrapper mockFirestore;
    @Mock
    private FirebaseAuthWrapper mockAuthWrapper;
    @Mock
    private FirebaseUser mockFirebaseUser;

    @Captor
    private ArgumentCaptor<FirebaseAuthWrapper.AuthCallback> authCallbackCaptor;

    // --- Class Under Test ---
    private UserRepositoryImpl repository;

    // Test data
    private UserEntity userEntity1 = new UserEntity("u1", "user1@example.com", 1000L);
    private User testUser = new User("u1", "user1@example.com", 1000L);

    @Before
    public void setUp() {
        // 1. Create real in-memory database
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        // 2. Get the real DAO from the real database
        realDao = db.userDao();

        // 3. Create the repository with the REAL db and MOCKED wrappers
        repository = new UserRepositoryImpl(db, mockFirestore, mockAuthWrapper);
    }

    @After
    public void tearDown() throws IOException {
        db.close();
    }

    @Test
    public void testInsertUser_WritesToRealDao() {
        // 1. Arrange
        // (No arrangement needed)

        // 2. Act
        // Call repository to insert the user model
        repository.insertUser(testUser);

        // 3. Assert
        // We can't verify mockDao.insert() because the DAO is real.
        // Instead, we check the real database to see if the data was *actually* written.
        UserEntity writtenEntity = realDao.getUserByEmail("user1@example.com");

        assertNotNull(writtenEntity);
        assertEquals("u1", writtenEntity.getId());
        assertEquals(1000L, writtenEntity.getCreatedAt());
    }

    @Test
    public void testGetUserByEmail_ReadsFromRealDao() {
        // 1. Arrange
        // Insert data directly into the real DAO
        realDao.insert(userEntity1);

        // 2. Act
        User result = repository.getUserByEmail("user1@example.com");

        // 3. Assert
        assertNotNull(result);
        assertEquals("u1", result.getId());
        assertEquals("user1@example.com", result.getEmail());
    }

    @Test
    public void testGetCurrentUser_WhenAuthUserNotCached_CachesToRealDao() {
        // 1. Arrange
        // Mock the Firebase Auth response
        when(mockAuthWrapper.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getEmail()).thenReturn("new@example.com");
        when(mockFirebaseUser.getUid()).thenReturn("uid-new");

        // The real DAO is empty, so getUserByEmail("new@example.com") will return null.

        // 2. Act
        User result = repository.getCurrentUser();

        // 3. Assert
        // Check that the returned user is correct
        assertNotNull(result);
        assertEquals("uid-new", result.getId());

        // Check that this new user was *also* written to the real database
        UserEntity writtenEntity = realDao.getUserByEmail("new@example.com");
        assertNotNull(writtenEntity);
        assertEquals("uid-new", writtenEntity.getId());
    }

    @Test
    public void testSignUp_OnSuccess_CachesUserToRealDao() {
        // 1. Arrange
        String email = "signup@example.com";
        String password = "password123";

        // Mock the Firebase Auth response
        when(mockFirebaseUser.getUid()).thenReturn("uid-signup");

        // Mock the "onSuccess" callback from the wrapper
        doAnswer(invocation -> {
            FirebaseAuthWrapper.AuthCallback callback = invocation.getArgument(2);
            callback.onSuccess(mockFirebaseUser);
            return null;
        }).when(mockAuthWrapper).signUp(eq(email), eq(password), authCallbackCaptor.capture());

        // --- THIS IS THE FIX ---
        // A simple callback for the test to receive the result
        FirebaseAuthWrapper.AuthCallback mockTestCallback = new FirebaseAuthWrapper.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) { /* Do nothing */ }
            @Override
            public void onFailure(Exception e) { /* Do nothing */ }
        };

        // 2. Act
        repository.signUp(email, password, mockTestCallback);

        // 3. Assert
        // Verify that the user was written to the real database
        UserEntity writtenEntity = realDao.getUserByEmail(email);
        assertNotNull(writtenEntity);
        assertEquals("uid-signup", writtenEntity.getId());
    }

    @Test
    public void testSignUp_OnFailure_DoesNotCacheUser() {
        // 1. Arrange
        String email = "fail@example.com";
        String password = "password123";
        Exception testException = new Exception("Sign-up failed");

        // Mock the "onFailure" callback from the wrapper
        doAnswer(invocation -> {
            FirebaseAuthWrapper.AuthCallback callback = invocation.getArgument(2);
            callback.onFailure(testException);
            return null;
        }).when(mockAuthWrapper).signUp(eq(email), eq(password), authCallbackCaptor.capture());

        // --- THIS IS THE FIX ---
        FirebaseAuthWrapper.AuthCallback mockTestCallback = new FirebaseAuthWrapper.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) { /* Do nothing */ }
            @Override
            public void onFailure(Exception e) { /* Do nothing */ }
        };

        // 2. Act
        repository.signUp(email, password, mockTestCallback);

        // 3. Assert
        // Verify that *nothing* was written to the real database
        UserEntity writtenEntity = realDao.getUserByEmail(email);
        assertNull(writtenEntity);
    }
}