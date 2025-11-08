package com.ibrahimekinci.barcrowd.ui.activity;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
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
        // EdgeToEdge.enable(this); // Disabling for now, can cause padding issues
        setContentView(R.layout.activity_main);

        // --- 1. Get UseCase ---
        DependencyInjector injector = ((BarCrowdApplication) getApplication()).getDependencyInjector();
        isUserLoggedInUseCase = injector.getIsUserLoggedInUseCase();

        // --- 2. Find NavController---
        // This is the fix for the crash
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // Get the NavController from the NavHostFragment
        navController = navHostFragment.getNavController();

        // --- 3. Setup BottomNav ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Standard navigation setup (for Home and Search)
        NavigationUI.setupWithNavController(bottomNav, navController);

        // --- 4. LOGIC: Listener for Protected Tabs ---
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.newLiveUpdateFragment) {
                    // These are protected tabs. Is the user logged in?
                    if (isUserLoggedInUseCase.execute()) {
                        // User is logged in, proceed normally.
                        return NavigationUI.onNavDestinationSelected(item, navController);
                    } else {
                        // Not logged in. Navigate to SignInFragment.
                        // 'action_global_to_signInFragment' was defined in nav_graph.xml
                        navController.navigate(R.id.action_global_to_signInFragment);
                        return false; // We handled the navigation, don't proceed.
                    }
                }

                // For all other items (Home, Search, account drawer menu), use the standard navigation.
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }
}