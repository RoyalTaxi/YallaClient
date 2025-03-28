plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.profile.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.profile)
    implementation(projects.feature.profile.domain)
    implementation(projects.service.auth)
    implementation(projects.feature.auth.domain)
}