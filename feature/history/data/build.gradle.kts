plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.history.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.histroy)
    implementation(projects.feature.history.domain)
    implementation(libs.paging.common)
    implementation(libs.paging.compose.common)
}