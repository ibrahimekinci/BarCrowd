package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.domain.model.User;

public interface UserRepository {

    // New callback for uniqueness checks
    interface UniquenessCallback {
        void onResult(boolean isUnique, Exception e);
    }

    /**
     * Checks if a username already exists in the Firestore /Users collection.
     * @param username The username to check.
     * @param callback Returns true if unique, false otherwise.
     */
    void checkUsernameExists(String username, UniquenessCallback callback);

    /**
     * Checks if a email already exists in the Firestore /Users collection.
     * @param email The username to check.
     * @param callback Returns true if unique, false otherwise.
     */
    void checkEmailExists(String email, UniquenessCallback callback);

    /**
     * Checks if a username is taken by *another* user.
     * @param username The username to check.
     * @param userId The ID of the *current* user (to exclude them from the search).
     * @param callback Returns true if unique, false otherwise.
     */
    void checkUsernameForUpdate(String username, String userId, UniquenessCallback callback);

    /**
     * Gets the currently authenticated user's data from Firestore.
     */
    LiveData<User> getCurrentUser();

    /**
     * Signs up a new user via Firebase Auth and creates their user document in Firestore.
     * This method now ALSO handles checking for email uniqueness before creating the user.
     */
    void signUp(String email, String password, String fullName, String username, FirebaseAuthWrapper.AuthCallback callback);

    void updateUser(User user, FirebaseAuthWrapper.AuthCallback callback);

    /**
     * Signs in a user via Firebase Auth.
     */
    void signIn(String email, String password, FirebaseAuthWrapper.AuthCallback callback);

    /**
     * Signs out the current user.
     */
    void signOut();

    /**
     * Checks if a user is currently signed in.
     */
    boolean isUserLoggedIn();
}