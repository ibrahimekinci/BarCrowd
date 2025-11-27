package com.ibrahimekinci.barcrowd.ui.liveupdate.details;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SoftDeleteLiveUpdateUseCase;

public class LiveUpdateDetailViewModelFactory implements ViewModelProvider.Factory {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final SoftDeleteLiveUpdateUseCase softDeleteLiveUpdateUseCase;

    public LiveUpdateDetailViewModelFactory(GetCurrentUserUseCase getCurrentUserUseCase, SoftDeleteLiveUpdateUseCase softDeleteLiveUpdateUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.softDeleteLiveUpdateUseCase = softDeleteLiveUpdateUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LiveUpdateDetailViewModel.class)) {
            return (T) new LiveUpdateDetailViewModel(getCurrentUserUseCase, softDeleteLiveUpdateUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}