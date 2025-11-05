package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

// domain/usecase/SoftDeleteLiveUpdateUseCase.java
public class SoftDeleteLiveUpdateUseCase {
    private final LiveUpdateRepository repository;

    public SoftDeleteLiveUpdateUseCase(LiveUpdateRepository repository) {
        this.repository = repository;
    }

    public void execute(LiveUpdate update, FirestoreWrapper.Callback<Void> callback) {
        repository.softDeleteUpdate(update, callback);
    }
}
