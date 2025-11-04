package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import java.util.List;

/**
 * Use case for getting the 5 most recent live updates for the Home Page.
 */
public class GetRecentLiveUpdatesUseCase {
    private final LiveUpdateRepository repository;

    public GetRecentLiveUpdatesUseCase(LiveUpdateRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<LiveUpdate>> execute() {
        // Calls the new repository method
        return repository.getRecentLiveUpdates();
    }
}