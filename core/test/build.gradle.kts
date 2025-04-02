plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

android {
    namespace = "uz.yalla.client.core.test"
}

dependencies {
    // Core testing dependencies
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.androidx.core.testing)
    api(libs.truth)
    api(libs.mockk)
    api(libs.mockwebserver)

    // Android specific test dependencies
    api(libs.core.ktx)
    api(libs.androidx.junit)
    api(libs.androidx.runner)
    api(libs.androidx.core)

    // UI testing (for Compose)
    debugApi(libs.androidx.ui.test.manifest)
}