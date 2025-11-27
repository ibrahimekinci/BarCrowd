package com.ibrahimekinci.barcrowd.ui.liveupdate.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SoftDeleteLiveUpdateUseCase;

public class LiveUpdateDetailViewModel extends ViewModel {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final SoftDeleteLiveUpdateUseCase softDeleteLiveUpdateUseCase;
    private final LiveData<User> currentUser;

    public LiveUpdateDetailViewModel(GetCurrentUserUseCase getCurrentUserUseCase, SoftDeleteLiveUpdateUseCase softDeleteLiveUpdateUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.softDeleteLiveUpdateUseCase = softDeleteLiveUpdateUseCase;

        // Automatically load the current user when ViewModel is created
        this.currentUser = this.getCurrentUserUseCase.execute();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    /**
     * Soft deletes the given update.
     * Uses the specific DeleteCallback from the Repository interface.
     */
    public void deleteUpdate(LiveUpdate update, LiveUpdateRepository.DeleteCallback callback) {
        softDeleteLiveUpdateUseCase.execute(update, callback);
    }
}