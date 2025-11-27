package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import java.util.List;

public interface LiveUpdateRepository {

    // --- Callback Interfaces ---

    interface LoadCallback {
        void onLoaded(List<LiveUpdate> updates);
        void onError(Exception e);
    }

    interface DeleteCallback {
        void onSuccess();
        void onError(Exception e);
    }

    interface SaveCallback {
        void onSuccess();
        void onError(Exception e);
    }

    // --- Methods ---

    /**
     * Fetches all live updates via callback (useful for filtering in ViewModel).
     */
    void getAllLiveUpdates(LoadCallback callback);

    /**
     * Performs a soft delete (sets isDeleted = true) on both remote and local DB.
     */
    void softDeleteUpdate(LiveUpdate update, DeleteCallback callback);

    /**
     * Saves or updates a LiveUpdate record to Firestore and Local DB.
     */
    void saveLiveUpdate(LiveUpdate liveUpdate, SaveCallback callback);

    /**
     * Fire-and-forget method to post an update (Legacy support).
     */
    void postUpdate(LiveUpdate update);

    /**
     * Returns a LiveData stream of all updates.
     */
    LiveData<List<LiveUpdate>> getAllLiveUpdates();

    /**
     * Returns a LiveData stream of recent updates.
     */
    LiveData<List<LiveUpdate>> getRecentLiveUpdates();

    /**
     * Returns updates for a specific venue as LiveData.
     */
    LiveData<List<LiveUpdate>> getUpdatesForVenue(String venueId);

    /**
     * Returns updates contributed by a specific user as LiveData.
     */
    LiveData<List<LiveUpdate>> getUserContributions(String userId);

    // --- Sync Methods ---
    void syncUpdates(String venueId);
    void syncPending();
    void syncRecentLiveUpdates();
}