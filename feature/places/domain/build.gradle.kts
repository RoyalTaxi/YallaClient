plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.places.domain"
}

dependencies {
    implementation(projects.core.domain)
}