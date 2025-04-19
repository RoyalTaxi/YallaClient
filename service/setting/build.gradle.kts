plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.service.setting"
}

dependencies {
    implementation(projects.core.service)
    testImplementation(projects.core.test)
}