package com.ibrahimekinci.barcrowd;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.google.firebase.FirebaseApp;
import com.ibrahimekinci.barcrowd.debug.SampleDataSeeder;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.util.ConnectivityReceiver;

/**
 * Custom Application class to initialize dependencies and handle global state.
 */
public class BarCrowdApplication extends Application {
    private DependencyInjector injector;

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Initialize Firebase FIRST
        FirebaseApp.initializeApp(this);

        // 2. Initialize Dependency Injector
        injector = new DependencyInjector();
        DependencyInjector.init(this);

        // 3. Register connectivity receiver for auto-sync
        registerReceiver(new ConnectivityReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // 4. Seed sample data
            new Thread(() -> {
                SampleDataSeeder.seedDatabase();
            }).start();

    }

    public DependencyInjector getDependencyInjector() {
        return injector;
    }
}