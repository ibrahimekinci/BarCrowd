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
import com.ibrahimekinci.barcrowd.util.ConnectivityUtil;
import com.ibrahimekinci.barcrowd.util.NetworkException;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of LiveUpdateRepository with offline-first sync.
 */
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
        // Generate ID if not set
        if (update.getId() == null) {
            update.setId(UUID.randomUUID().toString());
        }
        LiveUpdateEntity entity = LiveUpdateMapper.toEntity(update);
        entity.setSyncStatus(false); // Pending sync
        dao.insert(entity);
        AppLogger.d("Inserted local update: " + update.getId());

        // Attempt sync if online
        if (ConnectivityUtil.isOnline(app)) {
            syncPending();
        }
    }

    /**
     * Syncs all pending local updates to Firebase.
     */
    public void syncPending() { // Made public for receiver access
        List<LiveUpdateEntity> pending = dao.getPendingUpdates();
        for (LiveUpdateEntity entity : pending) {
            LiveUpdate model = LiveUpdateMapper.toModel(entity);
            firestore.setDocument("live_updates", entity.getId(), model, new FirestoreWrapper.Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    dao.markAsSynced(entity.getId());
                    AppLogger.i("Synced update: " + entity.getId());
                }

                @Override
                public void onFailure(Exception e) {
                    AppLogger.e("Failed to sync update: " + entity.getId(), e);
                }
            });
        }
    }

    @Override
    public LiveData<List<LiveUpdate>> getUpdatesForVenue(String venueId) {
        syncUpdates(venueId);
        return Transformations.map(dao.getUpdatesForVenue(venueId),
                entities -> entities.stream().map(LiveUpdateMapper::toModel).collect(Collectors.toList()));
    }

    @Override
    public LiveData<List<LiveUpdate>> getUserContributions(String userId) {
        return Transformations.map(dao.getUserContributions(userId),
                entities -> entities.stream().map(LiveUpdateMapper::toModel).collect(Collectors.toList()));
    }

    @Override
    public void syncUpdates(String venueId) {
        firestore.listenForChanges("live_updates", "venueId", venueId, new FirestoreWrapper.Listener<QuerySnapshot>() {
            @Override
            public void onUpdate(QuerySnapshot snapshots) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                        LiveUpdate update = dc.getDocument().toObject(LiveUpdate.class);
                        if (update != null) {
                            LiveUpdateEntity entity = LiveUpdateMapper.toEntity(update);
                            entity.setSyncStatus(true); // Synced from remote
                            dao.insert(entity);
                            AppLogger.d("Synced remote update for venue: " + venueId + ", id: " + update.getId());
                        }
                    }
                }
            }
        });
    }
}