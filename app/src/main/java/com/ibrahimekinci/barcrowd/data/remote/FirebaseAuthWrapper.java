package com.ibrahimekinci.barcrowd.data.remote;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.AuthException;

public class FirebaseAuthWrapper {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    // Callback for checking uniqueness
    public interface CheckUniquenessCallback {
        void onResult(boolean isUnique, Exception e);
    }

    // Callback for standard Auth operations
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);

        void onFailure(Exception e);
    }

    public FirebaseAuthWrapper() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Checks if an email is already registered in Firebase Auth.
     * This is called *before* attempting to sign up.
     */
    public void checkEmailExists(String email, @NonNull CheckUniquenessCallback callback) {
        auth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(result -> {
                    boolean emailExists = result.getSignInMethods() != null && !result.getSignInMethods().isEmpty();
                    callback.onResult(!emailExists, null);
                })
                .addOnFailureListener(e -> {
                    // This could be a network error, etc.
                    callback.onResult(false, e);
                });
    }

    /**
     * Signs up a new user (Auth) AND creates their user document (Firestore).
     * This is now only called AFTER email and username checks have passed.
     */
    public void signUp(String email, String password, @NonNull String fullName, @NonNull String username, @NonNull AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    AppLogger.i("Sign-up successful for " + email);
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        // Auth user created, now create the Firestore document
                        createNewUserDocument(firebaseUser, fullName, username, callback);
                    } else {
                        callback.onFailure(new AuthException("Sign-up succeeded but user object is null."));
                    }
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Sign-up failed", e);
                    callback.onFailure(new AuthException("Sign-up failed: " + e.getMessage(), e));
                });
    }

    /**
     * Creates the corresponding User document in the /Users collection.
     */
    private void createNewUserDocument(FirebaseUser firebaseUser, String fullName, String username, @NonNull AuthCallback callback) {
        User newUser = new User();
        newUser.setUserId(firebaseUser.getUid());
        newUser.setFullName(fullName);
        newUser.setUsername(username);
        newUser.setEmail(firebaseUser.getEmail());

        firestore.collection("Users").document(firebaseUser.getUid()).set(newUser)
                .addOnSuccessListener(aVoid -> {
                    AppLogger.i("User document created in Firestore for " + firebaseUser.getUid());
                    callback.onSuccess(firebaseUser);
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Failed to create user document", e);
                    firebaseUser.delete();
                    callback.onFailure(new AuthException("Failed to save user profile: " + e.getMessage(), e));
                });
    }

    /**
     * Signs in a user with email and password.
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

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signOut() {
        auth.signOut();
        AppLogger.i("User signed out");
    }
}