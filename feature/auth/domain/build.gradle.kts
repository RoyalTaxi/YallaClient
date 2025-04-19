plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.auth.domain"
}

dependencies {
    api(projects.core.domain)

    testImplementation(projects.core.test)
}