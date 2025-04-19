plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.profile.domain"
}

dependencies {
    implementation(projects.core.domain)

    testImplementation(projects.core.test)
}