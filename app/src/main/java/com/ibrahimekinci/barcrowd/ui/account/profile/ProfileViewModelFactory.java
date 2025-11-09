package com.ibrahimekinci.barcrowd.ui.account.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.data.remote.StorageWrapper;
import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.UpdateUserUseCase;

/**
 * Factory for creating ProfileViewModel instances with required dependencies.
 */
public class ProfileViewModelFactory implements ViewModelProvider.Factory {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final StorageWrapper storageWrapper;

    public ProfileViewModelFactory(GetCurrentUserUseCase getCurrentUserUseCase, UpdateUserUseCase updateUserUseCase, StorageWrapper storageWrapper) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.storageWrapper = storageWrapper;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(getCurrentUserUseCase, updateUserUseCase, storageWrapper);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
