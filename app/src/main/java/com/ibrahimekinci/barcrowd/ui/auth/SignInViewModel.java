package com.ibrahimekinci.barcrowd.ui.auth;

import androidx.lifecycle.ViewModel;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.domain.usecase.SignInUseCase;
import com.ibrahimekinci.barcrowd.util.ValidationException;

/**
 * ViewModel for the SignInFragment.
 */
public class SignInViewModel extends ViewModel {

    private final SignInUseCase signInUseCase;

    public SignInViewModel(SignInUseCase signInUseCase) {
        this.signInUseCase = signInUseCase;
    }

    /**
     * Passes the sign-in request to the use case.
     * The callback will handle all errors, including ValidationException.
     */
    public void signIn(String email, String password, FirebaseAuthWrapper.AuthCallback callback) {
        // The UseCase handles sync validation and returns errors via the callback
        signInUseCase.execute(email, password, callback);
    }
}