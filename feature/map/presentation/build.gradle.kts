plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.map.presentation"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.feature.map.data)
    implementation(projects.feature.map.domain)
    implementation(projects.feature.profile.domain)
    implementation(projects.feature.order.domain)
    implementation(projects.feature.places.domain)
    implementation(projects.feature.order.presentation)
    implementation(projects.feature.setting.domain)
    implementation(projects.feature.notification.domain)

    // Advanced BottomSheet
    implementation(libs.advanced.bottomsheet.material3)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}