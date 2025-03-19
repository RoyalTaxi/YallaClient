plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.settings"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.feature.setting.data)
}