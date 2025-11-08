package com.ibrahimekinci.barcrowd.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.ibrahimekinci.barcrowd.domain.usecase.SignInUseCase;

/**
 * Factory for creating SignInViewModel instances.
 */
public class SignInViewModelFactory implements ViewModelProvider.Factory {

    private final SignInUseCase signInUseCase;

    public SignInViewModelFactory(SignInUseCase signInUseCase) {
        this.signInUseCase = signInUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignInViewModel.class)) {
            return (T) new SignInViewModel(signInUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}