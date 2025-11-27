package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

public class SoftDeleteLiveUpdateUseCase {
    private final LiveUpdateRepository repository;

    public SoftDeleteLiveUpdateUseCase(LiveUpdateRepository repository) {
        this.repository = repository;
    }

    public void execute(LiveUpdate update, LiveUpdateRepository.DeleteCallback callback) {
        repository.softDeleteUpdate(update, callback);
    }
}