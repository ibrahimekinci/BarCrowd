// build.gradle.kts

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    id("com.android.library") version "8.13.0" apply false

    id("androidx.navigation.safeargs") version "2.9.5" apply false // for type-safe navigation and argument passing.
    id("com.google.gms.google-services") version "4.4.4" apply false // Required for Firebase
}