plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.service.places"
}

dependencies {
    implementation(projects.core.service)
}