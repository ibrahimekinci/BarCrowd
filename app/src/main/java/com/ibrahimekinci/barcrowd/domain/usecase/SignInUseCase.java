package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

/**
 * Use case for signing in a user with validation.
 */
public class SignInUseCase {
    private final UserRepository userRepository;

    public SignInUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the asynchronous sign-in flow.
     */
    public void execute(String email, String password, FirebaseAuthWrapper.AuthCallback callback) {
        // --- 1. Synchronous Validation ---
        try {
            if (!Validators.isValidEmail(email)) {
                throw new ValidationException("Invalid email format.");
            }
            if (password == null || password.isEmpty()) {
                throw new ValidationException("Password cannot be empty.");
            }
        } catch (ValidationException e) {
            callback.onFailure(e);
            return;
        }

        // --- 2. Proceed to Sign In ---
        userRepository.signIn(email, password, callback);
    }
}