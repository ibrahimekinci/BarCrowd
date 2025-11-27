package com.ibrahimekinci.barcrowd.ui.account;

import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.domain.usecase.SignOutUseCase;

/**
 * ViewModel for the AccountFragment.
 */
public class AccountViewModel extends ViewModel {

    private final SignOutUseCase signOutUseCase;

    public AccountViewModel(SignOutUseCase signOutUseCase) {
        this.signOutUseCase = signOutUseCase;
    }

    /**
     * Executes the sign-out use case.
     */
    public void signOut() {
        signOutUseCase.execute();
    }
}