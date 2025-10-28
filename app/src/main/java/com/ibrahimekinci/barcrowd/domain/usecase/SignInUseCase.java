// SignInUseCase.java
package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;

/**
 * Use case for signing in a user with validation.
 */
public class SignInUseCase {
    private final UserRepository userRepository;

    public SignInUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String email, String password, FirebaseAuthWrapper.AuthCallback callback) throws ValidationException {
        if (!Validators.isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
        userRepository.signIn(email, password, callback);
    }
}