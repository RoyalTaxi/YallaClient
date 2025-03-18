plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.auth.data"
}

dependencies {
    api(projects.core.data)
    implementation(projects.service.auth)
    implementation(projects.feature.auth.domain)
}