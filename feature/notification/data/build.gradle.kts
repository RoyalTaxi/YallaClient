plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.notification.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.notification)
    implementation(projects.feature.notification.domain)
    implementation(libs.paging.common)
    implementation(libs.paging.compose.common)
    testImplementation(projects.core.test)
}