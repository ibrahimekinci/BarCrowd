package com.ibrahimekinci.barcrowd.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.usecase.IsUserLoggedInUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SignInUseCase;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.ValidationException;

public class SignInFragment extends Fragment {

    private SignInViewModel viewModel;
    private NavController navController;
    private IsUserLoggedInUseCase isUserLoggedInUseCase; // For checking if already logged in

    // View Components
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnSignIn;
    private TextView tvGoToSignUp;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Get Dependencies ---
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        navController = Navigation.findNavController(view);

        // --- 1a. CHECK IF ALREADY LOGGED IN ---
        isUserLoggedInUseCase = injector.getIsUserLoggedInUseCase();
        if (isUserLoggedInUseCase.execute()) {
            navigateToHome(); // Skip this fragment and go directly to home
            return; // Stop executing the rest of onViewCreated
        }
        // --- END OF CHECK ---

        SignInUseCase signInUseCase = injector.getSignInUseCase();
        SignInViewModelFactory factory = new SignInViewModelFactory(signInUseCase);
        viewModel = new ViewModelProvider(this, factory).get(SignInViewModel.class);

        // --- 2. Find Views ---

        tilEmail = view.findViewById(R.id.til_email);
        etEmail = view.findViewById(R.id.et_email);
        tilPassword = view.findViewById(R.id.til_password);
        etPassword = view.findViewById(R.id.et_password);
        btnSignIn = view.findViewById(R.id.btn_sign_in);
        tvGoToSignUp = view.findViewById(R.id.tv_go_to_sign_up);
        progressBar = view.findViewById(R.id.progress_bar);

        // --- 3. Set Click Listeners ---
        setupListeners();
    }

    private void setupListeners() {
        btnSignIn.setOnClickListener(v -> handleSignIn());

        tvGoToSignUp.setOnClickListener(v -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });
    }

    private void handleSignIn() {
        clearErrors();
        showLoading(true);

        String email = etEmail.getText() != null ? etEmail.getText().toString() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        viewModel.signIn(email, password, new FirebaseAuthWrapper.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    AppLogger.i("Sign in successful: " + user.getEmail());
                    showLoading(false);
                    navigateToHome();
                });
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    AppLogger.e("Sign in failed", e); // Use .e for errors
                    showLoading(false);

                    // MERGED ERROR HANDLING:
                    if (e instanceof ValidationException) {
                        // This catches errors from the SignInUseCase (e.g., "Invalid email format")
                        AppLogger.w(e.getMessage()); // Use the single-string 'w' method
                        String errorMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

                        if (errorMessage.contains("email")) {
                            tilEmail.setError(e.getMessage());
                        } else if (errorMessage.contains("password")) {
                            tilPassword.setError(e.getMessage());
                        } else {
                            Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        // This block handles Firebase Auth errors
                        Throwable cause = e.getCause();

                        if (cause instanceof FirebaseAuthInvalidCredentialsException) {
                            // This exception is thrown for "wrong password"
                            // Set error on both fields for a user-friendly message
                            tilEmail.setError("Invalid email or password.");
                            tilPassword.setError("Invalid email or password.");
                        } else if (cause instanceof FirebaseAuthInvalidUserException) {
                            // This exception is thrown for "email not found"
                            tilEmail.setError("No account found with this email.");
                        } else {
                            // For other errors (network, config, etc.)
                            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown sign-in error";
                            Snackbar.make(requireView(), "Sign in failed: " + errorMessage, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // The 'catch (ValidationException e)' block is removed.
    }

    private void navigateToHome() {
        // Navigate to Home and clear the entire back stack
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true) // Clear the whole graph
                .build();

        // Add check to prevent crash if action is not found (e.g., rapid clicks)
        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getAction(R.id.action_signInFragment_to_homeFragment) != null) {
            navController.navigate(R.id.action_signInFragment_to_homeFragment, null, navOptions);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSignIn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSignIn.setEnabled(true);
        }
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }
}