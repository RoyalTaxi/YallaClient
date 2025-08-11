plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.profile.presentation"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.feature.profile.data)
    implementation(projects.feature.profile.domain)

    // LocalDate
    implementation(libs.threetenabp)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}