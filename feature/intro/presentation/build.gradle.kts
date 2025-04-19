plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.intro"
}

dependencies {
    implementation(projects.core.common)
    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}