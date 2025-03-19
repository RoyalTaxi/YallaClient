plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.info"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.feature.setting.domain)
}