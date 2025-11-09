package com.ibrahimekinci.barcrowd.data.repository;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.AuthException;

public class UserRepositoryImpl implements UserRepository {

    private final FirestoreWrapper firestore;
    private final FirebaseAuthWrapper authWrapper;
    private final FirebaseFirestore db; // Direct db access for queries

    private final MutableLiveData<User> currentUserData = new MutableLiveData<>(null);

    public UserRepositoryImpl(FirestoreWrapper firestore, FirebaseAuthWrapper authWrapper) {
        this.firestore = firestore;
        this.authWrapper = authWrapper;
        this.db = firestore.getDb(); // Get instance from wrapper

        // Fetch data in constructor (for app startup)
        if (isUserLoggedIn()) {
            fetchUserDocument(authWrapper.getCurrentUser().getUid());
        }
    }

    /**
     * Implements the username uniqueness check.
     */
    @Override
    public void checkUsernameExists(String username, UniquenessCallback callback) {
        db.collection("Users").whereEqualTo("username", username).limit(1).get()
                .addOnSuccessListener(snapshot -> {
                    // if snapshot is empty, username is unique
                    callback.onResult(snapshot.isEmpty(), null);
                })
                .addOnFailureListener(e -> {
                    // Query failed (e.g., network error)
                    callback.onResult(false, e);
                });
    }

    @Override
    public void checkEmailExists(String email, UniquenessCallback callback) {
        db.collection("Users").whereEqualTo("email", email).limit(1).get()
                .addOnSuccessListener(snapshot -> {
                    // if snapshot is empty, email is unique
                    callback.onResult(snapshot.isEmpty(), null);
                })
                .addOnFailureListener(e -> {
                    // Query failed (e.g., network error)
                    callback.onResult(false, e);
                });
    }

    /**
     * Checks if the username is taken by any user *other than* the one specified by userId.
     */
    @Override
    public void checkUsernameForUpdate(String username, String userId, UniquenessCallback callback) {
        db.collection("Users")
                .whereEqualTo("username", username)
                .whereNotEqualTo("userId", userId) // Key difference: "userId" field (per your model)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    // if snapshot is empty, the username is unique (or only belongs to the current user)
                    callback.onResult(snapshot.isEmpty(), null);
                })
                .addOnFailureListener(e -> {
                    // Query failed (e.g., network error)
                    callback.onResult(false, e);
                });
    }

    @Override
    public LiveData<User> getCurrentUser() {
        // If user is logged in BUT data hasn't (asynchronously) arrived yet,
        // (ProfileFragment opening immediately) re-trigger the fetch.
        if (isUserLoggedIn() && currentUserData.getValue() == null) {
            AppLogger.d("UserRepository: User is logged in but data is null. Triggering fetch.");
            fetchUserDocument(authWrapper.getCurrentUser().getUid());
        }
        return currentUserData;
    }

    private void fetchUserDocument(String uid) {
        db.collection("Users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            currentUserData.postValue(user);
                            AppLogger.i("User document fetched: " + user.getUsername());
                        } else {
                            // Mapping error log
                            AppLogger.e("User document exists but failed to map to User.class. Check User.java and Firestore field names (e.g., 'isDeleted' vs 'deleted').");
                            currentUserData.postValue(null);
                        }
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
    @Override
    public void signUp(String email, String password, String fullName, String username, FirebaseAuthWrapper.AuthCallback callback) {
        // Step 1: Check if email is unique
        authWrapper.checkEmailExists(email, (isUnique, e) -> {
            if (e != null) {
                // Network error or other Firebase issue
                callback.onFailure(e);
                return;
            }

            if (!isUnique) {
                // Email is already taken
                callback.onFailure(new AuthException("This email address is already in use."));
                return;
            }

            // Step 2: Email is unique, proceed to create the user
            authWrapper.signUp(email, password, fullName, username, new FirebaseAuthWrapper.AuthCallback() {
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
        });
    }

    @Override
    public void updateUser(User user, FirebaseAuthWrapper.AuthCallback callback) {
        db.collection("Users").document(user.getUserId())
                .set(user, SetOptions.merge()) // .merge() only updates fields
                .addOnSuccessListener(aVoid -> {
                    AppLogger.i("User profile updated in Firestore: " + user.getUserId());
                    // Also update the LiveData locally (no need to re-fetch)
                    currentUserData.postValue(user);
                    callback.onSuccess(null); // Success, no FirebaseUser to return
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("User profile update failed", e);
                    callback.onFailure(e);
                });
    }

    @Override
    public void signIn(String email, String password, FirebaseAuthWrapper.AuthCallback callback) {
        authWrapper.signIn(email, password, new FirebaseAuthWrapper.AuthCallback() {
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
        currentUserData.postValue(null);
    }

    @Override
    public boolean isUserLoggedIn() {
        return authWrapper.getCurrentUser() != null;
    }
}