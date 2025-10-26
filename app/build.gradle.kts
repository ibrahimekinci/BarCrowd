plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.ibrahimekinci.barcrowd"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ibrahimekinci.barcrowd"
        minSdk = 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Jetpack MVVM and Lifecycle
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)

    // Jetpack Navigation and Safe Args
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Jetpack Room Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler) // Annotation processor for code generation
}