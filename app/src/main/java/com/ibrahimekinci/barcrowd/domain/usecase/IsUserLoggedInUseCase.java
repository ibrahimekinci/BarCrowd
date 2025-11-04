package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;

/**
 * Use case to check if a user is currently logged in.
 */
public class IsUserLoggedInUseCase {
    private final UserRepository userRepository;

    public IsUserLoggedInUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean execute() {
        return userRepository.isUserLoggedIn();
    }
}