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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.usecase.SignUpUseCase;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.AuthException;
import com.ibrahimekinci.barcrowd.util.ValidationException;

public class SignUpFragment extends Fragment {

    private SignUpViewModel viewModel;
    private NavController navController;

    private TextInputLayout tilFullName, tilUsername, tilEmail, tilPassword;
    private TextInputEditText etFullName, etUsername, etEmail, etPassword;
    private Button btnSignUp;
    private TextView tvGoToSignIn;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Get Dependencies ---
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        navController = Navigation.findNavController(view);

        SignUpUseCase signUpUseCase = injector.getSignUpUseCase();
        SignUpViewModelFactory factory = new SignUpViewModelFactory(signUpUseCase);
        viewModel = new ViewModelProvider(this, factory).get(SignUpViewModel.class);

        // --- 2. Find Views ---

        tilFullName = view.findViewById(R.id.til_full_name);
        etFullName = view.findViewById(R.id.et_full_name);
        tilUsername = view.findViewById(R.id.til_username);
        etUsername = view.findViewById(R.id.et_username);
        tilEmail = view.findViewById(R.id.til_email);
        etEmail = view.findViewById(R.id.et_email);
        tilPassword = view.findViewById(R.id.til_password);
        etPassword = view.findViewById(R.id.et_password);
        btnSignUp = view.findViewById(R.id.btn_sign_up);
        tvGoToSignIn = view.findViewById(R.id.tv_go_to_sign_in);
        progressBar = view.findViewById(R.id.progress_bar);

        // --- 3. Set Click Listeners ---
        setupListeners();
    }

    private void setupListeners() {
        btnSignUp.setOnClickListener(v -> handleSignUp());

        tvGoToSignIn.setOnClickListener(v -> {
            // Go back to SignInFragment
            navController.popBackStack();
        });
    }

    private void handleSignUp() {
        clearErrors();
        showLoading(true);

        String fullName = etFullName.getText() != null ? etFullName.getText().toString() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        // The try/catch block is removed, as the UseCase now handles all errors via the callback.
        viewModel.signUp(email, password, fullName, username, new FirebaseAuthWrapper.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    AppLogger.i("Sign up successful: " + user.getEmail());
                    showLoading(false);
                    navigateToHome();
                });
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    showLoading(false);
                    AppLogger.e("Sign up failed", e);

                    // This is the user-friendly error handling logic
                    String errorMessage = e.getMessage() != null ? e.getMessage() : "An unknown error occurred.";
                    String lowerErrorMessage = errorMessage.toLowerCase();

                    if (e instanceof ValidationException) {
                        if (lowerErrorMessage.contains("full name")) {
                            tilFullName.setError(e.getMessage());
                        } else if (lowerErrorMessage.contains("username")) {
                            tilUsername.setError(e.getMessage());
                        } else if (lowerErrorMessage.contains("email")) {
                            tilEmail.setError(e.getMessage());
                        } else if (lowerErrorMessage.contains("password")) {
                            tilPassword.setError(e.getMessage());
                        } else {
                            Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    } else if (e instanceof AuthException) {
                        // This catches async email-in-use errors or other Firebase errors
                        Throwable cause = e.getCause();
                        if (cause instanceof FirebaseAuthUserCollisionException || lowerErrorMessage.contains("email address is already in use")) {
                            tilEmail.setError("This email address is already in use.");
                        } else {
                            // Other Firebase Auth errors (e.g., weak password, network error)
                            Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        // Other unexpected errors (e.g., network)
                        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void navigateToHome() {
        // Navigate to Home and clear the entire back stack (including SignIn)
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true) // Clear up to the start of the graph
                .build();

        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getAction(R.id.action_signUpFragment_to_homeFragment) != null) {
            navController.navigate(R.id.action_signUpFragment_to_homeFragment, null, navOptions);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSignUp.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSignUp.setEnabled(true);
        }
    }

    private void clearErrors() {
        tilFullName.setError(null);
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
    }
}