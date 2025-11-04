package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;

/**
 * Use case for signing out the current user.
 */
public class SignOutUseCase {
    private final UserRepository userRepository;

    public SignOutUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute() {
        userRepository.signOut();
    }
}