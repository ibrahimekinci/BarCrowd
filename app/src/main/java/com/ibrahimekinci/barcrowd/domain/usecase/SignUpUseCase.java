package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

/**
 * Use case for signing up a user.
 * 1. Validates all inputs synchronously.
 * 2. Checks for username uniqueness asynchronously.
 * 3. Calls the repository to sign up (which handles email uniqueness).
 */
public class SignUpUseCase {
    private final UserRepository userRepository;

    public SignUpUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the asynchronous sign-up flow.
     */
    public void execute(String email, String password, String fullName, String username, FirebaseAuthWrapper.AuthCallback callback) {

        // --- 1. Synchronous Validation ---
        try {
            if (!Validators.isValidName(fullName)) {
                throw new ValidationException("Full name must contain only letters and spaces.");
            }
            if (!Validators.isValidUsername(username)) {
                // Based on your Firestore rules [cite: Your provided firestore.rules]
                throw new ValidationException("Username must be between 3 and 30 characters.");
            }
            if (!Validators.isValidEmail(email)) {
                throw new ValidationException("Invalid email format.");
            }
            if (!Validators.isValidPassword(password)) {
                throw new ValidationException("Password must be at least 8 characters, with one uppercase letter and one number.");
            }
        } catch (ValidationException e) {
            callback.onFailure(e);
            return;
        }

        // --- 2. Asynchronous Username Uniqueness Check ---
        userRepository.checkUsernameExists(username, (isUniqueUsername, eUsername) -> {
            if (eUsername != null) {
                // Failed to query Firestore
                callback.onFailure(eUsername);
                return;
            }

            if (!isUniqueUsername) {
                // Username is already taken
                callback.onFailure(new ValidationException("This username is already taken."));
                return;
            }

            // --- 3. Asynchronous Email Uniqueness Check ---
            userRepository.checkEmailExists(email, (isUniqueEmail, eEmail) -> {
                if (eEmail != null) {
                    // Failed to query Firestore
                    callback.onFailure(eEmail);
                    return;
                }

                if (!isUniqueEmail) {
                    // email is already used by another user
                    callback.onFailure(new ValidationException("This email is already used by another user."));
                    return;
                }

                // --- 4. All checks passed, proceed to sign up ---
                // The repository's signUp method will handle the final email check.
                userRepository.signUp(email, password, fullName, username, callback);
            });
        });
    }
}