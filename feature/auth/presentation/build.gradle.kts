plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.auth"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.feature.auth.data)
    implementation(projects.feature.auth.domain)
    implementation(projects.feature.setting.domain)

    implementation(libs.play.services.auth)
    implementation(libs.play.services.auth.api.phone)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}