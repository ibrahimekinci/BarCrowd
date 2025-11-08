plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.navigation.safeargs)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ibrahimekinci.barcrowd"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ibrahimekinci.barcrowd"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.ibrahimekinci.barcrowd.CustomTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.all {
            // 'it' refers to the test task
            it.jvmArgs("-XX:+EnableDynamicAgentLoading")
        }
    }
}

dependencies {
    // Core UI
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.core)

    // Jetpack MVVM and Lifecycle
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)

    // Jetpack Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Jetpack Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // CameraX
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // glide    
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // WorkManager
    implementation(libs.work.runtime)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.robolectric.v416)
    testImplementation(libs.androidx.core.testing)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.androidx.core.testing)

}
