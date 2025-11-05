package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.SetOptions;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper.AuthCallback;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.util.AppLogger;

/**
 * Firebase-only implementation of UserRepository.
 * Fetches user data directly from Firestore on demand and does not cache locally in Room.
 */
public class UserRepositoryImpl implements UserRepository { // No "abstract" or "implements" error

    private final FirestoreWrapper firestore;
    private final FirebaseAuthWrapper authWrapper;

    // Use a MutableLiveData to hold the current user object.
    private final MutableLiveData<User> currentUserData = new MutableLiveData<>(null);

    // Constructor no longer takes AppDatabase
    public UserRepositoryImpl(FirestoreWrapper firestore, FirebaseAuthWrapper authWrapper) {
        this.firestore = firestore;
        this.authWrapper = authWrapper;

        // Check for a user immediately on init
        if (isUserLoggedIn()) {
            fetchUserDocument(authWrapper.getCurrentUser().getUid());
        }
    }

    /**
     * Returns a LiveData object that will contain the current user's data.
     */
    @Override
    public LiveData<User> getCurrentUser() {
        return currentUserData;
    }

    /**
     * Fetches the User document from Firestore based on the UID
     * and updates the currentUserData LiveData.
     */
    private void fetchUserDocument(String uid) {
        firestore.getDb().collection("Users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);

                        // --- FIX FOR NullPointerException ---
                        if (user != null) {
                            currentUserData.postValue(user);
                            AppLogger.i("User document fetched: " + user.getUsername());
                        } else {
                            AppLogger.e("User document exists but failed to map to User.class");
                            currentUserData.postValue(null);
                        }
                        // --- END FIX ---

                    } else {
                        AppLogger.w("User is authenticated but no user document found in Firestore!");
                        currentUserData.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Failed to fetch user document", e);
                    currentUserData.postValue(null);
                });
    }

    /**
     * THIS IS THE CORRECTED 5-ARGUMENT signUp METHOD
     */
    @Override
    public void signUp(String email, String password, String fullName, String username, AuthCallback callback) {
        authWrapper.signUp(email, password, fullName, username, new AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // After sign up, fetch the newly created user document
                fetchUserDocument(user.getUid());
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    // data/repository/UserRepositoryImpl.java
    @Override
    public void updateUser(User user, FirebaseAuthWrapper.AuthCallback callback) {
        // We update the full object to ensure all local changes are saved.
        // Use .set(user) instead of .update() if you also need to update local LiveData.
        firestore.getDb().collection("Users").document(user.getUserId())
                .set(user, SetOptions.merge()) // .merge() only updates fields in the object
                .addOnSuccessListener(aVoid -> {
                    AppLogger.i("User profile updated in Firestore: " + user.getUserId());
                    // Re-fetch or update local LiveData
                    currentUserData.postValue(user);
                    callback.onSuccess(null); // Success, no FirebaseUser to return
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("User profile update failed", e);
                    callback.onFailure(e);
                });
    }

    @Override
    public void signIn(String email, String password, AuthCallback callback) {
        authWrapper.signIn(email, password, new AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // After sign in, fetch the user document
                fetchUserDocument(user.getUid());
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void signOut() {
        authWrapper.signOut();
        // Clear the user data on sign out
        currentUserData.postValue(null);
    }

    @Override
    public boolean isUserLoggedIn() {
        return authWrapper.getCurrentUser() != null;
    }
}