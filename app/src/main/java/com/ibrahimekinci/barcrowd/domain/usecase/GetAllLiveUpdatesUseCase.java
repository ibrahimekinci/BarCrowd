package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import java.util.List;

/**
 * Fetches all non-deleted live updates as a LiveData stream.
 */
public class GetAllLiveUpdatesUseCase {

    private final LiveUpdateRepository repository;

    public GetAllLiveUpdatesUseCase(LiveUpdateRepository repository) {
        this.repository = repository;
    }

    /**
     * Executes the use case, providing a stream of all live updates.
     * @return LiveData stream of LiveUpdates
     */
    public LiveData<List<LiveUpdate>> execute() {
        return repository.getAllLiveUpdates();
    }
}