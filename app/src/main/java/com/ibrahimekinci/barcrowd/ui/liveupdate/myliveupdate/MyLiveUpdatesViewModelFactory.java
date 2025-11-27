package com.ibrahimekinci.barcrowd.ui.liveupdate.myliveupdate;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SoftDeleteLiveUpdateUseCase;

public class MyLiveUpdatesViewModelFactory implements ViewModelProvider.Factory {

    private final LiveUpdateRepository repository;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final SoftDeleteLiveUpdateUseCase softDeleteLiveUpdateUseCase;

    public MyLiveUpdatesViewModelFactory(LiveUpdateRepository repository,
                                         GetCurrentUserUseCase getCurrentUserUseCase,
                                         SoftDeleteLiveUpdateUseCase softDeleteLiveUpdateUseCase) {
        this.repository = repository;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.softDeleteLiveUpdateUseCase = softDeleteLiveUpdateUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyLiveUpdatesViewModel.class)) {
            return (T) new MyLiveUpdatesViewModel(repository, getCurrentUserUseCase, softDeleteLiveUpdateUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}