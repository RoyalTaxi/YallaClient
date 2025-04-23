plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.notification.presentation"
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.feature.notification.domain)
    implementation(projects.feature.notification.data)

    implementation(libs.paging.common)
    implementation(libs.paging.compose.common)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}