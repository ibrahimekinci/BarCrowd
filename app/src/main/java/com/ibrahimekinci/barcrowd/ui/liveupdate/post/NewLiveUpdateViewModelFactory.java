package com.ibrahimekinci.barcrowd.ui.liveupdate.post;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.data.remote.StorageWrapper;
import com.ibrahimekinci.barcrowd.domain.usecase.GetAllVenuesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.PostLiveUpdateUseCase;

/**
 * Factory for creating NewLiveUpdateViewModel instances with required dependencies.
 */
public class NewLiveUpdateViewModelFactory implements ViewModelProvider.Factory {

    private final GetAllVenuesUseCase getAllVenuesUseCase;
    private final PostLiveUpdateUseCase postLiveUpdateUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final StorageWrapper storageWrapper;

    public NewLiveUpdateViewModelFactory(GetAllVenuesUseCase getAllVenuesUseCase,
                                         PostLiveUpdateUseCase postLiveUpdateUseCase,
                                         GetCurrentUserUseCase getCurrentUserUseCase,
                                         StorageWrapper storageWrapper) {
        this.getAllVenuesUseCase = getAllVenuesUseCase;
        this.postLiveUpdateUseCase = postLiveUpdateUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.storageWrapper = storageWrapper;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NewLiveUpdateViewModel.class)) {
            return (T) new NewLiveUpdateViewModel(getAllVenuesUseCase, postLiveUpdateUseCase, getCurrentUserUseCase, storageWrapper);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}