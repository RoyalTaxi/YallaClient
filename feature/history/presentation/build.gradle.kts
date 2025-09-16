plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.history"
}

dependencies {


    implementation(projects.core.common)
    implementation(projects.feature.history.data)
    implementation(projects.feature.history.domain)
    implementation(projects.feature.home.domain)
    implementation(projects.feature.order.domain)

    implementation(libs.paging.common)
    implementation(libs.paging.compose.common)

    // Kotlinx Date time
    implementation(libs.kotlinx.datetime)

    // Advanced BottomSheet
    implementation(libs.advanced.bottomsheet.material3)

    // Maps
    implementation(libs.maps.compose)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}