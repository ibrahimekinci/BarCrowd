// SignUpUseCase.java
package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;

/**
 * Use case for signing up a user with validation.
 */
public class SignUpUseCase {
    private final UserRepository userRepository;

    public SignUpUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String email, String password, FirebaseAuthWrapper.AuthCallback callback) throws ValidationException {
        if (!Validators.isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        if (!Validators.isValidPassword(password)) {
            throw new ValidationException("Password must be at least 8 characters with uppercase and number");
        }
        userRepository.signUp(email, password, callback);
    }
}