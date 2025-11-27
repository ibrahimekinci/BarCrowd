package com.ibrahimekinci.barcrowd.ui.liveupdate.feed;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.domain.usecase.GetAllLiveUpdatesUseCase;

public class AllLiveUpdatesViewModelFactory implements ViewModelProvider.Factory {

    private final GetAllLiveUpdatesUseCase getAllLiveUpdatesUseCase;

    public AllLiveUpdatesViewModelFactory(GetAllLiveUpdatesUseCase getAllLiveUpdatesUseCase) {
        this.getAllLiveUpdatesUseCase = getAllLiveUpdatesUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AllLiveUpdatesViewModel.class)) {
            return (T) new AllLiveUpdatesViewModel(getAllLiveUpdatesUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}