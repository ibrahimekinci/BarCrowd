package com.ibrahimekinci.barcrowd.ui.account.profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.remote.StorageWrapper;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.UpdateUserUseCase;

/**
 * ViewModel for the ProfileFragment.
 * Manages fetching the current user and handling profile/photo updates.
 */
public class ProfileViewModel extends ViewModel {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final StorageWrapper storageWrapper;

    private final LiveData<User> currentUser;

    public ProfileViewModel(GetCurrentUserUseCase getCurrentUserUseCase, UpdateUserUseCase updateUserUseCase, StorageWrapper storageWrapper) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.storageWrapper = storageWrapper;

        // Initialize the user LiveData
        this.currentUser = this.getCurrentUserUseCase.execute();
    }

    /**
     * Gets the LiveData stream of the current user.
     */
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    /**
     * Attempts to update the user's profile information.
     * @param user The User object with updated name/username.
     * @param callback Callback for success or failure.
     */
    public void updateUser(User user, FirebaseAuthWrapper.AuthCallback callback) {
        // validation (sync and async) and reports errors via the callback.
        updateUserUseCase.execute(user, callback);
    }

    /**
     * Uploads a new profile photo to Firebase Storage.
     * @param photoUri The local Uri of the image file.
     * @param userId The ID of the current user.
     * @param callback Callback for success (returns download URL) or failure.
     */
    public void updateProfilePhoto(Uri photoUri, String userId, StorageWrapper.Callback<String> callback) {
        storageWrapper.uploadProfilePhoto(photoUri, userId, callback);
    }
}