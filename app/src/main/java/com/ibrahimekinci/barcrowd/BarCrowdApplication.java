package com.ibrahimekinci.barcrowd;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.util.ConnectivityReceiver;

import com.google.firebase.FirebaseApp;
import com.ibrahimekinci.barcrowd.debug.SampleDataSeeder;

/**
 * Custom Application class to initialize dependencies and handle global state.
 */
public class BarCrowdApplication extends Application {
    private DependencyInjector injector;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        injector = new DependencyInjector();
        DependencyInjector.init(this);

        // Register connectivity receiver for auto-sync
        registerReceiver(new ConnectivityReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Seed sample data (run only in debug mode)
         SampleDataSeeder.seedDatabase();


    }

    public DependencyInjector getDependencyInjector() {
        return injector;
    }
}