buildscript {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}


plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.android.test) apply false
}

configurations.all {
    exclude(group = "org.jogamp.gluegen", module = "gluegen-rt")
    exclude(group = "org.jogamp.jogl", module = "jogl-all")
    exclude(group = "dev.datlag", module = "jcef")
    exclude(group = "dev.datlag", module = "kcef")
}