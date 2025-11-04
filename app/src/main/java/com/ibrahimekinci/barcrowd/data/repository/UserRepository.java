package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper.AuthCallback; // Import this

/**
 * Repository for user operations.
 * This implementation is Firebase-only and does not use a local cache (Room).
 */
public interface UserRepository {

    /**
     * Gets the currently authenticated user's data from Firestore.
     * @return LiveData wrapping the User object, or null if not signed in.
     */
    LiveData<User> getCurrentUser();

    /**
     * Signs up a new user via Firebase Auth and creates their user document in Firestore.
     * THIS IS THE UPDATED 5-ARGUMENT METHOD
     */
    void signUp(String email, String password, String fullName, String username, AuthCallback callback);

    /**
     * Signs in a user via Firebase Auth.
     */
    void signIn(String email, String password, AuthCallback callback);

    /**
     * Signs out the current user.
     */
    void signOut();

    /**
     * Checks if a user is currently signed in.
     */
    boolean isUserLoggedIn();
}