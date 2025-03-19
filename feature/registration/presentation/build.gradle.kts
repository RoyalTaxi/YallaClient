plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.registration.presentation"
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.feature.auth.domain)

    // DatePicker
    implementation(libs.snapper)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // LocalDate
    implementation(libs.threetenabp)
}