plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.promocode.presentation"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.feature.promocode.data)
    implementation(projects.feature.promocode.domain)

    // LocalDate
    implementation(libs.threetenabp)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}