package com.ibrahimekinci.barcrowd.data.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.AuthException;

public class FirebaseAuthWrapper {
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void signUp(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    AppLogger.i("Sign-up successful for " + email);
                    callback.onSuccess(result.getUser());
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Sign-up failed", e);
                    callback.onFailure(new AuthException("Sign-up failed: " + e.getMessage(), e));
                });
    }

    public void signIn(String email, String password, AuthCallback callback) {
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

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }
}