package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper.AuthCallback;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

/**
 * Use case for signing up a user.
 * Validates all inputs before calling the repository.
 */
public class SignUpUseCase {
    private final UserRepository userRepository;

    public SignUpUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String email, String password, String fullName, String username, AuthCallback callback) throws ValidationException {
        // Perform all validations first
        if (!Validators.isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        if (!Validators.isValidPassword(password)) {
            throw new ValidationException("Password must be at least 8 characters with uppercase and number");
        }
        if (!Validators.isValidName(fullName)) {
            throw new ValidationException("Please enter a valid full name");
        }
        if (!Validators.isValidName(username)) { // Re-using name validator for username
            throw new ValidationException("Please enter a valid username");
        }

        // All checks passed, call the repository
        userRepository.signUp(email, password, fullName, username, callback);
    }
}