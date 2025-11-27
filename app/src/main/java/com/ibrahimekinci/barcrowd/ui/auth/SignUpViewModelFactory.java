package com.ibrahimekinci.barcrowd.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.domain.usecase.SignUpUseCase;

/**
 * Factory for creating SignUpViewModel instances.
 */
public class SignUpViewModelFactory implements ViewModelProvider.Factory {

    private final SignUpUseCase signUpUseCase;

    public SignUpViewModelFactory(SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignUpViewModel.class)) {
            return (T) new SignUpViewModel(signUpUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}