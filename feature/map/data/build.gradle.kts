plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.map.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.map)
    implementation(projects.feature.map.domain)
    implementation(projects.feature.places.domain)
}