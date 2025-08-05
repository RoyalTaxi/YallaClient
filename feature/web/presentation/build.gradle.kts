plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.web"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.presentation)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}