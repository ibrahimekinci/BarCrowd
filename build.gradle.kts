// build.gradle.kts

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    // 1. Safe Args Plugin: Necessary for generating Java classes
    // for type-safe navigation and argument passing.
    id("androidx.navigation.safeargs") version "2.9.5" apply false
}