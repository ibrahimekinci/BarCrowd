package com.ibrahimekinci.barcrowd.data.remote;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore; // Import Firestore
import com.ibrahimekinci.barcrowd.domain.model.User; // Import your User model
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.AuthException;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthWrapper {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore; // Add Firestore instance

    public FirebaseAuthWrapper() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance(); // Get Firestore instance
    }

    /**
     * Signs up a new user with email and password.
     * On success, it also creates their user document in the /Users collection.
     * @param email User's email
     * @param password User's password
     * @param callback Callback for success (with FirebaseUser) or failure
     */
    public void signUp(String email, String password, @NonNull String fullName, @NonNull String username, @NonNull AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    AppLogger.i("Sign-up successful for " + email);
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        // After auth user is created, create the user document in Firestore
                        createNewUserDocument(firebaseUser, fullName, username, callback);
                    } else {
                        // This should rarely happen, but handle it
                        callback.onFailure(new AuthException("Sign-up succeeded but user object is null."));
                    }
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Sign-up failed", e);
                    callback.onFailure(new AuthException("Sign-up failed: " + e.getMessage(), e));
                });
    }

    /**
     * Creates the corresponding User document in the /Users collection
     * after a successful sign-up.
     */
    private void createNewUserDocument(FirebaseUser firebaseUser, String fullName, String username, @NonNull AuthCallback callback) {
        // Use a Map to set initial values.
        // use a Map instead of the User POJO here to ensure @ServerTimestamp works.
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", firebaseUser.getUid());
        userData.put("fullName", fullName);
        userData.put("username", username);
        userData.put("email", firebaseUser.getEmail());
        userData.put("profilePhotoUrl", null);
        userData.put("isTrusted", false); // Default value
        userData.put("isDeleted", false); // Default value
        userData.put("deletedAt", null);
        // createdAt and updatedAt will be set by @ServerTimestamp in the POJO
        // or by Firestore Security Rules if defined.
        // For a direct .set() call with a Map, we should add them manually if not using rules.
        // Let's rely on the User model's @ServerTimestamp annotation.

        User newUser = new User();
        newUser.setUserId(firebaseUser.getUid());
        newUser.setFullName(fullName);
        newUser.setUsername(username);
        newUser.setEmail(firebaseUser.getEmail());
        // All other fields will use their Java defaults (false, null)

        firestore.collection("Users").document(firebaseUser.getUid()).set(newUser)
                .addOnSuccessListener(aVoid -> {
                    AppLogger.i("User document created in Firestore for " + firebaseUser.getUid());
                    callback.onSuccess(firebaseUser);
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Failed to create user document", e);
                    // Critical error: User auth was created but their database entry failed.
                    // We should delete the auth user to allow them to try again.
                    firebaseUser.delete();
                    callback.onFailure(new AuthException("Failed to save user profile: " + e.getMessage(), e));
                });
    }


    /**
     * Signs in a user with email and password.
     * This method NO LONGER interacts with the local database.
     * @param email User's email
     * @param password User's password
     * @param callback Callback for success (with FirebaseUser) or failure
     */
    public void signIn(String email, String password, @NonNull AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    AppLogger.i("Sign-in successful for " + email);
                    callback.onSuccess(result.getUser());
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Sign-in failed", e);
                    callback.onFailure(new AuthException("Sign-in failed: " + e.getMessage(), e));
                });
    }

    /**
     * Gets the currently authenticated FirebaseUser.
     * This method NO LONGER interacts with the local database.
     * @return FirebaseUser object, or null if not signed in.
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signOut() {
        auth.signOut();
        AppLogger.i("User signed out");
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }
}