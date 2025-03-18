plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.service.payment"
}

dependencies {
    implementation(projects.core.service)
}