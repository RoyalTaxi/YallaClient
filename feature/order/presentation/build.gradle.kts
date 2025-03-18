plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.order.presentation"
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.feature.order.data)
    implementation(projects.feature.order.domain)
    implementation(projects.feature.payment.domain)

    // Advanced BottomSheet
    implementation(libs.advanced.bottomsheet.material3)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}