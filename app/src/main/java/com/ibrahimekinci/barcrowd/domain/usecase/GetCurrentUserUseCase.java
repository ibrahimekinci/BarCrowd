package com.ibrahimekinci.barcrowd.domain.usecase;

import androidx.lifecycle.LiveData;

import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.domain.model.User;

/**
 * Use case for getting the current user.
 */
public class GetCurrentUserUseCase {
    private final UserRepository userRepository;

    public GetCurrentUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the use case.
     *
     * @return A LiveData object that will hold the User.
     * The return type is LiveData<User>, not User.
     */
    public LiveData<User> execute() { // <-- FIX: Return type changed to LiveData<User>
        return userRepository.getCurrentUser();
    }
}