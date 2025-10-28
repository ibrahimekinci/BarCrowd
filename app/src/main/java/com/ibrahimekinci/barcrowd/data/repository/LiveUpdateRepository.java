package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import java.util.List;

public interface LiveUpdateRepository {
    void postUpdate(LiveUpdate update);
    LiveData<List<LiveUpdate>> getUpdatesForVenue(String venueId);
    LiveData<List<LiveUpdate>> getUserContributions(String userId);
    void syncUpdates(String venueId);
    void syncPending();
}