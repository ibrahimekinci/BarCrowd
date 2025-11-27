package com.ibrahimekinci.barcrowd.ui.account.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.util.AppLogger;

/**
 * Fragment for displaying and editing user profile details.
 */
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private NavController navController;
    private User currentUser; // Store the current user object

    // Views
    private Button btnUpdateProfile;
    private TextInputLayout tilFullName, tilUsername, tilEmail;
    private TextInputEditText etFullName, etUsername, etEmail;
    private FrameLayout loadingOverlay;
    private MaterialToolbar toolbar;

    // Removed: imagePickerLauncher

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Find Views & NavController ---
        navController = Navigation.findNavController(view);
        findViews(view);

        // --- 2. Get Dependencies & ViewModel ---
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        ProfileViewModelFactory factory = new ProfileViewModelFactory(
                injector.getGetCurrentUserUseCase(),
                injector.getUpdateUserUseCase(),
                injector.getStorageWrapper()
        );
        viewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);

        // --- 3. Setup Listeners ---
        setupListeners();

        // --- 4. Observe ViewModel ---
        showLoading(true);
        observeViewModel();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        tilFullName = view.findViewById(R.id.til_full_name);
        tilUsername = view.findViewById(R.id.til_username);
        tilEmail = view.findViewById(R.id.til_email);
        etFullName = view.findViewById(R.id.et_full_name);
        etUsername = view.findViewById(R.id.et_username);
        etEmail = view.findViewById(R.id.et_email);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
        btnUpdateProfile.setOnClickListener(v -> onUpdateProfileClick());
    }

    /**
     * Observes the currentUser LiveData from the ViewModel.
     */
    private void observeViewModel() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                showLoading(false);
                this.currentUser = user;
                populateProfileData(user);
            } else {
                AppLogger.w("ProfileFragment: currentUser is null. Waiting for fetch to complete...");
            }
        });
    }

    /**
     * Populates the UI fields with data from the User object.
     */
    private void populateProfileData(User user) {
        etFullName.setText(user.getFullName());
        etUsername.setText(user.getUsername());
        etEmail.setText(user.getEmail());
    }

    /**
     * Handles the "Update Profile" button click.
     */
    private void onUpdateProfileClick() {
        if (currentUser == null) return;

        showLoading(true);

        // Update the local currentUser object with data from text fields
        currentUser.setFullName(etFullName.getText().toString());
        currentUser.setUsername(etUsername.getText().toString());
        // Email is not editable

        // Send the updated user object to the ViewModel
        updateUserDocument();
    }

    /**
     * Helper method to call the ViewModel to update the User document.
     */
    private void updateUserDocument() {
        viewModel.updateUser(currentUser, new FirebaseAuthWrapper.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // UI update must run on the main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showSnackbar(getString(R.string.profile_updated_success), false);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                AppLogger.e("Profile update failed", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showSnackbar(getString(R.string.profile_update_failed, e.getMessage()), true);
                    });
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showSnackbar(String message, boolean isError) {
        if (getView() == null) return;
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark));
        }
        snackbar.show();
    }
}