package com.ibrahimekinci.barcrowd.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.usecase.IsUserLoggedInUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SignOutUseCase;
import com.ibrahimekinci.barcrowd.util.AppLogger;

/**
 * Fragment for the "Account" screen.
 * Handles user sign-out and navigation to other info pages.
 */
public class AccountFragment extends Fragment {

    private IsUserLoggedInUseCase isUserLoggedInUseCase; // For auth check
    private AccountViewModel viewModel;
    private NavController navController;

    // View Ids from fragment_account.xml
    private View cardMyProfile;
    private View cardMyUpdates;
    private View cardLogout;
    private View cardLogin;

    private View navContactSupport;
    private View navFaqs;
    private View navPrivacy;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Get Dependencies ---
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        SignOutUseCase signOutUseCase = injector.getSignOutUseCase();
        isUserLoggedInUseCase = injector.getIsUserLoggedInUseCase();
        // The AccountViewModelFactory is necessary here for dependency injection
        AccountViewModelFactory factory = new AccountViewModelFactory(signOutUseCase);
        viewModel = new ViewModelProvider(this, factory).get(AccountViewModel.class);

        // --- 2. Find Views ---
        navController = Navigation.findNavController(view);
        
        cardMyProfile = view.findViewById(R.id.card_my_profile);
        cardMyUpdates = view.findViewById(R.id.card_my_updates);

        cardLogin = view.findViewById(R.id.nav_login);
        cardLogout = view.findViewById(R.id.nav_logout);

        navContactSupport = view.findViewById(R.id.nav_contact_support);
        navFaqs = view.findViewById(R.id.nav_faqs);
        navPrivacy = view.findViewById(R.id.nav_privacy);


        if (isUserLoggedInUseCase.execute()) {
            cardLogin.setVisibility(View.GONE);
            cardLogout.setVisibility(View.VISIBLE);
        } else {
            cardLogin.setVisibility(View.VISIBLE);
            cardLogout.setVisibility(View.GONE);
        }
        // --- 3. Set Click Listeners ---
        setupListeners();
    }

    private void setupListeners() {
        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> handleLogout());
        }
        if (cardLogin != null) {
            cardLogin.setOnClickListener(v -> {
                navController.navigate(R.id.action_accountFragment_to_signInFragment);
            });
        }


        // Example navigation (You need to create these Fragments and actions in nav_graph.xml)
        if (cardMyProfile != null) {
            cardMyProfile.setOnClickListener(v -> {
                // TODO: Uncomment when ProfileFragment is created
                // navController.navigate(R.id.action_accountFragment_to_profileFragment);
                AppLogger.d("My Profile clicked. Navigation not implemented yet.");
            });
        }
        if (cardMyUpdates != null) {
            cardMyUpdates.setOnClickListener(v -> {
                // TODO: Uncomment when MyUpdatesFragment is created
                // navController.navigate(R.id.action_accountFragment_to_myUpdatesFragment);
                AppLogger.d("My Updates clicked. Navigation not implemented yet.");
            });
        }

        if (navContactSupport != null) {
            navContactSupport.setOnClickListener(v -> {
                navController.navigate(R.id.action_accountFragment_to_contactSupportFragment);
            });
        }

        if (navFaqs != null) {
            navFaqs.setOnClickListener(v -> {
                navController.navigate(R.id.action_accountFragment_to_helpCenterFragment);
            });
        }

        if (navPrivacy != null) {
            navPrivacy.setOnClickListener(v -> {
                navController.navigate(R.id.action_accountFragment_to_privacyPolicyFragment);
            });
        }
        // -----------------------
    }

    /**
     * Handles the logout process: clears session and navigates to sign-in.
     */
    private void handleLogout() {
        AppLogger.i("User initiated logout.");
        viewModel.signOut();

        // Navigate back to the start (SignInFragment) and clear the entire app history
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true) // Pop all fragments up to the start of the graph
                .build();

        // Use the global action to go back to the sign-in screen
        // This action ID is defined in your nav_graph.xml
        if (navController != null) {
            navController.navigate(R.id.action_global_to_signInFragment, null, navOptions);
        }
    }
}