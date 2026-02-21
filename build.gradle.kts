// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false

    // Make sure that you have the Google services Gradle plugin 4.4.1+ dependency
    alias(libs.plugins.google.services) apply false
    // Add the dependency for the Crashlytics Gradle plugin
    alias(libs.plugins.firebase.crashlytics) apply false
}