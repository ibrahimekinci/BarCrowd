package com.ibrahimekinci.barcrowd.data.remote;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.NetworkException;

/**
 * Wrapper for Firestore operations with callbacks.
 */
public class FirestoreWrapper {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseFirestore getDb() {
        return db;
    }

    public <T> void addDocument(String collection, T data, Callback<String> callback) {
        getDb().collection(collection).add(data)
                .addOnSuccessListener(ref -> {
                    AppLogger.i("Document added to " + collection + ": " + ref.getId());
                    callback.onSuccess(ref.getId());
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Add document failed in " + collection, e);
                    callback.onFailure(new NetworkException("Add failed: " + e.getMessage(), e));
                });
    }

    /**
     * Sets a document with a custom ID (for local-generated IDs).
     */
    public <T> void setDocument(String collection, String id, T data, Callback<Void> callback) {
        getDb().collection(collection).document(id).set(data)
                .addOnSuccessListener(v -> {
                    AppLogger.i("Document set in " + collection + ": " + id);
                    callback.onSuccess(v);
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Set document failed in " + collection, e);
                    callback.onFailure(new NetworkException("Set failed: " + e.getMessage(), e));
                });
    }

    public <T> void updateDocument(String collection, String id, T data, Callback<Void> callback) {
        getDb().collection(collection).document(id).set(data)
                .addOnSuccessListener(v -> {
                    AppLogger.i("Document updated in " + collection + ": " + id);
                    callback.onSuccess(v);
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Update document failed in " + collection, e);
                    callback.onFailure(new NetworkException("Update failed: " + e.getMessage(), e));
                });
    }

    public void listenForChanges(String collection, String field, String value, Listener<QuerySnapshot> listener) {
        Query query = getDb().collection(collection);
        if (field != null && value != null) {
            query = query.whereEqualTo(field, value);
        }
        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                AppLogger.e("Snapshot listener failed for " + collection, e);
                return;
            }
            AppLogger.d("Snapshot received for " + collection + " (changes: " + snapshots.getDocumentChanges().size() + ")");
            listener.onUpdate(snapshots);
        });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public interface Listener<T> {
        void onUpdate(T data);
    }
}