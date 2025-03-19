plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.places.presentation"
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.feature.places.data)
    implementation(projects.feature.places.domain)
}