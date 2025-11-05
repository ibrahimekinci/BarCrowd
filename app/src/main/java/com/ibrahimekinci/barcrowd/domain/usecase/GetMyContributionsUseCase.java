package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

import java.util.List;

public class GetMyContributionsUseCase {
    private LiveUpdateRepository liveUpdateRepository;

    public GetMyContributionsUseCase(LiveUpdateRepository liveUpdateRepository) {
        this.liveUpdateRepository = liveUpdateRepository;
    }

    public LiveData<List<LiveUpdate>> execute(String userId) throws ValidationException {
        if (!Validators.isNotEmpty(userId)) {
            throw new ValidationException("User ID is required");
        }
        return liveUpdateRepository.getUserContributions(userId);
    }
}