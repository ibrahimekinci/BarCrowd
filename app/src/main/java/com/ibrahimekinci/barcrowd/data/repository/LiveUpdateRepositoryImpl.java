package com.ibrahimekinci.barcrowd.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateDao;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.data.mapper.LiveUpdateMapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LiveUpdateRepositoryImpl implements LiveUpdateRepository {

    private final LiveUpdateDao dao;
    private final FirestoreWrapper firestore;
    private final Application app;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveUpdateRepositoryImpl(AppDatabase db, FirestoreWrapper firestore, Application app) {
        this.dao = db.liveUpdateDao();
        this.firestore = firestore;
        this.app = app;
    }

    // --- IMPLEMENTATION OF CALLBACK METHODS ---

    @Override
    public void getAllLiveUpdates(LoadCallback callback) {
        // 1. Attempt to fetch from Firestore
        firestore.getDb().collection("LiveUpdates")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<LiveUpdate> list = new ArrayList<>();
                    if (snapshots != null) {
                        list = snapshots.toObjects(LiveUpdate.class);
                        // Save to local database
                        saveListToLocal(list);
                    }
                    callback.onLoaded(list);
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Remote fetch failed, trying local.", e);
                    // 2. Fallback to Local DB
                    executor.execute(() -> {
                        try {
                            // Using synchronous method from DAO
                            List<LiveUpdateEntity> entities = dao.getAllLiveUpdatesSync();
                            List<LiveUpdate> localList = new ArrayList<>();
                            for (LiveUpdateEntity ent : entities) {
                                localList.add(LiveUpdateMapper.toModel(ent));
                            }
                            callback.onLoaded(localList);
                        } catch (Exception ex) {
                            callback.onError(ex);
                        }
                    });
                });
    }

    @Override
    public void softDeleteUpdate(LiveUpdate update, DeleteCallback callback) {
        if (update.getUpdateId() == null) {
            if (callback != null) callback.onError(new Exception("Update ID is null"));
            return;
        }

        // 1. Update Firestore
        firestore.getDb().collection("LiveUpdates")
                .document(update.getUpdateId())
                .update("isDeleted", true)
                .addOnSuccessListener(aVoid -> {
                    // 2. Update Local DB
                    executor.execute(() -> {
                        update.setDeleted(true);
                        LiveUpdateEntity entity = LiveUpdateMapper.toEntity(update);
                        if (entity != null) dao.insert(entity);
                    });
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    @Override
    public void saveLiveUpdate(LiveUpdate liveUpdate, SaveCallback callback) {
        if (liveUpdate.getUpdateId() == null) {
            if (callback != null) callback.onError(new Exception("Update ID is null"));
            return;
        }

        // 1. Save to Firestore
        firestore.getDb().collection("LiveUpdates")
                .document(liveUpdate.getUpdateId())
                .set(liveUpdate)
                .addOnSuccessListener(aVoid -> {
                    // 2. Save to Local DB
                    executor.execute(() -> {
                        LiveUpdateEntity entity = LiveUpdateMapper.toEntity(liveUpdate);
                        if (entity != null) dao.insert(entity);
                    });
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    // --- IMPLEMENTATION OF LIVEDATA METHODS ---

    @Override
    public LiveData<List<LiveUpdate>> getAllLiveUpdates() {
        return Transformations.map(dao.getAllLiveUpdates(), entities ->
                entities.stream()
                        .map(LiveUpdateMapper::toModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void postUpdate(LiveUpdate update) {
        saveLiveUpdate(update, null);
    }

    @Override
    public LiveData<List<LiveUpdate>> getRecentLiveUpdates() {
        return getAllLiveUpdates();
    }

    @Override
    public LiveData<List<LiveUpdate>> getUpdatesForVenue(String venueId) {
        return new MutableLiveData<>(new ArrayList<>());
    }

    @Override
    public LiveData<List<LiveUpdate>> getUserContributions(String userId) {
        return new MutableLiveData<>(new ArrayList<>());
    }

    // --- SYNC METHODS ---

    @Override
    public void syncUpdates(String venueId) {
        // Optional sync logic
    }

    @Override
    public void syncPending() {
        // Optional sync logic
    }

    @Override
    public void syncRecentLiveUpdates() {
        Query query = firestore.getDb().collection("LiveUpdates")
                .whereEqualTo("isDeleted", false)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100);

        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) return;
            if (snapshots != null) {
                List<LiveUpdate> list = snapshots.toObjects(LiveUpdate.class);
                saveListToLocal(list);
            }
        });
    }

    // --- HELPER METHODS ---

    private void saveListToLocal(List<LiveUpdate> list) {
        executor.execute(() -> {
            for (LiveUpdate item : list) {
                LiveUpdateEntity entity = LiveUpdateMapper.toEntity(item);
                if (entity != null) {
                    entity.setSyncStatus(true);
                    dao.insert(entity);
                }
            }
        });
    }

    private void saveToLocal(List<LiveUpdate> list) {
        saveListToLocal(list);
    }
}