plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.cancel"
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
}