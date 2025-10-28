package com.ibrahimekinci.barcrowd;

import android.app.Application;

/**
 * A custom Application class for instrumentation tests.
 * * It intentionally does NOT initialize Firebase, DependencyInjector, or other
 * production services to avoid crashes and side effects during tests.
 * This provides a "clean" application context for our tests to run in.
 */
public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // We leave this empty on purpose.
        // DO NOT call injector.init(this) here.
    }
}