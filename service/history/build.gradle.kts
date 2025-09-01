plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.service.history"
}

dependencies {
    implementation(projects.core.service)

    testImplementation(projects.core.test)
}