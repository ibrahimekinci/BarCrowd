package com.ibrahimekinci.barcrowd.ui.auth;

import androidx.lifecycle.ViewModel;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.domain.usecase.SignUpUseCase;
import com.ibrahimekinci.barcrowd.util.ValidationException;

/**
 * ViewModel for the SignUpFragment.
 */
public class SignUpViewModel extends ViewModel {

    private final SignUpUseCase signUpUseCase;

    public SignUpViewModel(SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
    }

    /**
     * Passes the sign-up request to the asynchronous use case.
     * The callback will handle all errors, including ValidationException.
     */
    public void signUp(String email, String password, String fullName, String username, FirebaseAuthWrapper.AuthCallback callback) {
        signUpUseCase.execute(email, password, fullName, username, callback);
    }
}