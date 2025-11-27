package com.ibrahimekinci.barcrowd.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.usecase.IsUserLoggedInUseCase;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private IsUserLoggedInUseCase isUserLoggedInUseCase; // For auth check

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. Get Dependencies ---
        DependencyInjector injector = ((BarCrowdApplication) getApplication()).getDependencyInjector();
        isUserLoggedInUseCase = injector.getIsUserLoggedInUseCase();

        // --- 2. Find NavController ---
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // --- 3. Setup BottomNav ---
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

            // Initial setup for visual synchronization
            NavigationUI.setupWithNavController(bottomNav, navController);

            // --- 4. Custom Navigation Logic ---
            // A. Authentication checks for specific tabs.
            // B. Resetting the tab state (clearing back stack) on every click.
            bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    // --- A. Authentication Check ---
                    // If the user tries to access "New Live Update", check if logged in.
                    if (itemId == R.id.newLiveUpdateFragment) {
                        if (!isUserLoggedInUseCase.execute()) {
                            // Not logged in -> Redirect to Sign In
                            navController.navigate(R.id.action_global_to_signInFragment);
                            return false; // Do not select the tab
                        }
                    }

                    // --- B. Navigation with State Reset ---
                    // custom NavOptions to ensure the tab opens from "zero"
                    // (clearing any previous search results or nested screens).
                    NavOptions.Builder builder = new NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setRestoreState(false); // CRITICAL: Do not restore old state (fresh start)

                    // Clear the back stack up to the destination graph
                    builder.setPopUpTo(itemId, true);

                    try {
                        navController.navigate(itemId, null, builder.build());
                    } catch (IllegalArgumentException e) {
                        // Handle rare edge cases where destination is unknown
                        return false;
                    }

                    return true;
                }
            });
        }

        // --- 5. Handle Window Insets (Padding for system bars) ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }
}