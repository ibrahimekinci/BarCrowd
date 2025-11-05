package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import java.util.List;

/**
 * Use case for getting all (up to 100) non-deleted live updates
 * for the main "All Updates" feed.
 */
public class GetAllLiveUpdatesUseCase {
    private final LiveUpdateRepository repository;

    public GetAllLiveUpdatesUseCase(LiveUpdateRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<LiveUpdate>> execute() {
        // Calls the new repository method
        return repository.getAllLiveUpdates();
    }
}