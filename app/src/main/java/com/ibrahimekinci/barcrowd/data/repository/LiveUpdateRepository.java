package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import java.util.List;

public interface LiveUpdateRepository {
    void postUpdate(LiveUpdate update);
    LiveData<List<LiveUpdate>> getUpdatesForVenue(String venueId);
    LiveData<List<LiveUpdate>> getUserContributions(String userId);
    void syncUpdates(String venueId); // Syncs remote changes for a venue
    void syncPending(); // Syncs local changes to remote
    /**
     * Gets the 5 most recent, non-deleted updates.
     */
    LiveData<List<LiveUpdate>> getRecentLiveUpdates();

    /**
     * Gets all (up to 100) non-deleted updates.
     */
    LiveData<List<LiveUpdate>> getAllLiveUpdates();
}