package com.ibrahimekinci.barcrowd.ui.account;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.domain.usecase.SignOutUseCase;

/**
 * Factory for creating AccountViewModel instances.
 */
public class AccountViewModelFactory implements ViewModelProvider.Factory {

    private final SignOutUseCase signOutUseCase;

    public AccountViewModelFactory(SignOutUseCase signOutUseCase) {
        this.signOutUseCase = signOutUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AccountViewModel.class)) {
            return (T) new AccountViewModel(signOutUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}