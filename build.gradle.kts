// Top-level build file where you can add configuration options common to all sub-projects/modules.



plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    alias(libs.plugins.google.gms.google.services) apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

buildscript {


    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")

        // Secrets Gradle Plugin for managing API keys
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}