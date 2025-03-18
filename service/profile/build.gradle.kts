plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.service.profile"
}

dependencies {
    implementation(projects.core.service)
}