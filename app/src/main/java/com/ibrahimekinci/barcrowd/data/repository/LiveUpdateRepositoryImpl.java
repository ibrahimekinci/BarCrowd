package com.ibrahimekinci.barcrowd.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateDao;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.data.mapper.LiveUpdateMapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.util.AppLogger;
// import com.ibrahimekinci.barcrowd.util.ConnectivityUtil; // Removed
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LiveUpdateRepositoryImpl implements LiveUpdateRepository {
    private final LiveUpdateDao dao;
    private final FirestoreWrapper firestore;
    private final Application app;

    public LiveUpdateRepositoryImpl(AppDatabase db, FirestoreWrapper firestore, Application app) {
        this.dao = db.liveUpdateDao();
        this.firestore = firestore;
        this.app = app;
    }

    @Override
    public void postUpdate(LiveUpdate update) {
        if (update.getUpdateId() == null || update.getUpdateId().isEmpty()) {
            update.setUpdateId(UUID.randomUUID().toString());
        }

        LiveUpdateEntity entity = LiveUpdateMapper.toEntity(update);
        entity.setSyncStatus(false);

        new Thread(() -> {
            dao.insert(entity);
            AppLogger.d("Inserted local update: " + update.getUpdateId());
        }).start();
    }

    @Override
    public void syncPending() {
        new Thread(() -> {
            List<LiveUpdateEntity> pending = dao.getPendingUpdates();
            AppLogger.i(pending.size() + " pending updates to sync.");

            for (LiveUpdateEntity entity : pending) {
                LiveUpdate model = LiveUpdateMapper.toModel(entity);

                firestore.setDocument("LiveUpdates", entity.getUpdateId(), model, new FirestoreWrapper.Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        new Thread(() -> dao.markAsSynced(entity.getUpdateId())).start();
                        AppLogger.i("Synced update: " + entity.getUpdateId());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        AppLogger.e("Failed to sync update: " + entity.getUpdateId(), e);
                    }
                });
            }
        }).start();
    }

    @Override
    public LiveData<List<LiveUpdate>> getUpdatesForVenue(String venueId) {
        syncUpdates(venueId);
        return Transformations.map(dao.getUpdatesForVenue(venueId),
                entities -> entities.stream()
                        .map(LiveUpdateMapper::toModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<List<LiveUpdate>> getUserContributions(String userId) {
        return Transformations.map(dao.getUserContributions(userId),
                entities -> entities.stream()
                        .map(LiveUpdateMapper::toModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void syncUpdates(String venueId) {
        firestore.listenForChanges("LiveUpdates", "venueId", venueId, snapshots -> {
            if (snapshots == null) return;

            new Thread(() -> {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                        LiveUpdate update = dc.getDocument().toObject(LiveUpdate.class);
                        if (update != null) {
                            LiveUpdateEntity entity = LiveUpdateMapper.toEntity(update);
                            entity.setSyncStatus(true);
                            dao.insert(entity);
                            AppLogger.d("Synced remote update for venue: " + venueId + ", id: " + update.getUpdateId());
                        }
                    }
                }
            }).start();
        });
    }

    @Override
    public LiveData<List<LiveUpdate>> getRecentLiveUpdates() {
        return Transformations.map(dao.getRecentLiveUpdates(), entities ->
                entities.stream()
                        .map(LiveUpdateMapper::toModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<List<LiveUpdate>> getAllLiveUpdates() {
        return Transformations.map(dao.getAllLiveUpdates(), entities ->
                entities.stream()
                        .map(LiveUpdateMapper::toModel)
                        .collect(Collectors.toList())
        );
    }
}