package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.util.ValidationException;
import com.ibrahimekinci.barcrowd.util.Validators;

/**
 * Use case for updating a user's profile information.
 * Performs validation AND async uniqueness checks before sending to the repository.
 */
public class UpdateUserUseCase {
    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the update logic.
     * This method no longer throws ValidationException directly.
     * All errors (validation or async) are passed to the callback.
     *
     * @param user     The User object with updated fields.
     * @param callback Callback for success or failure.
     */
    public void execute(User user, FirebaseAuthWrapper.AuthCallback callback) {

        // --- 1. Synchronous Validation ---
        try {
            if (!Validators.isValidName(user.getFullName())) {
                throw new ValidationException("Full name must contain only letters and spaces.");
            }
            if (!Validators.isValidUsername(user.getUsername())) {
                throw new ValidationException("Username must be between 3 and 30 characters.");
            }
        } catch (ValidationException e) {
            callback.onFailure(e); // Return sync validation errors via callback
            return;
        }

        // --- 2. Asynchronous Username Uniqueness Check ---
        // We use the new repository method to check against *other* users.
        userRepository.checkUsernameForUpdate(user.getUsername(), user.getUserId(), (isUnique, e) -> {
            if (e != null) {
                // Failed to query Firestore
                callback.onFailure(e);
                return;
            }

            if (!isUnique) {
                // Username is already taken by someone else
                callback.onFailure(new ValidationException("This username is already taken."));
                return;
            }

            // --- 3. All checks passed, proceed to update ---
            userRepository.updateUser(user, callback);
        });
    }
}