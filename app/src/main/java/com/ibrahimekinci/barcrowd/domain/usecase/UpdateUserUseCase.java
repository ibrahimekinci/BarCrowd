package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

// domain/usecase/UpdateUserUseCase.java
public class UpdateUserUseCase {
    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user, FirebaseAuthWrapper.AuthCallback callback) throws ValidationException {
        if (!Validators.isValidName(user.getFullName())) {
            throw new ValidationException("Invalid full name");
        }
        if (!Validators.isValidName(user.getUsername())) {
            throw new ValidationException("Invalid username");
        }
        // Email is not editable
        userRepository.updateUser(user, callback);
    }
}
